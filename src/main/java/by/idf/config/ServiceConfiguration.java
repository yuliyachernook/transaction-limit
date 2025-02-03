package by.idf.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServiceConfiguration {

    @Value("${exchange.rate.currency.pairs}")
    private String[] currencyPairs;

    @Value("${exchange.rate.api}")
    private String apiKey;
}
