package test.java;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Epic("API Test")
@Feature("Courier Login API")
public class CourierLoginTest {

    // Перед каждым тестом настройка базового URL
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Description("Проверка успешного логина курьера с правильными данными")
    @Step("Отправляем запрос на успешный логин курьера")
    public void testSuccessfulLogin() {
        String requestBody = "{\"login\": \"pipa\", \"password\": \"1234\"}";

        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().response();

        // Проверка, что id курьера был возвращен
        assertNotNull("ID курьера не найден в ответе", response.jsonPath().getString("id"));
    }

    @Test
    @Description("Проверка на отсутствие обязательных данных для логина")
    @Step("Отправляем запрос без обязательных полей")
    public void testMissingLoginOrPassword() {
        String requestBodyMissingLogin = "{\"password\": \"1234\"}";
        String requestBodyMissingPassword = "{\"login\": \"pipa\"}";

        given()
                .contentType("application/json")
                .body(requestBodyMissingLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));

        given()
                .contentType("application/json")
                .body(requestBodyMissingPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Проверка на неверный логин или пароль")
    @Step("Отправляем запрос с неправильными данными для логина")
    public void testInvalidLoginOrPassword() {
        String requestBodyInvalidLogin = "{\"login\": \"pipa\", \"password\": \"wrongpassword\"}";

        given()
                .contentType("application/json")
                .body(requestBodyInvalidLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Проверка авторизации несуществующего курьера")
    @Step("Отправляем запрос с несуществующим пользователем")
    public void testNonExistentCourierLogin() {
        String requestBodyNonExistent = "{\"login\": \"nonexistentuser\", \"password\": \"1234\"}";

        given()
                .contentType("application/json")
                .body(requestBodyNonExistent)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Проверка успешного входа с корректными данными")
    @Step("Отправляем запрос на успешный логин курьера")
    public void testLoginWithValidCredentials() {
        String validRequestBody = "{\"login\": \"pipa\", \"password\": \"1234\"}";

        given()
                .contentType("application/json")
                .body(validRequestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", greaterThan(0));
    }
}
