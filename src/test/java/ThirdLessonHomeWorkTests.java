import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThirdLessonHomeWorkTests {

    @Test
    public void secondHomeworkTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookie = response.getCookie("HomeWork");
        System.out.println(cookie);
        assertEquals("hw_value", cookie, "The cookie is not correct");
    }
}
