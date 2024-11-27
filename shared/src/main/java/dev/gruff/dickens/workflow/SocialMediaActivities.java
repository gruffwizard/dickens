package dev.gruff.dickens.workflow;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface SocialMediaActivities {
    void postToPlatform(String platform, String content);
}

