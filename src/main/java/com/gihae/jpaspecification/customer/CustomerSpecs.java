package com.gihae.jpaspecification.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CustomerSpecs {

    private final CustomerRepository customerRepository;

    public List<Customer> getCustomers(){
        return customerRepository.findAll(isLongTermCustomer());
    }

    public Specification<Customer> isLongTermCustomer(){
        return (root, query, builder) -> {
            LocalDate date = LocalDate.now().minusYears(2);
            return builder.lessThan(root.get(Customer.createdAt), date);
        };
    }
}
