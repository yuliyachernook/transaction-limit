package by.idf.mapper;

import by.idf.dto.ExceededTransactionDto;
import by.idf.dto.TransactionDto;
import by.idf.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toEntity(TransactionDto transactionDto);

    TransactionDto toDto(Transaction transaction);

    @Mapping(source = "transactionLimit.sum", target = "limitSum")
    @Mapping(source = "transactionLimit.dateTime", target = "limitDateTime")
    @Mapping(source = "transactionLimit.currencyShortname", target = "limitCurrencyShortname")
    ExceededTransactionDto toExceededTransactionDto(Transaction transaction);
}
