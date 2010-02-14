package geotag;

import geotag.words.GeoRefDoc;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author Dario Bertini <berdario@gmail.com>
 */
public class Main {

    static GeoApplication mainApp;
    static String programName="GeoSearch";

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("g", "gui", false, "starts with the gui");
        options.addOption("h", "help", false, "print this message");
        options.addOption("c", "config", true, "loads the provided config file");
        options.addOption(OptionBuilder.withLongOpt("index").hasOptionalArg().withDescription("indexes the documents on the given path or reindex everything if without argument").create("i"));
        String usage = "\n" + programName + " [--config configfile] --index [path]\n" +
                programName + " [--config configfile] keyword place\n" +
                programName + " [--config configfile]";
        HelpFormatter usagehelp = new HelpFormatter();
        String cfgpath = null;
        try {
            CommandLine cmd = parser.parse(options, args);
            args = cmd.getArgs();

            if (cmd.hasOption("help")) {
                usagehelp.printHelp(usage, options);
            } else if (cmd.hasOption("gui")) {
                java.awt.EventQueue.invokeLater(new GeoApplicationGui());
            } else {
                if (cmd.hasOption("config")) {
                    cfgpath = cmd.getOptionValue("config");
                }
                mainApp = new GeoApplication(cfgpath);

                if (cmd.hasOption("index")) {
                    String inputpath = cmd.getOptionValue("index");
                    mainApp.updateIndexConfig(inputpath);

                    if (inputpath != null) {
                        String errortext = mainApp.createIndex(new File(
                                inputpath));
                        System.out.println(errortext);
                    } else {
                        String errortext = mainApp.createIndex();
                        System.out.println(errortext);
                    }
                } else if (args.length == 2) {
                    Vector<GeoRefDoc> results = mainApp.search(args[0], args[1]);
                    for (GeoRefDoc doc : results) {
                        System.out.println(doc);
                    }
                } else if (args.length == 0) {
                    commandLine();
                } else {
                    usagehelp.printHelp(usage, options);
                }
            }
        } catch (org.apache.commons.cli.ParseException e) {
            usagehelp.printHelp(usage, options);
        }
    }

    static void commandLine() {
        boolean flag = true;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String buffer;
        String[] pair;
        System.out.print(programName + ":\n" +
                "------------------------------------------------------------" +
                "--------------------\n" +
                "Interactive prompt: to quit type \"quit\" or press Ctrl-D\n");
        flag = true;
        do {
            try {
                System.out.print(">");
                buffer = in.readLine();

                if (buffer == null || buffer.equals("quit")) {
                    flag = false;
                } else if ((pair = buffer.split(",")).length == 2) {
                    for (GeoRefDoc doc : mainApp.search(pair[0], pair[1])) {
                        System.out.println(doc);
                    }
                } else if ((pair = buffer.split(" ")).length == 2) {
                    for (GeoRefDoc doc : mainApp.search(pair[0], pair[1])) {
                        System.out.println(doc);
                    }
                } else if (!buffer.equals("")) {
                    System.out.print("unknown command\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (flag);
    }

}