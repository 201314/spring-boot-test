package com.gitee.linzl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@EnableAutoCommons
public class MongoDBBootstrap {

    public static void main(String args[]) {
        SpringApplication app = new SpringApplication(MongoDBBootstrap.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Bean
    public GridFSBucket getGridFSBuckets() {
        MongoDatabase db = mongoDbFactory.getMongoDatabase();
        return GridFSBuckets.create(db);
    }
}