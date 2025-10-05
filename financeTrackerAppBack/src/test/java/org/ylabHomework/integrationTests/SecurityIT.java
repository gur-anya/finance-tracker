package org.ylabHomework.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.ylabHomework.DTOs.userDTOs.LoginRequestDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserRequestDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.enums.RoleEnum;
import org.ylabHomework.serviceClasses.security.UserDetailsImpl;
import org.ylabHomework.services.GoalService;
import org.ylabHomework.services.KafkaProducer;
import org.ylabHomework.services.TokenService;
import org.ylabHomework.services.TransactionStatisticsService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class SecurityIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TransactionStatisticsService transactionStatisticsService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoalService goalService;
    @MockBean
    private KafkaProducer kafkaProducer;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private User user;
    private User admin;
    private UserDetailsImpl userDetails;
    private UserDetailsImpl inactiveUserDetails;
    private UserDetailsImpl adminUserDetails;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:8.2.0").withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("jwt.secret", () -> "test-secret-for-integration-tests");
        registry.add("frontend.url", () -> "http://localhost:3000");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        try {
            redis.execInContainer("redis-cli", "flushall");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.user = userRepository.save(new User("test", "test@example.com", passwordEncoder.encode("somePass1!"), RoleEnum.USER, true, BigDecimal.ZERO, "cool goal", BigDecimal.valueOf(100000), null));
        userDetails = new UserDetailsImpl(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            true
        );

        User inactiveUser = userRepository.save(new User("inactive test", "inactivetest@example.com", passwordEncoder.encode("somePass12!"), RoleEnum.USER, false, BigDecimal.ZERO, "cool goal", BigDecimal.valueOf(100000), null));
        inactiveUserDetails = new UserDetailsImpl(
            inactiveUser.getId(),
            inactiveUser.getName(),
            inactiveUser.getEmail(),
            inactiveUser.getPassword(),
            inactiveUser.getRole(),
            false
        );

        this.admin = userRepository.save(new User("admin", "admin@example.com", passwordEncoder.encode("somePass13%"), RoleEnum.ADMIN, true, null, null, null, null));
        adminUserDetails = new UserDetailsImpl(
            admin.getId(),
            admin.getName(),
            admin.getEmail(),
            admin.getPassword(),
            admin.getRole(),
            true
        );
    }

    @Test
    void updateUser_whenUserUpdatesSelf_shouldSucceed() throws Exception {
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        updateRequest.setName("New Name");

        mockMvc.perform(patch("/api/v1/users/{userId}", user.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(user(userDetails)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.name", is("New Name")));
    }

    @Test
    void updateUser_withoutToken_shouldReturnUnauthorized() throws Exception {
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        updateRequest.setName("New Name");

        mockMvc.perform(patch("/api/v1/users/{userId}", user.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_whenUserUpdatesAnotherUser_shouldReturnForbidden() throws Exception {
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        updateRequest.setName("New Name");

        mockMvc.perform(patch("/api/v1/users/{userId}", admin.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(user(userDetails)))
            .andExpect(status().isForbidden());
    }

    @Test
    void login_whenUserIsInactive_shouldFail() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("inactivetest@example.com")
                .password("somePass12!"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_whenAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .with(user(adminUserDetails)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users.content", hasSize(3)))
            .andExpect(jsonPath("$.users.content[0].name").exists())
            .andExpect(jsonPath("$.users.content[1].name").exists());
    }

    @Test
    void getAllUsers_whenUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .with(user(userDetails)))
            .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_withBlacklistedToken_shouldReturnUnauthorized() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "somePass1!");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        redisTemplate.opsForValue().set(token, "blacklisted", 2592000000L, TimeUnit.SECONDS);

        mockMvc.perform(get("/api/v1/users/{userId}", user.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_withActiveToken_shouldGetBlacklisted() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "somePass1!");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        mockMvc.perform(post("/api/v1/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        assertThat(redisTemplate.hasKey(token)).isTrue();

        mockMvc.perform(get("/api/v1/users/{userId}", user.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }
}
