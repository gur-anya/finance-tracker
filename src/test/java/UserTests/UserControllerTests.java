//package UserTests;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.ylabHomework.controllers.UserController;
//import org.ylabHomework.models.User;
//import org.ylabHomework.services.UserService;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.PrintStream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@DisplayName("Тесты для контроллера пользователей")
//public class UserControllerTests {
//    private final UserService service = mock(UserService.class);
//
//    @Test
//    @DisplayName("Успешный вход существующего пользователя с корректными данными")
//    public void loginUserSuccess() {
//        String input = "anya@ya.ru\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.emailCheck("anya@ya.ru")).thenReturn("FOUND");
//        when(service.loginUser("anya@ya.ru", "1234")).thenReturn(new UserService.LoginResult(true, new User("anya", "anya@ya.ru", "1234", 1)));
//        when(service.isUserActive("anya@ya.ru")).thenReturn(true);
//        doNothing().when(controller).showMainPageUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.loginUser();
//
//        String expectedMessage = "Здравствуйте, anya!";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
//        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Обработка несуществующего email при входе с последующим успехом")
//    public void enterEmailInLogin() {
//        String input = "an@ya.ru\n1\nanya@ya.ru\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = new UserController(service);
//        when(service.emailCheck("an@ya.ru")).thenReturn("NOT_FOUND");
//        when(service.emailCheck("anya@ya.ru")).thenReturn("FOUND");
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        String finalEmail = controller.enterEmailInLogin();
//
//        String expectedMessage = "Пользователь с таким email не найден!";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        assertThat(finalEmail).isEqualTo("anya@ya.ru");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Успешная регистрация нового пользователя с корректными данными")
//    public void registerNewUserSuccess() {
//        String input = "anya\nanyaa@ya.ru\n1234\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.nameCheck("anya")).thenReturn("OK");
//        when(service.emailCheck("anyaa@ya.ru")).thenReturn("OK");
//        when(service.checkPasswordMatch("1234", "1234")).thenReturn(true);
//        doNothing().when(service).createUser("anya", "anyaa@ya.ru", "1234");
//        doNothing().when(controller).showGreetingScreen();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.registerNewUser();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Регистрация прошла успешно!");
//        verify(service).createUser("anya", "anyaa@ya.ru", "1234");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Обработка ошибки при регистрации с уже существующим email")
//    public void registerNewUserWithExistingEmail() {
//        String input = "newanya\nanya@ya.ru\nnewanya@ya.ru\n1234\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.nameCheck("newanya")).thenReturn("OK");
//        when(service.emailCheck("anya@ya.ru")).thenReturn("TAKEN");
//        when(service.emailCheck("newanya@ya.ru")).thenReturn("OK");
//        when(service.checkPasswordMatch("1234", "1234")).thenReturn(true);
//        doNothing().when(service).createUser("newanya", "newanya@ya.ru", "1234");
//        doNothing().when(controller).showGreetingScreen();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.registerNewUser();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пользователь с таким email уже зарегистрирован, попробуйте еще раз!");
//        verify(service).createUser("newanya", "newanya@ya.ru", "1234");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Ввод имени с пустым значением и последующим корректным вводом")
//    public void enterNameInRegistrationWithEmptyInput() {
//        String input = "\nanya\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.nameCheck("")).thenReturn("EMPTY");
//        when(service.nameCheck("anya")).thenReturn("OK");
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        String result = controller.enterNameInRegistration();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Имя не должно быть пустым!");
//        assertThat(result).isEqualTo("anya");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Ввод email с некорректным форматом и последующим корректным вводом")
//    public void enterEmailInRegistrationWithInvalidInput() {
//        String input = "anya\nnya@ya.ru\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.emailCheck("anya")).thenReturn("INVALID");
//        when(service.emailCheck("nya@ya.ru")).thenReturn("OK");
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        String result = controller.enterEmailInRegistration();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пожалуйста, введите корректный email!");
//        assertThat(result).isEqualTo("nya@ya.ru");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Ввод email с уже зарегистрированным значением и последующим корректным вводом")
//    public void enterEmailInRegistrationWithExistingEmail() {
//        String input = "anya@ya.ru\nan@ya.ru\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = new UserController(service);
//        when(service.emailCheck("anya@ya.ru")).thenReturn("TAKEN");
//        when(service.emailCheck("an@ya.ru")).thenReturn("OK");
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        String result = controller.enterEmailInRegistration();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пользователь с таким email уже зарегистрирован, попробуйте еще раз!");
//        assertThat(result).isEqualTo("an@ya.ru");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Успешный ввод корректного email с первой попытки")
//    public void enterEmailInRegistrationSuccess() {
//        String input = "nya@ya.ru\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.emailCheck("nya@ya.ru")).thenReturn("OK");
//
//        String result = controller.enterEmailInRegistration();
//        assertThat(result).isEqualTo("nya@ya.ru");
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Ввод пароля с несовпадением и последующим корректным вводом")
//    public void enterPasswordInRegistrationWithMismatch() {
//        String input = "1234\n5678\n1234\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = new UserController(service);
//        when(service.checkPasswordMatch("1234", "5678")).thenReturn(false);
//        when(service.checkPasswordMatch("1234", "1234")).thenReturn(true);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        String result = controller.enterPasswordInRegistration();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пароли не совпадают! Пожалуйста, повторите попытку!");
//        assertThat(result).isEqualTo("1234");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: просмотр пользователей при отсутствии пользователей")
//    public void adminCheckUsersEmpty() {
//        String input = "1\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.getAllUsers()).thenReturn(new java.util.ArrayList<>());
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "[]";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: попытка блокировки при отсутствии пользователей")
//    public void adminBlockUserEmpty() {
//        String input = "2\ntest@ya.ru\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        when(service.blockUser("test@ya.ru")).thenReturn("Пользователь не найден!");
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "Пользователь не найден!";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: попытка удаления пользователя при отсутствии пользователей")
//    public void adminDeleteUserEmpty() {
//        String input = "4\ntest@ya.ru\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        when(service.deleteUserByEmail("test@ya.ru")).thenReturn("Пользователь не найден!");
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "Пользователь не найден!";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: просмотр непустого списка пользователей")
//    public void adminPageCheckUsers() {
//        String input = "1\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        java.util.List<User> users = java.util.Arrays.asList(new User("anya", "anya@ya.ru", "1234", 1));
//        when(service.getAllUsers()).thenReturn(users);
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "anya, anya@ya.ru";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: успешная блокировка пользователя")
//    public void adminPageBlockUser() {
//        String input = "2\nanya@ya.ru\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        when(service.blockUser("anya@ya.ru")).thenReturn("Успешно");
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "Успешно";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка входа заблокированного пользователя")
//    public void testBlockedUser() {
//        String input = "test@ya.ru\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.emailCheck("test@ya.ru")).thenReturn("FOUND");
//        when(service.loginUser("test@ya.ru", "1234")).thenReturn(new UserService.LoginResult(true, new User("test", "test@ya.ru", "1234", 1)));
//        when(service.isUserActive("test@ya.ru")).thenReturn(false);
//        doNothing().when(controller).showGreetingScreen();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.loginUser();
//
//        String expectedMessage = "К сожалению, ваш аккаунт заблокирован! Обратитесь к администратору.";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        assertThat(controller.getLoggedUser()).isNull();
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: успешная разблокировка пользователя")
//    public void adminUnblockUser() {
//        String input = "3\nanya@ya.ru\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        when(service.unblockUser("anya@ya.ru")).thenReturn("Успешно");
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "Успешно";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Администрирование: успешное удаление пользователя")
//    public void adminDeleteUser() {
//        String input = "4\nanya@ya.ru\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        controller.setLoggedUser(new User("admin", "admin@ya.ru", "1234", 2));
//        when(service.deleteUserByEmail("anya@ya.ru")).thenReturn("Успешно");
//        doNothing().when(controller).logoutUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainPageAdmin();
//
//        String expectedMessage = "Успешно";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Переход к регистрации нового пользователя с приветственного экрана")
//    public void goToRegisterNewUser() {
//        String input = "1\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        doNothing().when(controller).registerNewUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showGreetingScreen();
//
//        String expectedMessage = "Здравствуйте! Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        verify(controller).registerNewUser();
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Вход в существующий аккаунт с приветственного экрана")
//    public void goToLoginUser() {
//        String input = "2\nanya@ya.ru\n1234\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        when(service.emailCheck("anya@ya.ru")).thenReturn("FOUND");
//        when(service.loginUser("anya@ya.ru", "1234")).thenReturn(new UserService.LoginResult(true, new User("anya", "anya@ya.ru", "1234", 1)));
//        when(service.isUserActive("anya@ya.ru")).thenReturn(true);
//        doNothing().when(controller).showMainPageUser();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showGreetingScreen();
//
//        String expectedMessage = "Здравствуйте, anya!";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
//        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Выход из программы с приветственного экрана")
//    public void goToExitProgram() {
//        String input = "3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        UserController controller = spy(new UserController(service));
//        doNothing().when(controller).exitApp();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showGreetingScreen();
//
//        String expectedMessage = "Здравствуйте! Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы";
//        String output = outContent.toString();
//        assertThat(output).contains(expectedMessage);
//        verify(controller).exitApp();
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//}