package com.tpadsz.lab.storm.tutorial.starter.state;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.List;

/**
 * Created by roger.wang on 2016/4/1.
 */
public class MongoDbWordCountUpdater extends BaseStateUpdater<MongoDbWordCounterState> {
    @Override
    public void updateState(MongoDbWordCounterState state, List<TridentTuple> tuples, TridentCollector collector) {
        state.updateState(tuples, collector);
    }
}
