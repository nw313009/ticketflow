package com.Writam.ticketflow.ticket;

import com.Writam.ticketflow.auth.dto.AuthResponse;
import com.Writam.ticketflow.auth.dto.LoginRequest;
import com.Writam.ticketflow.auth.dto.RegisterRequest;
import com.Writam.ticketflow.ticket.dto.CreateTicketRequest;
import com.Writam.ticketflow.ticket.dto.UpdateTicketStatusRequest;
import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createTicketWithBadRequest_returns400() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        CreateTicketRequest ticketRequest = new CreateTicketRequest(
                null,
                "The main server won't boot up",
                null
        );

        mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTicket() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        CreateTicketRequest ticketRequest = new CreateTicketRequest(
                "Broken Server",
                "The main server won't boot up",
                Priority.HIGH
        );

        mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Broken Server"))
                .andExpect(jsonPath("$.description").value("The main server won't boot up"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.priority").value(Priority.HIGH.name()));
    }

    @Test
    void getTicket() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        CreateTicketRequest ticketRequest = new CreateTicketRequest(
                "Broken Router",
                "No internet connection",
                Priority.MEDIUM
        );

        String response = createTicketAndReturnResponse(ticketRequest, token);
        Object ticketId = objectMapper.readValue(response, Map.class).get("id");

        mockMvc.perform(get("/api/v1/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.title").value("Broken Router"));
    }

    @Disabled("Deferred to week 3 - globalexceptionhandler")
    @Test
    void getTicketWithBadRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        String nonExistentId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/v1/tickets/" + nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listTicket() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        createTicketAndReturnResponse(
                new CreateTicketRequest("Ticket 1", "Desc 1", Priority.LOW),
                token
        );

        createTicketAndReturnResponse(
                new CreateTicketRequest("Ticket 2", "Desc 2", Priority.HIGH),
                token
        );

        mockMvc.perform(get("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void listTooManyTickets() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        mockMvc.perform(get("/api/v1/tickets?size=1000")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Disabled("Deferred to week 3 - globalexceptionhandler")
    @Test
    void listTicketBadRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        mockMvc.perform(get("/api/v1/tickets?page=abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTicketStatus() throws Exception {
        RegisterRequest staffRequest = new RegisterRequest(
                "agent@ticketflow.com",
                "password123",
                "Agent Staff"
        );

        String staffToken = registerAgentUser(staffRequest);

        RegisterRequest userRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String userToken = registerUser(userRequest);

        String response = createTicketAndReturnResponse(
                new CreateTicketRequest("Status Bug", "Fix state", Priority.LOW),
                userToken
        );

        Object ticketId = objectMapper.readValue(response, Map.class).get("id");

        UpdateTicketStatusRequest updatePayload = new UpdateTicketStatusRequest(
                TicketStatus.IN_PROGRESS
        );

        mockMvc.perform(patch("/api/v1/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Disabled("Deferred to week 3 - globalexceptionhandler")
    @Test
    void invalidStatusTransition() throws Exception {
        RegisterRequest staffRequest = new RegisterRequest(
                "agent@ticketflow.com",
                "password123",
                "Agent Staff"
        );

        String staffToken = registerAgentUser(staffRequest);

        RegisterRequest userRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String userToken = registerUser(userRequest);

        String response = createTicketAndReturnResponse(
                new CreateTicketRequest("Flow Bug", "Check cycle", Priority.LOW),
                userToken
        );

        Object ticketId = objectMapper.readValue(response, Map.class).get("id");

        UpdateTicketStatusRequest invalidPayload = new UpdateTicketStatusRequest(
                TicketStatus.CLOSED
        );

        mockMvc.perform(patch("/api/v1/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPaginatedTickets() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        createTicketAndReturnResponse(
                new CreateTicketRequest("Paging 1", "Desc 1", Priority.LOW),
                token
        );

        createTicketAndReturnResponse(
                new CreateTicketRequest("Paging 2", "Desc 2", Priority.HIGH),
                token
        );

        mockMvc.perform(get("/api/v1/tickets?page=0&size=1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void badPaginationRequestTickets() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(registerRequest);

        createTicketAndReturnResponse(
                new CreateTicketRequest("Lonely Ticket", "Only one here", Priority.LOW),
                token
        );

        mockMvc.perform(get("/api/v1/tickets?page=5&size=10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void roleBasedAccessControl() throws Exception {
        RegisterRequest regularUser = new RegisterRequest(
                "user@example.com",
                "password123",
                "John Doe"
        );

        String token = registerUser(regularUser);

        UpdateTicketStatusRequest updatePayload = new UpdateTicketStatusRequest(
                TicketStatus.IN_PROGRESS
        );

        mockMvc.perform(patch("/api/v1/tickets/" + UUID.randomUUID() + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tryGettingTicketsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tickets"))
                .andExpect(status().isForbidden());
    }

    private String registerUser(RegisterRequest registerRequest) throws Exception {
        String jsonResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse response = objectMapper.readValue(jsonResponse, AuthResponse.class);

        return response.accessToken();
    }

    private String registerAgentUser(RegisterRequest registerRequest) throws Exception {
        registerUser(registerRequest);

        User user = userRepository.findByEmail(registerRequest.email())
                .orElseThrow(() -> new AssertionError("User not found after registration"));

        user.setRole(Role.AGENT);
        userRepository.saveAndFlush(user);

        LoginRequest loginRequest = new LoginRequest(
                registerRequest.email(),
                registerRequest.password()
        );

        String loginResponseStr = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse response = objectMapper.readValue(loginResponseStr, AuthResponse.class);

        return response.accessToken();
    }

    private String createTicketAndReturnResponse(
            CreateTicketRequest ticketRequest,
            String token
    ) throws Exception {
        return mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}