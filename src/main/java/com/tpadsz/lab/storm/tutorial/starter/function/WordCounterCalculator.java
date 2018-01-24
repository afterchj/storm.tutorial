package com.tpadsz.lab.storm.tutorial.starter.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by roger.wang on 2016/3/25.
 */
public class WordCounterCalculator extends BaseFunction {
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        Integer count=tuple.getIntegerByField("count");
        Integer wordCounter=tuple.getIntegerByField("word.counter");
        collector.emit(new Values(count+(wordCounter)));
    }
}
