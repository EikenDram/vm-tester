package vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Main app
 * full disclosure: this is my first ever java project
 */
public class App {
    /**
     * List of files with pdf_*_Applicant.vm template in data/vm/ folder
     * 
     * @return List of files
     */
    public static File[] vmPDF() {
        File folder = new File("./data/vm/");
        File[] matchingFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("pdf_") && name.endsWith("_Applicant.vm");
            }
        });
        return matchingFiles;
    }

    /**
     * List of files with *_Applicant.vm template in data/vm/ folder
     * 
     * @return List of files
     */
    public static File[] vmXML() {
        File folder = new File("./data/vm/");
        File[] matchingFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !(name.startsWith("pdf_")) && name.endsWith("_Applicant.vm");
            }
        });
        return matchingFiles;
    }

    /**
     * List of files with *_Applicant.vm template in data/vm/ folder
     * 
     * @return List of files
     */
    public static File[] tXML() {
        File folder = new File("./data/vm/");
        File[] matchingFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("t_") && name.endsWith(".vm");
            }
        });
        return matchingFiles;
    }

    /**
     * List of .json requests in data/request/ folder
     * 
     * @return
     */
    public static File[] requests() {
        File folder = new File("./data/request/");
        File[] matchingFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        return matchingFiles;
    }

    public static void main(String[] args) {
        // ask if user wants to add link to schema.xsd in ./data folder so vscode could
        // automatically validate xml schema

        try {
            for (File r : requests()) {
                System.out.println(String.format("Processing request: %s:", r.getName()));

                // read application properties
                Properties appProps = new Properties();
                appProps.load(new FileInputStream("./app.properties"));

                // create result directory if doesnt exist
                Files.createDirectories(Paths.get("./data/result"));

                // search for pdf templates and process them
                for (File f : vmPDF()) {
                    System.out.println(String.format("Generating pdf from template %s...", f.getName()));
                    String resultPDF = new TemplatePublisher(appProps).publish(r, f, "json", null);
                    System.out.println(String.format("Result file: %s", resultPDF));
                    // 2D: check for valid json file as result
                    // or can check with vscode by opening file
                }

                // search for xml templates and process them
                for (File f : vmXML()) {
                    System.out.println(String.format("Generating xml from template %s...", f.getName()));
                    String resultXML = new TemplatePublisher(appProps).publish(r, f, "xml", "schema");
                    System.out.println(String.format("Result file: %s", resultXML));
                    // 2D: check if result file is valid xml
                    // 2D: check if xml validates schema.xsd
                }

                // search for transport templates and process them
                for (File f : tXML()) {
                    System.out.println(String.format("Generating transport xml from template %s...", f.getName()));
                    String resultT = new TemplatePublisher(appProps).publish(r, f, "xml", "t");
                    System.out.println(String.format("Result file: %s", resultT));
                    // 2D: check if result file is valid xml
                    // 2D: check if xml validates schema.xsd
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
