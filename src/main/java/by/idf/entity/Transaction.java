package by.idf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long accountFrom;
    private long accountTo;
    private String currencyShortname;
    @Column(name = "sum", scale = 2)
    private BigDecimal sum;
    @Enumerated(EnumType.STRING)
    private CategoryEnum expenseCategory;
    private ZonedDateTime dateTime;
    private boolean limitExceeded;

    @ManyToOne
    @JoinColumn(name = "transactionLimitId")
    private Limit transactionLimit;
}
