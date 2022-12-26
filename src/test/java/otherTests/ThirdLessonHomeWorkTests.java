package otherTests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ThirdLessonHomeWorkTests {

    @Test
    public void firstHomeworkTest() {
        String generatedString = RandomStringUtils.randomAlphabetic(1, 30);
        System.out.println(generatedString);
        assertTrue(generatedString.length() > 15, "Text is shorter than 15 characters");
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

    @MethodSource("userAgentValues")
    @ParameterizedTest
    public void fourthHomeworkTest(String userAgent, String platformExpectedValue, String browserExpectedValue, String deviceExpectedValue) {
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", userAgent);
        JsonPath response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();
        response.prettyPrint();

        String platformActualValue = response.get("platform");
        String browserActualValue = response.get("browser");
        String deviceActualValue = response.get("device");

        assertEquals(platformExpectedValue, platformActualValue, "Value for key 'platform' is incorrect");
        assertEquals(browserExpectedValue, browserActualValue, "Value for key 'browser' is incorrect");
        assertEquals(deviceExpectedValue, deviceActualValue, "Value for key 'device' is incorrect");
    }

    static Stream<Arguments> userAgentValues() {
        return Stream.of(
                arguments("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30"
                        + " (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30", "Mobile", "No", "Android"),
                arguments("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) "
                        + "CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1", "Mobile", "Chrome", "iOS"),
                arguments("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)", "Googlebot", "Unknown", "Unknown"),
                arguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0", "Web", "Chrome", "No"),
                arguments("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)"
                        + " Version/13.0.3 Mobile/15E148 Safari/604.1", "Mobile", "No", "iPhone")
        );
    }

}
