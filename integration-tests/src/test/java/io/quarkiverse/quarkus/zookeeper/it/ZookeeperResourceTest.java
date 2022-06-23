package io.quarkiverse.quarkus.zookeeper.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ZookeeperResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/zookeeper")
                .then()
                .statusCode(200)
                .body(endsWith("Hello World!!!"));
    }
}
