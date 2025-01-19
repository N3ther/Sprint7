package test.java;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private final String firstName = "Naruto";
    private final String lastName = "Uchiha";
    private final String address = "Konoha, 142 apt.";
    private final int metroStation = 4;
    private final String phone = "+7 800 355 35 35";
    private final int rentTime = 5;
    private final String deliveryDate = "2020-06-06";
    private final String comment = "Saske, come back to Konoha";
    private final String[] color;
    private String track;

    public CreateOrderTests(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    @Description("Тестирование создания заказа с параметризацией цветов.")
    @Step("Создать заказ с цветами: {0}")
    public void shouldCreateOrderWithVariousColors() {
        given()
                .header("Content-Type", "application/json")
                .body(createOrderBody())
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract().path("track");
    }

    private String createOrderBody() {
        return String.format("{ " +
                        "\"firstName\": \"%s\", " +
                        "\"lastName\": \"%s\", " +
                        "\"address\": \"%s\", " +
                        "\"metroStation\": %d, " +
                        "\"phone\": \"%s\", " +
                        "\"rentTime\": %d, " +
                        "\"deliveryDate\": \"%s\", " +
                        "\"comment\": \"%s\", " +
                        "\"color\": %s " +
                        "}", firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment,
                color.length > 0 ? String.format("[\"%s\"]", String.join("\", \"", color)) : "[]");
    }
    @After
    public void tearDown() {
        if (track != null) {
            cancelOrder(track); // Отменяем заказ после теста
        }
    }

    private void cancelOrder(String track) {
        given()
                .header("Content-Type", "application/json")
                .body("{ \"track\": " + track + " }")
                .when()
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders/cancel")
                .then()
                .statusCode(200);
    }
}