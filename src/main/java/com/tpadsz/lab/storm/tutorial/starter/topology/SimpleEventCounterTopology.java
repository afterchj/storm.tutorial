package com.tpadsz.lab.storm.tutorial.starter.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import com.tpadsz.lab.storm.tutorial.starter.bolt.EventCounterRedisStoreMapper;
import com.tpadsz.lab.storm.tutorial.starter.bolt.SimpleEventCounterBolt;
import com.tpadsz.lab.storm.tutorial.starter.bolt.StormEventCounterRedisStoreMapper;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.serializer.SimpleKryoSerializer;
import com.tpadsz.lab.storm.tutorial.starter.spout.SimpleEventEmittingSpout;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

/**
 * Created by roger.wang on 2016/3/10.
 */
public class SimpleEventCounterTopology {
    private static TopologyBuilder builder = new TopologyBuilder();
    public static void main(String[] args){
        Config config = new Config();
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost(Constants.getConstants().getRedisHost()).setPort(Constants.getConstants().getRedisPort()).build();
        RedisStoreMapper storeMapper = new StormEventCounterRedisStoreMapper();
        RedisStoreBolt storeBolt = new RedisStoreBolt(poolConfig, storeMapper);

        builder.setSpout("eventEmitter",new SimpleEventEmittingSpout(),2);
        builder.setBolt("eventCounter",new SimpleEventCounterBolt(),2).shuffleGrouping("eventEmitter");
        builder.setBolt("redisStore",storeBolt,2).shuffleGrouping("eventCounter");

        if (args != null && args.length > 0) {
            try {
                Integer numWorkers=1;
                if(args.length>=2){
                    try{
                        numWorkers=Integer.parseInt(args[1]);
                    }catch (NumberFormatException e){

                    }
                }
                config.setNumWorkers(numWorkers);
                config.registerSerialization(TickBean.class, SimpleKryoSerializer.class);
                StormSubmitter.submitTopology(args[0], config,
                        builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("SimpleEventCounter", config, builder.createTopology());
        }
    }
}
