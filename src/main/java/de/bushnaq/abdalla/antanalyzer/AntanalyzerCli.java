package de.bushnaq.abdalla.antanalyzer;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;


public class AntanalyzerCli {
    public static final String APPLICATION = "antalyzer";
    protected static final String CLI_OPTION_ANT_FILE = "ant-file";
    protected static final String CLI_OPTION_ANT_TARGETS = "ant-targets";
    protected static final String CLI_OPTION_PRINT_TREE = "print-tree";
    protected static final String CLI_OPTION_PRINT_UNUSED_TARGETS = "print-unused-targets";
    protected static final String CLI_OPTION_PRINT_ANT_FILES = "print-ant-files";
    protected static final String CLI_OPTION_HELP = "help";
    private final Context context;
    //    private List<Throwable> exceptions = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    Options options;
    CommandLine line;

    public AntanalyzerCli(Context context) {
        this.context = context;
    }

    private void createParser(String[] args) throws ParseException {
        options = new Options();
        options.addOption("h", CLI_OPTION_HELP, false, "print this message");
        options.addOption("af", CLI_OPTION_ANT_FILE, true, "relative or absolute path to the main ant file. This parameter is not optional.");
        options.addOption("pt", CLI_OPTION_PRINT_TREE, false, "print target dependency tree. This parameter is optional. Default is disabled.");
        options.addOption("put", CLI_OPTION_PRINT_UNUSED_TARGETS, false, "print unused target. This parameter is optional. Default is disabled.");
        options.addOption("paf", CLI_OPTION_PRINT_ANT_FILES, false, "print ant file list. This parameter is optional. Default is disabled.");
        options.addOption("at", CLI_OPTION_ANT_TARGETS, true, "list of all targets used when executing ant as parameter. This list is used to mark all targets that are needed to execute these targets as used. If omitted, the default target of the main ant file is used.");

        // create the parser
        CommandLineParser parser = new DefaultParser();
        // parse the command line arguments
        line = parser.parse(options, args);
    }

    private boolean parse() throws IOException {
        if (line.hasOption(CLI_OPTION_HELP)) {
            printHelp();
            return true;
        }
        if (line.hasOption(CLI_OPTION_ANT_FILE)) {
            context.setAntFile(line.getOptionValue(CLI_OPTION_ANT_FILE));
        } else {
            printHelp();
            return true;
        }
        if (line.hasOption(CLI_OPTION_ANT_TARGETS)) {
            String[] mainAntTargets = line.getOptionValue(CLI_OPTION_ANT_TARGETS).split(",");
            context.setMainAntTargets(Arrays.asList(mainAntTargets));
        } else {
        }
        if (line.hasOption(CLI_OPTION_PRINT_TREE)) {
            context.setPrintTree(true);
            logger.info("print-tree enabled.");
        } else {
            logger.info("print-tree disabled.");
        }
        if (line.hasOption(CLI_OPTION_PRINT_UNUSED_TARGETS)) {
            context.setPrintUnusedTargets(true);
            logger.info("print unused targets enabled.");
        } else {
            logger.info("print unused targets disabled.");
        }
        if (line.hasOption(CLI_OPTION_PRINT_ANT_FILES)) {
            context.setPrintAntFiles(true);
            logger.info("print ant files enabled.");
        } else {
            logger.info("print ant files disabled.");
        }

        return false;
    }

    private void printHelp() throws IOException {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        formatter.printHelp(pw, 200, APPLICATION, "----------", options, 0, 0, "----------", true);
        pw.flush();
        pw.close();
        System.out.print(out.toString());
        out.close();
    }

    public boolean start(String[] args) throws Exception {
        try {
            createParser(args);
            if (parse()) return true;

        } catch (ParseException exp) {
            logger.error(String.format("Parsing failed.  Reason: %s", exp.getMessage()), exp);
        } finally {
            line = null;
            options = null;
        }
        return false;
    }


}
