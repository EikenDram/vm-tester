package vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.json.JSONObject;

public class TemplatePublisher {
    protected VelocityEngine velocity;
    protected Properties props;

    public TemplatePublisher(Properties appProps) {
        // init velocity
        velocity = new VelocityEngine();
        velocity.init();
        // config
        props = appProps;
    }

    /**
     * Loads JSON object from json file
     * 
     * @param jsonFile json file
     * @return JSON object
     */
    public JSONObject getJson(File jsonFile) throws IOException {
        if (!jsonFile.exists()) {
            throw new NullPointerException("Cannot find resource file " + jsonFile.getName());
        }

        String content = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

    /**
     * Result file name for processing request
     * 
     * @param r            request file name
     * @param f            original file name
     * @param newExtension new extension
     * @return file name with new extension
     */
    public String getResult(String r, String f, String newExtension) {
        int i = f.lastIndexOf('.');
        int j = r.lastIndexOf('.');
        String f_name = f.substring(0, i);
        String r_name = r.substring(0, j);
        return r_name + "_" + f_name + "." + newExtension;
    }

    /**
     * Adds a link to XSD schema in ./data folder to generated XLS file
     * 
     * @param fname name of xls file
     * @throws IOException
     */
    public void AddSchemaLinkToXML(String fname) throws IOException {
        String filePath = "./data/result/" + fname;
        // java 1.8 version
        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8));
        String line;
        String input = "";

        int k = 0;
        while ((line = file.readLine()) != null) {
            input += line + System.lineSeparator();
            if (k == 0)
                input += "<?xml-model href=\"../schema.xsd\"?>" + System.lineSeparator();
            k = k + 1;
        }

        FileOutputStream os = new FileOutputStream(new File(filePath));
        os.write(input.getBytes(StandardCharsets.UTF_8));

        file.close();
        os.close();
    }

    /**
     * Publishes velocity template to result file
     * 
     * @param templateFile file to .vm template
     * @param resultExt    extension of result file
     * @return name of result file
     * @throws IOException
     */
    public String publish(File requestFile, File templateFile, String resultExt) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("dateTool", new DateTool());

        // global variables
        JSONObject jsonGlobal = getJson(new File("./data/global.json"));
        for (String key : jsonGlobal.keySet()) {
            context.put(key, jsonGlobal.get(key));
        }

        // request json from getNextStep -> scenarioDto.applicantAnswers
        try {
            JSONObject jsonRequest = getJson(requestFile);
            for (String key : jsonRequest.keySet()) {
                JSONObject root = jsonRequest.getJSONObject(key);
                if (root.has("value")) {
                    Object val = root.get("value");
                    context.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // export
        String template = "./data/vm/" + templateFile.getName();
        String result = getResult(requestFile.getName(), templateFile.getName(), resultExt);
        try {
            PrintWriter writer = new PrintWriter("./data/result/" + result, "UTF-8");
            velocity.mergeTemplate(template, "UTF-8", context, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if xml and config, add line to output
        if (resultExt == "xml" &&
                props.getProperty("AddSchemaLinkToXML").equals("true")) {
            AddSchemaLinkToXML(result);
        }

        return result;
    }
}
