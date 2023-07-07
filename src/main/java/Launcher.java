import java.io.File;
import java.util.Properties;

public class Launcher {
    private static Properties properties;
    private static File propertiesFile;

    public static void main(String[] args){
        Initializer initializer = new Initializer();
        setPropertiesFile(new File("launcher.properties")); // Set propertiesFile before invoking createConfig()

        initializer.createConfig();
        initializer.initialize();
    }

    public static void setProperties(Properties properties) {
        Launcher.properties = properties;
    }

    public static void setPropertiesFile(File propertiesFile) {
        Launcher.propertiesFile = propertiesFile;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static File getPropertiesFile() {
        return propertiesFile;
    }
}
