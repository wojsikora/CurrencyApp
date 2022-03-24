package com.example.currency.controller;

import com.example.currency.model.CurrencyRates;
import com.example.currency.model.FormFields;
import com.example.currency.model.Statistics;
import com.example.currency.service.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@org.springframework.stereotype.Controller
public class Controller {

    public Service service;

    public Controller(Service service){
        this.service = service;
    }

    @GetMapping("/")
    public String fillForm(Model model) throws  Exception {
        try {
            CurrencyRates currencies = service.getCurrencies();

            model.addAttribute("formFields", new FormFields());
            model.addAttribute("currencies", currencies.getRates().keySet());
            model.addAttribute("maxDate", LocalDate.now());
            return "form";
        } catch (Exception e){
            return "errorAnnouncement";
        }
    }

    @PostMapping("/result")
    public String returnResult(Model model, @ModelAttribute FormFields formFields) throws Exception {
        try {
            model.addAttribute("formFields", formFields);
            String currency = formFields.getCurrencyField();
            String startDate = formFields.getStartDateField();
            String endDate = formFields.getEndDateField();

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (end.isBefore(start)) {
                LocalDate temp = end;
                end = start;
                start = temp;
                startDate = start.toString();
                endDate = end.toString();
            }


            model.addAttribute("currency", currency);

            String currencyName = service.getCurrenciesName().getCurrencies().get(currency.toLowerCase());
            model.addAttribute("currencyName", currencyName);

            CurrencyRates currencies = service.getCurrencies();
            model.addAttribute("latestCurrencies", currencies.getRates().get(currency));

            CurrencyRates startHistoricalCurrencyRates;
            CurrencyRates endHistoricalCurrencyRates;

            if (start.equals(LocalDate.now())) {
                startHistoricalCurrencyRates = currencies;
            } else {
                startHistoricalCurrencyRates = service.getHistoricalCurrencyRates(startDate);
            }
            model.addAttribute("startHistoricalCurrencyRates", startHistoricalCurrencyRates.getRates().get(currency));
            model.addAttribute("startDate", startDate);

            if (end.equals(LocalDate.now())) {
                endHistoricalCurrencyRates = currencies;
            } else {
                endHistoricalCurrencyRates = service.getHistoricalCurrencyRates(endDate);
            }
            model.addAttribute("endHistoricalCurrencyRates", endHistoricalCurrencyRates.getRates().get(currency));
            model.addAttribute("endDate", endDate);


            double startCurrencyRate = startHistoricalCurrencyRates.getRates().get(currency);
            double endCurrencyRate = endHistoricalCurrencyRates.getRates().get(currency);
            double percentageChange = ((endCurrencyRate - startCurrencyRate) * 100) / startCurrencyRate;

            boolean ifPositive;
            ifPositive = percentageChange > 0;
            model.addAttribute("ifPositive", ifPositive);
            model.addAttribute("percentageChange", percentageChange);

            Statistics statistics = getStatistics(startDate, endDate, currency);
            model.addAttribute("min", statistics.getMin());
            model.addAttribute("max", statistics.getMax());
            model.addAttribute("avg", statistics.getAvg());

            model.addAttribute("dailyMaxDifUp", statistics.getDailyMaxDifUp());
            model.addAttribute("dailyMinDifUp", statistics.getDailyMinDifUp());
            model.addAttribute("dailyMaxDifDown", statistics.getDailyMaxDifDown());
            model.addAttribute("dailyMinDifDown", statistics.getDailyMinDifDown());

            model.addAttribute("lowestRateDay", statistics.getLowestRateDay());
            model.addAttribute("highestRateDay", statistics.getHighestRateDay());

            model.addAttribute("dailyMaxDifUpFirst", statistics.getDailyMaxDifUpFirst());
            model.addAttribute("dailyMaxDifUpSecond", statistics.getDailyMaxDifUpSecond());
            model.addAttribute("dailyMinDifUpFirst", statistics.getDailyMinDifUpFirst());
            model.addAttribute("dailyMinDifUpSecond", statistics.getDailyMinDifUpSecond());
            model.addAttribute("dailyMaxDifDownFirst", statistics.getDailyMaxDifDownFirst());
            model.addAttribute("dailyMaxDifDownSecond", statistics.getDailyMaxDifDownSecond());
            model.addAttribute("dailyMinDifDownFirst", statistics.getDailyMinDifDownFirst());
            model.addAttribute("dailyMinDifDownSecond", statistics.getDailyMinDifDownSecond());

            return "result";
        } catch (Exception e){
            return "errorAnnouncement";
        }
    }

    public Statistics getStatistics(String startDate, String endDate, String currency) throws JsonProcessingException, InterruptedException {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        double min = Double.POSITIVE_INFINITY;
        double max = 0.0;
        double sum = 0.0;
        double i = 0.0;
        double dailyMaxDifUp =0.0;
        double dailyMinDifUp = Double.POSITIVE_INFINITY;
        double dailyMaxDifDown =0.0;
        double dailyMinDifDown = Double.POSITIVE_INFINITY;
        double previousDayValue = 0.0;

        String lowestRateDay = null;
        String highestRateDay = null;

        String dailyMaxDifUpFirst = null;
        String dailyMaxDifUpSecond = null;
        String dailyMinDifUpFirst = null;
        String dailyMinDifUpSecond = null;
        String dailyMaxDifDownFirst = null;
        String dailyMaxDifDownSecond = null;
        String dailyMinDifDownFirst = null;
        String dailyMinDifDownSecond = null;

        ConcurrentHashMap<String, Double> currencyRatesMap = new ConcurrentHashMap<>();
        List<Thread> threadList = new ArrayList<>();

        for(LocalDate date = start; date.isBefore(end) || date.equals(end); date = date.plusDays(1)) {

            if (date.equals(LocalDate.now())) {
                CurrencyThread thread = new CurrencyThread(date.toString(), currency, service, currencyRatesMap, "latest");
                thread.start();
                threadList.add(thread);
            } else {
                CurrencyThread thread = new CurrencyThread(date.toString(), currency, service, currencyRatesMap, "historical");
                thread.start();
                threadList.add(thread);
            }
        }

        for(Thread t: threadList){
            t.join();
        }

        for(LocalDate date = start; date.isBefore(end) || date.equals(end); date = date.plusDays(1)){

            double certainCurrencyRate = currencyRatesMap.get(date.toString());
            sum+=certainCurrencyRate;
            i+=1.0;
            if(min > certainCurrencyRate){
                min = certainCurrencyRate;
                lowestRateDay = date.toString();
            }
            if(max < certainCurrencyRate) {
                max = certainCurrencyRate;
                highestRateDay = date.toString();
            }
            if(!date.equals(start)){
                if(previousDayValue > certainCurrencyRate && dailyMinDifDown > previousDayValue - certainCurrencyRate){
                    dailyMinDifDown = previousDayValue - certainCurrencyRate;
                    dailyMinDifDownSecond = date.toString();
                    dailyMinDifDownFirst = date.minusDays(1).toString();
                }
                if(previousDayValue > certainCurrencyRate && dailyMaxDifDown < previousDayValue - certainCurrencyRate){
                    dailyMaxDifDown = previousDayValue - certainCurrencyRate;
                    dailyMaxDifDownSecond = date.toString();
                    dailyMaxDifDownFirst = date.minusDays(1).toString();
                }
                if(certainCurrencyRate >  previousDayValue && dailyMinDifUp > certainCurrencyRate - previousDayValue){
                    dailyMinDifUp = certainCurrencyRate - previousDayValue;
                    dailyMinDifUpSecond = date.toString();
                    dailyMinDifUpFirst = date.minusDays(1).toString();
                }
                if(certainCurrencyRate >  previousDayValue && dailyMaxDifUp < certainCurrencyRate - previousDayValue) {
                    dailyMaxDifUp = certainCurrencyRate - previousDayValue;
                    dailyMaxDifUpSecond = date.toString();
                    dailyMaxDifUpFirst = date.minusDays(1).toString();

                }
            }
            previousDayValue = certainCurrencyRate;
        }

        double avg = sum/i;

        Statistics statistics = new Statistics();
        statistics.setMin(min);
        statistics.setMax(max);
        statistics.setAvg(avg);

        statistics.setDailyMaxDifUp(dailyMaxDifUp);
        statistics.setDailyMinDifUp(dailyMinDifUp);
        statistics.setDailyMaxDifDown(dailyMaxDifDown);
        statistics.setDailyMinDifDown(dailyMinDifDown);

        statistics.setLowestRateDay(lowestRateDay);
        statistics.setHighestRateDay(highestRateDay);

        statistics.setDailyMaxDifUpFirst(dailyMaxDifUpFirst);
        statistics.setDailyMaxDifUpSecond(dailyMaxDifUpSecond);
        statistics.setDailyMinDifUpFirst(dailyMinDifUpFirst);
        statistics.setDailyMinDifUpSecond(dailyMinDifUpSecond);
        statistics.setDailyMaxDifDownFirst(dailyMaxDifDownFirst);
        statistics.setDailyMaxDifDownSecond(dailyMaxDifDownSecond);
        statistics.setDailyMinDifDownFirst(dailyMinDifDownFirst);
        statistics.setDailyMinDifDownSecond(dailyMinDifDownSecond);

        return statistics;

    }



}
