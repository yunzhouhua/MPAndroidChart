package com.xxmassdeveloper.mpchartexample.bean;

public class PowerData {
    private long dateTime;
    private Long generationPower;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getGenerationPower() {
        return generationPower;
    }

    public void setGenerationPower(Long generationPower) {
        this.generationPower = generationPower;
    }

    @Override
    public String toString() {
        return "PowerData{" +
                "dateTime=" + dateTime +
                ", generationPower=" + generationPower +
                '}';
    }
}
