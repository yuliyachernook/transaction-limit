package by.idf.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Сущность транзакции, превышающей лимит")
public class ExceededTransactionDto {

    @Schema(description = "Банковский счет клиента", example = "124567", accessMode = Schema.AccessMode.READ_ONLY)
    private long accountFrom;
    @Schema(description = "Банковский счет контрагента", example = "124567", accessMode = Schema.AccessMode.READ_ONLY)
    private long accountTo;
    @Schema(description = "Валюта транзакции", example = "RUB", accessMode = Schema.AccessMode.READ_ONLY)
    private String currencyShortname;
    @Schema(description = "Сумма транзакции", example = "20000", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal sum;
    @Schema(description = "Категория расходов", allowableValues = {"SERVICE", "PRODUCT"})
    private String expenseCategory;
    @Schema(description = "Дата и время", example = "2023-10-01T10:15:30+03")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime dateTime;
    @Schema(description = "Флаг, указывающий, превышен ли лимит", example = "false")
    private boolean limitExceeded;
    @Schema(description = "Сумма лимита", example = "1200.00")
    private BigDecimal limitSum;
    @Schema(description = "Дата и время", example = "2023-10-01T10:15:30+03")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime limitDateTime;
    @Schema(description = "Валюта лимита", example = "USD", accessMode = Schema.AccessMode.READ_ONLY)
    private String limitCurrencyShortname;
}
