package scooter;

import java.util.UUID;

public class TestData {

    public static Courier generateCourier() {

        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "");

        return new Courier(
                "coolCourier" + unique,
                "Qwerty09876",
                "Flash"
        );
    }
}