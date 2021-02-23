import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Test {
    public static void main(String[] args) {
        Properties prop = new Properties();
        String fileName = "system.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
        }
        System.out.println(prop.getProperty("datadir"));
    }
}
