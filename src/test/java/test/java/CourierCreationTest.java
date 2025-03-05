package test.java;

import test.java.models.CourierApi;
import test.java.models.CourierModel;
import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CourierCreationTest {

    private CourierApi courierApi;
    private String courierId;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }

    @Test
    @Description("Проверка успешного создания курьера")
    public void testCreateCourier() {
        CourierModel courier = new CourierModel("ronicourier", "123", "TestFirstName");
        courierApi.createCourier(courier);
        courierId = courierApi.loginCourier(courier.getLogin(), courier.getPassword());
    }

    @Test
    @Description("Проверка создания курьера с дублирующимся логином")
    public void testCreateDuplicateCourier() {
        CourierModel courier = new CourierModel("ronicourier", "123", "FirstName");
        courierApi.createCourier(courier);
        courierApi.createCourierWithResponseCheck(courier, 409, "Этот логин уже используется");
        courierId = courierApi.loginCourier(courier.getLogin(), courier.getPassword());
    }

    @Test
    @Description("Проверка создания курьера без логина")
    public void testCreateCourierWithoutLogin() {
        CourierModel courier = new CourierModel("", "123", "FirstName");
        courierApi.checkCourierCreationWithoutRequiredField("login", courier);
    }

    @Test
    @Description("Проверка создания курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        CourierModel courier = new CourierModel("ronicourierr", "", "FirstName");
        courierApi.checkCourierCreationWithoutRequiredField("password", courier);
    }

    @Test
    @Description("Проверка создания курьера без имени")
    public void testCreateCourierWithoutFirstName() {
        CourierModel courier = new CourierModel("ronicourier", "123", "");
        courierApi.createCourier(courier);
        courierId = courierApi.loginCourier(courier.getLogin(), courier.getPassword());
    }
}