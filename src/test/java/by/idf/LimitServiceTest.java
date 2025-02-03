package by.idf;

import by.idf.dto.LimitDto;
import by.idf.entity.CategoryEnum;
import by.idf.service.LimitService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class LimitServiceTest {

    @Autowired
    private LimitService limitService;

    @Test
    public void contextTest(){
        assertNotNull(limitService);
    }

    @Test
    public void testCreateLimit_thenSuccess() {
        LimitDto testLimit = LimitDto.builder()
                .accountId(1)
                .currencyShortname("USD")
                .sum(BigDecimal.valueOf(1200))
                .expenseCategory(CategoryEnum.SERVICE)
                .build();

        LimitDto actualDto = limitService.createLimit(testLimit);

        assertEquals(testLimit.getAccountId(), actualDto.getAccountId());
        assertEquals(testLimit.getCurrencyShortname(), actualDto.getCurrencyShortname());
        assertEquals(testLimit.getSum(), actualDto.getSum());
        assertEquals(testLimit.getExpenseCategory(), actualDto.getExpenseCategory());
    }

    @Test
    public void testGetAllLimitsByAccountId_thenSuccess() {
        Long accountId = 2L;
        LimitDto testLimit = LimitDto.builder()
                .accountId(accountId)
                .currencyShortname("USD")
                .sum(BigDecimal.valueOf(1200))
                .expenseCategory(CategoryEnum.SERVICE)
                .build();
        limitService.createLimit(testLimit);

        List<LimitDto> allLimitsByAccountId = limitService.getAllLimitsByAccountId(accountId);

        assertThat(allLimitsByAccountId.size()).isEqualTo(1);
        assertThat(allLimitsByAccountId.get(0).getAccountId()).isEqualTo(accountId);
    }
}
