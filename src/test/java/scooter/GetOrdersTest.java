package scooter;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetOrdersTest {

    @Test
    void ordersListIsReturned() {

        Response response = given()
                .baseUri(ApiClient.BASE_URL)
                .when()
                .get(ApiClient.ORDERS_PATH);

        assertNotNull(response.jsonPath().getList("orders"));
    }
}