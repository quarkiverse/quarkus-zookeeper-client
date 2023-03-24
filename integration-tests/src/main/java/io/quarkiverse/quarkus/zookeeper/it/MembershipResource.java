/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.quarkiverse.quarkus.zookeeper.it;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkiverse.zookeeper.membership.model.GroupMembership.PartyStatus;
import io.quarkiverse.zookeeper.membership.model.MembershipStatus;
import io.quarkiverse.zookeeper.membership.model.ReactiveMembershipStatus;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@Path("/membership")
@RequestScoped
public class MembershipResource {

    @Inject
    MembershipService service;

    @Inject
    MembershipStatus status;

    @Inject
    ReactiveMembershipStatus reactiveStatus;

    @GET
    @Path("/reactive")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<PartyStatus> sayStatus() {

        return service.sayStatus();
    }

    @GET
    @Path("/imperative")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public PartyStatus sayStatusS() {

        return service.sayStatusS();
    }
}
