
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;



public class ScratchJira {


    @Test
    public void addBook(){
        RestAssured.baseURI = "http://216.10.245.166";
        String response = RestAssured.given().log().all()
                .header("Content-Type","application/json")
                .body("{\n" +
                        " \n" +
                        "\"name\":\"werewdr dsfotics\",\n" +
                        "\"isbn\":\"dsdsdd\",\n" +
                        "\"aisle\":\"22557dfd\",\n" +
                        "\"author\":\"Jbbgdn sdf\"\n" +
                        "}\n")
                .when()
                .post("/Library/Addbook.php")
                .then()
                .assertThat()
                .statusCode(200)
                .body("Msg",equalTo("successfully added"))
                .extract().response().asString();

        System.out.println(response);
        JsonPath js = new JsonPath(response);
        String id = js.get("ID");
        System.out.println(id);
    }



    @Test
    public void LoginJira(){

        String resourceUrl = "/rest/auth/1/session";
        RestAssured.baseURI = "http://localhost:8080";

        SessionFilter session = new SessionFilter();

        String response = RestAssured.given().log().all()
                .header("Content-Type","application/json")
                .body("{ \"username\": \"ashwin.k.gohil\", \"password\": \"nopassword\" }")
                .filter(session)
                .when().post(resourceUrl)
                .then()
                .log().all().extract().response().asString();

        System.out.println("Response is: "+response);
        JsonPath js = new JsonPath(response);
        String value = js.getString("session.value");
        System.out.println("Value is : "+value);


        String createissueResource = "/rest/api/2/issue";

        //u can use .cooke("JSESSIONID",value) after .header and can maintain session
        String response2 = RestAssured.given().log().all()
                .header("Content-Type","application/json")
                .body("{\n" +
                        "    \"fields\": {\n" +
                        "       \"project\":\n" +
                        "       {\n" +
                        "          \"key\": \"RES\"\n" +
                        "       },\n" +
                        "       \"summary\": \"issue charlie.\",\n" +
                        "       \"description\": \"None moresue\",\n" +
                        "       \"issuetype\": {\n" +
                        "          \"name\": \"Bug\"\n" +
                        "       }\n" +
                        "   }\n" +
                        "}")
                .filter(session)
                .when().post(createissueResource)
                .then().log().all().extract().response().asString();

        System.out.println("Response issue:"+response2);



    }



}

