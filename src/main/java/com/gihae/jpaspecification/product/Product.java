package com.gihae.jpaspecification.product;

import com.gihae.jpaspecification.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    Customer customer;
}
