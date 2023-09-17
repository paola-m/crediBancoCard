package com.crediBanco.crediBancoCard.service.serviceImp;

import com.crediBanco.crediBancoCard.dto.CardDto;
import com.crediBanco.crediBancoCard.dto.CardResponse;
import com.crediBanco.crediBancoCard.dto.errors.SystemException;
import com.crediBanco.crediBancoCard.model.Card;
import com.crediBanco.crediBancoCard.repository.CardRepository;
import com.crediBanco.crediBancoCard.service.CardService;
import com.crediBanco.crediBancoCard.utils.Constans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CardServiceImp implements CardService {

    @Autowired
    CardRepository cardRepository;



    public Card getCard(String cardId){
        Optional<Card> card = cardRepository.findById( new BigInteger(cardId));
        this.validationNullCard(card, "cardId");

        return card.get();
    }


    @Override
    public CardDto getCards(String productId) {

        String numerousOratorios = new Random()
                .ints(10, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        String numCard = productId.concat(numerousOratorios);

        this.createCard(new BigInteger(numCard), Integer.parseInt(productId));

        return Objects.requireNonNull(ResponseEntity.ok(CardDto.builder().cardId(numCard)).getBody()).build();

    }

    @Override
    public ResponseEntity<CardResponse> activateCard(String cardId) {

        Card cardModel = this.getCard(cardId);
        cardModel.setIsActive(true);
        cardRepository.save(cardModel);

        return ResponseEntity.ok(this.buildResponseCard(cardModel, "UPDATE: CARD ACTIVATED"));
    }

    @Override
    public ResponseEntity<CardResponse> deleteCard(String cardId) {
        Card detectiveCard = this.getCard(cardId);
        detectiveCard.setIsActive(false);
        return ResponseEntity.ok(this.buildResponseCard(detectiveCard, "DELETE: CARD DEACTIVATED"));
    }

    @Override
    public ResponseEntity<CardResponse> rechargeCredit(String cardId, String balance) {

        Card updateCredit = this.getCard(cardId);
        this.validationActiveCard(updateCredit.getIsActive());
        updateCredit.setSaldo(Integer.parseInt(balance));
        cardRepository.save(updateCredit);

        return ResponseEntity.ok(this.buildResponseCard(updateCredit, "UPDATE: RECHARGED CREDIT"));
    }

    @Override
    public ResponseEntity<String> skipConsultation(String cardId) {
        Card consult = this.getCard(cardId);

        return ResponseEntity.ok("Your credit is: "+consult.getSaldo());
    }


    private void createCard(BigInteger numCard, Integer productId){
        Card card = Card.builder()
                .cardId(numCard)
                .productId(productId)
                .holderName("N.N")
                .expirationDate(this.addThreeYears())
                .saldo(0)
                .isActive(false)
                .isBlockend(false)
                .balance(123)
                .build();
        cardRepository.save(card);
    }


    private String addThreeYears(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 3);

        return calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
    }
    @Override
    public void validationCreditCard (Integer saldo, Integer transactionValue){
        if(saldo==0 || saldo< transactionValue){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_003)
                    .description(Constans.LOG_ERROR_003_DESCRIPTION)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .tecError(Constans.VALIDATION_ERROR)
                    .build();
        }
    }
    @Override
    public void  validationActiveCard(Boolean isActive){
        if(!isActive){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_004)
                    .description(Constans.LOG_ERROR_004_DESCRIPTION)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .tecError(Constans.VALIDATION_ERROR)
                    .build();
        }
    }
    @Override
    public CardResponse buildResponseCard(Card card, String message){

        return CardResponse.builder()
                .message(message)
                .cardId(card.getCardId())
                .productId(card.getProductId())
                .holderName(card.getHolderName())
                .expirationDate(card.getExpirationDate())
                .saldo((card.getSaldo()))
                .isActive(card.getIsActive())
                .isBlocked(card.getIsBlockend())
                .balance(card.getBalance())
                .build();
    }


    private void validationNullCard(Optional<Card> value, String nameField){
        if(value.isEmpty()){
            throw SystemException.builder()
                    .code(Constans.LOG_ERROR_002)
                    .description(nameField+Constans.LOG_ERROR_002_DESCRIPTION)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .tecError(Constans.VALIDATION_ERROR)
                    .build();
        }
    }

}
