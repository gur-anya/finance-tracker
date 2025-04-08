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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.controllers.userControllers.RegistrationController;
import org.ylabHomework.mappers.UserMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import javax.validation.*;
import java.nio.charset.StandardCharsets;

import org.ylabHomework.serviceClasses.UniqueConstraintUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        MockitoAnnotations.initMocks(this);


        UniqueConstraintUser mockValidator = mock(UniqueConstraintUser.class);
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
                    public void releaseInstance(ConstraintValidator<?, ?> instance) {

                    }
                })
                .buildValidatorFactory()
                .getValidator();


        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }

    @Test
    @DisplayName("GET-запрос к /registration")
    public void testShowRegistrationPage() throws Exception {

        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.REGISTRATION_JSP));
    }

    @Test
    @DisplayName("Регистрация с ошибками валидации")
    public void registerUserSuccess() throws Exception {

        String jsonRequest = "{\"name\":\"anya anya\",\"email\":\"any@ya.ru\",\"password\":\"5678\",\"repeatedPassword\":\"5678\", \"role\":\"1\"}";


        User mockUser = new User("anya anya", "any@ya.ru", "5678", 1);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userMapper.toModel(any(BasicUserDTO.class))).thenReturn(mockUser);
        MvcResult result = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);


        assertThat(responseContent).contains("Успешно!");

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseMessageDTO responseDTO = objectMapper.readValue(responseContent, ResponseMessageDTO.class);


        assertThat(responseDTO.getMessage()).isEqualTo("Успешно!");
        verify(userService).createUser("anya anya", "any@ya.ru", "5678");
    }

    @Test
    @DisplayName("Регистрация с ошибками валидации")
    public void registerUserWithValidationErrors() throws Exception {

        String jsonRequest = "{\"name\":\"\",\"email\":\"email\",\"password\":\"5678\", \"repeatedPassword\":\"5678\", \"role\":\"1\"}";

        when(bindingResult.hasErrors()).thenReturn(true);

        MvcResult result = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);


        ObjectMapper objectMapper = new ObjectMapper();
        ResponseMessageDTO responseDTO = objectMapper.readValue(responseContent, ResponseMessageDTO.class);


        assertThat(responseDTO.getMessage()).contains("Имя не должно быть пустым");
        assertThat(responseDTO.getMessage()).contains("Введите корректный email");


        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }
}