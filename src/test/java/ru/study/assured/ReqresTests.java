package ru.study.assured;


import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import ru.study.assured.pojos.Reg;
import ru.study.assured.pojos.UserData;
import ru.study.assured.pojos.UserToken;

import java.lang.management.ManagementPermission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ReqresTests {

    private static final String URL = "https://reqres.in";

    @Test
    public void whenAvatarSame() {
        List<UserData> users = given().
                when().contentType(ContentType.JSON)
                .get(URL +"/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath()
                .getList("data", UserData.class);
        users.forEach(userData -> Assert.assertTrue(userData
                .getAvatar().contains(userData.getId().toString())));
        Assert.assertTrue(users.stream().
                allMatch(userData -> userData.getEmail().endsWith("@reqres.in")));
    }

    @Test
    public void whenSuccessRegistration() {
        Reg user = new Reg("eve.holt@reqres.in", "pistol");
       UserToken created = given().
                contentType(ContentType.JSON)
                .body(user)
               .when()
                .post(URL + "/api/register")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
               // .body("id", notNullValue())
               // .body("token", notNullValue())
                .extract()
                .as(UserToken.class);
       Assert.assertNotNull(created);
       Assert.assertEquals(Optional.of(4).get(), created.getId());
}

    @Test
    public void whenUnsuccessfulRegistration() {
        String email = "sydney@fife";
        Reg user = new Reg();
        user.setEmail(email);
      int code =  given().
                contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(URL + "/api/register")
                .then()
                .contentType(ContentType.JSON)
                .extract().statusCode();
      Assert.assertEquals(400, code);
    }

    @Test
    public void whenDeleteUser() {
        String req = "/api/users/2";
        int code = given()
                .when()
                .delete(URL+req)
                .then().log().all()
                .extract().statusCode();
        Assert.assertEquals(204, code);
    }

    @Test
    public void whenDoWithResponse() {
        Response response = given()
                .when()
                .get(URL+"/api/users/2")
                .then()
                .log()
                .all()
                .extract().response();
        JsonPath path = response.jsonPath();
        Integer id = path.getInt("data.id");
        String email = path.getString("data.email");
        Assert.assertEquals(Optional.of(2).get(), id);
        Assert.assertEquals("janet.weaver@reqres.in", email);


    }

    @Test
    public void whenRegWithoutPojos() {
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(URL + "/api/register")
                .then()
                .log().all()
                .body("id", equalTo(4))
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .statusCode(equalTo(200));


    }

    @Test
    public void whenRegResponseWithoutPojos() {
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
       Response response=  given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(URL + "/api/register")
                .then()
                .log().all()
                .statusCode(equalTo(200))
               .extract().response();
       JsonPath path = response.jsonPath();
       Integer id = path.getInt("id");
       String token = path.getString("token");
       Assert.assertEquals(Integer.valueOf(4), id);
       Assert.assertEquals("QpwL5tke4Pnpja7X4", token);


    }

}
