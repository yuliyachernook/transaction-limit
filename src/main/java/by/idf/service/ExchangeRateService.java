package by.idf.service;

import by.idf.config.ServiceConfiguration;
import by.idf.client.ExchangeRateClient;
import by.idf.dto.exchange.ExchangeRateDto;
import by.idf.dto.exchange.TimeSeriesData;
import by.idf.entity.ExchangeRate;
import by.idf.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private static final String FX_DAILY = "FX_DAILY";

    private final ExchangeRateClient exchangeRateClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ServiceConfiguration serviceConfiguration;

    @Scheduled(cron = "${exchange.rate.cron.expression}")
    public void updateExchangeRate() {
        for (String pair : serviceConfiguration.getCurrencyPairs()) {
            String[] currencies = pair.split("/");
            ResponseEntity<ExchangeRateDto> response = exchangeRateClient.getExchangeRate(FX_DAILY, currencies[0], currencies[1], serviceConfiguration.getApiKey());
            if (response.getStatusCode().is2xxSuccessful()) {
                ExchangeRateDto exchangeRateDto = response.getBody();
                if (exchangeRateDto != null) {
                    Map<String, TimeSeriesData> values = exchangeRateDto.getTimeSeries();
                    String today = LocalDate.now().toString();

                    Optional<TimeSeriesData> todayValue = values.entrySet().stream()
                            .filter(entry -> entry.getKey().equals(today))
                            .map(Map.Entry::getValue)
                            .findFirst();

                    if (todayValue.isPresent()) {
                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setCurrencyPair(pair);
                        exchangeRate.setDateTime(LocalDate.now());
                        exchangeRate.setRate(new BigDecimal(todayValue.get().getClose()));
                        exchangeRateRepository.save(exchangeRate);

                        log.info("Saved exchange rate {} for {}.", todayValue.get().getClose(), pair);
                    } else {
                        log.error("No exchange rate data for today for: {}", pair);
                    }
                }
            } else {
                log.error("Can not update exchange rate");
            }
        }
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency, ZonedDateTime transactionDateTime) {
        String currencyPair = fromCurrency + "/" + toCurrency;

        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencyPairAndDateTime(currencyPair, transactionDateTime.toLocalDate());
        if (exchangeRate.isPresent()) {
            return exchangeRate.get().getRate();
        }

        Optional<ExchangeRate> lastAvailableExchangeRate = exchangeRateRepository.findLatestExchangeRateForCurrencyPairBeforeDateTime(currencyPair, transactionDateTime.toLocalDate());
        if (lastAvailableExchangeRate.isPresent()) {
            return lastAvailableExchangeRate.get().getRate();
        }

        log.error("Exchange rate not found for {} at {}", currencyPair, transactionDateTime);
        throw new RuntimeException("Exchange rate is not found");
    }

}
