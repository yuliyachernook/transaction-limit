package by.idf.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateDto {

    @JsonProperty("Meta Data")
    private MetaData metaData;

    @JsonProperty("Time Series FX (Daily)")
    private Map<String, TimeSeriesData> timeSeries;
}
