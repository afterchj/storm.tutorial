package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Values;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class EventCounterRedisLookupMapper implements RedisLookupMapper{
    RedisDataTypeDescription type=new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING);
    @Override
    public List<Values> toTuple(ITuple iTuple, Object o) {
//        System.out.println("------------------------------------");
//        System.out.format("toTuple/Object o: %s\n",o==null?"null":o.toString());
//        System.out.println("------------------------------------");
//        return Arrays.asList(new Values[]{new Values(o==null?0:o.toString())});
        TickBean bean=new TickBean();
        bean.setProcessId(ManagementFactory.getRuntimeMXBean().getName());
        bean.setTick(o==null?0:Integer.parseInt(o.toString()));
        return Arrays.asList(new Values[]{new Values(bean)});
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("old.count"));
    }

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return type;
    }

    @Override
    public String getKeyFromTuple(ITuple iTuple) {
        return Constants.getConstants().getRedisKeyTridentEventCounter();
//        return iTuple.getStringByField("redis.key");
    }

    @Override
    public String getValueFromTuple(ITuple iTuple) {
        return null;
    }
}
