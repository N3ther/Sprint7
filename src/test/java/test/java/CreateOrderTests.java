package test.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.fasterxml.jackson.databind.ObjectMapper;
import test.java.models.OrderModel;


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
        OrderModel order = new OrderModel(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
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