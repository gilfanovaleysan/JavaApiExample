import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondLessonHomeWorkTests {

    @Test
    public void firstHomeworkTest() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }

    @Test
    public void secondHomeworkTest() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void thirdHomeworkTest() {
        String link = "https://playground.learnqa.ru/api/long_redirect";

        while (link != null) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(link)
                    .andReturn();

            String locationHeader = response.getHeader("Location");
            System.out.println(locationHeader);
            link = locationHeader;
            int statusCode = response.getStatusCode();
            System.out.println(statusCode);
        }
    }

    @Test
    public void fourthHomeworkTest() throws InterruptedException {
        Response createTask = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        createTask.print();
        String responseToken = createTask.getBody().path("token");
        int responseSeconds = createTask.getBody().path("seconds");
        Map<String, String> data = new HashMap<>();
        data.put("token", responseToken);

        Response checkUnreadyTask = RestAssured
                .given()
                .queryParams(data)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        checkUnreadyTask.print();
        String taskStatus = checkUnreadyTask.getBody().path("status");
        if (taskStatus.equals("Job is NOT ready")) {
            System.out.println("The key 'status' is correct");
        } else {
            System.out.println("The key 'status' is incorrect");
        }

        String responseSeconds2 = responseSeconds + "000";
        System.out.println(responseSeconds2);
        Thread.sleep(Long.parseLong(responseSeconds2));

        Response checkReadyTask = RestAssured
                .given()
                .queryParams(data)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        checkReadyTask.print();
        String secondStatus = checkReadyTask.getBody().path("status");
        String resultValue = checkReadyTask.getBody().path("result");
        if (secondStatus.equals("Job is ready")) {
            System.out.println("The key 'status' is correct");
        } else {
            System.out.println("The key 'status' is incorrect");
        }
        if (resultValue == null) {
            System.out.println("The key 'result' is absent");
        } else {
            System.out.println("result=" + resultValue);
        }
    }

    @Test
    public void fifthHomeworkTest() throws IOException {
        List<String> passwords = Files.readAllLines(Paths.get("src/test/resources/forHomework/passwords"));
        for (String password : passwords) {
            Map<String, Object> data = new HashMap<>();
            data.put("login", "super_admin");
            data.put("password", password);
            Response response = RestAssured
                    .given()
                    .body(data)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = response.getCookie("auth_cookie");
            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", responseCookie);
            Response responseForCheck = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .get("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();
            String stringResponse = responseForCheck.asString();

            if (stringResponse.equals("You are authorized")) {
                System.out.println(stringResponse + ". The correct password is: " + password);
            }
        }
    }
}
