import java.nio.file.*;
import java.io.*;
import java.util.Scanner;


public class Configuration {

    public static void main(String[] args) {
        File myObj = new File("../config/Configuration.txt");
        if (myObj.exists()) {
            System.out.println("File name: " + myObj.getName());
            System.out.println("Absolute path: " + myObj.getAbsolutePath());
            System.out.println("Writeable: " + myObj.canWrite());
            System.out.println("Readable: " + myObj.canRead());
            System.out.println("Writeable: " + myObj.canWrite());
            try {
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine())
                {
                    String data = myReader.nextLine();
                    System.out.println(data);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occured.");
                e.printStackTrace();
            }
        } else {
            System.out.println("The file does not exist.");
        }
    }
}
