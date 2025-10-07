package iscteiul.ista.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyExchangeService {
    private static final String API_URL = "https://api.frankfurter.app/latest?from=%s&to=%s";

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        String url = String.format(API_URL, fromCurrency, toCurrency);
        RestTemplate restTemplate = new RestTemplate();
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        if (response != null && response.rates.containsKey(toCurrency)) {
            return response.rates.get(toCurrency);
        }
        return null;
    }

    public static class ExchangeRateResponse {
        public java.util.Map<String, Double> rates;
    }
}
