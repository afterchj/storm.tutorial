package com.tpadsz.lab.storm.tutorial.starter.state;

import backtype.storm.task.IMetricsContext;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

/**
 * Created by roger.wang on 2016/3/29.
 */
public class MongoDbWordCounterStateFactory implements StateFactory {

    MongoDbWordCounterState.Options options;
    public MongoDbWordCounterStateFactory(MongoDbWordCounterState.Options options) {
        this.options = options;
    }
    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        MongoDbWordCounterState state = new MongoDbWordCounterState(options);
        state.prepare();

        return state;
    }
}
