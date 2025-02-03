package by.idf.mapper;

import by.idf.dto.LimitDto;
import by.idf.entity.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LimitMapper {

    LimitDto toDto(Limit transactionLimit);
    Limit toEntity(LimitDto limitDto);
}
