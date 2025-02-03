package by.idf.service;

import lombok.extern.slf4j.Slf4j;
import by.idf.dto.LimitDto;
import by.idf.entity.Limit;
import by.idf.mapper.LimitMapper;
import by.idf.repository.LimitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LimitService {

    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;

    public LimitDto createLimit(LimitDto limitDto) {

        Limit transactionLimit = limitMapper.toEntity(limitDto);

        transactionLimit.setDateTime(ZonedDateTime.now());
        limitRepository.save(transactionLimit);

        log.info("Limit set to {} for account: {}", limitDto.getSum(), limitDto.getAccountId());

        return limitMapper.toDto(transactionLimit);
    }

    public List<LimitDto> getAllLimitsByAccountId(Long accountId) {
        return limitRepository.findAllByAccountId(accountId)
                .stream()
                .map(limitMapper::toDto)
                .toList();
    }
}
