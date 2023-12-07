package com.example.parking.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingPrediction {
    private String ds;
    private double yhat;
    private double yhatLower;
    private double yhatUpper;
}
