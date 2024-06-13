package com.paymentchain.transactions.entities;

import java.util.Date;

import com.paymentchain.transactions.utils.ChannelEnum;
import com.paymentchain.transactions.utils.StatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String reference;
    private String accountIban;
    private Date date; 
    private double amount;
    private double fee;
    private String description;
    private StatusEnum status;
    private ChannelEnum channel;
}
