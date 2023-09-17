package com.crediBanco.crediBancoCard.service;

import com.crediBanco.crediBancoCard.dto.CardDto;
import com.crediBanco.crediBancoCard.dto.CardResponse;
import com.crediBanco.crediBancoCard.model.Card;
import org.springframework.http.ResponseEntity;

public interface CardService {
     Card getCard(String cardId);
     CardDto getCards (String productId);
     ResponseEntity<CardResponse> activateCard (String cardId);
     ResponseEntity<CardResponse> deleteCard (String cardId);
     ResponseEntity<CardResponse> rechargeCredit (String cardId, String balance);
     ResponseEntity<String> skipConsultation (String cardId);
     void validationCreditCard (Integer saldo, Integer transactionValue);
     void  validationActiveCard(Boolean isActive);
     CardResponse buildResponseCard(Card card, String message);
}
