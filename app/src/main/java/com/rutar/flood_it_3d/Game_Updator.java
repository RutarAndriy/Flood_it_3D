package com.rutar.flood_it_3d;

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

import static com.rutar.flood_it_3d.Flood_it_3D.*;

///////////////////////////////////////////////////////////////////////////////////////////////////

public class Game_Updator {

public static int need_help;                                   // Необхідність показу меню допомоги
public static int step_count;                                                    // Кількість ходів
public static int model_index;                                                     // Індекс моделі
public static int background_w;                                                      // Ширина фону
public static int background_h;                                                      // Висота фону
public static int triangle_count;                                          // Кількість трикутників
public static int model_count = 30;                                            // Кількість моделей
public static int color_index = -1;                                     // Індекс активного кольору
public static int change_index = -1;                                      // Індекс зміни стану гри
public static int sound_future = -1;                                 // Індекс перспективної музики
public static int sound_current = -1;                                   // Індекс актуальної музики
public static int rotate_index = 150;                               // Індекс повороту логотипу гри
public static int max_model_index = 30;                               // Доступна кількість моделей
public static int core_count = Runtime.getRuntime().availableProcessors();        // Кількість ядер

public static int[] color_indexes;                                       // Масив індексів кольорів
public static int[] neighborhoods;                                    // Масив сусідніх трикутників
public static int[] vertex_indexes = { 0,1,2 };                        // Масив вершин в трикутнику

public static final int[] model_triangles_count = new int[] {       // Кількість трикутників моделі

48,   72,   80,   108,  140,  180,  240,  252,  324,  400,
448,  500,  540,  576,  592,  600,  636,  720,  768,  828,
860,  896,  980,  1024, 1152, 1280, 1296, 1348, 1440, 1584,

};

public static final int[] start_points = new int[] {       // Індекси початкових трикутників моделі

5,    51,   39,   3,    58,   42,   94,   26,   205,  8,
350,  108,  180,  457,  84,   144,  186,  168,  21,   364,
147,  62,   275,  274,  213,  2,    205,  587,  719,  77,

};

public static final int[] max_steps_count = new int[] {    // Мax кількість ходів для кожної моделі

11,   14,   17,   17,   19,   24,   27,   27,   27,   59,
59,   84,   64,   79,   59,   69,   64,   54,   69,   74,
94,   99,   159,  134,  139,  124,  129,  279,  279,  349,

};

public static long repaint_time;                                     // Час перемальовування моделі
public static long processing_time;                                           // Час обробки моделі

public static Mesh temp_mesh;                                                  // Тимчасовий каркас
public static Mesh model_mesh;                                                     // Каркас моделі

public static Vector3f local_scale;                                            // Значення масштабу
public static Vector3f backgroung_position;                          // Позиція фонового зображення

public static Vector3f[] normals;                                                 // Масив нормалей
public static Vector3f[] vertices;                                                  // Масив вершин
public static Vector3f[] vertices_temp;                                  // Тимчасовий масив вершин

public static boolean is_done = true;              // Перемінна вказує на завершення обробки моделі
public static boolean game_is_running = false;                            // Якщо true - гра триває

public static Triangle temp_triangle;                                       // Типчасовий трикутник
public static Triangle start_triangle;                                      // Початковий трикутник

public static ParticleEmitter emitter;                                        // Генератор частинок
public static Picture background_picture;                                      // Фонове зображення

public static float preview_rotate_angle = 45;                  // Кут нахилу моделі передперегляду
public static float[] sound_volume = new float[4];       // Масив гучностей окремих звукових вузлів
public static float[] delta_volume = new float[4];                      // Перемінні зміни гучності

public static AudioNode[] sounds = new AudioNode[4];                                  // Аудіовузли

public static HashSet<Integer> static_parts = new HashSet<>();               // Статичні трикутники
public static HashSet<Integer> dinamic_parts = new HashSet<>();             // Динамічні трикутники

public static Quaternion quaternion = new Quaternion();             // Кватерніон повороту логотипу
public static Quaternion[] quaternions = new Quaternion[3];                   // Масив кватерніонів

public static Texture2D[] backgrounds = new Texture2D[3];                  // Масив фонових текстур
public static Process[] threads = new Process[core_count];                 // Масив потоків обробки

public static Material[] materials = new Material[9];                           // Масив матеріалів

public static Node[] clone_nodes = new Node[core_count];                         // Клоновані вузли
public static Node[] original_nodes = new Node[core_count];                    // Оригінальні вузли

public static Spatial logo;                                                          // Логотип гри

// Неактуально починаючи з версії 2.1
//public static Spatial[]  models_array_1 = new Spatial[model_count];    // Масив неігрових моделей
//public static Geometry[] models_array_2 = new Geometry[model_count];     // Масив ігрових моделей

public static Spatial preview_model;
public static Geometry[] geometries;                                 // Масив геометрій трикутників

///////////////////////////////////////////////////////////////////////////////////////////////////
// Попередня ініціалізація головних компонентів
public static void pre_Init (AssetManager manager) {

// Ініціалізація потоків та оригінальних вузлів

for (int z = 0; z < core_count; z++) { threads[z] = new Process(z);
                                       original_nodes[z] = new Node(); }

init_Materials(manager);

// init_Models(manager);
// В новій версії моделі підвантажуються динамічно

init_Sounds(manager);
init_Particles(manager);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація ігрових матеріалів
private static void init_Materials (AssetManager manager) {

for (int z = 0; z < materials.length; z++) {

materials[z] = new Material(manager, "Common/MatDefs/Light/Lighting.j3md");
materials[z].setBoolean("UseMaterialColors",true);

switch (z) {

case 0: materials[z].setColor("Diffuse", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f)); break; // Red
case 1: materials[z].setColor("Diffuse", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f)); break; // Green
case 2: materials[z].setColor("Diffuse", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)); break; // Blue
case 3: materials[z].setColor("Diffuse", new ColorRGBA(1.0f, 1.0f, 0.0f, 1.0f)); break; // Yellow
case 4: materials[z].setColor("Diffuse", new ColorRGBA(0.7f, 0.0f, 1.0f, 1.0f)); break; // Magenta
case 5: materials[z].setColor("Diffuse", new ColorRGBA(0.0f, 0.8f, 1.0f, 1.0f)); break; // Cyan
case 6: materials[z].setColor("Diffuse", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f)); break; // White
case 7: materials[z].setColor("Diffuse", new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f)); break; // Gray

case 8: materials[z].setColor("Diffuse", new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f)); break; // Black

}
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація моделей
// P.S. Неактуально, починаючи з версії 2.1
/*private static void init_Models (AssetManager manager) {

for (int z = 0; z < model_count; z++) {

String index = (z < 10 ? "0" : "") + z;
String path_1 = "models/models_a/m_" + index + "_a.j3o";
String path_2 = "models/models_b/m_" + index + "_b.j3o";

models_array_1[z] = manager.loadModel(path_1);

models_array_2[z] = ((Geometry)((Node)((Node)((Node)manager.
        loadModel(path_2)).getChild(0)).getChild(0)).getChild(0));

((Node)((Node)((Node)models_array_1[z]).getChild(0)).
        getChild(0)).getChild(0).setMaterial(materials[6]);

((Node)((Node)((Node)models_array_1[z]).getChild(0)).
        getChild(0)).getChild(1).setMaterial(z < 10 ? materials[2]:
                                             z < 20 ? materials[1]:
                                                      materials[0]);

}
}*/

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
// Ініціалізація звуків
private static void init_Sounds (AssetManager manager) {

for (int z = 0; z < sounds.length; z++) {

switch (z) {
    case 0: sounds[0] = new AudioNode(manager, "sounds/menu.ogg",   AudioData.DataType.Stream);
    case 1: sounds[1] = new AudioNode(manager, "sounds/easy.ogg",   AudioData.DataType.Stream);
    case 2: sounds[2] = new AudioNode(manager, "sounds/medium.ogg", AudioData.DataType.Stream);
    case 3: sounds[3] = new AudioNode(manager, "sounds/hard.ogg",   AudioData.DataType.Stream);
}

sounds[z].setLooping(true);
sounds[z].setPositional(false);
sounds[z].setVolume(0);

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка моделей та підготовка ігрового вузла
public static void load_Model (int index, AssetManager manager) {

step_count = 0;
color_index = 8;
game_is_running = true;

static_parts = new HashSet();
dinamic_parts = new HashSet();
dinamic_parts.add(start_points[model_index]);

String path = "models/models_b/m_" + ((index < 10 ? "0" : "") + index) + "_b.j3o";

model_mesh = ((Geometry)((Node)((Node)((Node)manager.
              loadModel(path)).getChild(0)).getChild(0)).getChild(0)).getMesh();

triangle_count = model_mesh.getTriangleCount();

color_indexes = new int[triangle_count];
geometries = new Geometry[triangle_count];
neighborhoods = new int[triangle_count * 3];
vertices = new Vector3f[triangle_count * 3];

game_node_child.setLocalScale(1);
game_node_child.setLocalRotation(new Quaternion());

game_node_child.detachAllChildren();
for (int q = 0; q < original_nodes.length; q++) { original_nodes[q].detachAllChildren(); }

// Розбивання моделі на трикутники

for (int z = 0; z < triangle_count; z++) {

temp_triangle = new Triangle();
model_mesh.getTriangle(z, temp_triangle);

vertices_temp = new Vector3f[3];
vertices_temp[0] = vertices[z*3+0] = temp_triangle.get1();
vertices_temp[1] = vertices[z*3+1] = temp_triangle.get2();
vertices_temp[2] = vertices[z*3+2] = temp_triangle.get3();

normals = new Vector3f[3];
normals[0] = normals[1] = normals[2] = temp_triangle.getNormal();

// Створення геометрії з кожного трикутника

temp_mesh = new Mesh();

temp_mesh.setBuffer(VertexBuffer.Type.Normal,   3, BufferUtils.createFloatBuffer(normals));
temp_mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices_temp));
temp_mesh.setBuffer(VertexBuffer.Type.Index,    3, BufferUtils.createIntBuffer(vertex_indexes));
temp_mesh.updateBound();

geometries[z] = new Geometry(null, temp_mesh);
original_nodes[z%core_count].attachChild(geometries[z]);

}

// Постобробка - визначення сусідніх трикутників та встановлення випадкових кольорів

for (int w = 0; w < color_indexes.length; w++) {
    color_indexes[w] = (int)(Math.random() * (4 + model_index/10 * 2));
}

color_indexes[start_points[model_index]] = 8;
set_Neighborhoods();
set_Color();

// Оптимізація моделі

for (int a = 0; a < clone_nodes.length; a++) { clone_nodes[a] = (Node) original_nodes[a].clone();
                                               game_node_child.attachChild(clone_nodes[a]); }

GeometryBatchFactory.optimize(game_node_child);

// Налаштування ParticleEmitter, який вказує розташування початкового трикутника

start_triangle = new Triangle();
model_mesh.getTriangle(start_points[model_index], start_triangle);
emitter.setLocalTranslation(start_triangle.getCenter().mult(1.05f));
emitter.setFaceNormal(start_triangle.getNormal());
emitter.setParticlesPerSec(3);

game_node_child.attachChild(emitter);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод оновлює кольори трикутників в моделі
public static void set_Color() {

// Замальовування фігури кольорами із масиву
for (int z = 0; z < triangle_count; z++) {
    geometries[z].setMaterial(materials[color_indexes[z]]);
}

// Замальовування динамічних трикутників активним кольором
Integer[] integers = dinamic_parts.toArray(new Integer[dinamic_parts.size()]);
for (int z = 0; z < integers.length; z++) {
    geometries[integers[z]].setMaterial(materials[color_index]);
}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Визначення сусідніх трикутників та занесення даних до масиву
private static void set_Neighborhoods() {

for (int a = 0; a < triangle_count; a++) {

int neighborhoods_count = 0;

for (int b = 0; b < triangle_count; b++) {

int common_points_count = 0;

for (int c = 0; c < 3; c++) {
for (int d = 0; d < 3; d++) {

if (vertices[a*3+c].equals(vertices[b*3+d])) { common_points_count++; }

}
}

if (common_points_count == 2) { neighborhoods[a*3 + neighborhoods_count++] = b; }
if (neighborhoods_count == 3) { b = triangle_count; }

}
}

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}