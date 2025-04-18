package TransactionControllersTests.TransactionStatsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.StateAndParamDTO;
import org.ylabHomework.Main;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class GoalTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionStatsService transactionStatsService;

    private MockHttpSession session;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
    }

    @Test
    @DisplayName("Успешное получение цели")
    public void testGetGoalSuccess() throws Exception {
        when(transactionStatsService.getGoal(user)).thenReturn(1000.0);

        MvcResult result = mockMvc.perform(get("/get_goal_management")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        SingleParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), SingleParamDTO.class);
        assertThat(response.getParam()).isEqualTo("1000.0");
    }

    @Test
    @DisplayName("Получение цели при нулевом значении")
    public void testGetGoalZero() throws Exception {
        when(transactionStatsService.getGoal(user)).thenReturn(0.0);

        MvcResult result = mockMvc.perform(get("/get_goal_management")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        SingleParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), SingleParamDTO.class);
        assertThat(response.getParam()).isEqualTo("0.0");
    }

    @Test
    @DisplayName("Успешная проверка прогресса цели")
    public void testCheckGoalSuccess() throws Exception {
        when(transactionStatsService.checkGoalProgress(user)).thenReturn(500.0);
        when(transactionStatsService.getGoal(user)).thenReturn(1000.0);

        MvcResult result = mockMvc.perform(get("/get_check_goal")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        StateAndParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StateAndParamDTO.class);
        assertThat(response.getState()).isEqualTo("500.0");
        assertThat(response.getParam()).isEqualTo("1000.0");
    }

    @Test
    @DisplayName("Проверка прогресса цели при SQLException")
    public void testCheckGoalSQLException() throws Exception {
        when(transactionStatsService.checkGoalProgress(user)).thenReturn(0.0);
        when(transactionStatsService.getGoal(user)).thenReturn(1000.0);

        MvcResult result = mockMvc.perform(get("/get_check_goal")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        StateAndParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StateAndParamDTO.class);
        assertThat(response.getState()).isEqualTo("0.0");
        assertThat(response.getParam()).isEqualTo("1000.0");
    }

    @Test
    @DisplayName("Успешное обновление цели")
    public void testUpdateGoalSuccess() throws Exception {
        String jsonRequest = "{\"newValue\":1500.0}";

        MvcResult result = mockMvc.perform(post("/update_goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Успешно обновлено!");
        verify(transactionStatsService).setGoal(user, 1500.0);
    }

    @Test
    @DisplayName("Неуспешное обновление цели с некорректным значением")
    public void testUpdateGoalValidationFailure() throws Exception {
        String jsonRequest = "{\"newValue\":-100.0}";

        MvcResult result = mockMvc.perform(post("/update_goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Новое значение должно быть положительным!");
        verify(transactionStatsService, never()).setGoal(any(User.class), anyDouble());
    }
}
