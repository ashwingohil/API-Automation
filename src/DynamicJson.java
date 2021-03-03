import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DynamicJson {


    String baseURI = "http://216.10.245.166";
    String resourceAddBook = " /Library/Addbook.php";
    String headerKey = "Content-Type";
    String headerKeyValue = "application/json";
    String resourceDeleteBook = "/Library/DeleteBook.php";


    @Test(priority = 1, dataProvider = "BooksData")
    public void addBook(String isbn, String aisle){
        RestAssured.baseURI = baseURI;
        String addBookResponse = RestAssured.given().log().all()
                .header(headerKey,headerKeyValue)
                .body(DynamicJsonPayload.getAddBookBody(isbn,aisle))
                .when().post(resourceAddBook)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract().response().asString();

        System.out.println("Response for adding book is: "+addBookResponse); //you will know about the fields from the postman like ID

        JsonPath jsonObject = new JsonPath(addBookResponse);
        String bookId = jsonObject.getString("ID");
        System.out.println("Book ID is: "+bookId);
    }

    @Test(priority = 2, dataProvider ="BooksData")
    public void deleteBook(String isbn, String aisle){

        //one way that i can do is that get data from provider, concat the values for each array to form a book id
        //and use the deletebook body (Library Api) and delete the values

        RestAssured.baseURI = baseURI;
        String deleteBookResponse = RestAssured.given()
                .header(headerKey,headerKeyValue)
                .body(DynamicJsonPayload.getDeleteBookBody(isbn,aisle))
                .when().post(resourceDeleteBook)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract().response().asString();
        System.out.println("Delete book response is: "+deleteBookResponse);
    }

    @DataProvider(name = "BooksData") //name not required
    public Object[][] getDataAddBook(){
        return new Object[][]{{"xyzj","5689"}, {"abde","8457"}, {"lmnop","7263"}};
    }

}
