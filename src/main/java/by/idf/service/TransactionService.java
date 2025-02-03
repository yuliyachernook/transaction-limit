package by.idf.service;

import by.idf.dto.ExceededTransactionDto;
import by.idf.dto.TransactionDto;
import by.idf.entity.Limit;
import by.idf.mapper.TransactionMapper;
import by.idf.entity.CategoryEnum;
import by.idf.entity.Transaction;
import by.idf.repository.LimitRepository;
import by.idf.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {

    private static final String USD_CURRENCY = "USD";

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ExchangeRateService exchangeRateService;
    private final LimitRepository limitRepository;

    public TransactionDto saveTransaction(TransactionDto transactionDto) {

        Transaction transaction = transactionMapper.toEntity(transactionDto);

        BigDecimal sumInUsd = convertToUsd(transaction.getSum(), transaction.getCurrencyShortname(), transaction.getDateTime());

        long account = transactionDto.getAccountFrom();
        CategoryEnum category = transaction.getExpenseCategory();
        ZonedDateTime transactionDateTime = transaction.getDateTime();
        ZonedDateTime monthStart = transactionDateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        Limit actualTransactionLimit = limitRepository.findLatestLimitByAccountIdAndExpenseCategoryBeforeDateTime(account, category.getName(), transactionDateTime)
                .orElseGet(() -> {
                    Limit newTransactionLimit = new Limit(transaction.getAccountFrom(), USD_CURRENCY, BigDecimal.valueOf(1000.00), transaction.getExpenseCategory(), monthStart);
                    limitRepository.save(newTransactionLimit);
                    return newTransactionLimit;
                });

        BigDecimal actualLimitAmount = actualTransactionLimit.getSum();

        List<Transaction> transactionsForThisMonthBeforeThisTransaction = transactionRepository.findByAccountFromAndExpenseCategoryAndDateTimeBetween(account, category, monthStart, transactionDateTime);

        BigDecimal sumForThisMonthBeforeTransaction = transactionsForThisMonthBeforeThisTransaction.stream()
                .map(tr -> convertToUsd(tr.getSum(), tr.getCurrencyShortname(), tr.getDateTime()))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal(0));

        if (sumForThisMonthBeforeTransaction.add(sumInUsd).compareTo(actualLimitAmount) > 0) {
            transaction.setLimitExceeded(true);
            transaction.setTransactionLimit(actualTransactionLimit);
        } else {
            transaction.setLimitExceeded(false);
        }

        transactionRepository.save(transaction);
        log.info("Transaction created from account '{}' to account '{}' with sum {} {}", transaction.getAccountFrom(), transaction.getAccountTo(), transaction.getSum(), transaction.getCurrencyShortname());

        return transactionMapper.toDto(transaction);
    }

    public List<ExceededTransactionDto> getExceededTransactions(Long accountFrom) {
        return transactionRepository.findExceededTransactionsByAccountFrom(accountFrom)
                .stream()
                .map(transactionMapper::toExceededTransactionDto)
                .toList();
    }

    private BigDecimal convertToUsd(BigDecimal sum, String currencyShortname, ZonedDateTime transactionDateTime) {
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currencyShortname, USD_CURRENCY, transactionDateTime);
        return sum.multiply(exchangeRate);
    }
 }
