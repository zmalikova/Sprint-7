package scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginCourierTest {

    private final CourierClient courierClient = new CourierClient();

    @Test
    void courierCanLogin() {

        Courier courier = TestData.generateCourier();

        try {
            createCourier(courier);

            Response response = courierClient.loginCourier(
                    courier.getLogin(),
                    courier.getPassword()
            );

            checkStatusCode(response, 200);
            checkId(response);

        } finally {
            deleteCourier(courier);
        }
    }

    @Test
    void cannotLoginWithIncorrectLogin() {

        Courier courier = TestData.generateCourier();

        try {
            createCourier(courier);

            Response response = courierClient.loginCourier(
                    courier.getLogin() + "Wrong",
                    courier.getPassword()
            );

            checkStatusCode(response, 404);

        } finally {
            deleteCourier(courier);
        }
    }

    @Test
    void cannotLoginWithIncorrectPassword() {

        Courier courier = TestData.generateCourier();

        try {
            createCourier(courier);

            Response response = courierClient.loginCourier(
                    courier.getLogin(),
                    "Qwerty12345"
            );

            checkStatusCode(response, 404);

        } finally {
            deleteCourier(courier);
        }
    }

    @Test
    void cannotLoginWithoutLogin() {

        Courier courier = TestData.generateCourier();

        try {
            createCourier(courier);

            String body = String.format(
                    "{\"password\":\"%s\"}",
                    courier.getPassword()
            );

            Response response = io.restassured.RestAssured
                    .given()
                    .baseUri(ApiClient.BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(ApiClient.COURIER_LOGIN_PATH);

            checkStatusCode(response, 400);

        } finally {
            deleteCourier(courier);
        }
    }

    @Test
    void cannotLoginWithoutPassword() {

        Courier courier = TestData.generateCourier();

        try {
            createCourier(courier);

            String body = String.format(
                    "{\"login\":\"%s\"}",
                    courier.getLogin()
            );

            Response response = io.restassured.RestAssured
                    .given()
                    .baseUri(ApiClient.BASE_URL)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(ApiClient.COURIER_LOGIN_PATH);

            checkStatusCode(response, 504);

        } finally {
            deleteCourier(courier);
        }
    }

    @Test
    void cannotLoginWithNonexistentCourier() {

        Courier courier = TestData.generateCourier();

        Response response = courierClient.loginCourier(
                courier.getLogin(),
                courier.getPassword()
        );

        checkStatusCode(response, 404);
    }

    @Step("Создать курьера для теста авторизации")
    private void createCourier(Courier courier) {

        Response response = courierClient.createCourier(courier);

        assertEquals(201, response.statusCode());
    }

    @Step("Проверить статус ответа: {expectedStatus}")
    private void checkStatusCode(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.statusCode());
    }

    @Step("Проверить, что в ответе есть id")
    private void checkId(Response response) {
        assertTrue(response.jsonPath().getInt("id") > 0);
    }

    @Step("Удалить курьера после теста")
    private void deleteCourier(Courier courier) {

        Response loginResponse = courierClient.loginCourier(
                courier.getLogin(),
                courier.getPassword()
        );

        if (loginResponse.statusCode() == 200) {
            int courierId = loginResponse.jsonPath().getInt("id");
            courierClient.deleteCourier(courierId);
        }
    }
}