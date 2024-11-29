package dev.gruff.dickens.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialMediaActivitiesImpl implements SocialMediaActivities {
    private static final Logger log = LoggerFactory.getLogger(SocialMediaActivitiesImpl.class);

    @Override
    public void postToPlatform(String platform, String content) {
        log.info("Posting to {}: {}", platform, content);
    }
}
