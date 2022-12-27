package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    public void testCreateUserWithIncorrectEmail() {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeRegisterPostRequestWithIncorrectEmail("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Test
    public void testCreateUserWithTooShortFirstname() {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeRegisterPostRequestWithShortFirstname("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too short");
    }

    @Test
    public void testCreateUserWithTooLongFirstname() {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeRegisterPostRequestWithLongFirstname("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too long");
    }

    @MethodSource("userData")
    @ParameterizedTest(name = " {0}")
    @DisplayName("Создание пользователя без указания поля")
    public void testCreateUserWithEmptyField(String type, String email, String password, String username, String firstName, String lastName) {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseCreateAuth = apiCoreRequests.makeRegisterPostRequestWithEmptyField("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of " + type + " field is too short");
    }

    static Stream<Arguments> userData() {
        return Stream.of(
                arguments("'email'", "", "123", "learnqa", "learnqa", "learnqa"),
                arguments("'password'", DataGenerator.getRandomEmail(), "", "learnqa", "learnqa", "learnqa"),
                arguments("'username'", DataGenerator.getRandomEmail(), "123", "", "learnqa", "learnqa"),
                arguments("'firstName'", DataGenerator.getRandomEmail(), "123", "learnqa", "", "learnqa"),
                arguments("'lastName'", DataGenerator.getRandomEmail(), "123", "learnqa", "learnqa", "")
        );
    }
}
