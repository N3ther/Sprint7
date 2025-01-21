package test.java.models;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class CourierApi {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private ObjectMapper objectMapper = new ObjectMapper();

    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    @Step("Создание курьера")
    public void createCourier(CourierModel courier) {
        given()
                .contentType(ContentType.JSON)
                .body(serializeToJson(courier)) // Используем сериализацию
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Step("Создание курьера с проверкой ответа")
    public void createCourierWithResponseCheck(CourierModel courier, int expectedStatusCode, String expectedMessage) {
        given()
                .contentType(ContentType.JSON)
                .body(serializeToJson(courier))
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(expectedStatusCode)
                .body("message", equalTo(expectedMessage));
    }

    @Step("Логин курьера")
    public String loginCourier(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(createLoginBody(login, password))
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(200)
                .extract()
                .path("id").toString();
    }

    @Step("Логин курьера с проверкой ответа")
    public Response loginCourierWithResponseCheck(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(createLoginBody(login, password))
                .when()
                .post(LOGIN_URL);
    }

    @Step("Удаление курьера")
    public void deleteCourier(String id) {
        given()
                .contentType(ContentType.JSON)
                .body("{\"id\": \"" + id + "\"}")
                .when()
                .delete(BASE_URL + "/" + id)
                .then()
                .statusCode(200);
    }

    @Step("Проверка создания курьера без обязательного поля: {field}")
    public void checkCourierCreationWithoutRequiredField(String field, CourierModel courier) {
        String body = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                (field.equals("login") ? "" : courier.getLogin()),
                (field.equals("password") ? "" : courier.getPassword()),
                (field.equals("firstName") ? "" : courier.getFirstName()));

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Проверка логина несуществующего курьера")
    public Response checkNonExistentCourierLogin(String login, String password) {
        return loginCourierWithResponseCheck(login, password)
                .then()
                .statusCode(404)
                .extract()
                .response();
    }

    @Step("Проверка некорректного логина или пароля")
    public Response checkInvalidLoginOrPassword(String login, String password) {
        return loginCourierWithResponseCheck(login, password)
                .then()
                .statusCode(404)
                .extract()
                .response();
    }

    private String createLoginBody(String login, String password) {
        return String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password);
    }
}