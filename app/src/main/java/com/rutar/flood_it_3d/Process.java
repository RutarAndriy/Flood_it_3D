package com.rutar.flood_it_3d;

import jme3tools.optimize.*;

import static com.rutar.flood_it_3d.Game_Updator.*;

public class Process extends Thread {

int index = -1;
boolean work_is_done = false;

///////////////////////////////////////////////////////////////////////////////////////////////////

Process (int node_index) { index = node_index; }

///////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void run() {

clone_nodes[index] = original_nodes[index].clone(true);
GeometryBatchFactory.optimize(clone_nodes[index]);

work_is_done = true;

}

// Кінець класу <Process> /////////////////////////////////////////////////////////////////////////

}