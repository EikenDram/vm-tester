package vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.io.*;
import java.nio.charset.*;
import org.apache.commons.io.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
     * Retrieve a value from a property using
     * 
     * @param obj          The object who's property you want to fetch
     * @param property     The property name
     * @param defaultValue A default value to be returned if either a) The property
     *                     is
     *                     not found or b) if the property is found but the value is
     *                     null
     * @return THe value of the property
     */
    public static <T> T getProperty(Object obj, String property, T defaultValue) {

        T returnValue = (T) getProperty(obj, property);
        if (returnValue == null) {
            returnValue = defaultValue;
        }

        return returnValue;

    }

    /**
     * Fetch a property from an object. For example of you wanted to get the foo
     * property on a bar object you would normally call {@code bar.getFoo()}. This
     * method lets you call it like {@code BeanUtil.getProperty(bar, "foo")}
     * 
     * @param obj      The object who's property you want to fetch
     * @param property The property name
     * @return The value of the property or null if it does not exist.
     */
    public static Object getProperty(Object obj, String property) {
        Object returnValue = null;

        try {
            String methodName = "get" + property.substring(0, 1).toUpperCase()
                    + property.substring(1, property.length());
            Class clazz = obj.getClass();
            Method method = clazz.getMethod(methodName, null);
            returnValue = method.invoke(obj, null);
        } catch (Exception e) {
            // Do nothing, we'll return the default value
        }

        return returnValue;
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

        InputStream is = new FileInputStream(jsonFile);
        JSONTokener tokener = new JSONTokener(is);
        return new JSONObject(tokener);
    }

    public JSONObject getJson2(File jsonFile) throws IOException {
        if (!jsonFile.exists()) {
            throw new NullPointerException("Cannot find resource file " + jsonFile.getName());
        }

        // InputStream is = new FileInputStream(jsonFile);

        // JSONTokener tokener = new JSONTokener(is);
        // String content = new Scanner(jsonFile).useDelimiter("\\Z").next();

        // String content = toString(jsonFile);
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

        // global variables
        JSONObject jsonGlobal = getJson(new File("./data/global.json"));
        for (String key : jsonGlobal.keySet()) {
            context.put(key, jsonGlobal.get(key));
        }

        // request json from getNextStep -> scenarioDto.applicantAnswers
        try {
            JSONObject jsonRequest = getJson2(requestFile);
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
