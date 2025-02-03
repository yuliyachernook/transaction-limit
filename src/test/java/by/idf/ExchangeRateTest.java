package by.idf;

import by.idf.client.ExchangeRateClient;
import by.idf.config.ServiceConfiguration;
import by.idf.dto.exchange.ExchangeRateDto;
import by.idf.dto.exchange.TimeSeriesData;
import by.idf.entity.ExchangeRate;
import by.idf.repository.ExchangeRateRepository;
import by.idf.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateTest {

    private static final String USD_CURRENCY = "USD";
    private static final String KZT_CURRENCY = "KZT";
    private static final String KZT_USD_PAIR = "KZT/USD";
    private static final String API_KEY = "apiKey";
    private static final String FX_DAILY = "FX_DAILY";

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ServiceConfiguration serviceConfiguration;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    public void testUpdateExchangeRate_thenSuccess() {
        String today = LocalDate.now().toString();
        TimeSeriesData timeSeriesData = new TimeSeriesData();
        timeSeriesData.setClose("1.1");

        Map<String, TimeSeriesData> timeSeriesMap = new HashMap<>();
        timeSeriesMap.put(today, timeSeriesData);

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        exchangeRateDto.setTimeSeries(timeSeriesMap);
        ResponseEntity<ExchangeRateDto> responseEntity = ResponseEntity.ok(exchangeRateDto);

        when(serviceConfiguration.getCurrencyPairs()).thenReturn(new String[]{KZT_USD_PAIR});
        when(serviceConfiguration.getApiKey()).thenReturn(API_KEY);
        when(exchangeRateClient.getExchangeRate(FX_DAILY, KZT_CURRENCY, USD_CURRENCY, API_KEY)).thenReturn(responseEntity);

        exchangeRateService.updateExchangeRate();

        verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    public void testUpdateExchangeRate_thenReturnNoDataForToday() {
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        exchangeRateDto.setTimeSeries(new HashMap<>());
        ResponseEntity<ExchangeRateDto> responseEntity = ResponseEntity.ok(exchangeRateDto);

        when(serviceConfiguration.getCurrencyPairs()).thenReturn(new String[]{KZT_USD_PAIR});
        when(serviceConfiguration.getApiKey()).thenReturn(API_KEY);
        when(exchangeRateClient.getExchangeRate(FX_DAILY, KZT_CURRENCY, USD_CURRENCY, API_KEY)).thenReturn(responseEntity);

        exchangeRateService.updateExchangeRate();

        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    public void testUpdateExchangeRate_thenReturnUnsuccessfulResponse() {
        ResponseEntity<ExchangeRateDto> responseEntity = ResponseEntity.badRequest().build();

        when(serviceConfiguration.getCurrencyPairs()).thenReturn(new String[]{KZT_USD_PAIR});
        when(serviceConfiguration.getApiKey()).thenReturn(API_KEY);
        when(exchangeRateClient.getExchangeRate(FX_DAILY, KZT_CURRENCY, USD_CURRENCY, API_KEY)).thenReturn(responseEntity);

        exchangeRateService.updateExchangeRate();

        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    public void testGetExchangeRate_thenReturnSuccess() {
        ZonedDateTime transactionDateTime = ZonedDateTime.now();
        BigDecimal expectedRate = BigDecimal.valueOf(1.1);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(expectedRate);

        when(exchangeRateRepository.findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate()))
                .thenReturn(Optional.of(exchangeRate));

        BigDecimal actualRate = exchangeRateService.getExchangeRate(KZT_CURRENCY, USD_CURRENCY, transactionDateTime);

        assertEquals(expectedRate, actualRate);

        verify(exchangeRateRepository, times(1)).findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate());
        verify(exchangeRateRepository, never()).findLatestExchangeRateForCurrencyPairBeforeDateTime(any(), any());
    }

    @Test
    public void testGetExchangeRate_thenReturnSuccessForPreviousDate() {
        ZonedDateTime transactionDateTime = ZonedDateTime.now();
        BigDecimal expectedRate = BigDecimal.valueOf(1.1);
        ExchangeRate previousExchangeRate = new ExchangeRate();
        previousExchangeRate.setRate(expectedRate);

        when(exchangeRateRepository.findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate()))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.findLatestExchangeRateForCurrencyPairBeforeDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate()))
                .thenReturn(Optional.of(previousExchangeRate));

        BigDecimal actualRate = exchangeRateService.getExchangeRate(KZT_CURRENCY, USD_CURRENCY, transactionDateTime);

        assertEquals(expectedRate, actualRate);

        verify(exchangeRateRepository, times(1)).findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate());
        verify(exchangeRateRepository, times(1)).findLatestExchangeRateForCurrencyPairBeforeDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate());
    }

    @Test
    public void testGetExchangeRate_thenNotFound() {
        ZonedDateTime transactionDateTime = ZonedDateTime.now();
        BigDecimal expectedRate = BigDecimal.valueOf(1.1);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(expectedRate);

        when(exchangeRateRepository.findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate()))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.findLatestExchangeRateForCurrencyPairBeforeDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            exchangeRateService.getExchangeRate(KZT_CURRENCY, USD_CURRENCY, transactionDateTime);
        });

        assertEquals("Exchange rate is not found", exception.getMessage());

        verify(exchangeRateRepository, times(1)).findByCurrencyPairAndDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate());
        verify(exchangeRateRepository, times(1)).findLatestExchangeRateForCurrencyPairBeforeDateTime(KZT_USD_PAIR, transactionDateTime.toLocalDate());
    }
}