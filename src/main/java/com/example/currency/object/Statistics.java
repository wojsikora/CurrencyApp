package com.example.currency.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statistics {
    private double min;
    private double max;
    private double avg;
    private double dailyMaxDifUp;
    private double dailyMinDifUp;
    private double dailyMaxDifDown;
    private double dailyMinDifDown;
    private String lowestRateDay;
    private String highestRateDay;
    private String dailyMaxDifUpFirst;
    private String dailyMaxDifUpSecond;
    private String dailyMinDifUpFirst;
    private String dailyMinDifUpSecond;
    private String dailyMaxDifDownFirst;
    private String dailyMaxDifDownSecond;
    private String dailyMinDifDownFirst;
    private String dailyMinDifDownSecond;
}
