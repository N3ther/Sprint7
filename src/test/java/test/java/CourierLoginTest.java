package test.java;

import test.java.models.CourierApi;
import test.java.models.CourierModel;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

@Epic("API Test")
@Feature("Courier Login API")
public class CourierLoginTest {

    private CourierApi courierApi;
    private String courierId; // ID курьера для удаления после тестов
    private final CourierModel courier = new CourierModel("pipa", "1234", "John"); // Объект курьера

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        courierApi.createCourier(courier); // Создание курьера через объект
        courierId = courierApi.loginCourier(courier.getLogin(), courier.getPassword());
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }

    @Test
    @Description("Проверка успешного логина курьера с правильными данными")
    public void testSuccessfulLogin() {
        assertNotNull("ID курьера не найден в ответе", courierId);
    }

    @Test
    @Description("Проверка на отсутствие обязательного поля логина")
    public void testMissingLogin() {
        CourierModel courierWithoutLogin = new CourierModel("", courier.getPassword(), courier.getFirstName());
        courierApi.checkCourierCreationWithoutRequiredField("login", courierWithoutLogin);
    }

    @Test
    @Description("Проверка на отсутствие обязательного поля пароля")
    public void testMissingPassword() {
        CourierModel courierWithoutPassword = new CourierModel(courier.getLogin(), "", courier.getFirstName());
        courierApi.checkCourierCreationWithoutRequiredField("password", courierWithoutPassword);
    }

    @Test
    @Description("Проверка авторизации несуществующего курьера")
    public void testNonExistentCourierLogin() {
        Response response = courierApi.checkNonExistentCourierLogin("nonexistentuser", "1234");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Проверка на неверный логин или пароль")
    public void testInvalidLoginOrPassword() {
        Response response = courierApi.checkInvalidLoginOrPassword(courier.getLogin(), "wrongpassword");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
