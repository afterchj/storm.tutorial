package com.tpadsz.lab.storm.tutorial.starter;


import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Properties props=new Properties();
////        File f=new File("jar:file:/D:/gitlib/storm.tutorial/target/storm.tutorial-jar-with-dependencies.jar!/storm.tutorial.properties");
////        System.out.println(f.exists());
//        props.load(new java.net.URI("jar:file:/D:/gitlib/storm.tutorial/target/storm.tutorial-jar-with-dependencies.jar!/storm.tutorial.properties").toURL().openStream());
//        System.out.println(props.getProperty("packaging.profile"));
        // Experiment on profiles.

//        props.load(Main.class.getResourceAsStream("/storm.tutorial.properties"));
//        System.out.format("1.Property URL in jar:%s\n",Main.class.getResource("/storm.tutorial.properties").toString());
//        String profile=props.getProperty("packaging.profile");
//        System.out.println(profile);
        props.load(Main.class.getResourceAsStream("/storm.tutorial.properties"));




        BufferedReader br=new BufferedReader(new InputStreamReader(
        ClassLoader.getSystemResourceAsStream("storm.tutorial.properties")
        ));




        System.out.println("AAAAAAAAAAAA");
        String line=null;
        while((line=br.readLine())!=null){
            System.out.println(line);
        }
        System.out.println("BBBBBBBBBBBBBBB");

        System.out.println(props.getProperty("redis.host"));
        System.out.format("Redis host:%s", Constants.getConstants().getRedisHost());
        System.out.format("Redis port:%d",Constants.getConstants().getRedisPort());
    }
}
