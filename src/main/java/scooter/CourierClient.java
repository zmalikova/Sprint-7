package scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {

        return given()
                .baseUri(ApiClient.BASE_URL)
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post(ApiClient.COURIER_PATH);
    }

    @Step("Авторизовать курьера")
    public Response loginCourier(String login, String password) {

        String body = String.format(
                "{\"login\":\"%s\",\"password\":\"%s\"}",
                login,
                password
        );

        return given()
                .baseUri(ApiClient.BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(ApiClient.COURIER_LOGIN_PATH);
    }

    @Step("Удалить курьера с id: {courierId}")
    public Response deleteCourier(int courierId) {

        return given()
                .baseUri(ApiClient.BASE_URL)
                .when()
                .delete(ApiClient.COURIER_PATH + "/" + courierId);
    }
}