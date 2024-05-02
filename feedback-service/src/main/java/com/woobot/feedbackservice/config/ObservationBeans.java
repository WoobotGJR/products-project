package com.woobot.feedbackservice.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;

public class ObservationBeans {
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(
            ObservationRegistry observationRegistry
    ) {
        return clientSettingsBuilder -> clientSettingsBuilder
                .contextProvider(ContextProviderFactory.create(observationRegistry))
                .addCommandListener(new MongoObservationCommandListener(observationRegistry));
    }
}
