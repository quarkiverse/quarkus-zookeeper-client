package io.quarkiverse.quarkus.zookeeper;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.smallrye.mutiny.Uni;

public class ExceptionMapper {

    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @ServerExceptionMapper
    public Uni<Response> mapException(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        LOG.error("Something went wrong", cause);
        return Uni.createFrom()
                .item(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                        String.format("[%s] - %s", cause.getClass().getSimpleName(), cause.getMessage())).build());
    }
}
