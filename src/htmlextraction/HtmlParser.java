package htmlextraction;

import java.util.ArrayList;
import java.util.List;

public class HtmlParser {
    private String htmlContent;
    private List<String> groupedContent;

    HtmlParser(String content) {
        this.htmlContent = content;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getTextFromHtml() {

        groupContent();

        return "";
    }

    private void groupContent() {

        this.groupedContent = new ArrayList<>();
        StringBuilder currentGroup = new StringBuilder();


        // TODO: Respect comments as well
        for(int i = 0; i < htmlContent.length(); i++) {
            char c = htmlContent.charAt(i);

            if (c == '<') {
                if(!currentGroup.isEmpty()){
                    groupedContent.add(currentGroup.toString());
                    currentGroup = new StringBuilder();
                }

                currentGroup.append(c);

            } else if (c == '>') {
                currentGroup.append(c);
                groupedContent.add(currentGroup.toString());

                currentGroup = new StringBuilder();
            } else {
                currentGroup.append(c);
            }
        }

        groupedContent.forEach(System.out::println);
    }



    private boolean isGroupSpecial(String group){

        return false;
    }

    private void filterGroupedContent() {

    }

    private void convertContentTags() {

    }
}
