package dev.gruff.dickens.workflow;

import io.temporal.workflow.Workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerRegistryWorkflowImpl implements WorkerRegistryWorkflow {
    private final Map<String, String> registeredWorkers = new ConcurrentHashMap<>();

    @Override
    public void run() {
        // Keep the workflow alive indefinitely
        Workflow.await(() -> false);
    }

    @Override
    public void registerWorker(String workerId, String targetServiceDescription) {
        System.out.println("registerWorker: " + workerId + " " + targetServiceDescription);
        registeredWorkers.put(workerId, targetServiceDescription);
        Workflow.getLogger(WorkerRegistryWorkflowImpl.class)
                .info("Registered worker: " + workerId + " for service: " + targetServiceDescription);
    }

    @Override
    public void deregisterWorker(String workerId) {
        System.out.println("deregisterWorker: " + workerId );
        registeredWorkers.remove(workerId);
        Workflow.getLogger(WorkerRegistryWorkflowImpl.class)
                .info("Deregistered worker: " + workerId);
    }

    @Override
    public Map<String, String> getRegisteredWorkers() {
        return new ConcurrentHashMap<>(registeredWorkers);
    }
}
