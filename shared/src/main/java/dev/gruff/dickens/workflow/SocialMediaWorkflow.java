package dev.gruff.dickens.workflow;


import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SocialMediaWorkflow {
    @WorkflowMethod
    void postToAllPlatforms(String content);
}

