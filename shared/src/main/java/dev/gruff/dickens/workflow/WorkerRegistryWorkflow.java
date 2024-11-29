package dev.gruff.dickens.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;

import java.util.Map;

@WorkflowInterface
public interface WorkerRegistryWorkflow {
    @WorkflowMethod
    void run();

    @SignalMethod
    void registerWorker(String workerId, String targetServiceDescription);

    @SignalMethod
    void deregisterWorker(String workerId);

    @QueryMethod
    Map<String, String> getRegisteredWorkers();
}
