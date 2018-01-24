package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Created by roger.wang on 2016/3/10.
 */
public class SimpleEventCounterBolt extends BaseRichBolt {
    String redisKey="simple.event.counter";
    String processName=ManagementFactory.getRuntimeMXBean().getName();
    OutputCollector _collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this._collector=outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
//        Integer tick=tuple.getIntegerByField("tick");
        Integer tick=TickBean.class.cast(tuple.getValueByField("tick")).getTick();
        _collector.ack(tuple);
        _collector.emit(new Values(tick,redisKey,processName));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tick","redis.key","processId"));
    }
}
