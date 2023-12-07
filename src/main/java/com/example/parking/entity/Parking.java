package com.example.parking.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;


@Getter
@Setter
public class Parking {
    @Override
    public String toString() {
        return "Parking{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", capacity=" + capacity +
                '}';
    }

    private String id;
    private Date time;
    private int capacity;

    //格式化
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(time);
    }
}
