package vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TemplatePublisher {
    protected VelocityEngine velocity;

    public TemplatePublisher() {
        // init velocity
        velocity = new VelocityEngine();
        velocity.init();
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

        return result;
    }
}
