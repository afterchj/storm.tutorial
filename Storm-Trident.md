# Storm-Trident用法总结

## 概述
* Storm Trident中的核心数据模型就是“Stream”，也就是说，Storm Trident处理的是Stream，但是实际上Stream是被成批处理的，Stream被切分成一个个的Batch分布到集群中，所有应用在Stream上的函数最终会应用到每个节点的Batch中，实现并行计算。

## Trident的节点类型
* Spout节点，数据源节点，连接数据存储工具（数据库或者消息中间件），从数据源中批量读取数据
* State节点，数据存储节点，连接数据存储工具（数据库或者消息中间件），提供增删改查功能
* Query节点，数据查询操作节点，连接数据存储节点，主要实现数据的查询
* Persist节点，数据存储操作节点，连接数据存储节点，主要实现数据的存储
* 操作与处理节点，数据的操作处理节点，执行数据的各种具体操作
* 



## Trident的操作类型
* Apply Locally:本地操作，所有操作应用在本地节点数据上，不会产生网络传输     
* Repartitioning:数据流重定向，单纯的改变数据流向，不会改变数据内容，这部分会有网络传输
* Aggragation:聚合操作，会有网络传输
* Grouped streams：分组操作
* Merge和Join：合并，连接操作

### Apply Locally:本地操作
#### Function:函数操作
* 函数的作用是接收一个tuple(需指定接收tuple的哪个字段)，输出0个或多个tuples。输出的新字段值会被追加到原始输入tuple的后面，如果一个function不输出tuple，那就意味这这个tuple被过滤掉了。
* 定义一个Function
> public class MyFunction extends BaseFunction {    
> &emsp;&emsp;@Override     
> &emsp;&emsp;public void execute(TridentTuple tuple, TridentCollector collector) {    
> &emsp;&emsp;&emsp;&emsp;for ( int i = 0; i < tuple.getInteger(0); i++) {      
> &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;collector.emit(new Values(i));  
> &emsp;&emsp;&emsp;&emsp;}    
> &emsp;&emsp;}     
> }     
* 对stream进行函数操作
> mystream.each(new Fields("a"), new MyFunction(), new Fields("b")));

#### Filter:过滤操作
* Filters很简单，接收一个tuple并决定是否保留这个tuple。
* 定义一个Filter
> public class MyFilter extends BaseFilter {        
> &emsp;&emsp;public boolean isKeep(TridentTuple tuple) {       
> &emsp;&emsp;&emsp;&emsp;return tuple.getInteger(0) == 1;   
> &emsp;&emsp;}     
> }    
* 对stream进行过滤操作
> mystream.each(new Fields("a"), new MyFilter());

#### Projection:投影操作
* 投影操作作用是仅保留Stream指定字段的数据。
* 对stream进行投影操作
> mystream.project(new Fields("c"))

#### Grouped:分组操作
* GroupBy操作是根据特定的字段对流进行重定向的，还有，在一个分区内部，每个相同字段的tuple也会被Group到一起，下面这幅图描述了这个场景：如果你在grouped Stream上面运行aggregators，聚合操作会运行在每个Group中而不是整个Batch。persistentAggregate也能运行在GroupedSteam上，不过结果会被保存在MapState中，其中的key便是分组的字段。当然，aggregators在Grouped Streams上也可以串联。

### Aggragation聚合操作
* Trident有aggregate和 persistentAggregate方法来做聚合操作。aggregate是独立的运行在Stream的每个Batch上的，而persistentAggregate则是运行在Stream的所有Batch上并把运算结果存储在state source中。运行aggregate方法做全局聚合。当你用到ReducerAggregator或Aggregator时，Stream首先被重定向到一个分区中，然后其中的聚合函数便在这个分区上运行。当你用到CombinerAggregator时，Trident会首先在每个分区上做局部聚合，然后把局部聚合后的结果重定向到一个分区，因此使用CombinerAggregator会更高效，可能的话我们需要优先考虑使用它。
* TridentAPI提供了三个聚合器的接口：CombinerAggregator, ReducerAggregator, Aggregator.

* CombinerAggregator接口只返回一个tuple，并且这个tuple也只包含一个field。init方法会先执行，它负责预处理每一个接收到的tuple，然后再执行combine函数来计算收到的tuples直到最后一个tuple到达，当所有tuple处理完时，CombinerAggregator会发射zero函数的输出。       

    * 定义一个CombinerAggregator
    > public class CombinerCount implements CombinerAggregator<Integer>{        
    > &emsp;&emsp;@Override     
    > &emsp;&emsp;public Integer init(TridentTuple tuple) {     
    > &emsp;&emsp;&emsp;&emsp;return 1;     
    > &emsp;&emsp;}     
    > &emsp;&emsp;@Override     
    > &emsp;&emsp;public Integer combine(Integer val1, Integer val2) {      
    > &emsp;&emsp;&emsp;&emsp;return val1 + val2;       
    > &emsp;&emsp;}     
    > &emsp;&emsp;@Override     
    > &emsp;&emsp;public Integer zero() {       
    > &emsp;&emsp;&emsp;&emsp;return 0;     
    > &emsp;&emsp;}     
    > }  

    * 对stream进行聚合操作      
    > mystream.aggregate(new CombinerCount(), new Fields("c"));
    
* ReducerAggregator通过init方法提供一个初始值，然后为每个输入的tuple迭代这个值，最后生产处一个唯一的tuple输出。

    * 定义一个ReducerAggregator
    > public class ReducerCount implements ReducerAggregator<Long>{     
    > &emsp;&emsp;@Override     
    > &emsp;&emsp;public Long init() {      
    > &emsp;&emsp;&emsp;&emsp;return 0L;        
    > &emsp;&emsp;}     
    > &emsp;&emsp;@Override     
    > &emsp;&emsp;public Long reduce(Long curr, TridentTuple tuple) {       
    > &emsp;&emsp;&emsp;&emsp;return curr + 1;      
    > &emsp;&emsp;}     
    > }         

    * 对stream进行聚合操作      
    > mystream.aggregate(new ReducerCount(), new Fields("c"));

*  Aggregator接口可以发射含任意数量属性的任意数据量的tuples,并且可以在执行过程中的任何时候发射；
init:在处理数据之前被调用，它的返回值会作为一个状态值传递给aggregate和complete方法；        
aggregate：用来处理每一个输入的tuple，它可以更新状态值也可以发射tuple；     
complete：当所有tuple都被处理完成后被调用；           

    * 定义一个Aggregator
    > public class MyAggregator extends BaseAggregator {        
    > &emsp;&emsp;public CountState init(Object batchId, TridentCollector collector) {      
    > &emsp;&emsp;&emsp;&emsp;return new CountState();      
    > &emsp;&emsp;}     
    > &emsp;&emsp;public void aggregate(CountState state, TridentTuple tuple, TridentCollector collector) {        
    > &emsp;&emsp;&emsp;&emsp;state.count+=1;       
    > &emsp;&emsp;}     
    > &emsp;&emsp;public void complete(CountState state, TridentCollector collector) {      
    > &emsp;&emsp;&emsp;&emsp;collector.emit(new Values(state.count));      
    > &emsp;&emsp;}     
    > }     
    
    * 对stream进行聚合操作      
    > mystream.aggregate(new Fields(“a”), new MyAggregator(), new Fields(“b”));
    
* PartitionAggregate 流分区，聚合操作
*  PartitionAggregate的作用对每个Partition中的tuple进行聚合，与前面的函数在原tuple后面追加数据不同，PartitionAggregate的输出会直接替换掉输入的tuple，仅收集PartitionAggregate中发射的tuple。
* 定义一个Aggregator
> public class Sum extends BaseAggregator {    
> &emsp;&emsp;static class CountState {     
> &emsp;&emsp;&emsp;&emsp;long count = 0;       
> &emsp;&emsp;}     
> &emsp;&emsp;public CountState init(Object batchId, TridentCollector collector) {      
> &emsp;&emsp;&emsp;&emsp;return new CountState();      
> &emsp;&emsp;}     
> &emsp;&emsp;public void aggregate(CountState state, TridentTuple tuple, TridentCollector collector) {        
> &emsp;&emsp;&emsp;&emsp;state.count+=1;       
> &emsp;&emsp;}     
> &emsp;&emsp;public void complete(CountState state, TridentCollector collector) {      
> &emsp;&emsp;&emsp;&emsp;collector.emit(new Values(state.count));      
> &emsp;&emsp;}     
> }     
* 对stream进行聚合操作
> mystream.partitionAggregate(new Fields("b"), new Sum(), new Fields("c"));

### Repartitioning:重定向操作
* 重定向操作是如何在各个任务间对tuples进行分区。分区的数量也有可能改变重定向的结果。重定向需要网络传输，下面介绍下重定向函数：
    * shuffle：通过随机分配算法来均衡tuple到各个分区
    * broadcast：每个tuple都被广播到所有的分区，这种方式在drcp时非常有用，比如在每个分区上做stateQuery
    * partitionBy：根据指定的字段列表进行划分，具体做法是用指定字段列表的hash值对分区个数做取模运算，确保相同字段列表的数据被划分到同一个分区
    * global：所有的tuple都被发送到一个分区，这个分区用来处理整个Stream
    * batchGlobal：一个Batch中的所有tuple都被发送到同一个分区，不同的Batch会去往不同的分区
    * Partition：通过一个自定义的分区函数来进行分区，这个自定义函数实现了backtype.storm.grouping.CustomStreamGrouping

### Merge和Joins:
* api的最后一部分便是如何把各种流汇聚到一起。最简单的方式就是把这些流汇聚成一个流。
> topology.merge(stream1, stream2, stream3);        
* 另一种合并流的方式就是join。一个标准的join就像是一个sql,必须有标准的输入，因此，join只针对符合条件的Stream。join应用在来自Spout的每一个小Batch中。join时候的tuple会包含：1. join的字段，如Stream1中的key和Stream2中的x；2.所有非join的字段，根据传入join方法的顺序，a和b分别代表steam1的val1和val2，c代表Stream2的val1；     
* 当join的是来源于不同Spout的stream时，这些Spout在发射数据时需要同步，一个Batch所包含的tuple会来自各个Spout。     

## TridentKafkaSpout
* 一个KafkaSpout只能去处理一个topic的内容，所以，它要求初始化时提供如下与topic相关信息： 
    * zookeeper_host：zookeeper地址
    * zookeeper_path：broker路径
    * topic:topic名字
    * spout_name: zookeeper上用于存储当前处理到哪个Offset了。
    * 当前topic中数据如何解码,了解Kafka的应该知道，Kafka中当前处理到哪的Offset是由客户端自己管理       的。
> ZkHosts hosts = new ZkHosts(zookeeper_host,zookeeper_path);      
> TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(hosts,topic,spout_name);        
> tridentKafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());      
> TransactionalTridentKafkaSpout transactionalTridentKafkaSpout = new TransactionalTridentKafkaSpout(tridentKafkaConfig);    

* 使用TridentKafkaSpout创建stream
> topology.newStream(spout_name,uicRegisterKtransactionalTridentKafkaSpoutafkaSpout);
