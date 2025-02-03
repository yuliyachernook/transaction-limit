package by.idf.controller;

import by.idf.dto.ExceededTransactionDto;
import by.idf.dto.LimitDto;
import by.idf.service.LimitService;
import by.idf.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/client")
@Tag(name = "Client Controller")
public class ClientController {

    private final LimitService limitService;
    private final TransactionService transactionService;

    @Operation(summary = "Установление нового лимита")
    @PostMapping("/limits/create")
    public ResponseEntity<LimitDto> createLimit(@RequestBody LimitDto limitDto) {
        LimitDto savedLimit = limitService.createLimit(limitDto);
        return ResponseEntity.ok(savedLimit);
    }

    @Operation(summary = "Получение лимитов клиента")
    @GetMapping("/accounts/{accountId}/limits")
    public ResponseEntity<List<LimitDto>> getAllLimitsByAccountId(@PathVariable @Parameter(description = "Банковский счет клиента", required = true) Long accountId) {
        List<LimitDto> limitsByAccountId = limitService.getAllLimitsByAccountId(accountId);
        return ResponseEntity.ok(limitsByAccountId);
    }

    @Operation(summary = "Получение транзакций, превысивших лимит")
    @GetMapping("/accounts/{accountFrom}/transactions/exceeded")
    public ResponseEntity<List<ExceededTransactionDto>> getExceededTransactions(@PathVariable @Parameter(description = "Банковский счет клиента", required = true) Long accountFrom) {
        List<ExceededTransactionDto> exceededTransactions = transactionService.getExceededTransactions(accountFrom);
        return ResponseEntity.ok(exceededTransactions);
    }
}
