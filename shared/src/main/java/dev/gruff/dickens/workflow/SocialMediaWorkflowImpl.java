package dev.gruff.dickens.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocialMediaWorkflowImpl implements SocialMediaPostWorkflow {

    private static final Duration ACTIVITY_TIMEOUT = Duration.ofMinutes(1);
    private static final int MAX_ATTEMPTS = 3;
    private static final String TASK_QUEUE = "social-media-queue";

    private final SocialMediaActivities activities = Workflow.newActivityStub(
            SocialMediaActivities.class, createActivityOptions()
    );

    @Override
    public String postToAnyPlatform(String postContent) {

        Async.function(() -> activities.postToPlatform("FACEBOOK", postContent));

        try {
            CompletableFuture<String> completedFuture = CompletableFuture.anyOf(
                    Async.function(() -> activities.postToPlatform("FACEBOOK", postContent)),
                    Async.function(() -> activities.postToPlatform("BLUESKY",postContent))
            );

            return completedFuture.get(2, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("No social media service available");
        } catch (Exception e) {
            throw new RuntimeException("Social media posting failed", e);
        }
    }

    public void initiateWorkflow(WorkflowClient client, String postContent) {
        SocialMediaPostWorkflow workflow = client.newWorkflowStub(
                SocialMediaPostWorkflow.class, WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .build()
        );

        WorkflowClient.start(workflow::postToAnyPlatform, postContent);
    }

    private ActivityOptions createActivityOptions() {
        return ActivityOptions.newBuilder()
                .setStartToCloseTimeout(ACTIVITY_TIMEOUT)
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(MAX_ATTEMPTS)
                        .build())
                .build();
    }
}
