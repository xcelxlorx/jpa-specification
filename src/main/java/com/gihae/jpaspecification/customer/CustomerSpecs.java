package com.gihae.jpaspecification.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public List<Customer> findCustomers(String start, String end, String status){
        Specification<Customer> spec = Specification.where(createdAtBetween(start, end))
                .and(statusEqual(status));
        return customerRepository.findAll(spec);
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

    private Specification<Customer> createdAtBetween(String start, String end) {
        return (root, query, criteriaBuilder) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startDate = start == null ? LocalDateTime.now().minusMonths(3) : LocalDate.parse(start, formatter).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(end, formatter).plusDays(1).atStartOfDay();
            return criteriaBuilder.between(root.get("startTime"), startDate, endDate);
        };
    }

    public static Specification<Customer> statusEqual(String status) {
        return (root, query, criteriaBuilder) -> switch (status) {
            case "true" -> criteriaBuilder.equal(root.get("status"), true);
            case "false" -> criteriaBuilder.equal(root.get("status"), false);
            default -> criteriaBuilder.conjunction();
        };
    }
}
