package by.idf.repository;

import by.idf.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    @Query(value = "SELECT * FROM transaction_limit l " +
            "WHERE l.account_id = :accountId AND l.expense_category = :expenseCategory AND l.date_time between :startDateTime and :endDateTime " +
            "ORDER BY l.date_time DESC LIMIT 1", nativeQuery = true)
    Optional<Limit> findLatestLimitByAccountIdAndExpenseCategoryBetweenDateTime(long accountId, String expenseCategory, ZonedDateTime startDateTime, ZonedDateTime endDateTime);

    List<Limit> findAllByAccountId(long accountId);
}
