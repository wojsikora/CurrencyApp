package com.example.currency.service;


import com.example.currency.object.Currencies;
import com.example.currency.object.CurrencyRates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@org.springframework.stereotype.Service
public class Service {

    public CurrencyRates getCurrencies() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = Sources.getData(Sources.source+"latest"+Sources.key);
        json = json.replace("{\"meta\":{\"code\":200,\"disclaimer\":\"Usage subject to terms: https:\\/\\/currencyscoop.com\\/terms\"},\"response\":","");
        json = json.substring(0, json.length()-1);
        return mapper.readValue(json, CurrencyRates.class);
    }


    public CurrencyRates getHistoricalCurrencyRates(String date) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = Sources.getData(Sources.source+"historical"+Sources.key+"&date="+date);
        json = json.replace("{\"meta\":{\"code\":200,\"disclaimer\":\"Usage subject to terms: https:\\/\\/currencyscoop.com\\/terms\"},\"response\":","");
        json = json.substring(0, json.length()-1);
        return mapper.readValue(json, CurrencyRates.class);
    }

    public Currencies getCurrenciesName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = Sources.getData(Sources.secondSource+"latest/currencies.json");
        json = json.replace("{    \"1inch\":", "{  \"currencies\":{  \"1inch\":");
        json+="}";
        return  mapper.readValue(json, Currencies.class);

    }





}
