package com.tpadsz.lab.storm.tutorial.starter.spout;

import backtype.storm.coordination.IBatchBolt;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.transactional.partitioned.IPartitionedTransactionalSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import storm.trident.testing.FixedBatchSpout;

import java.util.*;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class TridentEventEmittingSpout implements IBatchSpout{
    private Map<Long,List<Object>> batches=new HashMap<>();
    private Random rnd=new Random();
    @Override
    public void open(Map map, TopologyContext topologyContext) {

    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        if(batches.get(l)==null) {
            List<Object> ticks = new ArrayList<>(1);
            ticks.add(rnd.nextInt(10));
            batches.put(l,ticks);
        }

        tridentCollector.emit(batches.get(l));

    }

    @Override
    public void ack(long l) {
        batches.remove(l);
    }

    @Override
    public void close() {

    }

    @Override
    public Map getComponentConfiguration() {
        return null;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("tick");
    }
}