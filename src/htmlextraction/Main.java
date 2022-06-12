package htmlextraction;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        HtmlIO io = new HtmlIO();
        String name = "unileipzigwiki.html";

        String content = io.extractHtmlFromFile(name);
        HtmlParser parser = new HtmlParser(content);

        String s = parser.getTextFromHtml();


    }
}
