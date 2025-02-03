package by.idf.repository;

import by.idf.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(value = "SELECT * FROM exchange_rate " +
            "WHERE currency_pair = :currencyPair AND date_time < :dateTime " +
            "ORDER BY date_time DESC LIMIT 1", nativeQuery = true)
    Optional<ExchangeRate> findLatestExchangeRateForCurrencyPairBeforeDateTime(String currencyPair, ZonedDateTime dateTime);
}
