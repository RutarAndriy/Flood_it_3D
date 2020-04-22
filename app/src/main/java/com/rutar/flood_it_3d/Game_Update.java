package com.rutar.flood_it_3d;

import android.util.Log;

import java.util.*;

import com.jme3.ui.*;
import com.jme3.util.*;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.asset.*;
import com.jme3.audio.*;
import com.jme3.effect.*;
import com.jme3.texture.*;
import com.jme3.material.*;

import jme3tools.optimize.*;

import static com.jme3.math.FastMath.*;
import static com.jme3.scene.VertexBuffer.*;
import static com.rutar.flood_it_3d.Flood_it_3D.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.sound;

///////////////////////////////////////////////////////////////////////////////////////////////////

class Game_Update {

static int tab_count;                                               // Кількість символів табуляції
static int need_help;                                          // Необхідність показу меню допомоги
static int step_count;                                                           // Кількість ходів
static int model_index;                                                            // Індекс моделі
static int background_w;                                                             // Ширина фону
static int background_h;                                                             // Висота фону
static int triangle_count;                                                 // Кількість трикутників
static int model_count = 48;                                                   // Кількість моделей
static int color_index = -1;                                            // Індекс активного кольору
static int game_state_index = -1;                                             // Індекс зміни стану гри
static int rotate_index = 150;                                      // Індекс повороту логотипу гри

static int model_per_level = model_count / 4;              // Кількість моделей у конкретному рівні

private static int color_now;                                     // Активний на даний момент колір
private static int[] color_indexes;                                      // Масив індексів кольорів
private static int[] vertex_indexes = { 0,1,2 };                       // Масив вершин в трикутнику

public static int[] scores = new int[model_count];

public static long tmp;                                                      // Допоміжна перемінна
public static long processing_time = -1;                                      // Час обробки моделі
public static long optimizing_time = -1;                                  // Час оптимізації моделі

public static Mesh temp_mesh = new Mesh();                                     // Тимчасовий каркас
public static Mesh[] model_meshes = new Mesh[2];                                   // Каркас моделі

public static Vector3f background_position;                          // Позиція фонового зображення

public static Vector3f[] normals = new Vector3f[3];                               // Масив нормалей
public static Vector3f[] vertices_temp = new Vector3f[3];                // Тимчасовий масив вершин

public static boolean is_done = true;              // Перемінна вказує на завершення обробки моделі
public static boolean game_is_running = false;                            // Якщо true - гра триває
public static boolean work_start = false;

public static Triangle temp_triangle;                                       // Тимчасовий трикутник
public static Triangle start_triangle;                                      // Початковий трикутник

public static ParticleEmitter emitter;                                        // Генератор частинок
public static Picture background_picture;                                      // Фонове зображення

public static float preview_rotate_angle = 45;                  // Кут нахилу моделі передперегляду

// ................................................................................................

public static LinkedHashSet<Integer> dynamic_index_list    // Масив індексів динамічних трикутників
        = new LinkedHashSet<>();

public static ArrayList<Geometry> dynamic_parts                   // Масив динамічних частин фігури
        = new ArrayList<>();

public static ArrayList<Geometry>[] static_parts                  // Масив кольорових частин фігури
        = new ArrayList[10];

// ................................................................................................

public static Quaternion quaternion = new Quaternion();             // Кватерніон повороту логотипу
public static Quaternion[] quaternions = new Quaternion[3];                   // Масив кватерніонів

public static Texture2D[] backgrounds = new Texture2D[4];                  // Масив фонових текстур

public static Material[] materials = new Material[11];                          // Масив матеріалів
public static Material dynamic_material = null;                              // Динамічний матеріал


public static Geometry[] static_geometries = new Geometry[10];                // Статичні геометрії
public static Geometry dynamic_geometry = null;                              // Динамічна геометрія

public static Spatial logo;                                                          // Логотип гри
public static Spatial preview_model;                               // Модель попереднього перегляду
public static Triangle[] triangles_list;                           // Масив усіх трикутників фігури
public static Geometry[] geometries_list;                            // Масив геометрій трикутників
public static Game_Triangle[] game_triangles_list; // Масив ігрових трикутників

private static Mesh mesh_temp;

// ................................................................................................

static int func_index = -1;
static int func_stages = 60;                                                       // 60, 45, 30, 0

static float delta_r;
static float delta_g;
static float delta_b;
static float func_add;
static float func_total;

static float[] func_values = null;

static float dx = 0.5f;
static float dy = 0.5f;

// ................................................................................................

static final int[] model_triangles_count = new int[] {              // Кількість трикутників фігури

    48,   72,   80,   96,   108,  140,  168,  180,  240,  252,  284,  324,
    400,  448,  464,  500,  520,  540,  576,  600,  636,  704,  720,  768,
    816,  828,  836,  860,  876,  896,  912,  960,  992,  1024, 1120, 1134,
    1140, 1152, 1200, 1280, 1296, 1308, 1312, 1344, 1348, 1370, 1440, 1584

};

static final int[] max_steps_count = new int[] {           // Мax кількість ходів для кожної фігури

    999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,
    999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,
    999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,
    999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999,  999

};

private static final int[] start_points = new int[] {      // Індекси початкових трикутників фігури

    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,   1,   1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,   1,   1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,   1,   1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,   1,   1

};

///////////////////////////////////////////////////////////////////////////////////////////////////
// Попередня ініціалізація головних компонентів

static void pre_Init (AssetManager manager) {

init_Materials(manager);                                                // Ініціалізація матеріалів
init_Particles(manager);                                                  // Ініціалізація частинок

set_Background_Speed();
calculate_Function();                                  // Розрахунок значень функції зміни кольорів

for (int z = 0; z < static_parts.length; z++) {
    static_parts[z] = new ArrayList<>();
}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація ігрових матеріалів
private static void init_Materials (AssetManager manager) {

dynamic_material = new Material(manager, "Common/MatDefs/Light/Lighting.j3md");
dynamic_material.setBoolean("UseMaterialColors",true);

// ................................................................................................

for (int z = 0; z < materials.length; z++) {

materials[z] = new Material(manager, "Common/MatDefs/Light/Lighting.j3md");
materials[z].setBoolean("UseMaterialColors",true);

switch (z) {

// Колір початкового трикутника
case 0:  materials[z].setColor("Diffuse",                                                 // Чорний
         new ColorRGBA(0.100f, 0.100f, 0.100f, 1.0f)); break;

// Легко
case 1:  materials[z].setColor("Diffuse",                                               // Червоний
         new ColorRGBA(1.000f, 0.000f, 0.000f, 1.0f)); break;
case 2:  materials[z].setColor("Diffuse",                                                  // Синій
         new ColorRGBA(0.168f, 0.000f, 1.000f, 1.0f)); break;
case 3:  materials[z].setColor("Diffuse",                                                // Зелений
         new ColorRGBA(0.000f, 0.941f, 0.188f, 1.0f)); break;
case 4:  materials[z].setColor("Diffuse",                                                 // Жовтий
         new ColorRGBA(1.000f, 0.964f, 0.000f, 1.0f)); break;

// Нормально
case 5:  materials[z].setColor("Diffuse",
         new ColorRGBA(1.000f, 1.000f, 1.000f, 1.0f)); break;                              // Білий
case 6:  materials[z].setColor("Diffuse",
         new ColorRGBA(0.200f, 0.200f, 0.200f, 1.0f)); break;                        // Темно-сірий

// Важко
case 7:  materials[z].setColor("Diffuse",
         new ColorRGBA(0.698f, 0.000f, 1.000f, 1.0f)); break;                         // Фіолетовий
case 8:  materials[z].setColor("Diffuse",
         new ColorRGBA(0.000f, 0.850f, 1.000f, 1.0f)); break;                          // Лазуровий

// Дуже важко
case 9:  materials[z].setColor("Diffuse",
         new ColorRGBA(0.596f, 1.000f, 0.000f, 1.0f)); break;                          // Салатовий
case 10: materials[z].setColor("Diffuse",
         new ColorRGBA(1.000f, 0.533f, 0.000f, 1.0f)); break;                       // Помаранчевий

}
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація частинок

private static void init_Particles (AssetManager manager) {

Material mat_red = new Material(manager, "Common/MatDefs/Misc/Particle.j3md");
mat_red.setTexture("Texture", manager.loadTexture("textures/shockwave.png"));

emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 7);

emitter.setImagesX(1);
emitter.setImagesY(1);
emitter.setLowLife(1.5f);
emitter.setHighLife(1.5f);

emitter.setEndSize(0.9f);
emitter.setStartSize(0.5f);
emitter.setMaterial(mat_red);
emitter.setGravity(Vector3f.ZERO);

emitter.setRandomAngle(true);
emitter.setParticlesPerSec(3);
emitter.setInWorldSpace(false);

emitter.setStartColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
emitter.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.0f));

emitter.emitParticles(1);

}

///////////////////////////////////////////////////////////////////////////////////////////////////

static void load_Model (int index, AssetManager manager) {

tab_count = -1;

step_count = 0;
color_index = 0;
color_now = 0;

game_is_running = true;

dynamic_parts.clear();
for (ArrayList parts : static_parts) { parts.clear(); }

dynamic_material.setColor("Diffuse", ColorRGBA.Black);

dynamic_index_list.clear();
dynamic_index_list.add(start_points[model_index]);

String path = "models/m_" + ((index < 10 ? "0" : "") + index) + ".j3o";

Node spatial = (Node) manager.loadModel(path);
print_Node_Tree(spatial);

Node node = (Node)(((Node) spatial.getChild(0)).getChild(0));

model_meshes[0] = ((Geometry) node.getChild(0)).getMesh();
model_meshes[1] = ((Geometry) node.getChild(1)).getMesh();

triangle_count = node.getTriangleCount();

start_triangle      = new Triangle();
color_indexes       = new int[triangle_count];
triangles_list      = new Triangle[triangle_count];
geometries_list     = new Geometry[triangle_count];
game_triangles_list = new Game_Triangle[triangle_count];

game_node_child.detachAllChildren();

game_node_child.setLocalScale(1);
game_node_child.setLocalRotation(new Quaternion());

// ................................................................................................

int triangle_index = 0;

for (Mesh model_mesh : model_meshes) {
for (int z = 0; z < model_mesh.getTriangleCount(); z++) {

    temp_triangle = new Triangle();
    model_mesh.getTriangle(z, temp_triangle);

    vertices_temp[0] = temp_triangle.get1();
    vertices_temp[1] = temp_triangle.get2();
    vertices_temp[2] = temp_triangle.get3();

    normals[0] = normals[1] = normals[2] = temp_triangle.getNormal();

    temp_mesh = new Mesh();
    temp_mesh.setBuffer(Type.Normal,   3, BufferUtils.createFloatBuffer(normals));
    temp_mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_temp));
    temp_mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(vertex_indexes));
    temp_mesh.updateBound();

    if (triangle_index == start_points[model_index]) { start_triangle = temp_triangle.clone(); }

    triangles_list[triangle_index] = temp_triangle.clone();
    geometries_list[triangle_index] = new Geometry(null, temp_mesh);
    triangle_index++;

}
}

// Заповнення масиву кольорів трикутників випадковими значеннями
for (int w = 0; w < color_indexes.length; w++) {
    color_indexes[w] = (int)(Math.random() * (4 + model_index/model_per_level * 2)) + 1;
}

color_indexes[start_points[model_index]] = 0;             // Задання кольору початковому трикутнику

// ................................................................................................

set_Neighborhoods();                                                          // Визначення сусідів
set_Color_For_Triangles();                                      // Задання кольорів для трикутників
optimization();                                                               // Оптимізація фігури

// Налаштування ParticleEmitter, який вказує розташування початкового трикутника
emitter.setLocalTranslation(start_triangle.getCenter().mult(1.05f));
emitter.setFaceNormal(start_triangle.getNormal());
emitter.setParticlesPerSec(3);

model_meshes[0] = model_meshes[1] = null;
work_start = true;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оптимізація фігури

private static void optimization() {

for (int z = 0; z < static_parts.length; z++) {

    // Обробляємо лише ту кількість кольорів, яка відповідає складності рівня
    if (z == 4 + model_index/model_per_level * 2) { break; }

    // Тимчасовий меш для збереження оптимізованої моделі
    mesh_temp = new Mesh();

    // Групуємо однакові за кольором трикутники у спільний меш
    if (!static_parts[z].isEmpty())
        { GeometryBatchFactory.mergeGeometries(static_parts[z], mesh_temp); }

    // Заповнюємо існуючі геометрії новими даними
    static_geometries[z] = new Geometry(null, mesh_temp);
    static_geometries[z].setMaterial(materials[z + 1]);

}

// ................................................................................................

mesh_temp = new Mesh();
GeometryBatchFactory.mergeGeometries(dynamic_parts, mesh_temp);

dynamic_geometry = new Geometry(null, mesh_temp);
dynamic_geometry.setMaterial(dynamic_material);

is_done = true;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Плавний перехід кольорів

static void change_Color() {

is_done = false;
work_start = true;

func_add = 0;
func_index = 0;

color_prev = get_Color_From_Material(materials[color_now]);
color_next = get_Color_From_Material(materials[color_index]);
color_tmp  = color_prev;

delta_r = color_next.r - color_prev.r;
delta_g = color_next.g - color_prev.g;
delta_b = color_next.b - color_prev.b;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Розрахунок значень функції зміни кольору

static void calculate_Function() {

float func_min = -3;
float func_max =  3;

func_total = 0;
func_values = new float[func_stages];

// ................................................................................................
// y = cos(x)*0.5

for (int z = 0; z < func_stages; z++) {

    float x = func_min + (func_max - func_min) / (func_stages - 1) * z;
    float y = FastMath.cos(DEG_TO_RAD * x) * 0.5f;

    func_total += y;
    func_values[z] = y;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Отримання кольору із матеріалу

private static ColorRGBA get_Color_From_Material (Material material) {
    return (ColorRGBA) material.getParam("Diffuse").getValue();
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод перемальовує модель після обробки

static void repaint_Model() {

    tmp = System.currentTimeMillis();

    triangles_Processing();
    set_Color_For_Triangles();

    processing_time = System.currentTimeMillis() - tmp;
    tmp = System.currentTimeMillis();

    optimization();

    optimizing_time = System.currentTimeMillis() - tmp;
    color_now = color_index;
    is_done = true;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод оновлює кольори трикутників в моделі

private static void set_Color_For_Triangles() {

dynamic_parts.clear();
for (ArrayList parts : static_parts) { parts.clear(); }

// Сортування трикутників за кольорами
for (int z = 0; z < triangle_count; z++) {

    boolean contains = dynamic_index_list.contains(z);

    //color_now = color_indexes[z];
    if (!contains) { static_parts[color_indexes[z] - 1].add(geometries_list[z]); }
}

// Групування динамічних трикутників
for (int index : dynamic_index_list) {
    dynamic_parts.add(geometries_list[index]);
}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка трикутників

private static void triangles_Processing() {

int triangle_index;
int neighborhood_index;
int neighborhoods_count;

Game_Triangle triangle;

final HashSet <Integer> temp_set = new HashSet<>(dynamic_index_list);

// ................................................................................................

do {

final Integer[] elements = temp_set.toArray(new Integer[]{});
temp_set.clear();

for (Integer element : elements) {

    triangle_index = element;
    triangle = game_triangles_list[triangle_index];
    neighborhoods_count = triangle.get_Neighborhoods_Count();

    for (int b = 0; b < neighborhoods_count; b++) {

        neighborhood_index = triangle.get_Neighborhoods()[b];

        if (color_indexes[neighborhood_index] == color_index &&
           !dynamic_index_list.contains(neighborhood_index)) {
                temp_set.add(neighborhood_index);
        }
    }
}

dynamic_index_list.addAll(temp_set);

}

while (!temp_set.isEmpty());

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Задання випадкової швидкості руху заднього фону

public static void set_Background_Speed() {

do { dx = (float) (Math.random() * 3) - 1.5f; }
while (dx < 0.2f && dx > -0.2f);

do { dy = (float) (Math.random() * 3) - 1.5f; }
while (dy < 0.2f && dy > -0.2f);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення заднього фону
// Діапазон зміщень від -1.0 до 1.0

public static void update_Background() {

background_picture.move(dx, -dy, 0);
background_position = background_picture.getLocalTranslation();

if      (background_position.x >= 0)
    { background_picture.move(-background_w, 0, 0); }

else if (background_position.x < -background_w)
    { background_picture.move(background_w, 0, 0);  }

if      (background_position.y >= 0)
    { background_picture.move(0, -background_h, 0); }

else if (background_position.y < -background_h)
    { background_picture.move(0, background_h, 0);  }

background_picture.updateGeometricState();

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Визначення сусідніх трикутників та занесення даних до масиву

private static void set_Neighborhoods() {

for (int a = 0; a < triangle_count; a++) {                             // Обробляємо усі трикутники

int neighborhoods_count = 0;                                                   // Кількість сусідів
game_triangles_list[a] = new Game_Triangle(a);

for (int b = 0; b < triangle_count; b++) {               // Порівнюємо конкретний трикутник з усіма

if (a == b) { continue; }                   // Якщо індекси трикутників однакові - пропускаємо крок
int common_points_count = 0;                                            // Кількість спільних точок

for (int c = 0; c < 3; c++) {
for (int d = 0; d < 3; d++) {

if (triangles_list[a].get(c).equals                                 // Порівнюємо точки трикутників
   (triangles_list[b].get(d))) { common_points_count++; }

}
}

if (common_points_count == 2) { game_triangles_list[a].add_Neighborhood(b);
                                neighborhoods_count++; }                          // Додаємо сусіда
if (neighborhoods_count == 3) { b = triangle_count; }                        // Усі сусіди знайдені

}
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Виведення інформації про структуру завантажуваної моделі

private static void print_Node_Tree (Node node) {

final List <Spatial> children = node.getChildren();

tab_count++;
for (Spatial spatial : children) {

String tab = "";
for (int z = 0; z < tab_count; z++) { tab += "\t"; }

if (spatial instanceof Node) { Log.i("MODEL_TREE", tab + spatial.toString());
                               print_Node_Tree((Node) spatial); }

else { Log.i("MODEL_TREE", tab + spatial.toString()); }

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

}