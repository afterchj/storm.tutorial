package com.tpadsz.lab.storm.tutorial.starter.spout.builder;

/**
 * Created by roger.wang on 2016/3/22.
 */
abstract public class AbstractKafkaTridentSpoutBuilder<T> implements IKafkaTridentSpoutBuilder<T> {
    private KafkaConfig kConfig;
    public AbstractKafkaTridentSpoutBuilder(KafkaConfig kConfig){
        this.kConfig=kConfig;
    }
    protected KafkaConfig getKafkaConfig(){
        return kConfig;
    }

}

