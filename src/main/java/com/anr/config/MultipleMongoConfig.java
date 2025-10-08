package com.anr.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

//import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
public class MultipleMongoConfig {

    private static final String MONGO_DB_URL = "localhost";
    private static final String MONGO_DB_NAME = "test";

    @Primary
    @Bean(name = LocalmdbMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate localdbMongoTemplate() {
        MongoClient mdbClient = mongoClient();

        return new MongoTemplate(mdbClient, "test");
    }

    @Bean
    public MongoClient mongoClient() {
        String localURI = "mongodb://localhost:27017/test?maxPoolSize=20&minPoolSize=3&appName=TestApp&maxIdleTimeMS=2000";
        ConnectionString connectionString = new ConnectionString(localURI);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
                .build();
        MongoClient mdbClient = MongoClients.create(mongoClientSettings);

        return mdbClient;
    }

    @Bean
    @Qualifier("embededmdb")
    public MongoTemplate embeddedDbMongoTemplate() {
        MongoClient mdbClient = mongoClient();

        return new MongoTemplate(mdbClient, "test");
    }

}
