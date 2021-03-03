import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

public class JsonTraversal {

    public JsonPath jsonObject = new JsonPath(JsonTraversalPayload.getJsonData());

    public void getCountOfCourses(){
        //courses look into editor is actually an array. size() is used only for arrays
        int count = jsonObject.getInt("courses.size()");
        System.out.println("No of courses are: "+count);
    }

    public void getPurchaseAmount(){
        //see how dot (.) operator is used to traverse
        //it is in integer format  not in inverted quotes so getInt method of JsonPath
        int amount = jsonObject.getInt("dashboard.purchaseAmount");
        System.out.println("Purchase amount: "+amount);
    }

    public void getTitleOfFirstCourse(){
        //its an array so index courses[0] and then courses[0].title. It is letters so String so getString method of JsonPath
        //get() will also do, it also takes String argument
        String title = jsonObject.getString("courses[0].title");
        System.out.println("Title of first course: "+title);
        System.out.println();
    }

    public void getAllCourseTitlesPrices(){

        //very important. follow the technique

        int count = jsonObject.getInt("courses.size()");

        int price[] = new int[3];
        String title[] = new String[3];

        for(int i = 0; i<count; i++){
            title[i] = jsonObject.get("courses["+i+"].title");
            price[i] = jsonObject.get("courses["+i+"].price");
        }

        for(int i=0; i<3 ;i++){
            System.out.print("Tile of ["+(i+1)+"]: "+title[i]);
            System.out.print("   ");
            System.out.print("Price of ["+(i+1)+"]: "+price[i]);
            System.out.println();
        }

        System.out.println();
    }


    public void getNoOfCopiesSoldByRPA(){

        int count = jsonObject.getInt("courses.size()");
        int noOfCopies = -1;
        for(int i=0; i<count; i++){

            String title = jsonObject.get("courses["+i+"].title");
            if(title.equals("RPA")){
                noOfCopies = jsonObject.getInt("courses["+i+"].copies");
                break;
            }
        }

        System.out.println("No. of copies sold by RPA: "+noOfCopies);
    }


    public void verifySumOfAllCoursePricesMatchesPurchaseAmount(){

        //important
        int count = jsonObject.getInt("courses.size()");
        int purchaseAmount = jsonObject.getInt("dashboard.purchaseAmount");

        int totalprice = 0;
        for(int i=0; i<count; i++){
            totalprice = totalprice + (jsonObject.getInt("courses["+i+"].price")
                                        * jsonObject.getInt("courses["+i+"].copies"));
        }

        if(totalprice == purchaseAmount){
            System.out.println("Purchase amount is same "+totalprice);
        }
        else System.out.println("Purchase amount does not match the total");

        //This can be done with TestNg also. usually this is to be done. Then assert.assertEquals(totalprice,purchaseAmount)
        //I have opened main method here so cannot do.
    }

    public static void main(String[] args){

        JsonTraversal object = new JsonTraversal();
        object.getCountOfCourses();
        object.getPurchaseAmount();
        object.getTitleOfFirstCourse();
        object.getAllCourseTitlesPrices();
        object.getNoOfCopiesSoldByRPA();
        object.verifySumOfAllCoursePricesMatchesPurchaseAmount();
    }
}
