package com.Writam.ticketflow;

public class EnumRefresher {
    enum Role {
        CUSTOMER, AGENT, ADMIN
    }

    enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED;

        public boolean canTransitionTo(TicketStatus target) {
            return switch (this) {
                case OPEN -> target == IN_PROGRESS || target == CLOSED;
                case IN_PROGRESS -> target == RESOLVED || target == OPEN;
                case RESOLVED -> target == CLOSED || target == OPEN;
                case CLOSED -> target == OPEN;  // reopen
            };
        }
    }


    public static void main(String[] args) {
        Role role = Role.ADMIN;
        System.out.println("Role: " + role);
        System.out.println("Is admin? " + (role == Role.ADMIN));

        TicketStatus current = TicketStatus.OPEN;
        System.out.println("OPEN -> IN_PROGRESS: " + current.canTransitionTo(TicketStatus.IN_PROGRESS));  // true
        System.out.println("OPEN -> RESOLVED: " + current.canTransitionTo(TicketStatus.RESOLVED));

        for (TicketStatus from : TicketStatus.values()) {
            for (TicketStatus to : TicketStatus.values()) {
                if (from != to && from.canTransitionTo(to)) {
                    System.out.println(from + " -> " + to + ": ALLOWED");


                }
            }
        }
    }

}