package com.akn.ns.neighbour_snack_be.service.impl;

import com.akn.ns.neighbour_snack_be.entity.Customer;
import com.akn.ns.neighbour_snack_be.entity.User;
import com.akn.ns.neighbour_snack_be.repository.CustomerRepository;
import com.akn.ns.neighbour_snack_be.repository.UserRepository;
import com.akn.ns.neighbour_snack_be.service.FileService;
import com.akn.ns.neighbour_snack_be.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

import static com.akn.ns.neighbour_snack_be.entity.User.Role.ROLE_CUSTOMER;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void createCustomer(String name, String email, String password, String phoneNumber) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(ROLE_CUSTOMER)
                .isActive(TRUE)
                .phoneNumber(phoneNumber)
                .build();

        userRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .userData(Customer.UserData.builder()
                        .secretKey(UUID.randomUUID())
                        .secretKeyStatus(false)
                        .build())
                .addresses(new ArrayList<>())
                .build();

        customerRepository.save(customer);
    }
}
