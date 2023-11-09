package com.gihae.jpaspecification.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ColumnDefault("now()")
    String createdAt;

    Double sales;
    Integer age;
    Boolean status;
}
