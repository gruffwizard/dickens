package dev.gruff.dickens;

import dev.gruff.dickens.workflow.SocialMediaWorkflow;
import dev.gruff.dickens.workflow.WorkerRegistryWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class Dickens {

    private static final Logger log = LoggerFactory.getLogger(Dickens.class);
    @Inject
    WorkflowClient client;

    WorkerRegistryWorkflow workflow;
    SocialMediaWorkflow smf;
    @PostConstruct
    public void initialize() {

        log.info("\n\n\nDickens ON\n\n\n");

        workflow = client.newWorkflowStub(
                WorkerRegistryWorkflow.class,
                WorkflowOptions.newBuilder()
                .setTaskQueue("worker-registration")
                        .setWorkflowId("worker-registry").build()
        );
        WorkflowClient.start(workflow::run);
        log.info("\n\n\nDickens READY\n\n\n");
    }

    public Map<String, String> getFoo() {
        return workflow.getRegisteredWorkers();
    }


    public void post(String msg) {
        smf = client.newWorkflowStub(
                SocialMediaWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("SOCIAL_MEDIA_TASK_QUEUE")
                        .build()
        );
        smf.postToAllPlatforms(msg);

    }
}
