package by.idf.dto.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaData {
    @JsonProperty("1. Information")
    private String information;

    @JsonProperty("2. From Symbol")
    private String fromSymbol;

    @JsonProperty("3. To Symbol")
    private String toSymbol;

    @JsonProperty("4. Output Size")
    private String outputSize;

    @JsonProperty("5. Last Refreshed")
    private String lastRefreshed;

    @JsonProperty("6. Time Zone")
    private String timeZone;
}