package UserTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylabHomework.controllers.UserController;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для контроллера пользователей")
public class UserControllerTests {
    private final UserService service = new UserService(new UserRepository());

    @Test
    @DisplayName("Успешный вход существующего пользователя с корректными данными")
    public void loginUserSuccess() {
        String input = "anya@ya.ru\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showMainPageUser();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.loginUser();

        String expectedMessage = "Здравствуйте, anya!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка несуществующего email при входе с последующим успехом")
    public void enterEmailInLogin() {
        String input = "an@ya.ru\n1\nanya@ya.ru\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String finalEmail = controller.enterEmailInLogin();

        String expectedMessage = "Пользователь с таким email не найден!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(finalEmail).isEqualTo("anya@ya.ru");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя с корректными данными")
    public void registerNewUserSuccess() {
        String input = "anya\nanyaa@ya.ru\n1234\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.registerNewUser();

        String output = outContent.toString();
        assertThat(output).contains("Регистрация прошла успешно!");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка ошибки при регистрации с уже существующим email")
    public void registerNewUserWithExistingEmail() {
        String input = "newanya\nanya@ya.ru\nnewanya@ya.ru\n1234\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.registerNewUser();

        String output = outContent.toString();
        assertThat(output).contains("Пользователь с таким email уже зарегистрирован, попробуйте еще раз!");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод имени с пустым значением и последующим корректным вводом")
    public void enterNameInRegistrationWithEmptyInput() {
        String input = "\nanya\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String result = controller.enterNameInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Имя не должно быть пустым!");
        assertThat(result).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод email с некорректным форматом и последующим корректным вводом")
    public void enterEmailInRegistrationWithInvalidInput() {
        String input = "anya\nnya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String result = controller.enterEmailInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пожалуйста, введите корректный email!");
        assertThat(result).isEqualTo("nya@ya.ru");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод email с уже зарегистрированным значением и последующим корректным вводом")
    public void enterEmailInRegistrationWithExistingEmail() {
        String input = "anya@ya.ru\nan@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String result = controller.enterEmailInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пользователь с таким email уже зарегистрирован, попробуйте еще раз!");
        assertThat(result).isEqualTo("an@ya.ru");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешный ввод корректного email с первой попытки")
    public void enterEmailInRegistrationSuccess() {
        String input = "nya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();

        String result = controller.enterEmailInRegistration();
        assertThat(result).isEqualTo("nya@ya.ru");

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод пароля с несовпадением и последующим корректным вводом")
    public void enterPasswordInRegistrationWithMismatch() {
        String input = "1234\n5678\n1234\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String result = controller.enterPasswordInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пароли не совпадают! Пожалуйста, повторите попытку!");
        assertThat(result).isEqualTo("1234");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: просмотр непустого списка пользователей")
    public void adminPageCheckUsers() {

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        doNothing().when(controller).showMainPageAdmin();

        controller.showUsers();
        String expectedMessage = "1. anya@ya.ru, anya";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);

    }

    @Test
    @DisplayName("Администрирование: успешная блокировка пользователя")
    public void adminPageBlockUser() {
        String input = "anya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        doNothing().when(controller).showMainPageAdmin();

        controller.blockUser();
        System.setOut(new PrintStream(outContent));

        String expectedMessage = "Пользователь anya@ya.ru успешно заблокирован!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Проверка входа заблокированного пользователя")
    public void testBlockedUser() {
        String input = "anya@ya.ru\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        UserService myService = spy(new UserService(service.getRepository()));
        UserController controller = spy(new UserController(myService));
        myService.blockUser("anya@ya.ru");
        when(myService.emailCheck("anya@ya.ru")).thenReturn("FOUND");
        when(myService.loginUser("anya@ya.ru", "1234")).thenReturn(new UserService.LoginResult(true, new User("anya", "anya@ya.ru", "1234", 1)));
        when(myService.isUserActive("anya@ya.ru")).thenReturn(false);
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.loginUser();

        String expectedMessage = "К сожалению, ваш аккаунт заблокирован! Обратитесь к администратору.";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Удаление аккаунта администратором")
    void deleteAccountAdmin(){
        String input = "anya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(in);
        UserService service = mock(UserService.class);
        doReturn("Пользователь anya@ya.ru успешно удалён!").when(service).deleteUserByEmail(anyString());
        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showMainPageAdmin();
        controller.deleteUserByEmail();
        verify(service).deleteUserByEmail(anyString());
        System.setOut(System.out);
        System.setIn(System.in);

    }

    @Test
    @DisplayName("Разблокировка аккаунта администратором")
    void unblockAccountAdmin(){
        String input = "anya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(in);
        UserService service = mock(UserService.class);
        doReturn("Пользователь anya@ya.ru успешно разблокирован!").when(service).unblockUser(anyString());
        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showMainPageAdmin();
        controller.unblockUser();
        verify(service).unblockUser(anyString());
        System.setOut(System.out);
        System.setIn(System.in);

    }

    @Test
    @DisplayName("Изменение пароля")
    void updatePass(){
        String input = "1234\n5678\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(in);
        UserService service = mock(UserService.class);
        doReturn("Пароль успешно обновлён!").when(service).updatePassword(anyString(), anyString(), anyString());
        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(new User("anya", "anya@ya.ru", "1234", 1));

        doNothing().when(controller).showPersonalAccountSettings();
        controller.updatePass();
        verify(service).updatePassword(anyString(), anyString(), anyString());
        System.setOut(System.out);
        System.setIn(System.in);

    }
    @Test
    @DisplayName("Изменение почты")
    void updateEmail(){
        String input = "newanya@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(in);
        UserService service = mock(UserService.class);
        doReturn("Адрес электронной почты обновлён на newanya@ya.ru!").when(service).updateEmail(anyString(), anyString());
        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(new User("anya", "anya@ya.ru", "1234", 1));

        doNothing().when(controller).showPersonalAccountSettings();
        controller.updateEmail();
        verify(service).updateEmail(anyString(), anyString());
        System.setOut(System.out);
        System.setIn(System.in);

    }

    @Test
    @DisplayName("Изменение имени")
    void updateName(){
        String input = "newname\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(in);
        UserService service = mock(UserService.class);
        doReturn("Имя успешно изменено на newname!").when(service).updateName(anyString(), anyString());
        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(new User("anya", "anya@ya.ru", "1234", 1));

        doNothing().when(controller).showPersonalAccountSettings();
        controller.updateName();
        verify(service).updateName(anyString(), anyString());
        System.setOut(System.out);
        System.setIn(System.in);
    }

}