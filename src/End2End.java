import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class End2End {

    String baseURI = "https://rahulshettyacademy.com";
    String resourceAddPlace = "/maps/api/place/add/json";
    String key = "key";
    String keyvalue = "qaclick123";
    String headerKey = "Content-Type";
    String headerKeyValue = "application/json";
    String responseKeyHeader = "server";
    String responseKeyValueHeader = "Apache/2.4.18 (Ubuntu)"; //look into headers of response in AddPlace collection(Postman)

    String extractedResponse = null;
    String place_id = null;

    String resourceUpdatePlace = "/maps/api/place/update/json";
    String resourceGetPlace = "/maps/api/place/get/json";


    //AddPlace --> UpdatePlace with NewAddress --> GetPlace to valide if NewAddress is reflected in response
    /*
       The format is:
       given() - input details
       when() - resource, http method
       then() - validate the response
     */

    //You chain the methods as it is static. One returns an interface of static type. you look into restassured api for
    //that return type or various methods in api to chain methods. One can have several same name methods like for header,
    //body and so on

    //given() can be log().all() for logging input
    //then() can be log().all() for logging response
    //in then() everything after assertThat() is about asserting like statusCode, body, header
    //extract().response() is extract information after then() which means response into a String


    @Test(priority = 1)
    public void AddPlace(){
        RestAssured.baseURI = baseURI;
        extractedResponse = RestAssured.given().log().all().queryParam(key,keyvalue)
                .header(headerKey,headerKeyValue)
                .body(End2EndPayLoad.addPlace())
                .when().post(resourceAddPlace)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("scope",equalTo("APP"))
                .header(responseKeyHeader,responseKeyValueHeader)
                .extract().response().asString();

        //System.out.println(extractedResponse); //i can run the method at this time
        JsonPath js = new JsonPath(extractedResponse); //parser
        End2EndPayLoad.placeId = js.getString("place_id"); //is actually a path of the json. json traversal in the data
        System.out.println(End2EndPayLoad.placeId); // i can run the method again at this point also. place_id will keep changing
    }

    @Test(priority = 1)
    public void UpdatePlace(){
        //refer API contract and update collection from postman

        RestAssured.given().log().all().queryParam(key,keyvalue)
                .header(headerKey,headerKeyValue)
                .body(End2EndPayLoad.updatePlace())
                .when().put(resourceUpdatePlace)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("msg",equalTo("Address successfully updated"));
    }

    @Test(priority = 2)
    public void GetPlace(){
        //note here we do not provide body but parameters query parameters
        //same placeId is required which we received in AddPlace method

        String getResponse = RestAssured.given()
                .log().all()
                .queryParam(key,keyvalue)
                .queryParam("place_id",End2EndPayLoad.placeId)
                .when().get(resourceGetPlace)
                .then().log().all()
                .statusCode(200)
                .extract().response().asString();

        //what we will receive will be a body where "address" will be there with status 200

        JsonPath js = new JsonPath(getResponse);
        String actualAddress = js.getString("address");
        Assert.assertEquals(End2EndPayLoad.newAddress,actualAddress);
        System.out.println("Actual address: "+actualAddress);
    }
}
