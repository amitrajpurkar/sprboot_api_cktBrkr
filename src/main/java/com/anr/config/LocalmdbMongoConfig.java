package com.anr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * this is where we configure the different mongoTemplates to be wired to correct
 * domain-model-classes wired to respective collections. the Repository classes are wired to the
 * domain-model-classes which then wired to these mongoTemplates to connect to the right database.
 * Note that both Model classes and respective Repository classes should be in same base package as
 * mentioned here
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.anr.localmdb", mongoTemplateRef = LocalmdbMongoConfig.MONGO_TEMPLATE)
public class LocalmdbMongoConfig {

    public static final String MONGO_TEMPLATE = "localmdbMongoTemplate";

}
