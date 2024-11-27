package dev.gruff.dickens;

import dev.gruff.dickens.workflow.SocialMediaActivitiesImpl;
import dev.gruff.dickens.workflow.SocialMediaWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.time.Duration;

public class TemporalWorker {
    public static void main(String[] args) {
        // Get Temporal server address from the environment or use a default value
        String temporalServiceAddress = System.getenv("TEMPORAL_SERVICE_ADDRESS");

        System.out.println("Temporal service address: " + temporalServiceAddress);
        if (temporalServiceAddress == null || temporalServiceAddress.isEmpty()) {
            temporalServiceAddress = "127.0.0.1:7233"; // Default to localhost
        }

        System.out.println("Temporal service address2: " + temporalServiceAddress);



        // Connect to Temporal service

        WorkflowServiceStubs service =
                WorkflowServiceStubs.newInstance(
                        WorkflowServiceStubsOptions.newBuilder().setTarget(temporalServiceAddress).build());

        System.out.println("service: " + service);

        service.connect(Duration.ofMinutes(1));


        System.out.println("connected");

        // Create a WorkflowClient
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Create a WorkerFactory
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Create a worker for the task queue and register workflows and activities
        Worker worker = factory.newWorker("SOCIAL_MEDIA_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(SocialMediaWorkflowImpl.class);
        worker.registerActivitiesImplementations(new SocialMediaActivitiesImpl());

        // Start the worker
        factory.start();

        System.out.println("Worker started and connected to Temporal at " + temporalServiceAddress);
    }
}
