package de.bushnaq.abdalla.antanalyzer;

import java.util.ArrayList;
import java.util.List;

public class AntTree {
    public List<List<TreeNode>> row = new ArrayList<List<TreeNode>>();

    public TreeNode get(int x, int y) {
        if (y > row.size() - 1)
            return null;
        List<TreeNode> column = row.get(y);
        if (x > column.size() - 1)
            return null;
        return column.get(x);
    }

    public void put(int x, int y, TreeNode treeNode) {
        int rowSize = row.size();
        for (int iy = 0; iy <= y - rowSize; iy++) {
            List<TreeNode> column = new ArrayList<TreeNode>();
            row.add(column);
        }
        List<TreeNode> column = row.get(y);
        int columnSize = column.size();
        for (int ix = 0; ix < x - columnSize; ix++) {
            column.add(null);
        }
        column.add(treeNode);
    }

    public int size() {
        return row.size();
    }

    public int size(int y) {
        List<TreeNode> column = row.get(y);
        return column.size();
    }
}
