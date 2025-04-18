package UserControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.Main;
import org.ylabHomework.mappers.UserMappers.LoginMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class AuthorizationAndGreetingControllersTests {
    @Autowired
    private MockMvc mockMvcAuthorization;
    @Autowired
    private MockMvc mockMvcGreeting;

    @MockBean
    private LoginMapper loginMapper;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET-запрос к /")
    public void testShowMainPage() throws Exception {
        mockMvcGreeting.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.INDEX_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /login")
    public void testShowLogin() throws Exception {
        mockMvcAuthorization.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.LOGIN_JSP));
    }

    @Test
    @DisplayName("Успешный логин")
    void loginUserSuccess() throws Exception {
        String jsonRequest = "{\"email\":\"anya@ya.ru\",\"password\":\"1234\"}";
        User mockUser = new User("anya", "anya@ya.ru", "1234", 1);

        when(loginMapper.toModel(any(LoginDTO.class))).thenReturn(mockUser);
        when(userService.loginUser(eq("anya@ya.ru"), eq("1234")))
                .thenReturn(new UserService.LoginResult(true, mockUser));


        MvcResult result = mockMvcAuthorization.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();


        ResponseMessageDTO response = parseResponse(result);
        assertThat(response.getMessage()).contains("Успешно!");
    }

    @Test
    @DisplayName("Безуспешный логин с неверным паролем")
    void loginUserFailInvalidCredentials() throws Exception {
        String jsonRequest = "{\"email\":\"anya@ya.ru\",\"password\":\"wrongPass\"}";
        User mockUser = new User("anya", "anya@ya.ru", "wrongPass", 1);

        when(loginMapper.toModel(any(LoginDTO.class))).thenReturn(mockUser);
        when(userService.loginUser(eq("anya@ya.ru"), eq("wrongPass")))
                .thenReturn(new UserService.LoginResult(false, null));


        MvcResult result = mockMvcAuthorization.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andReturn();


        ResponseMessageDTO response = parseResponse(result);
        assertThat(response.getMessage()).contains("Произошла ошибка! Попробуйте еще раз!");
    }

    @Test
    @DisplayName("Безуспешный логин: некорректный email")
    void loginUserFailValidationErrors() throws Exception {

        String jsonRequest = "{\"email\":\"invalid\",\"password\":\"\"}";


        MvcResult result = mockMvcAuthorization.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();


        ResponseMessageDTO response = parseResponse(result);
        assertThat(response.getMessage())
                .contains("Введите корректный email")
                .contains("Пароль не должен быть пустым");
    }

    @Test
    @DisplayName("Успешный выход")
    void logoutSuccess() throws Exception {

        MockHttpSession session = new MockHttpSession();
        User user = new User("anya", "anya@ya.ru", "1234", 1);
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
        session.setAttribute("transactionRepository", transactionRepository);


        mockMvcAuthorization.perform(get("/logout")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));


        assertThat(session.isInvalid()).isTrue();
    }

    private ResponseMessageDTO parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(content, ResponseMessageDTO.class);
    }
}