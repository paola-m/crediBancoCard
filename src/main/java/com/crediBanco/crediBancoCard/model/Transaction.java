package com.crediBanco.crediBancoCard.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name ="transaction_", schema = "credibanco")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "transaxtion_type")
    private String transactionType ;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "time")
    private Date time;

    @Column(name = "transaction_cancelled")
    private Boolean transactionCancelled ;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "card_id")
    private Card cardId ;
}
