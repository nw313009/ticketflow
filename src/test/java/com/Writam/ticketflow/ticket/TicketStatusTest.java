package com.Writam.ticketflow.ticket;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.Test;


public class TicketStatusTest {

    @Test
    void testAllInvalidTransitionsNested () {
        //Map all combinations into a flat list of executable assertions
        Executable[] invalidAssertions = Arrays.stream(TicketStatus.values())
                .flatMap(source -> Arrays.stream(TicketStatus.values())
                //Only include the combinations that should be invalid
                    .filter(target -> !isValidTransition(source,target))
                //Create an assertion for each invalid pair
                    .map(target -> (Executable) ()-> assertFalse(
                        source.canTransitionTo(target),String.format("Invalid transition failed: %s should NOT go to %s", source,target)
                ))
                ).toArray(Executable[] :: new);

        //Run them all together if multiple fail, JUnit reports ALL of them.
        assertAll(invalidAssertions);

    }

    @Test
    void testAllValidTransitionsNested() {
        Executable [] validAssertions = Arrays.stream(TicketStatus.values())
                .flatMap(source -> Arrays.stream(TicketStatus.values())
                        .filter(target ->isValidTransition(source,target))
                        .map(target -> (Executable) () -> assertTrue(
                                source.canTransitionTo(target),
                                String.format("Valid transition failed %s should be able to go to %s", source, target)
                        ))
                ).toArray(Executable[]::new);
        assertAll(validAssertions);
    }
    private boolean isValidTransition(TicketStatus source, TicketStatus target){
        return switch (source) {
            case NEW -> target == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> target == TicketStatus.RESOLVED;
            case RESOLVED -> target == TicketStatus.CLOSED || target == TicketStatus.IN_PROGRESS;
            case CLOSED -> false;
        };
    }




    }

