package htmlextraction;


/**
 * This class starts the whole process of extracting the html-code and changing it.
 */
public class Main {

    /**
     * This method extracts the html-code from a file and writes the updated into another file.
     * @param args Parameter, which is transmtted when starting the program.
     */
    public static void main(String[] args) {

        HtmlIO io = new HtmlIO();
        String name = "unileipzigwiki.html";

        String content = io.extractHtmlFromFile(name);
        HtmlParser parser = new HtmlParser(content);

        String s = parser.getTextFromHtml();

        io.safeTextToFile(name.replace(".html",".txt"), s);

    }
}
