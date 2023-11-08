package com.gihae.jpaspecification.customer;

public record MonetaryAmount (
    Double amount,
    Currency currency
){}
