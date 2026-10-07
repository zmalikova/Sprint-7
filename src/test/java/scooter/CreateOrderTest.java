package scooter;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();

    private static Stream<Arguments> colors() {
        return Stream.of(Arguments.of((Object) new String[]{"BLACK"}),
                Arguments.of((Object) new String[]{"GREY"}),
                Arguments.of((Object) new String[]{"BLACK", "GREY"}),
                Arguments.of((Object) null));
    }

    @ParameterizedTest
    @MethodSource("colors")
    void orderCanBeCreatedWithDifferentColors(String[] colors) {

        Order order = new Order("Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2026-10-10",
                "Saske, come back to Konoha",
                colors);

        Response response = orderClient.createOrder(order);

        assertEquals(201, response.statusCode());
        assert response.jsonPath().getInt("track") > 0;
    }
}