package iscteiul.ista.exchange;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    private static final BigDecimal SPREAD = new BigDecimal("0.01"); // 1%
    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Currency createConversion(String from, String to, BigDecimal amount) {
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("Moedas 'from' e 'to' são obrigatórias.");
        }
        if (from.equalsIgnoreCase(to)) {
            throw new IllegalArgumentException("'from' e 'to' não podem ser iguais.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("O montante tem de ser positivo.");
        }

        String url = "https://api.exchangerate.host/latest?base=" + from + "&symbols=" + to;
        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("rates")) {
            throw new IllegalStateException("Resposta inválida do serviço de câmbios.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> rates = (Map<String, Object>) response.get("rates");
        if (rates == null || !rates.containsKey(to)) {
            throw new IllegalStateException("Taxa não encontrada para " + to);
        }

        BigDecimal rate = new BigDecimal(rates.get(to).toString());
        BigDecimal rateWithSpread = rate.multiply(BigDecimal.ONE.add(SPREAD), MathContext.DECIMAL64);
        BigDecimal result = amount.multiply(rateWithSpread, MathContext.DECIMAL64);

        var currency = new Currency(from.toUpperCase(), to.toUpperCase(), amount, result);
        return repository.saveAndFlush(currency);
    }

    @Transactional(readOnly = true)
    public List<Currency> list(Pageable pageable) {
        return repository.findAllBy(pageable).toList();
    }
}
