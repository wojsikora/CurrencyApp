package com.example.currency.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Sources {

    public static String source = "https://api.currencyscoop.com/v1/";
    public static String key = "?api_key=25603587f67260760090221adb6ec8a8";
    public static String secondSource = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/";


    public static String getData(String urlSource) {
        try {
            URL url = new URL(urlSource);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();
                return inline.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
