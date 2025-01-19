package test.java;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;


import java.io.BufferedReader;
import java.io.InputStreamReader;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class OrdersApiTest {

    private final String url = "https://qa-scooter.praktikum-services.ru/api/v1/orders";

    @Test
    @Description("Проверка, что в тело ответа возвращается список заказов.")
    @Step("Отправка GET запроса к {url}")
    public void testGetOrders() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            // Проверяем статус код
            assertEquals(200, response.getStatusLine().getStatusCode());

            // Проверяем тело ответа
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
            // Предполагаем, что возвращаемое тело должно содержать список заказов
            assertTrue(responseBody.toString().contains("orders"));
        }
    }
}