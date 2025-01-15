package test.java;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class CourierCreationTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @Description("Проверка успешного создания курьера")
    public void testCreateCourier() {
        createCourier("ronicourier", "123", "TestFirstName");
    }

    @Test
    @Description("Проверка создания курьера с дублирующимся логином")
    public void testCreateDuplicateCourier() {
        createCourier("roniicourier", "123", "FirstName");

        // Попробуем создать курьера с таким же логином
        createCourierWithResponseCheck("roniicourier", "123", "FirstName", 409, "Этот логин уже используется");
    }

    @Test
    @Description("Проверка создания курьера без логина")
    public void testCreateCourierWithoutLogin() {
        checkCourierCreationWithoutRequiredField("login", "", "123", "FirstName");
    }

    @Test
    @Description("Проверка создания курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        checkCourierCreationWithoutRequiredField("password", "ronicourierr", "", "FirstName");
    }

    @Test
    @Description("Проверка создания курьера без имени")
    public void testCreateCourierWithoutFirstName() {
        checkCourierCreationWithoutRequiredField("firstName", "ronicourierrr", "123", "");
    }

    @Step("Создание курьера с параметрами: логин={login}, пароль={password}, имя={firstName}")
    private void createCourier(String login, String password, String firstName) {
        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\"}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Step("Создание курьера с проверкой: логин={login}, пароль={password}, ожидаемый код ответа={expectedStatusCode}, сообщение={expectedMessage}")
    private void createCourierWithResponseCheck(String login, String password, String firstName, int expectedStatusCode, String expectedMessage) {
        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\"}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedMessage));
    }

    @Step("Проверка создания курьера без обязательного поля: отсутствующее поле={field}, логин={login}, пароль={password}")
    private void checkCourierCreationWithoutRequiredField(String field, String login, String password, String firstName) {
        String body = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                (field.equals("login") ? "" : login),
                (field.equals("password") ? "" : password),
                (field.equals("firstName") ? "" : firstName));

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}