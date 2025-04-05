package UserControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.controllers.GreetingController;
import org.ylabHomework.controllers.userControllers.AuthorizationController;
import org.ylabHomework.mappers.UserMappers.LoginMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.serviceClasses.UniqueConstraintUser;
import org.ylabHomework.services.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthorizationAndGreetingControllersTests {
    private MockMvc mockMvcAuthorization;
    private MockMvc mockMvcGreeting;

    @Mock
    private LoginMapper loginMapper;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthorizationController authorizationController;
    @InjectMocks
    private GreetingController greetingController;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        MockitoAnnotations.initMocks(this);


        UniqueConstraintUser mockValidator =
                mock(UniqueConstraintUser.class);
        when(mockValidator.isValid(anyString(), any())).thenReturn(true);

        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .constraintValidatorFactory(new ConstraintValidatorFactory() {
                    @Override
                    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                        if (key == UniqueConstraintUser.class) {
                            return (T) mockValidator;
                        }
                        return new ConstraintValidatorFactoryImpl().getInstance(key);
                    }

                    @Override
                    public void releaseInstance(ConstraintValidator<?, ?> instance) {}
                })
                .buildValidatorFactory()
                .getValidator();

        mockMvcAuthorization = MockMvcBuilders.standaloneSetup(authorizationController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();

        mockMvcGreeting = MockMvcBuilders.standaloneSetup(greetingController).build();
    }

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

    @Test
    @DisplayName("Попытка выхода без сессии")
    void logoutNoSession() throws Exception {
        
        mockMvcAuthorization.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    private ResponseMessageDTO parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(content, ResponseMessageDTO.class);
    }
}