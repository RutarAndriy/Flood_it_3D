package com.rutar.flood_it_3d;

import com.jme3.ui.*;
import com.jme3.app.*;
import com.jme3.math.*;
import com.jme3.font.*;
import com.jme3.input.*;
import com.jme3.light.*;
import com.jme3.scene.*;
import com.jme3.audio.*;
import com.jme3.texture.*;
import com.jme3.renderer.*;
import com.jme3.material.*;
import com.jme3.input.controls.*;

import static com.jme3.math.FastMath.*;
import static com.rutar.flood_it_3d.Constants.*;
import static com.rutar.flood_it_3d.Unification.*;
import static com.rutar.flood_it_3d.Game_Update.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

// ................................................................................................

public class Flood_it_3D extends SimpleApplication {

static Node logo_node = new Node("logo");                           // Вузол з логотипом гри
static Node game_node_main = new Node("game_main");                // Головний ігровий вузол
static Node game_node_child = new Node("game_child");            // Допоміжний ігровий вузол
static Node preview_node_main = new Node("preview_main");           // Головний вузол вибору
static Node preview_node_child = new Node("preview_child");       // Допоміжний вузол вибору

static AudioNode current_sound = new AudioNode();                              // Аудіовузол

static ColorRGBA color_tmp = null;
static ColorRGBA color_prev = null;
static ColorRGBA color_next = null;

static Geometry[] debug_meshes = new Geometry[4];                                 // Debug елементи
static Material[] debug_materials = new Material[4];                    // Матеріали debug елемнтів

private final Runtime runtime = Runtime.getRuntime();
private BitmapText debug;                          // Допоміжна інформація про використання пам'яті

private int fps = 0;
private int frame_counter = 0;
private float second_counter = 0.0f;

private static final float volume_change_speed = 0.05f;

private static int sound_future = -1;                                    // Індекс наступної музики

private static float sound_volume = 0.0f;                               // Гучність звукового вузла
private static float delta_volume = 0.0f;               // Перемінна зміни гучності звукового вузла

private static boolean sound_is_changed = false;

static int debug_mode = 0;
static int debug_index = 0;

private String full_debug = "FPS: %1$d\n" +
                            "Total Memory: %2$.3f Mb\n" +
                            "Free Memory:  %3$.3f Mb\n" +
                            "Processing time: %4$d ms\n" +
                            "Optimizing time: %5$d ms";

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void simpleInitApp() {

flyCam.setMoveSpeed(10);
flyCam.setDragToRotate(false);
flyCam.setEnabled(false);

debug = new BitmapText(guiFont, false);
debug.setSize(guiFont.getCharSet().getRenderedSize());
debug.setLocalTranslation(0, settings.getHeight(), 0);
guiNode.attachChild(debug);

DirectionalLight light = new DirectionalLight(cam.getDirection());
rootNode.addLight(light);

getStateManager().getState(StatsAppState.class).setDisplayFps(false);
getStateManager().getState(StatsAppState.class).setDisplayStatView(false);

quaternions[2] = new Quaternion();

// Ініціалізація фонового зображення
for (int z = 0; z < backgrounds.length; z++) {
    backgrounds[z] = (Texture2D) assetManager.loadTexture("textures/background_0" + z + ".jpg");
    backgrounds[z].setWrap(Texture.WrapMode.Repeat);
}

background_w = backgrounds[model_index/model_per_level].getImage().getWidth();
background_h = backgrounds[model_index/model_per_level].getImage().getHeight();

int x_count = W / background_w + 3;
int y_count = H / background_h + 3;

background_picture = new Picture("background_picture");
background_picture.getMesh().scaleTextureCoordinates(new Vector2f(x_count, y_count));
background_picture.setTexture(assetManager, backgrounds[model_index/model_per_level], false);
background_picture.setWidth(background_w * x_count);
background_picture.setHeight(background_h * y_count);

viewPort.setClearFlags(false, true, true);

ViewPort preViewPort = renderManager.createPreView("preViewPort", cam);
preViewPort.attachScene(background_picture);

background_picture.updateGeometricState();

// Ініціалізація введення

inputManager.addMapping("touch", new TouchTrigger(TouchInput.ALL));
inputManager.addMapping("x-", new MouseAxisTrigger(MouseInput.AXIS_X, true));
inputManager.addMapping("x+", new MouseAxisTrigger(MouseInput.AXIS_X, false));
inputManager.addMapping("y-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
inputManager.addMapping("y+", new MouseAxisTrigger(MouseInput.AXIS_Y, false));

inputManager.addListener(Listener.touchListener, "touch");
inputManager.addListener(Listener.analog_Listener, "x-", "x+", "y-", "y+");

// Ініціалізація вузлів та інших компонентів

game_node_main.attachChild(game_node_child);
preview_node_main.attachChild(preview_node_child);
preview_node_main.setLocalTranslation(0, -0.3f, 0);

logo = assetManager.loadModel("models/logo.j3o");
logo.setLocalTranslation(0, 2.3f, 0);

if (W * 1f / H < 1.7f) { logo.setLocalScale(0.9f); }
else                   { logo.setLocalScale(1.1f); }

logo_node.attachChild(logo);
rootNode.attachChild(logo_node);

for (int z = 0; z < debug_meshes.length; z++)
    { debug_meshes[z] = new Geometry("Debug mesh " + z); }

for (int z = 0; z < debug_materials.length; z++)
    { debug_materials[z] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      debug_materials[z].setColor("Color", ColorRGBA.Blue); }

Game_Update.pre_Init(assetManager);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення ігрового циклу

@Override
public void simpleUpdate (float tpf) {

update_Game_State();
update_Components();
update_Background();
update_FPS();

// ................................................................................................
// Плавний перехід кольорів

if (func_index != -1 &&
    func_index < func_stages) {

    func_add += func_values[func_index] / func_total;

    color_tmp = color_prev.clone();
    color_tmp = color_tmp.add(new ColorRGBA(delta_r * func_add,
                                            delta_g * func_add,
                                            delta_b * func_add, 0));

    func_index++;
    dynamic_material.setColor("Diffuse", color_tmp);

}

else if (func_index >= func_stages) {

    dynamic_material = materials[color_index].clone();

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            repaint_Model();
        }
    });
    t.setPriority(Thread.MAX_PRIORITY);
    t.start();

    step_count++;
    func_index = -1;
}

// ................................................................................................
// Формування debug інформації

switch (debug_mode) {

case 1: debug.setText(String.format(full_debug.substring(0,
                                    full_debug.indexOf("\n")), fps)); break;

case 2: debug.setText(String.format(full_debug, fps,
                                    runtime.totalMemory()/1024/1024f,
                                    runtime.freeMemory()/1024/1024f,
                                    processing_time, optimizing_time)); break;

case 3: debug.setText("Game debug mode" + "\n" +
                      "Debug triangle: " + debug_index); break;

default: debug.setText(""); break;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення ігрових компонентів

private void update_Components() {

if (rotate_index >=     50 &&
    rotate_index < 36 + 50) { logo.rotate(10 * DEG_TO_RAD, 0, 0); }

// ................................................................................................
// Заміна фігури на оброблену

if (work_start && is_done) {

work_start = false;

game_node_child.detachAllChildren();
game_node_child.attachChild(emitter);

if (debug_mode == 3) {
    for (Geometry debug_mesh : debug_meshes) { game_node_child.attachChild(debug_mesh); }
}

for (int z = 0; z < 4 + model_index/model_per_level * 2; z++)
    { game_node_child.attachChild(static_geometries[z]); }

game_node_child.attachChild(dynamic_geometry);
is_done = true;

}

// ................................................................................................

// Перевірка на завершення рівня
if (game_is_running &&
    dynamic_index_list.size() == triangle_count) { game_is_running = false;
                                                   handler.sendEmptyMessage(4); }

// Оновлення інших компонентів
quaternion.fromAngleAxis(preview_rotate_angle * DEG_TO_RAD, Vector3f.UNIT_X);

preview_node_child.rotate(0, model_index%2 == 0 ? -0.01f : 0.01f, 0);
preview_node_main.setLocalRotation(quaternion);

if (rotate_index > 500) { rotate_index = 0; }
else                    { rotate_index++;   }

update_Sound_Nodes();

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Відтворення музики із заданим індексом

void play_Sounds (int id) {

    delta_volume = volume_change_speed * -1;
    sound_is_changed = true;
    sound_future = id;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення звукових вузлів

private void update_Sound_Nodes() {

// Зміна гучності аудіовузлів
if (sound > 0 ||
    delta_volume < 0) { sound_volume += delta_volume; }

// Спадання гучності музики
if (sound_is_changed) {

    if (sound_volume < 0) { sound_volume = 0;
                            sound_is_changed = false;
                            delta_volume = volume_change_speed;
                            init_Sounds(sound_future); }
}

// Зростання гучності музики
else {

    if (sound_future != -1) {

        if (current_sound != null) { current_sound.play(); }

            if (sound_volume > 1.0f) { sound_volume = 1.0f;
                                       delta_volume = 0; }

    }
}

// Задання рівня гучності аудіовузлів
if (current_sound != null) { current_sound.setVolume(sound_volume); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Ініціалізація звукового вузла

private void init_Sounds (int sound_index) {

String file = null;
if (current_sound != null) { current_sound.stop(); }

if (sound_index == -1) { current_sound = null;
                         return; }

switch (sound_index) {
    case 0: file = sound == 1 ? "old_menu.ogg"      : "new_menu.ogg";      break;
    case 1: file = sound == 1 ? "old_easy.ogg"      : "new_easy.ogg";      break;
    case 2: file = sound == 1 ? "old_medium.ogg"    : "new_medium.ogg";    break;
    case 3: file = sound == 1 ? "old_hard.ogg"      : "new_hard.ogg";      break;
    case 4: file = sound == 1 ? "new_very_hard.ogg" : "new_very_hard.ogg"; break;
}

current_sound = new AudioNode(assetManager, "sounds/" + file, AudioData.DataType.Stream);

current_sound.setPositional(false);
current_sound.setLooping(true);
current_sound.setVolume(0);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Мотод оновлює ігровий стан

private void update_Game_State() {

switch (game_state_index) {

// Видалення всіх моделей
case 1:

    rootNode.detachAllChildren();
    game_state_index = -1;
    break;

// Показ заставки
case 2:

    rootNode.detachAllChildren();
    rootNode.attachChild(logo_node);
    game_state_index = -1;
    break;

// Показ моделі передперегляду
case 3:

    rootNode.detachAllChildren();
    preview_node_child.detachAllChildren();
    String path = "models/m_" + ((model_index < 10 ? "0" : "") + model_index) + ".j3o";
    preview_model = assetManager.loadModel(path);

    int color = (model_index/model_per_level == 1 || model_index/model_per_level == 2) ? 6 : 5;

    ((Node)((Node)((Node)preview_model).getChild(0)).
             getChild(0)).getChild(0).setMaterial(materials[color]);

    int color_index = -1;
    switch (model_index/model_per_level) {
        case 0: color_index = 2; break; // Синій
        case 1: color_index = 3; break; // Зелений
        case 2: color_index = 1; break; // Червоний
        case 3: color_index = 6; break; // Темно-сірий
    }

    ((Node)((Node)((Node)preview_model).getChild(0)).
             getChild(0)).getChild(1).setMaterial(materials[color_index]);

    preview_node_child.attachChild(preview_model);
    game_node_main.setLocalRotation(quaternions[2]);
    game_node_child.setLocalRotation(quaternions[2]);
    rootNode.attachChild(preview_node_main);
    background_picture.setTexture(assetManager, backgrounds[model_index/model_per_level], false);
    game_state_index = -1;
    break;

// Показ ігрової моделі
case 4:

    rootNode.detachAllChildren();
    rootNode.attachChild(game_node_main);
    preview_node_child.detachAllChildren();

    Game_Update.load_Model(model_index, assetManager);
    new Thread(new Runnable() {
        @Override
        public void run() {
            try { Thread.sleep(300); }
            catch (Exception ignored) {}
            handler.sendEmptyMessage(3);
        }
    }).start();
    game_state_index = -1;
    break;

// Перемальовування моделі
case 5:

    change_Color();
    emitter.setParticlesPerSec(0);
    game_state_index = -1;
    break;

// Оновлення debug-інформації
case 6: game_state_index = -1;
        update_Debug_View();
        break;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////

void restart_App() {

activity.recreate();
game_state_index = 2;
sound_volume = 0;
delta_volume = 0;

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Визначення FPS

private void update_FPS() {

if (debug_mode == 1 ||
    debug_mode == 2) {

second_counter += getTimer().getTimePerFrame();
frame_counter++;

if (second_counter >= 1.0f) {
    fps = (int) (frame_counter / second_counter);
    second_counter = 0.0f;
    frame_counter = 0;
}

}
}

// Кінець класу <Flood_it_3D> /////////////////////////////////////////////////////////////////////

}
