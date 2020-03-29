package com.rutar.flood_it_3d;

import java.util.*;

// ................................................................................................

class Game_Triangle {

private final int index;                                                       // Індекс трикутника
private final int[] neighborhoods = new int[3];                               // Сусідні трикутники

private int neighborhoods_count;

///////////////////////////////////////////////////////////////////////////////////////////////////

Game_Triangle (int index) { this.index = index;
                            this.neighborhoods_count = 0; }

///////////////////////////////////////////////////////////////////////////////////////////////////

void add_Neighborhood (int index) { neighborhoods[neighborhoods_count] = index;
                                    neighborhoods_count++; }

///////////////////////////////////////////////////////////////////////////////////////////////////

int get_Neighborhoods_Count() { return neighborhoods_count; }

///////////////////////////////////////////////////////////////////////////////////////////////////

int[] get_Neighborhoods() { return neighborhoods; }

///////////////////////////////////////////////////////////////////////////////////////////////////

String print_Info() {

return "Index: " + index                                  + "\n" +
       "Neighborhoods: " + Arrays.toString(neighborhoods) + "\n" +
       "Neighborhoods count: " + neighborhoods_count;

}

///////////////////////////////////////////////////////////////////////////////////////////////////

}