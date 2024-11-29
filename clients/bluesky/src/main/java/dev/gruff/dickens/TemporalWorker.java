package dev.gruff.dickens;

import dev.gruff.dickens.workflow.SocialMediaActivitiesImpl;
import dev.gruff.dickens.workflow.SocialMediaWorkflowImpl;
import dev.gruff.dickens.workflow.WorkerRegistryWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class TemporalWorker {
    private static final Logger log = LoggerFactory.getLogger(TemporalWorker.class);

    public static void main(String[] args) {


        ///
        // Get Temporal server address from the environment or use a default value
        String temporalServiceAddress = System.getenv("TEMPORAL_SERVICE_ADDRESS");

        log.info("Temporal service address: {}", temporalServiceAddress);
        if (temporalServiceAddress == null || temporalServiceAddress.isEmpty()) {
            temporalServiceAddress = "127.0.0.1:7233"; // Default to localhost
        }

        log.info("Temporal service address2: {}", temporalServiceAddress);



        // Connect to Temporal service

        WorkflowServiceStubs service =
                WorkflowServiceStubs.newServiceStubs(
                        WorkflowServiceStubsOptions.newBuilder().setTarget(temporalServiceAddress).build());

        log.info("service: {}", service);

        service.connect(Duration.ofMinutes(1));


        log.info("connected");

        // Create a WorkflowClient
        WorkflowClient client = WorkflowClient.newInstance(service);

        String registryWorkflowId = "worker-registry";

        // Wait until the registry workflow is started
        waitForRegistryWorkflow(client, registryWorkflowId);

        // Once the workflow is running, register this worker
        WorkerRegistryWorkflow registryWorkflow = client.newWorkflowStub(
                WorkerRegistryWorkflow.class,
                registryWorkflowId
        );

        registryWorkflow.registerWorker("bluesky", "Bluesky");

        log.info("registered worker");


        // Simulate worker running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> registryWorkflow.deregisterWorker("bluesky")));


        // Create a WorkerFactory
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Create a worker for the task queue and register workflows and activities
        Worker worker = factory.newWorker("SOCIAL_MEDIA_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(SocialMediaWorkflowImpl.class);
        worker.registerActivitiesImplementations(new SocialMediaActivitiesImpl());

        // Start the worker
        factory.start();

        log.info("Worker started and connected to Temporal at {}", temporalServiceAddress);
    }

    private static void waitForRegistryWorkflow(WorkflowClient client, String registryWorkflowId) {
        while (true) {
            try {
                // Get a stub for the workflow
                WorkflowStub stub = client.newUntypedWorkflowStub(registryWorkflowId);

                // Query the workflow's status
                stub.getResult(Void.class); // Will throw if the workflow isn't running
                log.info("Worker registry workflow is running.");
                return; // Exit loop if workflow is running
            } catch (Exception e) {
                log.info("Waiting for worker registry workflow to start...");
                try {
                    Thread.sleep(1000); // Wait before retrying
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
