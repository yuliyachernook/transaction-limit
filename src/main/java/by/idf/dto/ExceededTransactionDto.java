package by.idf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExceededTransactionDto {

    private long accountFrom;
    private long accountTo;
    private String currencyShortname;
    private BigDecimal sum;
    private String expenseCategory;
    private ZonedDateTime dateTime;
    private boolean limitExceeded;
    private BigDecimal limitSum;
    private ZonedDateTime limitDateTime;
    private String limitCurrencyShortname;
}
