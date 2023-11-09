package com.gihae.jpaspecification.customer;

import com.gihae.jpaspecification.item.Item;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerSpecs {

    private final CustomerRepository customerRepository;

    public List<Customer> findCustomersByCreatedAtAndSales(Double amount){
        return customerRepository.findAll(
                isLongTermCustomer().or(hasSalesOfMoreThan(amount))
        );
    }

    public List<Customer> findCustomers(Long customerId, String start, String end, String status){
        Specification<Customer> spec = Specification.where(createdAtBetween(start, end))
                .and(statusEqual(status))
                .and(conditionEqual(customerId));
        return customerRepository.findAll(spec);
    }

    public void deleteCustomersByAge(Integer age){
        customerRepository.delete(ageLessThanOrEqualTo(age));
    }

    private Specification<Customer> isLongTermCustomer(){
        return (root, query, builder) -> {
            LocalDate date = LocalDate.now().minusYears(2);
            return builder.lessThan(root.get("createdAt"), date);
        };
    }

    private Specification<Customer> hasSalesOfMoreThan(Double value){
        return (root, query, builder) ->
                builder.greaterThan(root.get("sales"), value);
    }

    private Specification<Customer> ageLessThanOrEqualTo(Integer age){
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("age"), age);
    }

    private Specification<Customer> createdAtBetween(String start, String end) {
        return (root, query, builder) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startDate = start == null ? LocalDateTime.now().minusMonths(3) : LocalDate.parse(start, formatter).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(end, formatter).plusDays(1).atStartOfDay();
            return builder.between(root.get("startTime"), startDate, endDate);
        };
    }

    public static Specification<Customer> statusEqual(String status) {
        return (root, query, builder) -> switch (status) {
            case "true" -> builder.equal(root.get("status"), true);
            case "false" -> builder.equal(root.get("status"), false);
            default -> builder.conjunction();
        };
    }

    public static Specification<Customer> conditionEqual(Long customerId) {
        return (root, query, builder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Item> productRoot = subquery.from(Item.class);
            Join<Item, Customer> productCustomerJoin = productRoot.join("customer", JoinType.INNER);
            subquery.select(productCustomerJoin.get("id"))
                    .where(
                            builder.and(
                                    builder.isTrue(productRoot.get("status")),
                                    builder.equal(productRoot.get("customer").get("id"), customerId)
                            )
                    );

            return root.get("id").in(subquery);
        };
    }
}
