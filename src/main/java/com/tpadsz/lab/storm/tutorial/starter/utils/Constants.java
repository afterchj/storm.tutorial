package com.tpadsz.lab.storm.tutorial.starter.utils;

import java.util.Properties;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class Constants {
    public String getRedisHost() {
        return redisHost;
    }

    public Integer getRedisPort() {
        return redisPort;
    }

    public String getNimbusHost() {
        return nimbusHost;
    }

    public Integer getNimbusPort() {
        return nimbusPort;
    }

    public String getRedisKeyTridentEventCounter() {
        return redisKeyTridentEventCounter;
    }

    public String getRedisKeyTridentEventCounterTxid() {
        return redisKeyTridentEventCounterTxid;
    }

    public String getKafkaTridentSpoutId() {
        return kafkaTridentSpoutId;
    }

    public Boolean getKafkaTridentForceFromStart() {
        return kafkaTridentForceFromStart;
    }

    public Integer getKafkaTridentStartOffsetTime() {
        return kafkaTridentStartOffsetTime;
    }

    public String getMongodbTridentHost() {
        return mongodbTridentHost;
    }

    public Integer getMongodbTridentPort() {
        return mongodbTridentPort;
    }

    public String getMongodbTridentDbName() {
        return mongodbTridentDbName;
    }

    public String getMongodbTridentDbCollection() {
        return mongodbTridentDbCollection;
    }

    private String kafkaZookeeperConnectString;
    private String kafkaZookeeperRoot;
    private String kafkaTopic;
    private String kafkaTridentSpoutId;
    private Boolean kafkaTridentForceFromStart;
    private Integer kafkaTridentStartOffsetTime;
    private String redisHost;
    private Integer redisPort;
    private String redisKeyTridentEventCounter;
    private String redisKeyTridentEventCounterTxid;
    private String mongodbTridentHost;
    private Integer mongodbTridentPort;
    private String mongodbTridentDbName;
    private String mongodbTridentDbCollection;
    private String nimbusHost;
    private Integer nimbusPort;
    private Constants(){
        try {
            props=new Properties();
            props.load(
                    Constants.class.getResourceAsStream("/storm.tutorial.properties")
            );
            redisHost = props.getProperty("redis.host");
            redisPort = Integer.parseInt(props.getProperty("redis.port", "6379"));
            redisKeyTridentEventCounter = props.getProperty("redis.key.trident.event.counter");
            redisKeyTridentEventCounterTxid = props.getProperty("redis.key.trident.event.counter.txid");
            nimbusHost = props.getProperty("nimbus.host");
            nimbusPort = Integer.parseInt(props.getProperty("nimbus.port", "6627"));
            kafkaZookeeperConnectString = props.getProperty("kafka.zookeeper.connect.string");
            kafkaZookeeperRoot = props.getProperty("kafka.zookeeper.root");
            kafkaTopic = props.getProperty("kafka.topic");
            kafkaTridentForceFromStart = Boolean.parseBoolean(props.getProperty("kafka.trident.force.from.start"));
            kafkaTridentSpoutId = props.getProperty("kafka.trident.spout.id");
            kafkaTridentStartOffsetTime = Integer.parseInt(props.getProperty("kafka.trident.start.offset.time"));
            mongodbTridentHost = props.getProperty("mongodb.trident.host");
            mongodbTridentPort = Integer.parseInt(props.getProperty("mongodb.trident.port", "27017"));
            mongodbTridentDbName = props.getProperty("mongodb.trident.db.name");
            mongodbTridentDbCollection = props.getProperty("mongodb.trident.db.collection");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String getKafkaZookeeperConnectString() {
        return kafkaZookeeperConnectString;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }


    private static Properties props;

    private static Constants singlton;
    private static byte[] lock=new byte[0];

    public String getKafkaZookeeperRoot() {
        return kafkaZookeeperRoot;
    }

    public static Constants getConstants(){
        if(singlton!=null){
            return singlton;
        }else{
            synchronized (lock){
                singlton=new Constants();
                return singlton;
            }
        }
    }



}
