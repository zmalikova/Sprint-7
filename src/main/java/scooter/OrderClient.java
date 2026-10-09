package scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return given()
                .baseUri(ApiClient.BASE_URL)
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ApiClient.ORDERS_PATH);
    }
}