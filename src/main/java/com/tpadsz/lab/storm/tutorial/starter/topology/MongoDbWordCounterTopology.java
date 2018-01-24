package com.tpadsz.lab.storm.tutorial.starter.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Fields;
import com.mongodb.ServerAddress;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.function.WordCounterCalculator;
import com.tpadsz.lab.storm.tutorial.starter.function.WordCounterFunction;
import com.tpadsz.lab.storm.tutorial.starter.serializer.SimpleKryoSerializer;
import com.tpadsz.lab.storm.tutorial.starter.spout.builder.KafkaConfig;
import com.tpadsz.lab.storm.tutorial.starter.state.MongoDbWordCountUpdater;
import com.tpadsz.lab.storm.tutorial.starter.state.MongoDbWordCounterQuerier;
import com.tpadsz.lab.storm.tutorial.starter.state.MongoDbWordCounterState;
import com.tpadsz.lab.storm.tutorial.starter.state.MongoDbWordCounterStateFactory;
import com.tpadsz.lab.storm.tutorial.starter.utils.Arguments;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import com.tpadsz.lab.storm.tutorial.starter.utils.StringScheme;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.trident.state.RedisState;
import storm.kafka.ZkHosts;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by roger.wang on 2016/4/6.
 */
public class MongoDbWordCounterTopology {
    public static void main(String[] argv) throws UnknownHostException {
        Arguments arg = new Arguments(argv);
        Config config = new Config();
        Constants constants = Constants.getConstants();
        TridentTopology topology = new TridentTopology();
        KafkaConfig kConfig = KafkaConfig.makeConfig(constants.getKafkaZookeeperConnectString(), constants.getKafkaZookeeperRoot(), constants.getKafkaTopic(), constants.getKafkaTridentSpoutId(), constants.getKafkaTridentForceFromStart(), constants.getKafkaTridentStartOffsetTime(), new StringScheme());
        TridentKafkaConfig tkConfig = new TridentKafkaConfig(new ZkHosts(kConfig.getZookeeperHosts(), kConfig.getZookeeperRoot()), kConfig.getKafkaTopic(), kConfig.getSpoutId());
        tkConfig.scheme = new SchemeAsMultiScheme(kConfig.getScheme());

        // TODO substitute RedisState.Factory for MongoDbWordCounterStateFactory


        MongoDbWordCounterState.Options dbOpt=new MongoDbWordCounterState.Options()
                .setHosts(Arrays.asList(new ServerAddress[]{new ServerAddress(constants.getMongodbTridentHost(), constants.getMongodbTridentPort())}))
                .setDbName(constants.getMongodbTridentDbName())
                .setCollectionName(constants.getMongodbTridentDbCollection());

        System.out.printf("MongoDB Options:\n%s\n",dbOpt.toString());

        StateFactory stateFactory = new RedisState.Factory(new JedisPoolConfig.Builder().setHost(constants.getRedisHost()).build());
        StateFactory mongoDbWordCounterStateFactory = new MongoDbWordCounterStateFactory(
                dbOpt
        );

        Stream kafkaStream = topology.newStream(constants.getKafkaTridentSpoutId(), new TransactionalTridentKafkaSpout(tkConfig));

        // TODO substitute redisWordCounterState for MongoDbWordCounterState
        TridentState redisWordCounterState = topology.newStaticState(stateFactory);
        TridentState mongoDbWordCounterState = topology.newStaticState(mongoDbWordCounterStateFactory);

        kafkaStream.each(new Fields("string"), new WordCounterFunction(), new Fields("count"))
                // TODO substitute RedisStateQuerier for MongoDbWordCounterQuerier
//                .stateQuery(redisWordCounterState, new RedisStateQuerier(new WordCounterRedisLookupMapper()), new Fields("word.counter"))
                .stateQuery(mongoDbWordCounterState, new MongoDbWordCounterQuerier(), new Fields("word.counter"))

                .each(new Fields("count", "word.counter"), new WordCounterCalculator(), new Fields("new.counter"))
                // TODO substitute RedisStateUpdater for MongoDbWordCounterQuerier
//                .partitionPersist(stateFactory, new Fields("new.counter"), new RedisStateUpdater(new WordCounterRedisStoreMapper()))
                .partitionPersist(mongoDbWordCounterStateFactory, new Fields("new.counter"), new MongoDbWordCountUpdater())
                .parallelismHint(2);


        if (arg.getLocal() == false) {
            try {
                config.put(Config.NIMBUS_HOST, Constants.getConstants().getNimbusHost());
                config.put(Config.NIMBUS_THRIFT_PORT, Constants.getConstants().getNimbusPort());
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
            config.put(Config.NIMBUS_THRIFT_PORT, 6627);
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(arg.getTopologyName(), config, topology.build());
        }
    }
}
