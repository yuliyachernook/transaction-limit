package by.idf;

import by.idf.dto.ExceededTransactionDto;
import by.idf.dto.TransactionDto;
import by.idf.entity.CategoryEnum;
import by.idf.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void contextTest(){
        assertNotNull(transactionService);
    }

    @Test
    public void testSaveTransaction_withoutExceededLimit_thenSuccess() {
        TransactionDto testTransaction = TransactionDto.builder()
                .accountFrom(1)
                .accountTo(2)
                .currencyShortname("KZT")
                .sum(BigDecimal.valueOf(600))
                .expenseCategory(CategoryEnum.PRODUCT)
                .dateTime(ZonedDateTime.now())
                .build();

        TransactionDto actualTransaction = transactionService.saveTransaction(testTransaction);

        assertEquals(testTransaction.getAccountFrom(), actualTransaction.getAccountFrom());
        assertEquals(testTransaction.getAccountTo(), actualTransaction.getAccountTo());
        assertEquals(testTransaction.getCurrencyShortname(), actualTransaction.getCurrencyShortname());
        assertEquals(testTransaction.getSum(), actualTransaction.getSum());
        assertEquals(testTransaction.getExpenseCategory(), actualTransaction.getExpenseCategory());
        assertThat(actualTransaction.isLimitExceeded()).isFalse();
    }

    @Test
    public void testSaveTransaction_withExceededLimit_thenSuccess() {
        TransactionDto testTransaction = TransactionDto.builder()
                .accountFrom(1)
                .accountTo(2)
                .currencyShortname("KZT")
                .sum(BigDecimal.valueOf(1100))
                .expenseCategory(CategoryEnum.PRODUCT)
                .dateTime(ZonedDateTime.now())
                .build();

        TransactionDto actualTransaction = transactionService.saveTransaction(testTransaction);

        List<ExceededTransactionDto> exceededTransactions = transactionService.getExceededTransactions(1L);

        assertEquals(testTransaction.getAccountFrom(), actualTransaction.getAccountFrom());
        assertEquals(testTransaction.getAccountTo(), actualTransaction.getAccountTo());
        assertEquals(testTransaction.getCurrencyShortname(), actualTransaction.getCurrencyShortname());
        assertEquals(testTransaction.getSum(), actualTransaction.getSum());
        assertEquals(testTransaction.getExpenseCategory(), actualTransaction.getExpenseCategory());
        assertThat(actualTransaction.isLimitExceeded()).isTrue();
        assertThat(exceededTransactions.size()).isEqualTo(1);
        assertThat(exceededTransactions.get(0).getSum()).isEqualTo(BigDecimal.valueOf(1100));
    }
}
