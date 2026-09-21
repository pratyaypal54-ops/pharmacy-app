package com.pharmacy.pharmacyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PharmacyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyAppApplication.class, args);
    }
}
