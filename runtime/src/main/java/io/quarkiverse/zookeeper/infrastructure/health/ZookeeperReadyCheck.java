package io.quarkiverse.zookeeper.infrastructure.health;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import io.quarkiverse.zookeeper.infrastructure.ZookeeperClientProducerBean;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;

@Readiness
@ApplicationScoped
public class ZookeeperReadyCheck implements AsyncHealthCheck {

    // public void configure(ZookeeperConfig config) {
    //     System.out.println(config.session.connectionString);
    // }

    @Override
    public Uni<HealthCheckResponse> call() {
        return Uni.createFrom().item(HealthCheckResponse.up(ZookeeperClientProducerBean.EXTENSION_NAME));
    }
}
