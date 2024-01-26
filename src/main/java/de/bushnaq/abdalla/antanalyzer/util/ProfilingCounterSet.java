package de.bushnaq.abdalla.antanalyzer.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class ProfilingCounterSet implements Cloneable {
    public long calcTimeNanoSec = 0;
    private long calcCount = 0;
    private long graphicsCount = 0;
    private long graphicsTimeNanoSec = 0;
    private long smbCount = 0;
    private long smbTimeNanoSec = 0;
    private long sqlCount = 0;
    private long sqlTimeNanoSec = 0;
    private long netCount = 0;
    private long netTimeNanoSec = 0;
    private long totalTimeNanoSec = System.nanoTime();//time of creation
    private long gcCount = 0;
    private long gcTime = 0;

    @Override
    public ProfilingCounterSet clone() throws CloneNotSupportedException {
        ProfilingCounterSet s = (ProfilingCounterSet) super.clone();
        s.setTotalTimeNanoSec(System.nanoTime());
        int count = 0;
        int time = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            count += gc.getCollectionCount();
            time += gc.getCollectionTime();
        }
        s.setGcCount(count);
        s.setGcTime(time);
        return s;
    }

    public long getCalcCount() {
        return calcCount;
    }

    public long getGcCount() {
        return gcCount;
    }

    public long getGcTime() {
        return gcTime;
    }

    public long getGraphicsCount() {
        return graphicsCount;
    }

    public long getGraphicsTimeNanoSec() {
        return graphicsTimeNanoSec;
    }

    public long getNetCount() {
        return netCount;
    }

    public long getNetTimeNanoSec() {
        return netTimeNanoSec;
    }

    public long getSmbCount() {
        return smbCount;
    }

    public long getSmbTimeNanoSec() {
        return smbTimeNanoSec;
    }

    public long getSqlCount() {
        return sqlCount;
    }

    public long getSqlTimeNanoSec() {
        return sqlTimeNanoSec;
    }

    public long getTotalTimeNanoSec() {
        return totalTimeNanoSec;
    }

    public void setCalcCount(long calcCount) {
        this.calcCount = calcCount;
    }

    public void setGcCount(long gcCount) {
        this.gcCount = gcCount;
    }

    public void setGcTime(long gcTime) {
        this.gcTime = gcTime;
    }

    public void setGraphicsCount(long graphicsCount) {
        this.graphicsCount = graphicsCount;
    }

    public void setGraphicsTimeNanoSec(long graphicsTimeNanoSec) {
        this.graphicsTimeNanoSec = graphicsTimeNanoSec;
    }

    public void setNetCount(long netCount) {
        this.netCount = netCount;
    }

    public void setNetTimeNanoSec(long netTimeNanoSec) {
        this.netTimeNanoSec = netTimeNanoSec;
    }

    public void setSmbCount(long smbCount) {
        this.smbCount = smbCount;
    }

    public void setSmbTimeNanoSec(long smbTimeNanoSec) {
        this.smbTimeNanoSec = smbTimeNanoSec;
    }

    public void setSqlCount(long sqlCount) {
        this.sqlCount = sqlCount;
    }

    public void setSqlTimeNanoSec(long sqlTimeNanoSec) {
        this.sqlTimeNanoSec = sqlTimeNanoSec;
    }

    public void setTotalTimeNanoSec(long totalTimeNanoSec) {
        this.totalTimeNanoSec = totalTimeNanoSec;
    }

}