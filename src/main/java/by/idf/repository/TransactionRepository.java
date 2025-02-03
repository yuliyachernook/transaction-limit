package by.idf.repository;

import by.idf.entity.CategoryEnum;
import by.idf.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountFromAndExpenseCategoryAndDateTimeBetween(long account, CategoryEnum category, ZonedDateTime from, ZonedDateTime to);

    @Query(value = "SELECT t.* FROM transactions t " +
            "JOIN transaction_limit l ON t.transaction_limit_id = l.id " +
            "WHERE t.limit_exceeded = true AND t.account_from = :accountFrom " +
            "ORDER BY t.date_time DESC", nativeQuery = true)
    List<Transaction> findExceededTransactionsByAccountFrom(Long accountFrom);
}
