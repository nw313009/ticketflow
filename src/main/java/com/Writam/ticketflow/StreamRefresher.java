package com.Writam.ticketflow;

import java.util.List;
import java.util.stream.Collectors;

public class StreamRefresher {
    record Ticket(String title, String status, int priority) {}

    public static void main(String[] args) {
        List<Ticket> tickets = List.of(
                new Ticket("Login broken", "OPEN", 1),
                new Ticket("Slow dashboard", "IN_PROGRESS", 3),
                new Ticket("Cannot export CSV", "OPEN", 2),
                new Ticket("UI glitch", "RESOLVED", 4),
                new Ticket("Payment failed", "OPEN", 1)
        );

        //Filter: keep only OPEN tickets
        List<Ticket> openTickets = tickets.stream()
                .filter(t -> t.status().equals("OPEN"))
                .collect(Collectors.toList());
        System.out.println("Open tickets: " + openTickets);

        //Map: extract just the titles
        List<String> titles = tickets.stream()
                .map(Ticket::title)
                .collect(Collectors.toList());
        System.out.println("All titles: " + titles);

        //Chain: filter + sort + collect
        List<Ticket> urgentOpen = tickets.stream()
                .filter(t -> t.status().equals("OPEN"))
                .sorted((a,b) -> Integer.compare(a.priority(), b.priority()))
                .collect(Collectors.toList());
        System.out.println("Urgent open:" + urgentOpen);
        //Count
        long openCount = tickets.stream()
                .filter(t->t.status().equals("OPEN"))
                .count();
        System.out.println("Open count: " +openCount);

        //Any match?
        boolean hasResolved = tickets.stream()
                .anyMatch(t->t.status().equals("RESOLVED"));
        System.out.println("Has resolved? " + hasResolved);

    }
}