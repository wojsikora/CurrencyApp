package com.example.currency.controller;

import com.example.currency.service.Service;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyThread extends Thread{
    String date;
    String currency;
    Service service;
    ConcurrentHashMap<String, Double> currencyRatesMap;
    String type;

    public CurrencyThread(String date, String currency, Service service, ConcurrentHashMap<String, Double> currencyRatesMap, String type) throws JsonProcessingException {
       this.date = date;
       this.currency = currency;
       this.service = service;
       this.currencyRatesMap = currencyRatesMap;
       this.type = type;
    }

    @Override
    public void run(){
        try {
            if(type.equals("latest")) {
                currencyRatesMap.put(date.toString(), service.getCurrencies().getRates().get(currency));
            }
            else if(type.equals("historical")){
                currencyRatesMap.put(date.toString(), service.getHistoricalCurrencyRates(date.toString()).getRates().get(currency));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}
