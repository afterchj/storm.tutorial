package com.tpadsz.lab.storm.tutorial.starter.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by roger.wang on 2016/3/24.
 */
public class WordCounterFunction extends BaseFunction {
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String str = tuple.getStringByField("string");
        if(str==null || "".equals(str)){
            collector.emit(new Values(0));
        }else{
            collector.emit(new Values(str.split("").length));
        }
    }
}
