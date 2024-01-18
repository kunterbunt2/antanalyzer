package de.bushnaq.abdalla.antanalyzer.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceCounters implements AutoCloseable {
    static Map<String, Long> counters = new HashMap<>();
    static final String DELTA_SYMBOL = "";
    static final String BLACK_LEFT_POINTING_TRIANGLE = "<";
    // private final static Logger logger =
    // LoggerFactory.getLogger(PerformanceCounters.class);
    static Deque<ProfilingCounterSet> stack = new ArrayDeque<>();
    static final String SUM_SYMBOL = "";
    static ProfilingCounterSet total = new ProfilingCounterSet();
    static {
        push(LoggerFactory.getLogger(PerformanceCounters.class));// push first element
    }
    PerformanceCounterType type;
    long start;

    public PerformanceCounters(PerformanceCounterType type) {
        this.type = type;
        start = System.nanoTime();
    }

    public static void incrementCounter(String counterName, long count) {
        Long counter = counters.get(counterName);
        if (counter == null) {
            counter = 0L;
        }
        counter += count;
        counters.put(counterName, counter);
    }

    private static void info(String name, Long count, long delta, String comment, Logger logger) {
        if (count != null) {
            if (count != 0) {
                //                MDC.put(name, nanoToString(delta));
                //                MDC.put("count", count.toString());
                //                MDC.put("comment", comment);
                logger.info(String.format("%s = %s, count = %d %s.", name, nanoToString(delta), count, comment));
                //                logger.info(String.format("Performance result"));
                //                MDC.clear();
            }
        } else {
            //            MDC.put(name, nanoToString(delta));
            //            MDC.put("comment", comment);
            logger.info(String.format("%s = %s %s.", name, nanoToString(delta), comment));
            //            logger.info(String.format("Performance result"));
            //            MDC.clear();
        }
    }

    private static void info(String name, Long count, Logger logger) {
        if (count != null && count != 0) {
            //            MDC.put("'" + name + "'", count.toString());
            logger.info(String.format("'%s' = %d", name, count));
            //            MDC.clear();
        }
    }

    private static void log(String subject, Logger logger) {

        long count2 = 0;
        long time2 = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            count2 += gc.getCollectionCount();
            time2 += gc.getCollectionTime();
        }

        long totalDelta = System.nanoTime() - stack.peek().getTotalTimeNanoSec();
        long netDelta = total.getNetTimeNanoSec() - stack.peek().getNetTimeNanoSec();
        long sqlDelta = total.getSqlTimeNanoSec() - stack.peek().getSqlTimeNanoSec();
        long smbDelta = total.getSmbTimeNanoSec() - stack.peek().getSmbTimeNanoSec();
        long calcDelta = total.calcTimeNanoSec - stack.peek().calcTimeNanoSec;
        long graphicsDelta = total.getGraphicsTimeNanoSec() - stack.peek().getGraphicsTimeNanoSec();
        long performanceDelta = total.getSqlTimeNanoSec() - stack.peek().getSqlTimeNanoSec() + total.getSmbTimeNanoSec() - stack.peek().getSmbTimeNanoSec()
                + total.calcTimeNanoSec - stack.peek().calcTimeNanoSec + total.getGraphicsTimeNanoSec() - stack.peek().getGraphicsTimeNanoSec();
        long restDelta = totalDelta - performanceDelta;
        long delta = totalDelta / 100;//one percent
        String seriousDiscrepancy = restDelta > delta ? "(over 1% discrepancy)" : "";
        logger.info("--------------------------------------------------");
        if (subject.length() > 0) {
            logger.info(String.format(BLACK_LEFT_POINTING_TRIANGLE + "[%s]", subject));
        }
        info(DELTA_SYMBOL + "NET", total.getNetCount() - stack.peek().getNetCount(), netDelta, "", logger);
        info(DELTA_SYMBOL + "SQL", total.getSqlCount() - stack.peek().getSqlCount(), sqlDelta, "", logger);
        info(DELTA_SYMBOL + "SMB", total.getSmbCount() - stack.peek().getSmbCount(), smbDelta, "", logger);
        info(DELTA_SYMBOL + "CALC", total.getCalcCount() - stack.peek().getCalcCount(), calcDelta, "", logger);
        info(DELTA_SYMBOL + "2D", total.getGraphicsCount() - stack.peek().getGraphicsCount(), graphicsDelta, "", logger);
        info(DELTA_SYMBOL + "REST", null, restDelta, seriousDiscrepancy, logger);
        // String totalDeltaString = nanoToString(totalDelta);
        info(DELTA_SYMBOL + "ALL", null, totalDelta, "", logger);
        for (String counterName : counters.keySet()) {
            Long counter = counters.get(counterName);
            info(counterName, counter, logger);
        }
        info(SUM_SYMBOL + "Garbage Collections", (count2 - stack.peek().getGcCount()), logger);
        info(SUM_SYMBOL + "Garbage Collection Time (ms)", (time2 - stack.peek().getGcTime()), logger);
        logger.info("--------------------------------------------------");
    }

    public static void logReport(Logger logger) throws Exception {
        if (stack.size() != 1) {
            throw new Exception("stack size of 1 expected but has " + stack.size());
        }
        String subject = "";
        log(subject, logger);
    }

    public static void logReport(String subject, Logger logger) {
        log(subject, logger);
    }

    private static String nanoToString(long time) {
        return Util.create24hDurationString(time / 1000000L, true, true, true, true);
    }

    public static void pop(Logger logger) {
        stack.pop();
    }

    public static void push(Logger logger) {
        try {
            stack.push(total.clone());
        } catch (CloneNotSupportedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        switch (type) {
        case Graphics: {
            total.setGraphicsCount(total.getGraphicsCount() + 1);
            final long end = System.nanoTime();
            final long delta = end - start;
            total.setGraphicsTimeNanoSec(total.getGraphicsTimeNanoSec() + delta);
            // logger.trace(String.format("count = %d delta = %dns sum = %ds",
            // graphicsCount, delta, graphicsTime / 1000000000));
        }
            break;
        case Smb: {
            total.setSmbCount(total.getSmbCount() + 1);
            final long end = System.nanoTime();
            final long delta = end - start;
            total.setSmbTimeNanoSec(total.getSmbTimeNanoSec() + delta);
            // logger.trace(String.format("count = %d delta = %dns sum = %ds", sqlCount,
            // delta, sqlTime / 1000000000));
        }
            break;
        case Calc: {
            total.setCalcCount(total.getCalcCount() + 1);
            final long end = System.nanoTime();
            final long delta = end - start;
            total.calcTimeNanoSec += delta;
            // logger.trace(String.format("calc-delta = %dms", delta / 1000000));
        }
            break;
        case Sql: {
            total.setSqlCount(total.getSqlCount() + 1);
            final long end = System.nanoTime();
            final long delta = end - start;
            total.setSqlTimeNanoSec(total.getSqlTimeNanoSec() + delta);
            // logger.trace(String.format("count = %d delta = %dns sum = %ds", sqlCount,
            // delta, sqlTime / 1000000000));
        }
            break;
        case Net: {
            total.setNetCount(total.getNetCount() + 1);
            final long end = System.nanoTime();
            final long delta = end - start;
            total.setNetTimeNanoSec(total.getNetTimeNanoSec() + delta);
            // logger.trace(String.format("count = %d delta = %dns sum = %ds", sqlCount,
            // delta, sqlTime / 1000000000));
        }
            break;
        }
    }
}
