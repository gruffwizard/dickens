package dev.gruff.dickens.api;

import dev.gruff.dickens.Dickens;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import dev.gruff.dickens.workflow.SocialMediaWorkflow;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestPath;

@ApplicationScoped
@Path("/api")
public class WorkflowResource {

    @Inject
    WorkflowClient client;
    @Inject
    Dickens dickens;

    SocialMediaWorkflow workflow;
    @GET
    @Path("/send/{msg}")
    public Response postContent(@RestPath String msg) {

        workflow.postToAllPlatforms(msg);
        return Response.ok("Content posted successfully").build();
    }

    @GET
    @Path("/workers")
    public Response workers() {
        return Response.ok(dickens.getFoo()).build();
    }

    @PostConstruct
    public void init() {
         workflow = client.newWorkflowStub(
                SocialMediaWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("SOCIAL_MEDIA_TASK_QUEUE")
                        .build()
        );
    }
}
