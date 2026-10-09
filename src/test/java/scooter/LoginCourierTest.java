package scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courierToDelete;

    @AfterEach
    void cleanUpTestData() {
        if (courierToDelete != null) {
            deleteCourier(courierToDelete);
            courierToDelete = null;
        }
    }

    @Test
    void courierCanLogin() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

        createCourier(courier);

        Response response = courierClient.loginCourier(
                    courier.getLogin(),
                    courier.getPassword()
            );

        checkStatusCode(response, 200);
        checkId(response);

    }

    @Test
    void cannotLoginWithIncorrectLogin() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

        createCourier(courier);

        Response response = courierClient.loginCourier(
                    courier.getLogin() + "Wrong",
                    courier.getPassword()
            );

        checkStatusCode(response, 404);

    }

    @Test
    void cannotLoginWithIncorrectPassword() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

        createCourier(courier);

        Response response = courierClient.loginCourier(
                    courier.getLogin(),
                    "Qwerty12345"
            );

        checkStatusCode(response, 404);

    }

    @Test
    void cannotLoginWithoutLogin() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

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

    }

    @Test
    void cannotLoginWithoutPassword() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

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