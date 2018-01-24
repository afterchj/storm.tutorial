package com.tpadsz.lab.storm.tutorial.starter.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Created by roger.wang on 2016/3/10.
 */
public class SimpleEventEmittingSpout extends BaseRichSpout {
    private SpoutOutputCollector _collector;
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tick"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this._collector=spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
//        _collector.emit(new Values(new Integer(1)));
        TickBean bean=new TickBean();
        bean.setTick(1);
        bean.setProcessId(ManagementFactory.getRuntimeMXBean().getName());
        _collector.emit(new Values(bean));
    }

}
