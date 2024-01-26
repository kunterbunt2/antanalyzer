package de.bushnaq.abdalla.antanalyzer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeKeeping implements AutoCloseable {
    static final String BLACK_RIGHT_POINTING_TRIANGLE = ">";
    long time = System.currentTimeMillis();
    String parent = null;
    private Logger logger = null;
    private boolean logPerformanceCounters = false;
    private String subject;

    public TimeKeeping(String subject) {
        createLogger();
        this.subject = subject;
        logStart(subject);
    }

    public TimeKeeping(String subject, boolean logPerformanceCounters) {
        createLogger();
        this.subject = subject;
        this.logPerformanceCounters = logPerformanceCounters;
        if (logPerformanceCounters) {
            PerformanceCounters.push(logger);
        }
        logStart(subject);
    }

    public TimeKeeping() {
        createLogger();
    }

    @Override
    public void close() {
        long delta = getDelta();
        if (logPerformanceCounters) {
            PerformanceCounters.logReport(subject, logger);
            PerformanceCounters.pop(logger);
        } else {
            logger.trace(String.format("%s in %s.", subject, Util.create24hDurationString(delta, true, true, true, false)));
        }
    }

    private void createLogger() {
        String parent = new Exception().getStackTrace()[2].getClassName();
        logger = LoggerFactory.getLogger(parent);
    }

    public long getDelta() {
        return System.currentTimeMillis() - time;
    }

    private void logStart(String subject) {
        if (logPerformanceCounters) {
            logger.info("--------------------------------------------------");
            logger.info(String.format(BLACK_RIGHT_POINTING_TRIANGLE + "[%s]", subject));
            logger.info("--------------------------------------------------");
        }
    }

    public void setMessage(String subject) {
        this.subject = subject;
    }

}
