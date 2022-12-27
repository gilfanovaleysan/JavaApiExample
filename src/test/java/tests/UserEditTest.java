package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void editWithoutAuthTest() {
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutEditUserWithoutAuthRequest("https://playground.learnqa.ru/api/user/2", editData);

        assertEquals(responseEditUser.asString(), "Auth token not supplied", "Error message is not correct");
    }

    @Test
    public void editUserWithAnotherUserTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.registerUserRequest("https://playground.learnqa.ru/api/user/", userData);

        //GENERATE EDITED USER
        Map<String, String> userDataForGeneratedUser = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuthForEditedUser = apiCoreRequests
                .registerUserRequest("https://playground.learnqa.ru/api/user/", userDataForGeneratedUser);

        String userId = responseCreateAuthForEditedUser.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //LOGIN WITH EDITED USER
        Map<String, String> authDataForEditedUser = new HashMap<>();
        authDataForEditedUser.put("email", userDataForGeneratedUser.get("email"));
        authDataForEditedUser.put("password", userDataForGeneratedUser.get("password"));

        Response responseGetAuthForEditedUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authDataForEditedUser);
        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutEditUserWithAuthRequest("https://playground.learnqa.ru/api/user/" + userId,
                editData, this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = apiCoreRequests.makeGetUserDataRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuthForEditedUser, "x-csrf-token"), this.getCookie(responseGetAuthForEditedUser, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", "learnqa");
    }

    @Test
    public void editDataOnIncorrectEmailTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.registerUserRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT
        String newEmail = "lesyacom.ru";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutEditUserWithAuthRequest("https://playground.learnqa.ru/api/user/" + userId,
                editData, this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        assertEquals(responseEditUser.asString(), "Invalid email format", "Error message is not correct");

        //GET
        Response responseUserData = apiCoreRequests.makeGetUserDataRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        assertNotEquals(newEmail, userEmail, "Email is changed");
    }

    @Test
    public void editDataOnShortFirstnameTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.registerUserRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");
        String userName = userData.get("firstName");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT
        String newName = "X";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutEditUserWithAuthRequest("https://playground.learnqa.ru/api/user/" + userId,
                editData, this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");

        //GET
        Response responseUserData = apiCoreRequests.makeGetUserDataRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        assertNotEquals(newName, userName, "FirstName is changed");
    }
}
