import { Connection, Client } from '@temporalio/client';
import { Worker, NativeConnection } from '@temporalio/worker';
import type { WorkflowClient } from '@temporalio/client';
import { Activities } from './interfaces';

class LinkedInActivities implements Activities {
  async postToPlatform(platform: string, content: string): Promise<void> {
    if (platform.toLowerCase() === 'linkedin') {
      console.log(`Posting to LinkedIn: ${content}`);
      // Here you would implement actual LinkedIn API integration
    }
  }
}

async function registerWorker(client: WorkflowClient, workerId: string): Promise<any> {
  try {
    const handle = await client.getHandle('worker-registry');
    await handle.signal('registerWorker', workerId, 'LinkedIn Service');
    console.log(`Registered worker: ${workerId}`);
    return handle;
  } catch (error) {
    console.error('Error registering worker:', error);
    throw error;
  }
}

async function deregisterWorker(client: WorkflowClient, workerId: string): Promise<void> {
  try {
    const handle = await client.getHandle('worker-registry');
    await handle.signal('deregisterWorker', workerId);
    console.log(`Deregistered worker: ${workerId}`);
  } catch (error) {
    console.error('Error deregistering worker:', error);
  }
}

async function run() {
  try {
    // Create Native Connection for the Worker
    const connection = await NativeConnection.connect({
      address: process.env.TEMPORAL_HOST || 'localhost:7233'
    });

    // Create Client Connection
    const clientConnection = await Connection.connect({
      address: process.env.TEMPORAL_HOST || 'localhost:7233'
    });
    
    const client = new Client({
      connection: clientConnection
    });

    // Create worker
    const worker = await Worker.create({
      connection,
      taskQueue: 'SOCIAL_MEDIA_TASK_QUEUE',
      activities: new LinkedInActivities(),
    });

    // Handle graceful shutdown
    process.on('SIGINT', async () => {
      console.log('\nShutting down...');
      await deregisterWorker(client.workflow, 'linkedin');
      process.exit(0);
    });

    process.on('SIGTERM', async () => {
      console.log('\nShutting down...');
      await deregisterWorker(client.workflow, 'linkedin');
      process.exit(0);
    });

    // Register the worker
    await registerWorker(client.workflow, 'linkedin');

    // Start worker
    console.log('Starting worker...');
    await worker.run();
  } catch (error) {
    console.error('Error running worker:', error);
    process.exit(1);
  }
}

run().catch((err) => {
  console.error('Fatal error:', err);
  process.exit(1);
}); 