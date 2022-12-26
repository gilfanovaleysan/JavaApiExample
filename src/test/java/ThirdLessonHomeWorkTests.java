import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThirdLessonHomeWorkTests {

    @Test
    public void firstHomeworkTest() {
        String generatedString = RandomStringUtils.randomAlphabetic(1, 30);
        System.out.println(generatedString);
        assertTrue(generatedString.length()>15, "Text is shorter than 15 characters");
    }

    @Test
    public void secondHomeworkTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookie = response.getCookie("HomeWork");
        System.out.println(cookie);
        assertEquals("hw_value", cookie, "The cookie is not correct");
    }

    @Test
    public void thirdHomeworkTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String header = response.getHeader("x-secret-homework-header");
        System.out.println(header);
        assertEquals("Some secret value", header, "The header is not correct");
    }
}
