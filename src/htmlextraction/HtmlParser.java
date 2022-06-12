package htmlextraction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class can parse, filter and return text from html content.
 */
public class HtmlParser {
    /**
     * Stores the original html content as a string.
     */
    private String htmlContent;
    /**
     * Stores html content as groups, separated by tags or the content between tags.
     */
    private List<String> groupedContent;
    /**
     * Stores the filtered html content.
     */
    private String updatedHtmlContent;

    /**
     * Constructor of the HtmlParser class.
     * Sets the content that can be filtered when using the {@link HtmlParser#getTextFromHtml()} function.
     * @param content HTML content that will be filtered.
     */
    HtmlParser(String content) {
        this.htmlContent = content;
    }

    /**
     * Sets the html content which can be filtered using the {@link HtmlParser#getTextFromHtml()} function.
     * @param htmlContent HTML text
     */
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    /**
     * Returns a filtered Text as String given an unfiltered html-style string.
     * @return Extracted text as String.
     */
    public String getTextFromHtml() {

        groupContent();
        filterGroupedContent();
        convertContentTags();

        return updatedHtmlContent;
    }

    /**
     * Checks if a given string is at the place of the given position in the {@link HtmlParser#htmlContent} string.
     * @param position Position to start the comparison from.
     * @param tag The tag to compare.
     * @param source The source string, in which the comparison takes place.
     * @return Result of whether the tag is ahead or not.
     */
    private boolean isTagAhead(int position, String tag, String source) {
        return source.regionMatches(position, tag, 0, tag.length());
    }

    /**
     * Returns the first index of a given character in the htmlContent String from a specific index.
     * @param c The character which will be searched for.
     * @param from The index from which to search from.
     * @return The next index at which the character has been found.
     */
    private int getFirstIndex(char c, int from) {
        int idx = from;

        for(int i = from + 1; i < htmlContent.length(); i++) {
            if(htmlContent.charAt(i) == c) {
                idx = i;
                break;
            }
        }

        return idx;
    }

    /**
     * Groups the tags and the content in between tags from {@link HtmlParser#htmlContent} in a list.
     * The groups will be stored in {@link HtmlParser#groupedContent} and will be later used for easy filtering.
     */
    private void groupContent() {
        this.groupedContent = new ArrayList<>();
        StringBuilder currentGroup = new StringBuilder();

        // Useful to determine if the current char is inside a comment. Will be true if <!-- structure is detected
        boolean inComment = false;

        // We will iterate through each character of the html content
        // i is the cursor, at which we will read the current character c
        for(int i = 0; i < htmlContent.length(); i++) {
            char c = htmlContent.charAt(i);
            boolean betweenScriptTags = false;
            boolean inTag = false;
            boolean betweenStyleTags = false;

            if(!groupedContent.isEmpty()) {
                // Check if last group was script tag opening and remember
                if(groupedContent.get(groupedContent.size() - 1).contains("<script")) {
                    betweenScriptTags = true;
                }

                // Check if last group was style tag opening and remember
                if(groupedContent.get(groupedContent.size() - 1).contains("<style")) {
                    betweenStyleTags = true;
                }

            }

            // Check if current group is a tag and remember
            if(!currentGroup.isEmpty()) {
                if(currentGroup.charAt(0) == '<') {
                    inTag = true;
                }
            }


            // Now we will try to detect special things inside the content


            // Detect a style ending tag
            if(betweenStyleTags && !inComment && isTagAhead(i, "</style>", htmlContent)){
                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

                currentGroup.append("</style>");
                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

                i = i + 7;

            } else if(betweenScriptTags && !inComment && isTagAhead(i, "</script>", htmlContent)) {
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


            } else if (c == '<' && !betweenScriptTags && !betweenStyleTags) {
                // Do not add empty groups to list or incomplete comments.
                if(!currentGroup.isEmpty() && !inComment){
                    groupedContent.add(currentGroup.toString());
                    currentGroup = new StringBuilder();
                }

                // Check if there will be a comment
                if (!inComment && isTagAhead(i, "<!--", htmlContent)) {
                    // We will be in a comment, add whole comment tag
                    currentGroup.append("<!--");

                    i = i + 3;
                    inComment = true;

                } else {
                    // No comment was detected, so we will gather the rest of the opening tag
                    currentGroup.append(c);
                }

            } else if (!betweenScriptTags && inComment && isTagAhead(i, "-->", htmlContent)) {
                // Detected closing tag for comment
                currentGroup.append("-->");
                i = i + 2;

                inComment = false;

                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

            } else if (!betweenScriptTags && !betweenStyleTags && !inComment && c == '>') {
                // Detected tag ending
                currentGroup.append(c);

                groupedContent.add(currentGroup.toString());
                currentGroup = new StringBuilder();

            } else if (inTag && !inComment && c == '=' && (htmlContent.charAt(i+1) == '"' || htmlContent.charAt(i+1) == '\'')) {
                // Found a property in the form of ="value" or ='value'.

                // Find next ' or "
                int idx;

                // We append the first =" or ='
                currentGroup.append("=");
                currentGroup.append(htmlContent.charAt(i+1));

                // We get the next occurrence of " or '
                if(htmlContent.charAt(i+1) == '"') {
                    idx = getFirstIndex('"', i + 1);
                } else {
                    idx = getFirstIndex('\'', i + 1);
                }

                // We append the next " or '
                currentGroup.append(htmlContent.charAt(idx));

                // We skip the stuff in between and move the cursor to the index of the last " or '
                i = idx;

            } else {
                // Nothing special was detected, we will just add the character to the current group
                currentGroup.append(c);
            }
        }

        //groupedContent.forEach(System.out::println);
    }


    /**
     * Checks if a group (String) is special.
     * @param group Group to analyse
     * @return Result of whether the group is special or not
     */
    private boolean isGroupSpecial(String group){

        if(!(group.contains("''") || group.contains("\"\""))){
            if(group.isEmpty() || group.isBlank()) {
                return true;
            }
            if(group.charAt(0) == '<' && group.charAt(group.length() - 1) == '>') {
                return true;
            }
        }

        return false;
    }

    /**
     * This method filters the special tags and joins the updated list to a string.
     */
    private void filterGroupedContent(){
        // filter group
        groupedContent = groupedContent.stream().filter(entry -> !isGroupSpecial(entry)).collect(Collectors.toList());

        // write filtered group in final String
        StringBuilder stringBuilder = new StringBuilder();
        for (String content : groupedContent) {
            stringBuilder.append(content);
        }
        updatedHtmlContent = stringBuilder.toString();
    }

    /**
     * This method converts the html-specifiers for certain letters oder special signs to the ascii-value.
     */
    private void convertContentTags(){

        // create List with signs and List with asciiValues
        List<String> specialSigns = Arrays.asList("&auml;","&Auml;","&ouml;","&Ouml;","&uuml;","&Uuml;",
                "&szlig;","&lt;","&gt;","&amp;","&quot;","&nbsp;");
        List<Integer> values = Arrays.asList(228,196,246,214,252,220,223,60,62,38,34,32);

        // create Map with signs and asciiValues
        Map<String, Integer> specialSignsAndValue = new HashMap<>();
        for (int i = 0; i < specialSigns.size(); i++) {
            specialSignsAndValue.put(specialSigns.get(i), values.get(i));
        }

        // change all occurrences of signs in String with asciiValues
        for (var entry : specialSignsAndValue.entrySet()) {
            char asciiValue = (char) entry.getValue().intValue();
            updatedHtmlContent = updatedHtmlContent.replace(entry.getKey(), String.valueOf(asciiValue));
        }
    }

}
