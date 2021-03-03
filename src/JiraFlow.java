import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

//DO THIS FIRST. JIRA NEEDS TO BE RESTARTED
//sudo /etc/init.d/jira start

public class JiraFlow {

    //Create a project in Jira and note down its key
    String baseURI = "http://localhost:8080";
    SessionFilter session = new SessionFilter();
    String sessionValue = null;
    String issueID = null;
    String commentID = null;
    String myComment = null;

    @Test(priority = 1)
    public void Login(){

        RestAssured.baseURI = baseURI;
        String resourceLogin = "/rest/auth/1/session"; //POST
        String headerKey = "Content-Type";
        String headerKeyValue = "application/json";

        //.cookie("cookie","value") can also be used
        String loginResponse = RestAssured.given().log().all()
                .header(headerKey,headerKeyValue)
                .body(JiraFlowPayload.getLoginBody())
                .filter(session)
                .when().post(resourceLogin)
                .then().log().all()
                .statusCode(200)
                .extract().response().asString();

        JsonPath js = new JsonPath(loginResponse);
        sessionValue = js.getString("session.value");
    }


    @Test(priority = 2)
    public void createIssue(){

        RestAssured.baseURI = baseURI;
        String issueSummary = null;
        String issueDescription = null;
        String resourceCreateIssue = "/rest/api/2/issue"; //POST
        String headerKey = "Content-Type";
        String headerKeyValue = "application/json";

        issueSummary = "Defect raised";
        issueDescription = "This is description of the issue";

        String createIssueResponse = RestAssured.given().log().all()
                .header(headerKey,headerKeyValue)
                .filter(session)
                .body(JiraFlowPayload.getCreateIssueBody(issueSummary,issueDescription))
                .when().post(resourceCreateIssue)
                .then().log().all()
                .extract().response().asString();

        JsonPath js = new JsonPath(createIssueResponse);
        issueID = js.getString("id");
        System.out.println("Issue id is: "+issueID);
    }

    @Test(priority = 3)
    public void addComment(){
        RestAssured.baseURI = baseURI;
        String resourceAddComment = "/rest/api/2/issue/{issueIdOrKey}/comment"; //POST
        String headerKey = "Content-Type";
        String headerKeyValue = "application/json";

        myComment = "This is my comment on the issue. Check it out";

        //note the pathParam when you see the above resource
        //read the documentation. everything is there. check for response and status codes

        String addCommentResponse = RestAssured.given().log().all()
                .header(headerKey,headerKeyValue).pathParam("issueIdOrKey",issueID)
                .body(JiraFlowPayload.getAddCommentBody(myComment))
                .filter(session)
                .when().post(resourceAddComment)
                .then().log().all()
                .assertThat().statusCode(201).extract().response().asString();

        JsonPath js = new JsonPath(addCommentResponse);
        commentID = js.getString("id");
        System.out.println("Comment id is: "+commentID);
    }

    @Test(priority = 4)
    public void addAttachmentToIssue(){
        //This is important. Refer notes and api. multipart concept
        RestAssured.baseURI = baseURI;
        String resourceAddAttacment = "/rest/api/2/issue/{issueIdOrKey}/attachments"; //POST
        String headerKey = "Content-Type";
        String headerKeyValue = "multipart/form-data";
        String headerKey1 = "X-Atlassian-Token";
        String headerKeyValue1 = "no-check";

        //multipart(String s, File file) method accepts File object
        String addAttachmentResponse = RestAssured.given()
                .header(headerKey,headerKeyValue)
                .header(headerKey1,headerKeyValue1)
                .filter(session)
                .pathParam("issueIdOrKey",issueID)
                .multiPart("file",new File("SampleAttachment"))
                .when().post(resourceAddAttacment)
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response().asString();
    }

    @Test(priority = 5)
    public void GetIssueDetails(){

        RestAssured.baseURI = baseURI;
        String resourceGetIssue = "/rest/api/2/issue/{issueIdOrKey}";  //GET
        //important the queryParam() . From the api we filter the response with fields which is query parameter
        //first don't use queryParam() and log all then copy that huge json of response in jsoneditor
        String getIssueResponse = RestAssured.given()
                .log().all()
                .filter(session)
                .pathParam("issueIdOrKey",issueID)
                .queryParam("fields","comment")
                .when().get(resourceGetIssue)
                .then().log().all()
                .extract().response().asString();
        System.out.println("Issue Details Response: "+getIssueResponse);
        //At this point, i have narrowed down by query parameter. copy the response in jsoneditor and look at the
        //traversal. you will understand below.

        JsonPath js = new JsonPath(getIssueResponse);
        int commentCounts = js.getInt("fields.comment.comments.size()");

        for(int i=0; i<commentCounts; i++){
            String commentsId = js.get("fields.comment.comments["+i+"].id").toString();
            if(commentsId.equalsIgnoreCase(commentID)){
                String message = js.get("fields.comment.comments["+i+"].body").toString();
                System.out.println("The comment is: "+message);
                Assert.assertEquals(message,myComment);
            }
        }
    }

    /*
    In real world it is different authentication ways. Here we are using localhost:8080 so session is easy with
    wither Sessionfilter() object or cookie (which also works). In real world there may be certificates involved.
    It can be tricky. So just include a method .relaxedHTTPSValidation() after given() or in the given part
     */


    @Test(priority = 6)
    public void deleteIssue(){

        System.out.println("Waiting for 90000 before deleting the issue. Check jira for changes automated");
        try{
            Thread.sleep(90000);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }

        RestAssured.baseURI = baseURI;
        String resourceDeleteIssue = "/rest/api/2/issue/{issueIdOrKey}";  //DELETE
        String queryParameterKey = "deleteSubtasks";
        String queryParameterValue = "true";

        String deleteIssueResponse = RestAssured.given().log().all()
                .filter(session)
                .pathParam("issueIdOrKey",issueID)
                .queryParam(queryParameterKey,queryParameterValue)
                .when().delete(resourceDeleteIssue)
                .then().log().all()
                .assertThat().statusCode(204)
                .extract().response().asString();

        System.out.println("Delete response is: "+deleteIssueResponse);
    }

}
