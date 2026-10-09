package scooter;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateCourierTest {

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
    void courierCanBeCreated() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

        Response response = courierClient.createCourier(courier);

        checkStatusCode(response, 201);
        checkOk(response);

    }

    @Test
    void cannotCreateCourierWithoutLogin() {

        Courier courier = TestData.generateCourier();

        String body = String.format(
                "{\"password\":\"%s\",\"firstName\":\"%s\"}",
                courier.getPassword(),
                courier.getFirstName()
        );

        Response response = io.restassured.RestAssured
                .given()
                .baseUri(ApiClient.BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(ApiClient.COURIER_PATH);

        checkStatusCode(response, 400);
    }

    @Test
    void cannotCreateCourierWithoutPassword() {

        Courier courier = TestData.generateCourier();

        String body = String.format(
                "{\"login\":\"%s\",\"firstName\":\"%s\"}",
                courier.getLogin(),
                courier.getFirstName()
        );

        Response response = io.restassured.RestAssured
                .given()
                .baseUri(ApiClient.BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(ApiClient.COURIER_PATH);

        checkStatusCode(response, 400);
    }

    @Test
    void cannotCreateTwoIdenticalCouriers() {

        Courier courier = TestData.generateCourier();
        courierToDelete = courier;

        Response firstResponse = courierClient.createCourier(courier);

        checkStatusCode(firstResponse, 201);

        Response secondResponse = courierClient.createCourier(courier);

        checkStatusCode(secondResponse, 409);
    }

    @Test
    void cannotCreateCourierWithExistingLogin() {

        Courier firstCourier = TestData.generateCourier();
        courierToDelete = firstCourier;

        Response firstResponse = courierClient.createCourier(firstCourier);

        checkStatusCode(firstResponse, 201);

            Courier secondCourier = new Courier(
                    firstCourier.getLogin(),
                    "Qwerty12345",
                    "Turbo"
            );

        Response secondResponse = courierClient.createCourier(secondCourier);

        checkStatusCode(secondResponse, 409);

    }

    @Step("Проверить статус ответа: {expectedStatus}")
    private void checkStatusCode(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.statusCode());
    }

    @Step("Проверить, что ok равно true")
    private void checkOk(Response response) {
        assertTrue(response.jsonPath().getBoolean("ok"));
    }

    @Step("Удалить созданного курьера")
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