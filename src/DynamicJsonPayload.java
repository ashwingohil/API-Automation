public class DynamicJsonPayload {


    public static String getAddBookBody(String isbn, String aisle){

        return "{\n" +
                " \n" +
                "\"name\":\"Learn Appium Automation with Java\",\n" +
                "\"isbn\":\""+isbn+"\",\n" +
                "\"aisle\":\""+aisle+"\",\n" +
                "\"author\":\"John foe\"\n" +
                "}\n";
    }


    public static String getDeleteBookBody(String isbn, String aisle){
        return "{\n" +
                " \n" +
                "\"ID\" : \""+isbn+aisle+"\"\n" +
                " \n" +
                "} \n";
    }
}
