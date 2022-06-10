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


    private String getTagAhead(int p){
        String tag = "";

        if(htmlContent.regionMatches(p, "<!--", 0, "<!--".length())) {
            return "<!--";
        }
        else if (htmlContent.regionMatches(p, "<script>", 0, "<script>".length())) {
            return "<script>";
        }
        else if (htmlContent.regionMatches(p, "</script>", 0, "</script>".length())) {
            return "</script>";
        }

        return tag;
    }

    private int getFirstIndex(char c, int from) {
        int idx = -1;

        for(int i = from + 1; i < htmlContent.length(); i++) {
            if(htmlContent.charAt(i) == '"' || htmlContent.charAt(i) == '\'') {
                idx = i;
                break;
            }
        }

        return idx;
    }

    private void groupContent() {

        this.groupedContent = new ArrayList<>();
        StringBuilder currentGroup = new StringBuilder();

        boolean inComment = false;

        // TODO: Respect script tags as well
        for(int i = 0; i < htmlContent.length(); i++) {
            char c = htmlContent.charAt(i);
            boolean inScriptTag = false;
            boolean inTag = false;


            // Check if last group was script tag opening
            if(!groupedContent.isEmpty()) {
                if(groupedContent.get(groupedContent.size() - 1).contains("<script")) {
                    inScriptTag = true;
                }
            }

            // Check if current group is a tag
            if(!currentGroup.isEmpty()) {
                if(currentGroup.charAt(0) == '<') {
                    inTag = true;
                }
            }




            if (!inComment && getTagAhead(i).equals("<script>")) {
                currentGroup.append("<script>");
                i = i + 7;

                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

            } else if(inScriptTag && !inComment && getTagAhead(i).equals("</script>")) {
                // Add everything before </script> tag as a group
                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

                // Add the tag itself as a group
                currentGroup.append("</script>");
                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

                // Change the cursor so we will be at: </script><newTag>...</newtag>
                //                                                     |^|
                i = i + 8;


            } else if (c == '<' && !inScriptTag) {
                // Do not add empty groups to list or incomplete comments.
                if(!currentGroup.isEmpty() && !inComment){
                    groupedContent.add(currentGroup.toString());
                    currentGroup = new StringBuilder();
                }

                // Check if there will be comment
                if (!inComment && htmlContent.charAt(i+1) == '!' && htmlContent.charAt(i+2) == '-' && htmlContent.charAt(i+3) == '-') {
                    // We will be in a comment, add whole comment tag
                    currentGroup.append("<!--");

                    i = i + 3;
                    inComment = true;
                } else {
                    currentGroup.append(c);
                }

            } else if (!inScriptTag && inComment && c == '-' && htmlContent.charAt(i+1) == '-' && htmlContent.charAt(i+2) == '>') {
                // Detected closing tag for comment
                currentGroup.append("-->");
                i = i + 2;

                inComment = false;

                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

            } else if (!inScriptTag && !inComment && c == '>') {

                currentGroup.append(c);

                // Only add group if we are not in comment, as '>' is insufficient for closing a tag ('-->' required!)
                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

            } else if (inTag && !inComment && c == '=' && (htmlContent.charAt(i+1) == '"' || htmlContent.charAt(i+1) == '\'')) {
                // Find next ' or "
                int idx = i;

                currentGroup.append("=");
                currentGroup.append(htmlContent.charAt(i+1));

                if(htmlContent.charAt(i+1) == '"') {
                    //idx = htmlContent.indexOf('"', htmlContent.charAt(i+2));
                    idx = getFirstIndex('1', i + 1);
                } else {
                    idx = getFirstIndex('1', i + 1);
                    //idx = htmlContent.indexOf('\'', htmlContent.charAt(i+2));
                }

                currentGroup.append(htmlContent.charAt(idx));

                i = idx;

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
