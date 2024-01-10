package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Target;

public class GlobalTarget {
    public static final int MAX_X = 200;
    public static final int MAX_Y = 2000;
    public Target target;
    public boolean used = false;
    public String[][] tree;
    public int[] lineSize = new int[MAX_Y];

    public GlobalTarget(Target target) {
        this.target = target;
        tree = new String[MAX_Y][];
        for (int y = 0; y < MAX_Y; y++) {
            tree[y] = new String[MAX_X];
        }

    }
}
