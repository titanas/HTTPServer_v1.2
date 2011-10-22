package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static Properties properties;
    private static PropertiesReader ref;
    
    public static PropertiesReader getPropertiesReader() {
        if (properties == null) {
            properties = new Properties();
            loadProperties("src/httpserver/config.properties");
        }
        if (ref == null)
            ref = new PropertiesReader();
        return ref;
    }

    private static void loadProperties (String uri) {        
        try {
            File f = new File(uri);           
            InputStream in = new FileInputStream(f);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void showPropertyNames() {
        System.out.println(properties.stringPropertyNames().toString());  
    }
}
