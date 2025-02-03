package by.idf.client;

import by.idf.dto.exchange.ExchangeRateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${feign.client.alphaVantage.url}", name = "exchange-rate-client")
public interface ExchangeRateClient {

    @GetMapping("/query")
    ResponseEntity<ExchangeRateDto> getExchangeRate(@RequestParam("function") String function,
                                                    @RequestParam("from_symbol") String fromSymbol,
                                                    @RequestParam("to_symbol") String toSymbol,
                                                    @RequestParam("apikey") String apikey);
}
