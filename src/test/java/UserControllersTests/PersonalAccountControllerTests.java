package UserControllersTests;

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
import org.ylabHomework.controllers.userControllers.PersonalAccountController;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PersonalAccountControllerTests {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private MockHttpSession session;

    @InjectMocks
    private PersonalAccountController personalAccountController;

    @BeforeEach
    public void setup() {

        User testUser = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", testUser);
        session.setAttribute("username", testUser.getName());
        session.setAttribute("useremail", testUser.getEmail());
        MockitoAnnotations.initMocks(this);


        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();


        mockMvc = MockMvcBuilders.standaloneSetup(personalAccountController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }

    @Test
    @DisplayName("GET-запрос к /personal_account")
    public void testPersonalAccountPage() throws Exception {
        mockMvc.perform(get("/personal_account"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.PERSONAL_ACCOUNT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /delete_account")
    public void testDeleteAccountPage() throws Exception {
        mockMvc.perform(get("/delete_account"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.DELETE_ACCOUNT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /update_account")
    public void testUpdateAccountPage() throws Exception {
        mockMvc.perform(get("/update_account"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.UPDATE_ACCOUNT_JSP));
    }

    @Test
    @DisplayName("Удаление аккаунта")
    public void testDeleteAccount() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("useremail", "anya@ya.ru");
        session.setAttribute("loggedUser", "anya");
        session.setAttribute("username", "anya");
        session.setAttribute("transactionRepository", "transRep");


        when((userService).deleteUserByEmail("anya@ya.ru")).thenReturn("Пользователь anya@ya.ru успешно удалён!");


        mockMvc.perform(delete("/delete_account")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/"));


        verify(userService).deleteUserByEmail("anya@ya.ru");
    }

    @Test
    @DisplayName("Успешное обновление имени")
    void updateNameSuccess() throws Exception {
        String jsonRequest = "{\"name\":\"newAnya\", \"email\":\"anya@ya.ru\", \"updatedValues\":\"name\"}";

        when(userService.updateName(anyString(), anyString())).thenReturn("newAnya, имя изменено успешно!");

        MvcResult result = mockMvc.perform(post("/update_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("newAnya, имя изменено успешно!");
        verify(userService).updateName("newAnya", "anya@ya.ru");
    }

    @Test
    @DisplayName("Безуспешное обновление аккаунта: email занят")
    void updateEmailFailAlreadyExists() throws Exception {
        
        String jsonRequest = "{\"name\":\"anya\", \"email\":\"anya@ya.ru\",\"updatedValues\":\"email\"}";

        when(userService.emailCheck(anyString()))
                .thenReturn("Пользователь с таким email уже зарегистрирован!");

        
        MvcResult result = mockMvc.perform(post("/update_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isConflict())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Пользователь с таким email уже зарегистрирован!");
        verify(userService, never()).updateEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Успешное обновление пароля")
    void updatePasswordSuccess() throws Exception {
        
        String jsonRequest = "{\"name\":\"anya\", \"email\":\"anya@ya.ru\", \"password\":\"5678\",\"updatedValues\":\"password\"}";

        when(userService.updatePassword(anyString(), anyString()))
                .thenReturn("Пароль успешно обновлён!");

        
        MvcResult result = mockMvc.perform(post("/update_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Пароль успешно обновлён!");
    }

    @Test
    @DisplayName("Безуспешное обновление аккаунта: пустое имя, неверный email")
    void updateAccountInvalidDTO() throws Exception {
        
        String jsonRequest = "{\"name\":\"\", \"email\":\"newAnya\", \"updatedValues\":\"nameemail\"}";

        
        MvcResult result = mockMvc.perform(post("/update_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage())
                .contains("Имя не должно быть пустым")
                .contains("Введите корректный email");
    }

    @Test
    @DisplayName("Частично успешное обновление аккаунта: успешное изменение имени и пароля, безуспешное - email")
    void updateMultipleFieldsPartialSuccess() throws Exception {
        
        String jsonRequest = "{\"name\":\"newAnya\", \"email\":\"anya@ya.ru\", \"password\":\"5678\", \"updatedValues\":\"nameemailpassword\"}";

        when(userService.updateName(anyString(), anyString())).thenReturn("Имя успешно изменено на newAnya!");
        when(userService.emailCheck(anyString()))
                .thenReturn("Пользователь с таким email уже зарегистрирован!");
        when(userService.updatePassword(anyString(), anyString()))
                .thenReturn("Пароль успешно обновлён!");

        
        MvcResult result = mockMvc.perform(post("/update_account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isConflict())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage())
                .contains("имя изменено успешно!")
                .contains("Пользователь с таким email уже зарегистрирован!")
                .contains("Пароль успешно обновлён!");
    }
}

