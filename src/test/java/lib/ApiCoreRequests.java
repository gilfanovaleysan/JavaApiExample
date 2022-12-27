package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request with token only")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with user data")
    public JsonPath registerUserRequest(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .jsonPath();
    }

    @Step("Make a POST-request with incorrect email")
    public Response makeRegisterPostRequestWithIncorrectEmail(String url, Map<String, String> userData) {
        String email = "vinkotovexample.com";
        userData.put("email", email);
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with short firstname")
    public Response makeRegisterPostRequestWithShortFirstname(String url, Map<String, String> userData) {
        String firstName = "X";
        userData.put("firstName", firstName);
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with long firstname")
    public Response makeRegisterPostRequestWithLongFirstname(String url, Map<String, String> userData) {
        String firstName = RandomStringUtils.randomAlphabetic(255, 300);
        userData.put("firstName", firstName);
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with empty field")
    public Response makeRegisterPostRequestWithEmptyField(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a GET-request of getting user data with token and auth cookie")
    public Response makeGetUserDataRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a PUT-request of editing user w/o auth")
    public Response makePutEditUserWithoutAuthRequest(String url, Map<String, String> editData) {
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request of editing user with auth")
    public Response makePutEditUserWithAuthRequest(String url, Map<String, String> editData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .put(url)
                .andReturn();
    }

    @Step("Make a DELETE-request of editing user with auth")
    public Response makeDeleteUserWithAuthRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .delete(url)
                .andReturn();
    }
}
