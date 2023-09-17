package com.crediBanco.crediBancoCard.controller;

import com.crediBanco.crediBancoCard.dto.BalanceTransactionalDto;
import com.crediBanco.crediBancoCard.dto.TransactionAnnulationResponse;
import com.crediBanco.crediBancoCard.dto.TransactionAnulationDto;
import com.crediBanco.crediBancoCard.dto.TransactionResponse;
import com.crediBanco.crediBancoCard.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/card")
public class Transaction {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction/purchase")
    public ResponseEntity<TransactionResponse> balanceTransactional (
            @Valid @RequestBody BalanceTransactionalDto request) {

        return transactionService.balanceTransactional(
                request.getCardId(),
                request.getPrice());
    }

    @GetMapping(value="/transaction/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@Valid @PathVariable("transactionId") String transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @PostMapping("/transaction/anulation")
    public ResponseEntity<TransactionAnnulationResponse> transactionAnulation (
            @Valid @RequestBody TransactionAnulationDto request) {

        return transactionService.transactionAnnulation(request.getTransactionId(),
                                                        request.getCardId());
    }
}
