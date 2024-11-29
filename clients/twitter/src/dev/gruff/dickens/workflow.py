from dataclasses import dataclass
from temporalio import workflow, activity

@dataclass
class PostRequest:
    content: str

@activity.defn
class SocialMediaActivities:
    @activity.defn
    async def post_to_platform(self, platform: str, content: str):
        raise NotImplementedError

@workflow.defn
class WorkerRegistryWorkflow:
    @workflow.run
    async def run(self):
        raise NotImplementedError

    @workflow.signal
    async def register_worker(self, worker_id: str, description: str):
        raise NotImplementedError

    @workflow.signal
    async def deregister_worker(self, worker_id: str):
        raise NotImplementedError

    @workflow.query
    async def get_registered_workers(self) -> dict:
        raise NotImplementedError 