package com.tpadsz.lab.storm.tutorial.starter.spout.builder;

import backtype.storm.transactional.partitioned.IPartitionedTransactionalSpout;
import storm.kafka.Partition;
import storm.kafka.trident.GlobalPartitionInformation;
import storm.trident.spout.IPartitionedTridentSpout;

import java.util.Map;

/**
 * Created by roger.wang on 2016/3/22.
 */
public interface IKafkaTridentSpoutBuilder<T> {
    abstract public T build();
}
