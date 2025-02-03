package by.idf.service;

import by.idf.client.ExchangeRateClient;
import by.idf.config.ServiceConfiguration;
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
        ZonedDateTime today = ZonedDateTime.now();
        for (String pair : serviceConfiguration.getCurrencyPairs()) {
            String[] currencies = pair.split("/");

            ResponseEntity<ExchangeRateDto> response = exchangeRateClient.getExchangeRate(FX_DAILY, currencies[0], currencies[1], serviceConfiguration.getApiKey());
            if (response.getStatusCode().is2xxSuccessful()) {
                ExchangeRateDto exchangeRateDto = response.getBody();
                if (exchangeRateDto != null) {
                    Map<String, TimeSeriesData> timeSeries = exchangeRateDto.getTimeSeries();
                    TimeSeriesData todayData = timeSeries.get(today.toLocalDate().toString());

                    if (todayData != null) {
                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setCurrencyPair(pair);
                        exchangeRate.setDateTime(today);
                        exchangeRate.setRate(new BigDecimal(todayData.getClose()));
                        exchangeRateRepository.save(exchangeRate);

                        log.info("Saved exchange rate {} for {}.", todayData.getClose(), pair);
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

        Optional<ExchangeRate> lastAvailableExchangeRate = exchangeRateRepository.findLatestExchangeRateForCurrencyPairBeforeDateTime(currencyPair, transactionDateTime);
        if (lastAvailableExchangeRate.isPresent()) {
            return lastAvailableExchangeRate.get().getRate();
        }

        log.error("Exchange rate not found for {} at {}", currencyPair, transactionDateTime);
        throw new RuntimeException("Exchange rate is not found");
    }
}
