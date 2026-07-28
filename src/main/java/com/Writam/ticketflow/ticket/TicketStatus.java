package com.Writam.ticketflow.ticket;

public enum TicketStatus {

        NEW,
        IN_PROGRESS,
        RESOLVED,
        CLOSED;

        public boolean canTransitionTo(TicketStatus target) {
            return switch (this) {
                case NEW -> target == IN_PROGRESS;
                case IN_PROGRESS -> target == RESOLVED;
                case RESOLVED -> target == CLOSED || target == IN_PROGRESS;
                case CLOSED -> false;  // terminal state
            };
        }

}
