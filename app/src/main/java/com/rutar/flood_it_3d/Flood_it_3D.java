package com.rutar.flood_it_3d;

import java.util.*;

import com.jme3.ui.*;
import com.jme3.app.*;
import com.jme3.math.*;
import com.jme3.font.*;
import com.jme3.input.*;
import com.jme3.light.*;
import com.jme3.scene.*;
import com.jme3.texture.*;
import com.jme3.renderer.*;
import com.jme3.input.controls.*;

import jme3tools.optimize.*;

import static com.rutar.flood_it_3d.Unificator.*;
import static com.rutar.flood_it_3d.Game_Updator.*;
import static com.rutar.flood_it_3d.Flood_it_Activity.*;

///////////////////////////////////////////////////////////////////////////////////////////////////

public class Flood_it_3D extends SimpleApplication {

public static Node logo_node = new Node("logo");                     // Вузол з логотипом гри
public static Node game_node_main = new Node("game_main");          // Головний ігровий вузол
public static Node game_node_child = new Node("game_child");      // Допоміжний ігровий вузол
public static Node preview_node_main = new Node("preview_main");     // Головний вузол вибору
public static Node preview_node_child = new Node("preview_child"); // Допоміжний вузол вибору

private final Runtime runtime = Runtime.getRuntime();
private BitmapText debug;                          // Допоміжна інформація про використання пам'яті

private int fps = 0;
private int frame_counter = 0;
private float second_counter = 0.0f;

public static int debug_index = 0;

private String memory_debug = "Total Memory: %1$.3f Mb\n"+
                              "Free Memory:  %2$.3f Mb\n";

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

for (int z = 0; z < 3; z++) {
    backgrounds[z] = (Texture2D) assetManager.loadTexture("textures/background_0" + z + ".jpg");
    backgrounds[z].setWrap(Texture.WrapMode.Repeat);
}

background_w = backgrounds[model_index/10].getImage().getWidth();
background_h = backgrounds[model_index/10].getImage().getHeight();

int x_count = (int) (W / background_w) + 3;
int y_count = (int) (H / background_h) + 3;

background_picture = new Picture("background_picture");
background_picture.getMesh().scaleTextureCoordinates(new Vector2f(x_count, y_count));
background_picture.setTexture(assetManager, backgrounds[model_index/10], false);
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

Game_Updator.pre_Init(assetManager);

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення ігрового циклу
@Override
public void simpleUpdate (float tpf) {

update_Game_State();
update_Components();
update_Background();
update_FPS();

if (debug_index == 1) { debug.setText("FPS: " + fps); }

else if (debug_index == 2) { debug.setText(String.format(memory_debug,
                                                         runtime.totalMemory()/1024/1024f,
                                                         runtime.freeMemory()/1024/1024f)); }

else { debug.setText(""); }

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Визначення FPS

private void update_FPS() {

if (debug_index == 1) {

    second_counter += getTimer().getTimePerFrame();
    frame_counter++;

    if (second_counter >= 1.0f) {
        fps = (int) (frame_counter / second_counter);
        second_counter = 0.0f;
        frame_counter = 0;
    }
}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Мотод оновлює ігровий стан
private void update_Game_State() {

switch (change_index) {

// Видалення всіх моделей
case 1:
rootNode.detachAllChildren();
change_index = -1;
break;

// Показ заставки
case 2:
rootNode.detachAllChildren();
rootNode.attachChild(logo_node);
change_index = -1;
break;

// Показ моделі передперегляду
case 3:
rootNode.detachAllChildren();
preview_node_child.detachAllChildren();

String path = "models/models_a/m_" + ((model_index < 10 ? "0" : "") + model_index) + "_a.j3o";
preview_model = assetManager.loadModel(path);

((Node)((Node)((Node)preview_model).getChild(0)).
        getChild(0)).getChild(0).setMaterial(materials[6]);

((Node)((Node)((Node)preview_model).getChild(0)).
        getChild(0)).getChild(1).setMaterial(model_index < 10 ? materials[2]:
                                             model_index < 20 ? materials[1]:
                                                                materials[0]);

preview_node_child.attachChild(preview_model);
game_node_main.setLocalRotation(quaternions[2]);
game_node_child.setLocalRotation(quaternions[2]);
rootNode.attachChild(preview_node_main);
background_picture.setTexture(assetManager, backgrounds[model_index/10], false);
change_index = -1;
break;

// Показ ігрової моделі
case 4:
rootNode.detachAllChildren();
rootNode.attachChild(game_node_main);
preview_node_child.detachAllChildren();

Game_Updator.load_Model(model_index, assetManager);
    new Thread(new Runnable() {
        @Override
        public void run() {
            try { Thread.sleep(300); }
            catch (Exception e) {}
            handler.sendEmptyMessage(3);
        }
    }).start();
change_index = -1;
break;

// Перемальовування моделі
case 5:
repaint_Model();
step_count++;
emitter.setParticlesPerSec(0);
change_index = -1;
break;

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення ігрових компонентів
private static void update_Components() {

if (rotate_index >= 0 + 50 &&
    rotate_index < 36 + 50) { logo.rotate(10 * FastMath.DEG_TO_RAD, 0, 0); }

boolean end_work = true;

for (int z = 0; z < core_count; z++) {
    if (!threads[z].work_is_done) { end_work = false; }
}

// Оптимізація компонентів після обробки

if (end_work && !is_done) {

game_node_child.detachAllChildren();
for (int z = 0; z < core_count; z++) {
    game_node_child.attachChild(clone_nodes[z]);
}

quaternions[0] = game_node_main.getLocalRotation().clone();
quaternions[1] = game_node_child.getLocalRotation().clone();

local_scale = game_node_child.getLocalScale().clone();

game_node_main.setLocalRotation(new Quaternion());
game_node_child.setLocalRotation(new Quaternion());
game_node_child.setLocalScale(1);

GeometryBatchFactory.optimize(game_node_child);
game_node_child.attachChild(emitter);

game_node_main.setLocalRotation(quaternions[0]);
game_node_child.setLocalRotation(quaternions[1]);
game_node_child.setLocalScale(local_scale);

is_done = true;
repaint_time = System.currentTimeMillis() - repaint_time;

}

// Перевірка на завершення рівня

if (game_is_running && dinamic_parts.size() == triangle_count) { game_is_running = false;
                                                                 handler.sendEmptyMessage(4); }

// Оновлення інших компонентів

quaternion.fromAngleAxis(preview_rotate_angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);

preview_node_child.rotate(0, 0.01f, 0);
preview_node_main.setLocalRotation(quaternion);
rotate_index++;

// Оновлення гучності звукових вузлів

for (int z = 0; z < sounds.length; z++) {

    sound_volume[z] += delta_volume[z];
    if (sound_volume[z] > 0.5f) {
        sound_volume[z] = 0.5f;
        delta_volume[z] = 0;
    }
    if (sound_volume[z] < 0) {
        sound_volume[z] = 0;
        delta_volume[z] = 0;
        sounds[z].stop();
    }

    sounds[z].setVolume(sound_volume[z] * sound);

}

// Перемикання музики

if (sound_current != sound_future) {

boolean is_mute = true;
for (int z = 0; z < sounds.length; z++) { is_mute = sound_volume[z] == 0; }

if (is_mute) { sounds[sound_future].play();
               sound_current = sound_future;
               delta_volume[sound_future] = 0.005f; }

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Оновлення заднього фону
private static void update_Background() {

background_picture.move(0.5f, -0.5f, 0);
backgroung_position = background_picture.getLocalTranslation();

if      (backgroung_position.x >= 0)
{ background_picture.move(-background_w, 0, 0); }

else if (backgroung_position.x < -background_w)
{ background_picture.move(background_w, 0, 0);  }

if      (backgroung_position.y >= 0)
{ background_picture.move(0, -background_h, 0); }

else if (backgroung_position.y < -background_h)
{ background_picture.move(0, background_h, 0);  }

background_picture.updateGeometricState();

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Метод перемальовує модель після обробки
private static void repaint_Model() {

if (is_done) {

is_done = false;
processing_time = System.currentTimeMillis();

processing();

repaint_time = System.currentTimeMillis();
processing_time = repaint_time - processing_time;

set_Color();

for (int z = 0; z < threads.length; z++) { threads[z] = new Process(z);
                                           threads[z].start(); }

}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Обробка трикутників
private static void processing() {

int triangle_index = -1;
HashSet <Integer> temp_set = new HashSet();
temp_set.addAll(dinamic_parts);

do {

Integer[] array_z = temp_set.toArray(new Integer[temp_set.size()]);
temp_set.clear();

for (int a = 0; a < array_z.length; a++) {

triangle_index = array_z[a];

for (int b = 0; b < 3; b++) {

int index = neighborhoods[triangle_index * 3 + b];
if (color_indexes[index] == color_index &&
    !temp_set.contains(index) &&
    !dinamic_parts.contains(index)) { temp_set.add(index); }

}
}

dinamic_parts.addAll(temp_set);

}

while (!temp_set.isEmpty());

}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Відтворення музики із заданим індексом
public static void play_Sounds (int id) {

for (int z = 0; z < sounds.length; z++) { delta_volume[z] = -0.005f; }
sound_future = id;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}