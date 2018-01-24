package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Values;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;
import org.apache.storm.redis.trident.state.RedisClusterState;
import org.apache.storm.redis.trident.state.RedisState;

import java.util.Arrays;
import java.util.List;

/**
 * Created by roger.wang on 2016/3/25.
 */
public class WordCounterRedisLookupMapper implements RedisLookupMapper {
    RedisDataTypeDescription type=new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING);
    @Override
    public List<Values> toTuple(ITuple input, Object value) {
        String v="0";
        if(value!=null && StringUtils.isNotBlank(value.toString())){
            v=value.toString();
        }
        return Arrays.asList(new Values[]{new Values(v)});
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word.counter"));
    }

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return type;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return "word.counter";
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        return null;
    }
}
