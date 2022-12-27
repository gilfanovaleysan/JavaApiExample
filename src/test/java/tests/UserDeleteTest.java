package tests;

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

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void successfulDeleteUserTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.registerUserRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteUserWithAuthRequest("https://playground.learnqa.ru/api/user/" + userId
                , this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = apiCoreRequests.makeGetUserDataRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        assertEquals("User not found", responseUserData.asString(), "User is not deleted");
    }

    @Test
    public void deleteProtectedUserTest() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteUserWithAuthRequest("https://playground.learnqa.ru/api/user/2"
                , this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        assertEquals("Please, do not delete test users with ID 1, 2, 3, 4 or 5.", responseDeleteUser.asString(), "Error message is not correct");
    }


    @Test
    public void deleteUserWithAnotherUserTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.registerUserRequest("https://playground.learnqa.ru/api/user/", userData);

        //GENERATE USER FOR DELETING
        Map<String, String> userDataForDeletingUser = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuthForEditedUser = apiCoreRequests
                .registerUserRequest("https://playground.learnqa.ru/api/user/", userDataForDeletingUser);

        String userId = responseCreateAuthForEditedUser.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //LOGIN USER FOR DELETING
        Map<String, String> authDataForDeletedUser = new HashMap<>();
        authDataForDeletedUser.put("email", userDataForDeletingUser.get("email"));
        authDataForDeletedUser.put("password", userDataForDeletingUser.get("password"));

        Response responseGetAuthForEditedUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authDataForDeletedUser);
        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteUserWithAuthRequest("https://playground.learnqa.ru/api/user/"  + userId
                , this.getHeader(responseGetAuth, "x-csrf-token"), this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = apiCoreRequests.makeGetUserDataRequest("https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuthForEditedUser, "x-csrf-token"), this.getCookie(responseGetAuthForEditedUser, "auth_sid"));

        String[] expectedFields = {"id", "username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }
}
