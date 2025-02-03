package by.idf.controller;

import by.idf.dto.TransactionDto;
import by.idf.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Bank Controller")
public class BankController {

    private final TransactionService transactionService;

    @Operation(summary = "Создание транзакции")
    @PostMapping("/transactions/create")
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transaction) {
        TransactionDto savedTransaction = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }
}
