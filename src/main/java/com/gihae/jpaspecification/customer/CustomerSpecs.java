package com.gihae.jpaspecification.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CustomerSpecs {

    private final CustomerRepository customerRepository;

    public List<Customer> findCustomersByCreatedAt(){
        return customerRepository.findAll(isLongTermCustomer());
    }

    public List<Customer> findCustomersByCreatedAtAndSales(){
        double amount = 200.0;
        return customerRepository.findAll(
                isLongTermCustomer().or(hasSalesOfMoreThan(amount))
        );
    }

    public void deleteCustomersByAge(){
        int age = 18;
        customerRepository.delete(ageLessThanOrEqualTo(age));
    }

    private Specification<Customer> isLongTermCustomer(){
        return (root, query, builder) -> {
            LocalDate date = LocalDate.now().minusYears(2);
            return builder.lessThan(root.get("createdAt"), date);
        };
    }

    private Specification<Customer> hasSalesOfMoreThan(double value){
        return (root, query, builder) ->
                builder.greaterThan(root.get("sales"), value);
    }

    private Specification<Customer> ageLessThanOrEqualTo(int age){
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("age"), age);
    }
}
