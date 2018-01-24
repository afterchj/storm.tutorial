package com.tpadsz.lab.storm.tutorial.starter.spout.builder;

import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.Scheme;

/**
 * Created by roger.wang on 2016/3/22.
 */
public class KafkaConfig {
    String zookeeperHosts;
    String zookeeperRoot;
    String kafkaTopic;
    String spoutId;
    Boolean forceFromStart;
    Integer startOffsetTime;
    Scheme scheme;

    private KafkaConfig() {

    }

    public static KafkaConfig makeConfig(String zookeeperHosts,
                                         String zookeeperRoot,
                                         String kafkaTopic,
                                         String spoutId,
                                         Boolean forceFromStart,
                                         Integer startOffsetTime,
                                         Scheme scheme) {
        return new KafkaConfig().
                setZookeeperHosts(zookeeperHosts).
                setZookeeperRoot(zookeeperRoot).
                setKafkaTopic(kafkaTopic).
                setSpoutId(spoutId).
                setForceFromStart(forceFromStart).
                setStartOffsetTime(startOffsetTime).
                setScheme(scheme);
    }

    public String getZookeeperHosts() {
        return zookeeperHosts;
    }

    private KafkaConfig setZookeeperHosts(String zookeeperHosts) {
        this.zookeeperHosts = zookeeperHosts;
        return this;
    }

    public String getZookeeperRoot() {
        return zookeeperRoot;
    }

    private KafkaConfig setZookeeperRoot(String zookeeperRoot) {
        this.zookeeperRoot = zookeeperRoot;
        return this;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    private KafkaConfig setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
        return this;
    }

    public String getSpoutId() {
        return spoutId;
    }

    private KafkaConfig setSpoutId(String spoutId) {
        this.spoutId = spoutId;
        return this;
    }

    public Boolean getForceFromStart() {
        return forceFromStart;
    }

    private KafkaConfig setForceFromStart(Boolean forceFromStart) {
        this.forceFromStart = forceFromStart;
        return this;
    }

    public Integer getStartOffsetTime() {
        return startOffsetTime;
    }

    private KafkaConfig setStartOffsetTime(Integer startOffsetTime) {
        this.startOffsetTime = startOffsetTime;
        return this;
    }

    public Scheme getScheme() {
        return scheme;
    }

    private KafkaConfig setScheme(Scheme scheme) {
        this.scheme = scheme;
        return this;
    }
}
