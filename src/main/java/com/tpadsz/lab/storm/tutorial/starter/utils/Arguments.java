package com.tpadsz.lab.storm.tutorial.starter.utils;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Created by roger.wang on 2016/3/22.
 */
public class Arguments{

    public Arguments(String[] args){
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        try{
            parser.parseArgument(args);
        }catch (CmdLineException e){
            parser.printUsage(System.err);
            System.err.println();
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }

    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    public Integer getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void setNumberOfWorkers(Integer numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    @Option(name = "-n",required = true)
    String topologyName;
    @Option(name = "-w",required = false)
    Integer numberOfWorkers = 1;
    @Option(name="-l", required = false)
    Boolean local=false;
}
