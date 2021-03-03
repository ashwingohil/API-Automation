public class JiraFlowPayload {


    public static String getLoginBody(){
        return "{ \"username\": \"ashwin.k.gohil\", \"password\": \"nopassword\" }";
    }


    public static String getCreateIssueBody(String issueSummary, String issueDescription){

        return "{\n" +
                "    \"fields\": {\n" +
                "       \"project\":\n" +
                "       {\n" +
                "          \"key\": \"RES\"\n" +
                "       },\n" +
                "       \"summary\": \""+issueSummary+"\",\n" +
                "       \"description\": \""+issueDescription+"\",\n" +
                "       \"issuetype\": {\n" +
                "          \"name\": \"Bug\"\n" +
                "       }\n" +
                "   }\n" +
                "}";

    }


    public static String getAddCommentBody(String comment){
        return "{\n" +
                "  \"visibility\": {\n" +
                "    \"type\": \"role\",\n" +
                "    \"value\": \"Administrators\"\n" +
                "  },\n" +
                "  \"body\": \""+comment+"\"\n" +
                "}";
    }
}
