package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class ConfigLoader {
    private final Properties properties = new Properties();
    private static ConfigLoader instance;
    String configFilePath = "/Users/navin/Repos/iion/AccuWeather/resources/config.properties";

    private ConfigLoader() throws IOException {
        properties.load(new FileInputStream(this.configFilePath));
    }

    public static ConfigLoader getInstance() throws IOException {
        if(instance==null){
            instance = new ConfigLoader();
        }
        return instance;
    }

    // Generic getter
    public String get(String key) {
        return properties.getProperty(key);
    }

    // Generic typed getters
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    // Generic setter
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
}

