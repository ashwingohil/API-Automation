import POJO.GetCourse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.json.Json;
import org.testng.annotations.Test;

public class OauthFlow {

    String code = null;
    String accessToken = null;


    //Refer OAuth+2+contract doc
    /*
    I HAVE IMPORTED MiminalREST, JUST EXCLUDE guava-28.2-android.jar FROM THE DEPENDENCIES IN PROJECT STRUCTURE
    Then selenium will also work with RestAssured
    Refer Notes
     */

    @Test(priority = 1)
    public void getCode(){

        String emailText = "ashwin.k.gohil@gmail.com";
        String password = "!Nopassword9979737596";

        //These are the parameters and endpoint(baseURI)
        String baseURI = "https://accounts.google.com/o/oauth2/v2/auth";
        String scopeKey = "scope";
        String scopeValue = "https://www.googleapis.com/auth/userinfo.email";
        String auth_urlKey = "auth_url";
        String auth_urlValue = "https://accounts.google.com/o/oauth2/v2/auth";
        String client_idKey = "client_id";
        String client_idValue = "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com";
        String response_typeKey = "response_type";
        String response_typeValue = "code";
        String redirect_uriKey = "redirect_uri";
        String redirect_uriValue = "https://rahulshettyacademy.com/getCourse.php";

        //Forming the string
        //After endpoing ? comes and parameters are chained by &
        String URL = baseURI+"?"+scopeKey+"="+scopeValue+"&"+auth_urlKey+"="+auth_urlValue+"&"
                +client_idKey+"="+client_idValue+"&"+response_typeKey+"="+response_typeValue+"&"
                +redirect_uriKey+"="+redirect_uriValue;

        System.out.println(URL);


        //You will have to import selenium here in project structure
        WebDriver driver;
        System.setProperty("webdriver.gecko.driver","/home/ashwin/Desktop/geckodriver");
        driver = new FirefoxDriver();
        driver.get(URL);

        WebElement emailBox = driver.findElement(By.cssSelector("input#identifierId"));
        emailBox.sendKeys(emailText);
        WebElement next1Button = driver.findElement(By.cssSelector("div.VfPpkd-RLmnJb"));
        next1Button.click();
        try{
            Thread.sleep(3000);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
        WebElement passwordBox = driver.findElement(By.cssSelector("input[type='password']"));
        passwordBox.sendKeys(password);
        WebElement next2Button = driver.findElement(By.cssSelector("div.VfPpkd-RLmnJb"));
        next2Button.click();

        try{
            Thread.sleep(6000);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }

        String responseUrl = driver.getCurrentUrl();


        String afterlimiter = responseUrl.split("code=")[1];
        //[0] means before the compare value, the string is returned. [1] means after the compare value string is returned
        code = afterlimiter.split("&scope")[0];
        System.out.println("Code is: "+code);

        driver.close();
    }


    @Test(priority = 2)
    public void getAccessToken(){

        //POST
        String baseURI = "https://www.googleapis.com/oauth2/v4/token";
        String codeKey = "code";
        String codeValue = code;
        String client_idKey = "client_id";
        String client_idValue = "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com";
        String clientSecretKey = "client_secret";
        String clientSecretValue = "erZOWM9g3UtwNRj340YYaK_W";
        String redirect_uriKey = "redirect_uri";
        String redirect_uriValue = "https://rahulshettyacademy.com/getCourse.php";
        String grant_typeKey = "grant_type";
        String grant_typeValue = "authorization_code";

        //urlencoding is new method here
        //here we are forming RestAssured.baseURI wont come. in POST we pass the baseURI
        //or you can try it and see

        String getAccessTokenResponse = RestAssured.given().log().all()
                .urlEncodingEnabled(false)
                .queryParam(codeKey,codeValue)
                .queryParam(client_idKey,client_idValue)
                .queryParam(clientSecretKey,clientSecretValue)
                .queryParam(redirect_uriKey,redirect_uriValue)
                .queryParam(grant_typeKey,grant_typeValue)
                .when()
                .post(baseURI).asString(); //shortcut method to get the response not extract().response()

        JsonPath js = new JsonPath(getAccessTokenResponse);
        accessToken = js.getString("access_token");
    }

    @Test(priority = 3)
    public void ActualGetCourse(){

        //This is important
        //Uncomment below for standard response on the screen
        /*
        String baseURI = "https://rahulshettyacademy.com/getCourse.php";
        String getCourseResponse = RestAssured.given()
                .queryParam("access_token",accessToken)
                .when().log().all()
                .get(baseURI).asString();

        System.out.println("Response for getCourse is: "+getCourseResponse);
        */

        //Below is implementation of deserialization. Pojo classes for the json response (inspect postman)
        //is to be created
        //VERY IMPORTANT

        String baseURI = "https://rahulshettyacademy.com/getCourse.php";
        GetCourse getCourseObject = RestAssured.given()
                .queryParam("access_token",accessToken)
                .expect().defaultParser(Parser.JSON)
                .when()
                .get(baseURI).as(GetCourse.class);


        //now is simple getter and setter methods in pojo classes as getCourseObject is an object

        String instructor = getCourseObject.getInstructor();
        String url = getCourseObject.getUrl();
        String service = getCourseObject.getServices();
        String expertise = getCourseObject.getExpertise();
        String linkedin = getCourseObject.getLinkedIn();

        System.out.println("instructor : "+instructor);
        System.out.println("url : "+url);
        System.out.println("service : "+service);
        System.out.println("expertise : "+expertise);
        System.out.println("linkedin : "+linkedin);

        int countWeb  = 0;
        countWeb = getCourseObject.getCourses().getWebAutomation().size();
        System.out.println("WebAutomation:");
        for(int i=0; i < countWeb; i++){
            String title = getCourseObject.getCourses().getWebAutomation().get(i).getCourseTitle();
            String price = getCourseObject.getCourses().getWebAutomation().get(i).getPrice();
            System.out.println("WebAutomation["+i+"]");
            System.out.println(title);
            System.out.println(price);
        }

        int countApi = 0;
        countApi = getCourseObject.getCourses().getApi().size();
        System.out.println("API:");
        for(int i=0; i<countApi; i++){
            String title = getCourseObject.getCourses().getApi().get(i).getCourseTitle();
            String price = getCourseObject.getCourses().getApi().get(i).getPrice();
            System.out.println("API["+i+"]");
            System.out.println(title);
            System.out.println(price);
        }

    }

}
