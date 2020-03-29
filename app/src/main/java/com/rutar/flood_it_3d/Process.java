package com.rutar.flood_it_3d;

public class Process extends Thread {

private int index;
private boolean work_is_done;

///////////////////////////////////////////////////////////////////////////////

public Process (int node_index) { index = node_index;
                                  work_is_done = false; }

///////////////////////////////////////////////////////////////////////////////

public boolean is_Work_Done() { return work_is_done; }

///////////////////////////////////////////////////////////////////////////////

@Override
public void run() {

//clone_nodes[index] = original_nodes[index].clone(true);
//GeometryBatchFactory.optimize(clone_nodes[index]);

//work_is_done = true;

}

///////////////////////////////////////////////////////////////////////////////

}