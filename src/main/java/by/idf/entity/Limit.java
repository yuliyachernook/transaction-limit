package by.idf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transaction_limit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountId;
    private String currencyShortname;
    private BigDecimal sum;
    @Enumerated(EnumType.STRING)
    private CategoryEnum expenseCategory;
    private ZonedDateTime dateTime;

    public Limit(long accountId, String currencyShortname, BigDecimal sum, CategoryEnum expenseCategory, ZonedDateTime dateTime) {
        this.accountId = accountId;
        this.currencyShortname = currencyShortname;
        this.sum = sum;
        this.expenseCategory = expenseCategory;
        this.dateTime = dateTime;
    }
}
