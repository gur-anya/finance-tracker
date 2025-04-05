package UserControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.Main;
import org.ylabHomework.mappers.UserMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class RegistrationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private BindingResult bindingResult;


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