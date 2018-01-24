package com.tpadsz.lab.storm.tutorial.starter.utils;

import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by roger.wang on 2016/3/22.
 */
public class StringScheme implements Scheme {
    @Override
    public List<Object> deserialize(byte[] ser) {
        String string=null;
        try {
            string=new String(ser,"UTF-8");
            System.out.printf("Read from Kafka: %s\n",string);
        } catch (UnsupportedEncodingException e) {

        }
        return new Values(string);
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("string");
    }
}
