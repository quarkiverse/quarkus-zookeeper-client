package io.quarkiverse.quarkus.zookeeper.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkiverse.quarkus.zookeeper.ZookeeperTestServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ZookeeperTestServer.class)
@TestHTTPEndpoint(ZookeeperResource.class)
public class ZookeeperResourceTest {

    @BeforeAll
    public static void init() {
        System.setProperty("quarkus.zookeeper.session.connectionString", "localhost:2181");
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .body(endsWith("Hello World!!!"));
    }
}
