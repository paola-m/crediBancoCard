package com.crediBanco.crediBancoCard.service.serviceImp;

import com.crediBanco.crediBancoCard.dto.TransactionAnnulationResponse;
import com.crediBanco.crediBancoCard.dto.TransactionResponse;
import com.crediBanco.crediBancoCard.dto.errors.SystemException;
import com.crediBanco.crediBancoCard.model.Card;
import com.crediBanco.crediBancoCard.model.Transaction;
import com.crediBanco.crediBancoCard.repository.CardRepository;
import com.crediBanco.crediBancoCard.repository.TransactionRepository;
import com.crediBanco.crediBancoCard.service.CardService;
import com.crediBanco.crediBancoCard.service.TransactionService;
import com.crediBanco.crediBancoCard.utils.Constans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

@Service
public class TransactionServiceImp implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardService cardService;
    @Override
    public Transaction consultTransaction (Integer transactionId){
        Optional<Transaction> transactionConsult = transactionRepository.findById(transactionId);
        this.validationNullTransaction(transactionConsult,"transactionId");
        return transactionConsult.get();
    }
    @Override
    public ResponseEntity<TransactionResponse> balanceTransactional(String cardId, Integer price) {
        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .transactionType("Advance")
                        .amount(price)
                        .time(new Date())
                        .cardId(cardService.getCard(cardId))
                        .transactionCancelled(false)
                        .build());
        this.subtractCreditCard(cardId, price);

        return ResponseEntity.ok(this.buildResponseTransaction(transaction, cardId));
    }

    @Override
    public ResponseEntity<TransactionResponse> getTransaction(String transactionId) {
        Optional<Transaction> transactionConsult = transactionRepository.findById(transactionId);
        this.validationNullTransaction(transactionConsult,"transactionId");

        return ResponseEntity.ok(this.buildResponseTransaction(
                transactionConsult.get(),
                String.valueOf(transactionConsult.get().getCardId().getCardId())));
    }

    @Override
    public ResponseEntity<TransactionAnnulationResponse> transactionAnnulation(Integer transactionId,
                                                                               String cardId) {
        Transaction transaction = this.consultTransaction(transactionId);
        this.validationTransactionAnnulation(transaction.getTransactionCancelled());
        this.validationTimeTransaction(transaction.getTime());

        Card card = cardService.getCard(cardId);
        Integer saldoCard = card.getSaldo()+transaction.getAmount();

        transaction.setTransactionCancelled(true);
        card.setSaldo(saldoCard);

        Transaction transactionUpdate = transactionRepository.save(transaction);
        cardRepository.save(card);


        return ResponseEntity.ok(this.buildResponseTransactionAnnulada(
                transactionUpdate, card));
    }

    private TransactionAnnulationResponse buildResponseTransactionAnnulada (Transaction transaction, Card card){
        return TransactionAnnulationResponse.builder()
                .message("TRANSACTION CANCELLED")
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .time(transaction.getTime())
                .transactionCancelled(transaction.getTransactionCancelled())
                .card(cardService.buildResponseCard(card,""))
                .build();
    }

    private TransactionResponse buildResponseTransaction (Transaction transaction, String cardId){
        return TransactionResponse.builder()
                .message("TRANSACTION COMPLETED")
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .time(transaction.getTime())
                .transactionCancelled(transaction.getTransactionCancelled())
                .cardId(new BigInteger(cardId))
                .build();
    }

    private void subtractCreditCard( String cardId,Integer transactionValue){
        Card card = cardService.getCard(cardId);
        cardService.validationActiveCard(card.getIsActive());
        cardService.validationCreditCard(card.getSaldo(), transactionValue);
        Integer balanceSheet = card.getSaldo() - transactionValue;

        cardService.rechargeCredit(cardId , String.valueOf(balanceSheet));
    }

    private void validationNullTransaction(Optional<Transaction> value, String nameField){
        if(value.isEmpty()){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_002)
                    .description(nameField+Constans.LOG_ERROR_002_DESCRIPTION)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .tecError(Constans.VALIDATION_ERROR)
                    .build();
        }
    }

    private void validationTimeTransaction(Date time){
        Date hace24Horas = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); // Restamos 24 horas en milisegundos

        if(hace24Horas.after(time)){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_002)
                    .description("Transaction went over 24 hours. ")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .tecError("HORA DE TRANSACCION: "+ time)
                    .build();
        }
    }

    private void validationTransactionAnnulation(Boolean transactionAnnulation){

        if(Boolean.TRUE.equals(transactionAnnulation)){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_005)
                    .description(Constans.LOG_ERROR_005_DESCRIPTION)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .tecError(Constans.VALIDATION_ERROR)
                    .build();
        }
    }

}
