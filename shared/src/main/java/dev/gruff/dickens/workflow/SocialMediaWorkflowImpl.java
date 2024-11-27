package dev.gruff.dickens.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SocialMediaWorkflowImpl implements SocialMediaWorkflow {
    private final SocialMediaActivities activities =

            Workflow.newActivityStub(
                    SocialMediaActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(10)) // Activity execution timeout
                            .setRetryOptions(
                                    io.temporal.common.RetryOptions.newBuilder()
                                            .setMaximumAttempts(3) // Retry up to 3 times
                                            .setInitialInterval(Duration.ofSeconds(2)) // Time between retries
                                            .build()
                            )
                            .build()
            );

    @Override
    public void postToAllPlatforms(String content) {

        System.out.println("Posting to all platforms: " + content);
        activities.postToPlatform("Twitter", content);
        activities.postToPlatform("LinkedIn", content);
        activities.postToPlatform("BlueSky", content);
    }
}
