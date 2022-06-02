package htmlextraction;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        HtmlIO io = new HtmlIO();

        String content = io.extractHtmlFromFile("golem.html");

        //System.out.println(content);

        HtmlParser parser = new HtmlParser(content);

        String s = parser.getTextFromHtml();


    }
}
