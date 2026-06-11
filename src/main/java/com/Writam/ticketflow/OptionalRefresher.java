package com.Writam.ticketflow;

import java.util.Optional;

public class OptionalRefresher{

    public static void main(String[] args) {
        //simulates: userRepository.findById(id)
        //User exists

        Optional<String> found = findUser("Alice");
        System.out.println("Is present? " + found.isPresent());

        // Safe access - provide a fallback
        String name = found.orElse("Unknown");
        System.out.println("Name: " + name);

        //orElseThrow - what you'll actually use in services
        //pattern for find it or throw 404

        String user = found.orElseThrow(() ->
                new RuntimeException("User not found")
        );

        System.out.println("User: " +user);

        //User doesn't exist

        Optional<String> missing = findUser("bob");
        System.out.println("Bob present? " + missing.isPresent());
        //map - transform the value IF it exists
        Optional<Integer> nameLength = found.map(String::length);
        System.out.println("Name length: " + nameLength.orElse(0));

    }

    private static Optional<String> findUser(String name) {
        if ( name.equals("Alice")) {
            return Optional.of("Alice");
        }
        return Optional.empty();
    }
}