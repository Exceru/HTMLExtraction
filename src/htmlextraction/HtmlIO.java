package htmlextraction;

import java.io.*;

/**
 * This class extracts html-Sourcecode from a given file and also writes content to a file.
 */
public class HtmlIO {

    /**
     * The Method extracts html-Code and returns it as a String.
     * @param path It specifies the path to the file where the extraction takes place.
     * @return Sourcecode as String.
     */
    public String extractHtmlFromFile(String path) {

        // create StringBuilder to build a String later on
        StringBuilder result = new StringBuilder();
        String line;

        // extract the text out of a file
        File file = new File(path);
        try (FileReader fr = new FileReader(file);
             BufferedReader bR = new BufferedReader(fr)) {

            // appends line by line to the StringBuilder
            while ((line = bR.readLine()) != null){
                result.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    /**
     * Prints a given String to the console and also writes it in a file.
     * @param destinationPath It specifies the path where the content will be written in a file.
     * @param content String, which stands for the text that has to be written.
     */
    public void safeTextToFile(String destinationPath, String content){

        // print text to console
        System.out.println(content);

        // write text in file
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(destinationPath))) {
            writer.write(content);
            writer.flush();
        }
        catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
