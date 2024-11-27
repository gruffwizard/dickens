package dev.gruff.dickens.api;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import dev.gruff.dickens.workflow.SocialMediaWorkflow;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestPath;

@Path("/api")
public class WorkflowResource {

    @Inject
    WorkflowClient client;

    @GET
    @Path("/send/{msg}")
    public Response postContent(@RestPath String msg) {

        SocialMediaWorkflow workflow = client.newWorkflowStub(
                SocialMediaWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("SOCIAL_MEDIA_TASK_QUEUE")
                        .build()
        );
        workflow.postToAllPlatforms(msg);
        return Response.ok("Content posted successfully").build();
    }
}
