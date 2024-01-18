package de.bushnaq.abdalla.antanalyzer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeKeeping implements AutoCloseable {
    private Logger logger = null;
    private boolean logPerformanceCounters = false;
    private String subject;
    long time = System.currentTimeMillis();
    String parent = null;
    static final String BLACK_RIGHT_POINTING_TRIANGLE = ">";

    public TimeKeeping(String subject) {
        createLogger();
        this.subject = subject;
        logStart(subject);
    }

    private void createLogger() {
        String parent = new Exception().getStackTrace()[2].getClassName();
        logger = LoggerFactory.getLogger(parent);
    }

    private void logStart(String subject) {
        if (logPerformanceCounters) {
            logger.info("--------------------------------------------------");
            logger.info(String.format(BLACK_RIGHT_POINTING_TRIANGLE + "[%s]", subject));
            logger.info("--------------------------------------------------");
        }
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

    public long getDelta() {
        return System.currentTimeMillis() - time;
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

    public void setMessage(String subject) {
        this.subject = subject;
    }

}
