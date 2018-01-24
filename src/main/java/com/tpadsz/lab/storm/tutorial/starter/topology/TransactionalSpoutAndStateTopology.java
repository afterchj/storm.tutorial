package com.tpadsz.lab.storm.tutorial.starter.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Fields;
import com.tpadsz.lab.storm.tutorial.starter.bolt.WordCounterRedisLookupMapper;
import com.tpadsz.lab.storm.tutorial.starter.bolt.WordCounterRedisStoreMapper;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.function.WordCounterCalculator;
import com.tpadsz.lab.storm.tutorial.starter.function.WordCounterFunction;
import com.tpadsz.lab.storm.tutorial.starter.serializer.SimpleKryoSerializer;
import com.tpadsz.lab.storm.tutorial.starter.spout.builder.KafkaConfig;
import com.tpadsz.lab.storm.tutorial.starter.utils.Arguments;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import com.tpadsz.lab.storm.tutorial.starter.utils.StringScheme;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.trident.state.RedisState;
import org.apache.storm.redis.trident.state.RedisStateQuerier;
import org.apache.storm.redis.trident.state.RedisStateUpdater;
import storm.kafka.ZkHosts;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;

import java.net.UnknownHostException;

/**
 * Created by roger.wang on 2016/3/22.
 */
public class TransactionalSpoutAndStateTopology {
    public static void main(String[] argv) throws UnknownHostException {
        Arguments arg=new Arguments(argv);
        Config config=new Config();
        Constants constants=Constants.getConstants();
        TridentTopology topology = new TridentTopology();
        KafkaConfig kConfig=KafkaConfig.makeConfig(constants.getKafkaZookeeperConnectString(),constants.getKafkaZookeeperRoot(),constants.getKafkaTopic(),constants.getKafkaTridentSpoutId(),constants.getKafkaTridentForceFromStart(),constants.getKafkaTridentStartOffsetTime(),new StringScheme());
        TridentKafkaConfig tkConfig=new TridentKafkaConfig(new ZkHosts(kConfig.getZookeeperHosts(),kConfig.getZookeeperRoot()),kConfig.getKafkaTopic(),kConfig.getSpoutId());
        tkConfig.scheme=new SchemeAsMultiScheme(kConfig.getScheme());
        StateFactory stateFactory = new RedisState.Factory(new JedisPoolConfig.Builder().setHost(constants.getRedisHost()).build());
        Stream kafkaStream = topology.newStream(constants.getKafkaTridentSpoutId(), new TransactionalTridentKafkaSpout(tkConfig));
        TridentState redisWordCounterState = topology.newStaticState(stateFactory);
        kafkaStream.each(new Fields("string"),new WordCounterFunction(),new Fields("count"))
                .stateQuery(redisWordCounterState,new RedisStateQuerier(new WordCounterRedisLookupMapper()),new Fields("word.counter"))
                .each(new Fields("count","word.counter"),new WordCounterCalculator(),new Fields("new.counter"))
                .partitionPersist(stateFactory, new Fields("new.counter"),new RedisStateUpdater(new WordCounterRedisStoreMapper()))
                .parallelismHint(2);


        if (arg.getLocal()==false) {
            try {
                config.put(Config.NIMBUS_HOST, Constants.getConstants().getNimbusHost());
                config.put(Config.NIMBUS_THRIFT_PORT,Constants.getConstants().getNimbusPort());
                config.registerSerialization(TickBean.class, SimpleKryoSerializer.class);
                config.setNumWorkers(arg.getNumberOfWorkers());
                System.out.format("Prepare to submit topology %s to host %s.\n", arg.getTopologyName(), config.get(Config.NIMBUS_HOST));
                StormSubmitter.submitTopology(arg.getTopologyName(), config,
                        topology.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            config.setDebug(true);
            config.put(Config.NIMBUS_HOST, "192.168.200.160");
            config.put(Config.NIMBUS_THRIFT_PORT,6627);
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(arg.getTopologyName(), config,topology.build());
        }



    }
}
