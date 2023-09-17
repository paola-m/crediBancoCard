package com.crediBanco.crediBancoCard.service;

import com.crediBanco.crediBancoCard.dto.TransactionAnnulationResponse;
import com.crediBanco.crediBancoCard.dto.TransactionResponse;
import com.crediBanco.crediBancoCard.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;


public interface TransactionService {
    Transaction consultTransaction (Integer transactionId);
    ResponseEntity<TransactionResponse> balanceTransactional (String cardId, Integer price);
    ResponseEntity<TransactionResponse> getTransaction (String transactionId);
    ResponseEntity<TransactionAnnulationResponse> transactionAnnulation (Integer transactionId, String cardId);

}
