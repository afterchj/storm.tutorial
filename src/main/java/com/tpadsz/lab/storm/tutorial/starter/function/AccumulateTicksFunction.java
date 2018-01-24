package com.tpadsz.lab.storm.tutorial.starter.function;

import backtype.storm.tuple.Values;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class AccumulateTicksFunction extends BaseFunction {
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        Integer tick=tuple.getIntegerByField("tick");
//        Integer oldCount=Integer.parseInt(tuple.getValueByField("old.count").toString());
        Integer oldCount= TickBean.class.cast(tuple.getValueByField("old.count")).getTick();
        collector.emit(new Values(tick+oldCount, Constants.getConstants().getRedisKeyTridentEventCounter(), ManagementFactory.getRuntimeMXBean().getName()));
    }
}
