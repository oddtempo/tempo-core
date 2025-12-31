package com.tempo.core.shared.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service to clean up the event_publication table.
 * Helps prevent the database from bloating by removing old completed events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventCleanupService {

    private final CompletedEventPublications completedEventPublications;

    /**
     * Delete completed events older than 7 days.
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupCompletedEvents() {
        log.info("Starting cleanup of completed event publications...");

        Duration retentionPeriod = Duration.ofDays(7);
        completedEventPublications.deletePublicationsOlderThan(retentionPeriod);

        log.info("Finished cleanup of completed event publications.");
    }
}
