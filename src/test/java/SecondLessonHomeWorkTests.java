import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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

        Thread.sleep(20000);

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
}
