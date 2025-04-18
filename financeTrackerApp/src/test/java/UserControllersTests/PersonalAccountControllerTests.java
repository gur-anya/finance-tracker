package UserControllersTests;


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
import org.ylabHomework.Main;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class PersonalAccountControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private MockHttpSession session;


    @BeforeEach
    public void setup() {
        User testUser = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", testUser);
        session.setAttribute("username", testUser.getName());
        session.setAttribute("useremail", testUser.getEmail());
    }

    @Test
    @DisplayName("GET-запрос к /personal_account")
    public void testPersonalAccountPage() throws Exception {
        mockMvc.perform(get("/personal_account")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.PERSONAL_ACCOUNT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /delete_account")
    public void testDeleteAccountPage() throws Exception {
        mockMvc.perform(get("/delete_account")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.DELETE_ACCOUNT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /update_account")
    public void testUpdateAccountPage() throws Exception {
        mockMvc.perform(get("/update_account")
                        .session(session))
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

        when(userService.updatePassword(any(), anyString()))
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
        when(userService.updatePassword(any(), anyString()))
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

