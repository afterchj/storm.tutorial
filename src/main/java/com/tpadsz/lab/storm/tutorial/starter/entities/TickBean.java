package com.tpadsz.lab.storm.tutorial.starter.entities;

/**
 * Created by roger.wang on 2016/3/10.
 */
public class TickBean {
    private Integer tick;
    private String processId;

    public Integer getTick() {
        return tick;
    }

    public void setTick(Integer tick) {
        this.tick = tick;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TickBean tickBean = (TickBean) o;

        if (!tick.equals(tickBean.tick)) return false;
        return processId.equals(tickBean.processId);

    }

    @Override
    public int hashCode() {
        int result = tick.hashCode();
        result = 31 * result + processId.hashCode();
        return result;
    }
}
