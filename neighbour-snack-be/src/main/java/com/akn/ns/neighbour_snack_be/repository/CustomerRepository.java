package com.akn.ns.neighbour_snack_be.repository;

import com.akn.ns.neighbour_snack_be.entity.Customer;
import com.akn.ns.neighbour_snack_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByUserEmail(String email);

}