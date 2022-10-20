package vm;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        try {
            for (File r : requests()) {
                System.out.println(String.format("Processing request: %s:", r.getName()));

                // create result directory if doesnt exist
                Files.createDirectories(Paths.get("./data/result"));

                // search for pdf templates and process them
                for (File f : vmPDF()) {
                    System.out.println(String.format("Generating pdf from template %s...", f.getName()));
                    String resultPDF = new TemplatePublisher().publish(r, f, "json");
                    System.out.println(String.format("Result file: %s", resultPDF));
                    // 2D: check for valid json file as result
                    // also check there's no final comma in array cuz then pdf generator breaks
                }

                // search for xml templates and process them
                for (File f : vmXML()) {
                    System.out.println(String.format("Generating xml from template %s...", f.getName()));
                    String resultXML = new TemplatePublisher().publish(r, f, "xml");
                    System.out.println(String.format("Result file: %s", resultXML));
                    // 2D: need to check if result file is valid xml
                    // also check if xml validates schema.xsd
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
