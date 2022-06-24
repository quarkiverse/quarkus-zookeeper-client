package io.quarkiverse.quarkus.zookeeper.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.apache.zookeeper.ZooKeeper.States;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ZookeeperTestServer.class)
@TestHTTPEndpoint(ZookeeperResource.class)
public class ZookeeperResourceTest {

    @Test
    public void testReactiveEndpoint() {
        given()
                .when().get("/reactive")
                .then()
                .statusCode(200)
                .body(is(States.CONNECTED.name()));
    }

    @Test
    public void testImperativeEndpoint() {
        given()
                .when().get("/imperative")
                .then()
                .statusCode(200)
                .body(is(States.CONNECTED.name()));
    }
}
