package de.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Target;

public class MultiAntTarget {
    public Target target;
    public boolean isUsed = false;// if true, then a main target has a direct or indirect dependency to this target.
    public boolean isMainNode = false;// if true, this target is one of the main targets, e.g. cant is called with this target in some use case.
    public AntTree tree = new AntTree();
    public boolean isSubTarget = false;// targets that other targets are dependent on

    public MultiAntTarget(Target target) {
        this.target = target;

    }
}
