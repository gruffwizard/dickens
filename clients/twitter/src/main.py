from temporalio.client import Client
from temporalio.worker import Worker
import asyncio
import signal
import sys
import os

# Define activities
class TwitterActivities:
    async def post_to_platform(self, platform: str, content: str):
        if platform.lower() == "twitter":
            print(f"Posting to Twitter: {content}")
            # Here you would implement actual Twitter API integration
            # using something like tweepy

async def register_worker(client: Client, worker_id: str):
    try:
        # Get handle to registry workflow
        handle = await client.get_workflow_handle('worker-registry')
        
        # Register this worker
        await handle.signal('registerWorker', worker_id, "Twitter Service")
        print(f"Registered worker: {worker_id}")
        return handle
    except Exception as e:
        print(f"Error registering worker: {e}")
        raise

async def deregister_worker(client: Client, worker_id: str):
    try:
        handle = await client.get_workflow_handle('worker-registry')
        await handle.signal('deregisterWorker', worker_id)
        print(f"Deregistered worker: {worker_id}")
    except Exception as e:
        print(f"Error deregistering worker: {e}")

async def main():
    try:
        # Create client connected to server at the given address
        client = await Client.connect(os.getenv('TEMPORAL_HOST', 'localhost:7233'))

        # Create worker for handling activity tasks
        worker = Worker(
            client,
            task_queue="SOCIAL_MEDIA_TASK_QUEUE",
            activities=[TwitterActivities()]
        )

        # Signal handler for graceful shutdown
        def signal_handler(sig, frame):
            print("\nShutting down...")
            asyncio.create_task(deregister_worker(client, "twitter"))
            sys.exit(0)

        signal.signal(signal.SIGINT, signal_handler)
        signal.signal(signal.SIGTERM, signal_handler)

        # Register the worker
        await register_worker(client, "twitter")

        # Start worker
        print("Starting worker...")
        await worker.run()

    except Exception as e:
        print(f"Error in main: {e}")
        sys.exit(1)

if __name__ == "__main__":
    asyncio.run(main()) 