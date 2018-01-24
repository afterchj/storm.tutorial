package com.tpadsz.lab.storm.tutorial.starter.state;

import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.tuple.TridentTuple;

import java.util.List;

/**
 * Created by roger.wang on 2016/3/30.
 */
public class MongoDbWordCounterQuerier extends BaseQueryFunction<MongoDbWordCounterState, List<Values>> {
    @Override
    public List<List<Values>> batchRetrieve(MongoDbWordCounterState state, List<TridentTuple> args) {

        System.out.println("MongoDbWordCounterQuerier.batchRetrieve");
        // NOTE: batchRetrieve must return exactly the same numbers of results as the length of 'args' parameter of this function.
        return state.batchRetrieve(args);
    }

    @Override
    public void execute(TridentTuple tuple, List<Values> result, TridentCollector collector) {
        System.out.printf("MongoDbWordCounterQuerier.execute\nResult size: %d", result.size());

        for(Values v:result){
            collector.emit(v);
            // NOTE: here we simply return the first value sine the word count in mongodb is unique. Maybe there is a better way to do
            // this query, but we aim at illustrating the process of MongoDbState query and update. Thus this defect isn't actually
            // a problem.
            return;
        }
    }
}
