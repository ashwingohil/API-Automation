import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {

    public static void main(String[] args){
        /*
        validate if AddPlace API is working as expected

        given - all input details
        when - submit api - resourse, http method
        then - validate the response
         */
        //Add place --> Update place with New address --> Get Place to validate if new address is present in response

        RestAssured.baseURI = "https://rahulshettyacademy.com";

        String response =  RestAssured.given().log().all().queryParam("key","qaclick123")
                .header("Content-Type","application/json")
                .body(BasicsPayload.AddPlace())
                .when().post("maps/api/place/add/json")
                .then()
                .assertThat().statusCode(200).body("scope",equalTo("APP"))
                .header("server","Apache/2.4.18 (Ubuntu)")
                .extract().response().asString();

        System.out.println(response);

        JsonPath js = new JsonPath(response); //for parsing json
        String placeId = js.getString("place_id");
        System.out.println(placeId);


        //Update Place
        String newAddress = "Summer Walk, Africa";
        given().log().all().queryParam("key","qaclick123")
                .header("Content-Type","application/json")
                .body("{\n" +
                        "\"place_id\":\""+placeId+"\",\n" +
                        "\"address\":\""+newAddress+"\",\n" +
                        "\"key\":\"qaclick123\"\n" +
                        "}\n")
                .when().put("maps/api/place/update/json")
                .then().assertThat().log().all().statusCode(200)
                .body("msg",equalTo("Address successfully updated"));

        //Get Place

        String getPlaceResponse = RestAssured.given().log().all().queryParam("key","qaclick123")
                .queryParam("place_id",placeId)
                .when().get("maps/api/place/get/json")
                .then().log().all().statusCode(200).extract().response().asString();

        JsonPath jsobject = new JsonPath(getPlaceResponse);
        String actualAddress = jsobject.getString("address");
        System.out.println(actualAddress);
        Assert.assertEquals(newAddress,actualAddress);

    }
}
