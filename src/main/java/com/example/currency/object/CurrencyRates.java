package com.example.currency.object;


import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class CurrencyRates {
    private String date;
    private String base;
    private LinkedHashMap<String, Double> rates;
}
