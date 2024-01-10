package com.bushnaq.abdalla.ant.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;


@ComponentScan(basePackages = {"com.ricoh.sdced"})
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static boolean lazyStart = true;//for junit tests
    //    private static String moduleVersion = MavenProperiesProvider.getProperty(Application.class, "module.version");
//    private static String buildNumber = MavenProperiesProvider.getProperty(Application.class, "build.number");
    private static String startupMessage;
    private static boolean started = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Application() {
    }

    /**
     * APPLICATION
     * Called 1st when started as APPLICATION
     * Not called when running junit test
     */
    public static void main(String[] args) {
//        startupMessage = String.format("starting %s %s.%s as application", Xlsx2mppParameterOptions.APPLICATION, moduleVersion, buildNumber);
        lazyStart = false;
        started = true;
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Application.class);
        ConfigurableApplicationContext context = springApplicationBuilder.headless(false).run(args);
        context.close();
    }

    /**
     * UNIT TEST
     * Called when running as application
     * Called when running UNIT TEST
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) throws Exception {
        if (!started) {
//            startupMessage = String.format("starting qcd.tool %s.%s within a unit test", moduleVersion, buildNumber);
        }
        logger.info("----------------------------------------------");
        logger.info("ant.analyzer: analyzes main ant file and any referenced ant files for unused targets.");
        logger.info(startupMessage);
        logger.info("----------------------------------------------");
    }

    @Override
    public void run(String... args) throws Exception {
        if (!lazyStart) {
            Antanalyzer antAnalyzer = new Antanalyzer();
            antAnalyzer.start(args);

//                main.start(args);
            logger.info("------------------------------------------------------------------------");
//                logger.info(String.format("executed %s %s.%s in %s", Xlsx2mppParameterOptions.APPLICATION, moduleVersion, buildNumber,XlsxUtil.createDurationString(timeKeeping.getDelta(), true, true, false)));
            logger.info("------------------------------------------------------------------------");
        }
    }

}
