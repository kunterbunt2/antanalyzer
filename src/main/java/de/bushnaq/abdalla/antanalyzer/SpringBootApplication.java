package de.bushnaq.abdalla.antanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Locale;
import java.util.ResourceBundle;


@ComponentScan(basePackages = {"com.ricoh.sdced"})
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SpringBootApplication implements CommandLineRunner {
    private static final String moduleVersion = getProperty(SpringBootApplication.class, "project.version");
    private static final String buildTime = getProperty(SpringBootApplication.class, "build.time");
    private static boolean lazyStart = true;//for junit tests
    private static String startupMessage;
    private static boolean started = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SpringBootApplication() {
    }

    public static String getProperty(Class<?> clazz, String name) {
        ResourceBundle bundle = ResourceBundle.getBundle("maven", Locale.getDefault(), clazz.getClassLoader());
        return bundle.getString(name);
    }

    /**
     * APPLICATION
     * Called 1st when started as APPLICATION
     * Not called when running junit test
     */
    public static void main(String[] args) {
        startupMessage = String.format("starting %s %s-%s as application", AntanalyzerCli.APPLICATION, moduleVersion, buildTime);
        lazyStart = false;
        started = true;
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(SpringBootApplication.class);
        ConfigurableApplicationContext context = springApplicationBuilder.headless(false).run(args);
        context.close();
    }

    /**
     * UNIT TEST
     * Called when running as application
     * Called when running UNIT TEST
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!started) {
            startupMessage = String.format("starting %s %s-%s within a unit test", AntanalyzerCli.APPLICATION, moduleVersion, buildTime);
        }
        logger.info("------------------------------------------------------------------------");
        logger.info(String.format("%s: analyzes main ant file and any referenced ant files for unused targets.", AntanalyzerCli.APPLICATION));
        logger.info(startupMessage);
        logger.info("------------------------------------------------------------------------");
    }

    @Override
    public void run(String... args) throws Exception {
        if (!lazyStart) {
            try (de.bushnaq.abdalla.antanalyzer.util.TimeKeeping timeKeeping = new de.bushnaq.abdalla.antanalyzer.util.TimeKeeping(String.format("ended [%-25s].", AntanalyzerCli.APPLICATION))) {
                Antanalyzer antAnalyzer = new Antanalyzer();
                antAnalyzer.start(args);

                logger.info("------------------------------------------------------------------------");
                logger.info(String.format("executed %s %s-%s in %s", AntanalyzerCli.APPLICATION, moduleVersion, buildTime, de.bushnaq.abdalla.antanalyzer.util.Util.createDurationString(timeKeeping.getDelta(), true, true, false)));
                logger.info("------------------------------------------------------------------------");
            }
        }
    }

}
