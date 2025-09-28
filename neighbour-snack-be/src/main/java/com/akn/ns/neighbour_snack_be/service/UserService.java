package com.akn.ns.neighbour_snack_be.service;

public interface UserService {

    boolean userExistsByEmail(String email);

    void createCustomer(String name, String email, String password, String phoneNumber);

}
