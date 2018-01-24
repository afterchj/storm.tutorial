package com.tpadsz.lab.storm.tutorial.starter.spout.builder;

import backtype.storm.spout.SchemeAsMultiScheme;
import storm.kafka.BrokerHosts;
import storm.kafka.ZkHosts;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;

/**
 * Created by roger.wang on 2016/3/22.
 */
public class TransactionalTridentKafkaSpoutBuilder extends AbstractKafkaTridentSpoutBuilder<TransactionalTridentKafkaSpout> {
    public TransactionalTridentKafkaSpoutBuilder(KafkaConfig kafkaConfig){
        super(kafkaConfig);
    }

    @Override
    public TransactionalTridentKafkaSpout build() {
        BrokerHosts brokerHosts = new ZkHosts(getKafkaConfig().getZookeeperHosts(),getKafkaConfig().getZookeeperRoot());
        TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(brokerHosts,getKafkaConfig().getKafkaTopic(), getKafkaConfig().getSpoutId());
//        tridentKafkaConfig.forceFromStart=false;
        tridentKafkaConfig.scheme = new SchemeAsMultiScheme(getKafkaConfig().getScheme());
        tridentKafkaConfig.startOffsetTime = getKafkaConfig().getStartOffsetTime();
        TransactionalTridentKafkaSpout spout=new TransactionalTridentKafkaSpout(tridentKafkaConfig);
        return spout;
    }

}
