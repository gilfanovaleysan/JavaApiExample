import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
public class HelloWorldTest {

    @Test
    public void testHelloWorldLeysan(){
        System.out.println("Hello from Leysan");
    }

    @Test
    public void firstApiTest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void firstHomeworkTest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
}
