package com.tpadsz.lab.storm.tutorial.starter.state;

import backtype.storm.tuple.Values;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.State;
import storm.trident.state.ValueUpdater;
import storm.trident.state.map.IBackingMap;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * MongoDbWordCounter document sample:
 * {
 *     "_id":"wordCounter",
 *     "_class":"WordCounter",
 *     "count":666
 * }
 * Created by roger.wang on 2016/3/28.
 */
public class MongoDbWordCounterState<T> implements MapState<T>, IBackingMap<T>{
    private static final Logger LOG = LoggerFactory.getLogger(MongoDbWordCounterState.class);
    private  UpdateOptions upsertOption=new UpdateOptions();
    private MongoCollection<Document> collection;
    private MongoClient client;
    private Options options;


    // TODO close mongodb client. not sure if this method will be invoked. need further coding.
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(client!=null){
            client.close();
        }
    }

    @Override
    public void beginCommit(Long txid) {
        LOG.info(String.format("beginCommit/txid: %s", Long.toString(txid)));
    }

    @Override
    public void commit(Long txid) {
        LOG.info(String.format("commit/txid: %s", Long.toString(txid)));
    }

    public MongoDbWordCounterState(Options options){
        this.options=options;
    }

    @Override
    public List<T> multiGet(List<List<Object>> keys) {
        LOG.info(String.format("multiGet"));
        return null;
    }

    @Override
    public List<T> multiUpdate(List<List<Object>> list, List<ValueUpdater> list1) {
        LOG.info(String.format("multiUpdate"));
        return null;
    }

    @Override
    public void multiPut(List<List<Object>> keys, List<T> vals) {
        LOG.info(String.format("multiPut"));
    }

    public void updateState(List<TridentTuple> tuples, TridentCollector collector){
        LOG.info(String.format("updateState"));
        if(tuples!=null&&tuples.size()>0){
            int count=tuples.get(0).getIntegerByField("new.counter");
            collection.updateOne(Filters.and(Filters.eq("_id", "wordCounter"),Filters.eq("_class","WordCounter")), Updates.inc("count",count), upsertOption);
        }
    }

    public List<List<Values>> batchRetrieve(List<TridentTuple> tuples){
        LOG.info(String.format("batchRetrieve"));
        Iterable<Document> docs = collection.find(Filters.and(Filters.eq("_id", "wordCounter"), Filters.eq("_class", "WordCounter"))).projection(Projections.include("count")).limit(1);
        final List<Integer> counts= Lists.newLinkedList();
        docs.forEach(new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                counts.add(document.getInteger("count"));
            }
        });

        int count = counts.size()<1?0:counts.get(0);
        List<List<Values>> ret=new ArrayList<>(tuples.size());
        for(int i=0;i<tuples.size();i++){
            ret.add(Arrays.asList(new Values[]{new Values(count)}));
        }
        return ret;

    }

    protected void prepare(){
//        this.client=new MongoClient(options.getHosts(),options.getCollectionName());
        this.client=new MongoClient(options.getHosts());
        this.upsertOption.upsert(true);
        this.collection = client.getDatabase(options.getDbName()).getCollection(options.getCollectionName());
    }

    public static class Options implements Serializable{
        public List<ServerAddress> getHosts() {
            return hosts;
        }

        // TODO MongoDB CodecRegistry is not serializable. Consequently we cannot
        // use it in a Trident/Storm topology. Abstractions of operations on mongo documents
        // are needed.
//        public Options(){
//            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
//                    MongoClient.getDefaultCodecRegistry()
//            );
//            MongoClientOptions dbClientOpts = MongoClientOptions.builder()
//                    .codecRegistry(codecRegistry).build();
//            this.options=dbClientOpts;
//        }

        public Options setHosts(List<ServerAddress> hosts) {
            this.hosts = hosts;return this;
        }

        public MongoClientOptions getOptions() {
            return options;
        }

        public Options setOptions(MongoClientOptions options) {
            this.options = options;return this;
        }

        public String getDbName() {
            return dbName;
        }

        public Options setDbName(String dbName) {
            this.dbName = dbName;return this;
        }

        public String getUserName() {
            return userName;
        }

        public Options setUserName(String userName) {
            this.userName = userName;return this;
        }

        public String getPassword() {
            return password;
        }

        public Options setPassword(String password) {
            this.password = password;return this;
        }
        public String getCollectionName() {
            return collectionName;
        }

        public Options setCollectionName(String collectionName) {
            this.collectionName = collectionName;return  this;
        }
        List<ServerAddress> hosts;
        MongoClientOptions options;
        String dbName;
        String collectionName;
        String userName;
        String password;

        @Override
        public String toString() {
            return "Options{" +
                    "hosts=" + hosts +
                    ", options=" + options +
                    ", dbName='" + dbName + '\'' +
                    ", collectionName='" + collectionName + '\'' +
                    ", userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
