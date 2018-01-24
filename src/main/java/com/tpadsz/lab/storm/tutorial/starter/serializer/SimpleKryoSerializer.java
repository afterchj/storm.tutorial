package com.tpadsz.lab.storm.tutorial.starter.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;

import java.io.*;

/**
 * Created by roger.wang on 2016/3/17.
 */
public class SimpleKryoSerializer<T extends TickBean> extends Serializer<T> implements Serializable{



    @Override
    public void write(Kryo kryo, Output output, T t) {
        Integer tick=t.getTick();
        String processId=t.getProcessId();
        output.writeInt(tick);
        output.writeString(processId);


        try {
            File f=new File("/home/kryo.out");
            FileWriter fw=new FileWriter(f,true);
            BufferedWriter bw=new BufferedWriter(fw);
            bw.append(String.format("Serializing TickBean with hashCode %s",t.hashCode()));
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.format("Serializing TickBean with hashCode %s",t.hashCode());

    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> aClass) {
        try {
            TickBean bean=aClass.newInstance();
            bean.setTick(input.readInt());
            bean.setProcessId(input.readString());
            return aClass.cast(bean);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
