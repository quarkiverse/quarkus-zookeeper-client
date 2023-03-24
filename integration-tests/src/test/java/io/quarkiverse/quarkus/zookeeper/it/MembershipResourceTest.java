package io.quarkiverse.quarkus.zookeeper.it;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkiverse.zookeeper.membership.model.GroupMembership.PartyStatus;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ZookeeperTestServer.class)
@TestHTTPEndpoint(MembershipResource.class)
class MembershipResourceTest {

    @Test
    void testReactiveEndpoint() {
        given()
                .when().get("/reactive")
                .then()
                .statusCode(200)
                .extract().body().as(PartyStatus.class)
                .equals(PartyStatus.Partecipating);
    }

    @Test
    void testImperativeEndpoint() {
        given()
                .when().get("/imperative")
                .then()
                .statusCode(200)
                .extract().body().as(PartyStatus.class)
                .equals(PartyStatus.Partecipating);
    }
}
