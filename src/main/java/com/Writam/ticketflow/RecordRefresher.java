package com.Writam.ticketflow;


import java.time.LocalDateTime;

public class RecordRefresher {

    record UserDTO(String email, String fullName, String role) {}

    record TicketDTO(Long id, String title, String status, LocalDateTime createdAt) {}

    public static void main(String [] args) {

        //Constructors generated - all fields are required
        UserDTO user = new UserDTO("alice@test.com", "Alice Smith", "CUSTOMER");

        //Getters use field name not getFieldName()

        System.out.println("Email: " +user.email());
        System.out.println("Name: " + user.fullName());

        //tString is generated automatically
        System.out.println("Full object: " + user);

        //equals compares all fields by value, not reference

        UserDTO sameUser = new UserDTO("alice@test.com", "Alice Smith", "CUSTOMER");
        System.out.println("Equal? " + user.equals(sameUser));

        //Records are immutable - no setters(nothing to add to the constructor)
        TicketDTO ticket = new TicketDTO(1L, "Login broken", "OPEN", LocalDateTime.now());
        System.out.println("Ticket: " + ticket);
        System.out.println("Status: " + ticket.status());
    }

}