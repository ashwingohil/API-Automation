import POJO.AddPlace;
import POJO.Location;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SerializationFlow {


    @Test
    public void addPlace(){

        String baseURI = "https://rahulshettyacademy.com";
        String resourceAddPlace = "/maps/api/place/add/json";
        String key = "key";
        String keyValue = "qaclick123";

        AddPlace javaObject = new AddPlace();
        javaObject.setAccuracy(50);
        javaObject.setName("Frontline house");
        javaObject.setPhone_number("(+91) 983 893 3937");
        javaObject.setAddress("29, side layout, cohen 09");
        javaObject.setWebsite("http://google.com");
        javaObject.setLanguage("French-IN");

        List<String> types = new ArrayList<String>();
        types.add("shoe park");
        types.add("shop");
        javaObject.setTypes(types);

        Location locationObject = new Location();
        locationObject.setLat(-38.383494);
        locationObject.setLng(33.427362);
        javaObject.setLocation(locationObject);

        RestAssured.baseURI = baseURI;
        String responseAddPlace = RestAssured.given().log().all()
                .queryParam(key,keyValue)
                .body(javaObject)
                .when()
                .post(resourceAddPlace)
                .then()
                .log().all()
                .assertThat().statusCode(200).extract().response().asString(); //it is not yet .asString so response object

        System.out.println("Response string: "+responseAddPlace);
    }
}
