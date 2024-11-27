package dev.gruff.dickens.workflow;

public class SocialMediaActivitiesImpl implements SocialMediaActivities {
    @Override
    public void postToPlatform(String platform, String content) {
        System.out.println("Posting to " + platform + ": " + content);
    }
}
