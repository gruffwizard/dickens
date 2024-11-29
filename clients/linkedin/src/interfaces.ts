import { WorkflowHandle } from '@temporalio/client';

export interface Activities {
  postToPlatform(platform: string, content: string): Promise<void>;
}

export interface WorkerRegistryWorkflow {
  run(): Promise<void>;
  signals: {
    registerWorker(workerId: string, description: string): void;
    deregisterWorker(workerId: string): void;
  };
  queries: {
    getRegisteredWorkers(): Promise<Map<string, string>>;
  };
} 