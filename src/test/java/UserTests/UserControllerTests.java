package UserTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylabHomework.controllers.UserController;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для контроллера пользователя")
public class UserControllerTests {
    UserRepository repo = new UserRepository();
    UserService service = new UserService(repo);

    @Test
    @DisplayName("Переход в личный кабинет с главной страницы пользователя")
    public void goToPersonalAccount() {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showPersonalAccountSettings();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showMainPageUser();

        verify(controller).showPersonalAccountSettings();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного ввода на главной странице пользователя с последующим корректным выбором")
    public void mainPageUserOptionMismatch() {
        String input = "invalid\n2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).registerNewUser();
        doNothing().when(controller).showPersonalAccountSettings();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showMainPageUser();

        String expectedMessage = "Пожалуйста, нажмите 1, чтобы перейти к трекингу финансов, 2 - чтобы перейти в личный кабинет, 3 - чтобы выйти из программы!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к изменению личной информации из настроек аккаунта")
    public void goToUpdatePersonalInformation() {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).updateUser();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showPersonalAccountSettings();
        verify(controller).updateUser();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Выход из аккаунта из настроек")
    public void goToLogout() {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).logoutUser();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showPersonalAccountSettings();
        verify(controller).logoutUser();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Удаление аккаунта из настроек")
    public void goToDeleteAccount() {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).deleteUser();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showPersonalAccountSettings();
        verify(controller).deleteUser();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Возврат на главную страницу из настроек аккаунта")
    public void goToMainPageFromPersonalAccount() {
        String input = "4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showMainPageUser();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showPersonalAccountSettings();
        verify(controller).showMainPageUser();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного ввода в настройках аккаунта с последующим корректным выбором")
    public void personalAccountSettingsOptionMismatch() {
        String input = "ttt\n4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).showMainPageUser();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.showPersonalAccountSettings();

        String expectedMessage = "Пожалуйста, введите 1, чтобы изменить личную информацию, 2 - чтобы выйти из аккаунта, 3 - чтобы удалить аккаунт, 4 - вернуться назад!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к изменению имени пользователя из меню обновления аккаунта")
    public void goToUpdateUsername() {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).updateName();
        doNothing().when(controller).showPersonalAccountSettings();

        controller.updateUser();
        verify(controller).updateName();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к изменению email из меню обновления аккаунта")
    public void goToUpdateEmail() {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).updateEmail();
        doNothing().when(controller).showPersonalAccountSettings();
        controller.updateUser();
        verify(controller).updateEmail();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к изменению пароля из меню обновления аккаунта")
    public void goToUpdatePassword() {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).updatePass();
        doNothing().when(controller).showPersonalAccountSettings();
        controller.updateUser();
        verify(controller).updatePass();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного ввода в меню обновления пользователя с последующим корректным выбором")
    public void updateUserOptionMismatch() {
        String input = "ttt\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).updatePass();
        doNothing().when(controller).showPersonalAccountSettings();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.updateUser();

        String expectedMessage = "Пожалуйста, введите цифру 1, чтобы изменить имя пользователя, 2, чтобы изменить email или 3, чтобы изменить пароль!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешное обновление пароля пользователя с корректным вводом")
    public void basicUpdatePass() {
        String input1 = "1234\n5678\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updatePass();

        String expectedMessage = "Пароль успешно обновлен!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешное обновление email пользователя с корректным вводом")
    public void basicUpdateEmail() {
        String input1 = "an@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Введите новый адрес электронной почты\r\nАдрес электронной почты обновлен успешно! Новый адрес: an@ya.ru";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешное обновление имени пользователя с корректным вводом")
    public void basicUpdateName() {
        String input = "анечка\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.updateName();

        String expectedMessage = "Введите новое имя\r\nанечка, имя изменено успешно!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getName()).isEqualTo("анечка");
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка несоответствия старого пароля при обновлении пароля")
    public void updatePassPasswordMismatch() {
        String input1 = "1111\n1234\n5678\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updatePass();

        String expectedMessage = "Неправильный пароль! Повторите попытку!\r\nВведите старый пароль";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного email (несоответствие шаблону) при обновлении с последующим успехом")
    public void updateEmailIncorrectEmail1() {
        String input1 = "anya\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Пожалуйста, введите корректный email!\r\nВведите новый адрес электронной почты";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного email (несоответствие шаблону) при обновлении с последующим успехом")
    public void updateEmailIncorrectEmail2() {
        String input1 = "anya@ya\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Пожалуйста, введите корректный email!\r\nВведите новый адрес электронной почты";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного email (кириллица) при обновлении с последующим успехом")
    public void updateEmailIncorrectEmail3() {
        String input1 = "аня@я.ру\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Пожалуйста, введите корректный email!\r\nВведите новый адрес электронной почты";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного email (несоответствие шаблону) при обновлении с последующим успехом")
    public void updateEmailIncorrectEmail4() {
        String input1 = "anya.ru\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Пожалуйста, введите корректный email!\r\nВведите новый адрес электронной почты";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка пустого email при обновлении с последующим успехом")
    public void updateEmailIncorrectEmail5() {
        String input1 = "\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = "Пожалуйста, введите корректный email!\r\nВведите новый адрес электронной почты";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка неуникального email при обновлении с последующим успехом")
    public void updateEmailNonUniqueEmail() {
        String input1 = "anya@ya.ru\nan@ya.ru\n";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);

        UserController controller = new UserController(service);
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.updateEmail();

        String expectedMessage = """
                Пользователь с таким email уже зарегистрирован!\r
                Введите новый адрес электронной почты\r
                Адрес электронной почты обновлен успешно! Новый адрес: an@ya.ru""";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("an@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка пустого имени при обновлении имени с последующим успехом")
    public void updateNameBlankName() {
        String input = "\nанечка\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller.updateName();

        String expectedMessage = "Имя не может быть пустым! Пожалуйста, введите имя!\r\nВведите новое имя\r\nанечка, имя изменено успешно!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getName()).isEqualTo("анечка");
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешный выход из аккаунта")
    public void logoutUser() {
        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.logoutUser();

        assertThat(controller.getLoggedUser()).isNull();
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Обработка неизвестного ответа при удалении пользователя")
    public void deleteUserUnknownResponse() {
        String input = "unknown\nнет\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        doNothing().when(controller).showPersonalAccountSettings();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.deleteUser();

        String expectedMessage = "Введите да или нет.";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
    }

    @Test
    @DisplayName("Успешное удаление аккаунта пользователя при подтверждении")
    public void deleteUserYes() {
        String input = "да\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.deleteUser();

        String expectedMessage = "Аккаунт успешно удален.";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(service.readUserByEmail("anya@ya.ru")).isNull();
        assertThat(controller.getLoggedUser()).isNull();
    }

    @Test
    @DisplayName("Отказ от удаления аккаунта и сохранение пользователя")
    public void deleteUserNo() {
        String input = "нет\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));

        doNothing().when(controller).showPersonalAccountSettings();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.deleteUser();

        assertThat(service.getAllUsers()).contains(service.readUserByEmail("anya@ya.ru"));
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
    }

    @Test
    @DisplayName("Обработка неверного пароля при входе с последующим успехом")
    public void enterPass() {
        String input = "5678\n1\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = new UserController(service);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.enterPasswordInLogin("anya@ya.ru");

        String expectedMessage1 = "Введен неверный пароль! Повторите попытку!";
        String expectedMessage2 = "Авторизируем...";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage1);
        assertThat(output).contains(expectedMessage2);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Успешный вход пользователя с корректными данными")
    public void loginUser() {
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

        UserController controller = new UserController(service);

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
        assertThat(service.readUserByEmail("anyaa@ya.ru")).isNotNull();

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
        assertThat(output).contains("Пользователь с таким email уже зарегистрирован!");


        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод имени с пустым значением и последующим корректным вводом")
    public void enterNameInRegistrationWithEmptyInput() {
        String input = "\nanya\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        UserController controller = spy(new UserController(service));

        String result = controller.enterNameInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Имя не может быть пустым! Пожалуйста, введите имя!");
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

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        UserController controller = spy(new UserController(service));

        controller.enterEmailInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пожалуйста, введите корректный email!");

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Ввод email с уже зарегистрированным значением и последующим корректным вводом")
    public void enterEmailInRegistrationWithExistingEmail() {
        String input = "anya@ya.ru\nan@ya.ru\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        UserController controller = new UserController(service);

        controller.enterEmailInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пользователь с таким email уже зарегистрирован!");


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

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        UserController controller = new UserController(service);

        controller.enterPasswordInRegistration();

        String output = outContent.toString();
        assertThat(output).contains("Пароли не совпадают! Пожалуйста, повторите попытку!");

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: просмотр пользователей при отсутствии пользователей")
    public void adminCheckUsersEmpty() {
        String input = "1\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();

        service.deleteUserByEmail("anya@ya.ru");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainPageAdmin();

        String expectedMessage = "Ни одного пользователя не создано!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: попытка блокировки при отсутствии пользователей")
    public void adminBlockUserEmpty() {
        String input = "2\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        service.deleteUserByEmail("anya@ya.ru");
        controller.showMainPageAdmin();

        String expectedMessage = "Ни одного пользователя не создано!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: попытка удаления пользователя при отсутствии пользователей")
    public void adminDeleteUserEmpty() {
        String input = "3\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("anya@ya.ru"));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        service.deleteUserByEmail("anya@ya.ru");
        controller.showMainPageAdmin();

        String expectedMessage = "Ни одного пользователя не создано!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: просмотр непустого списка пользователей")
    public void adminPageCheckUsers() {
        String input = "1\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainPageAdmin();

        String expectedMessage = "1. anya, anya@ya.ru, активен=true";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: успешная блокировка пользователя")
    public void adminPageBlockUser() {
        String input = "2\nanya@ya.ru\n1\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainPageAdmin();

        String expectedMessage1 = "Успешно";
        String expectedMessage2 = "1. anya, anya@ya.ru, активен=false";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage1);
        assertThat(output).contains(expectedMessage2);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Проверка входа заблокированного пользователя")
    public void testBlockedUser() {
        String input = "test@ya.ru\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        service.createUser("test", "test@ya.ru", "1234");
        service.updateActive(false, "test@ya.ru");
        controller.loginUser();

        String expectedMessage = "К сожалению, ваш аккаунт заблокирован! Обратитесь к администратору.";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser()).isNull();
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: успешная разблокировка пользователя")
    public void adminUnblockUser() {
        String input = "3\nanya@ya.ru\n1\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        service.updateActive(false, "anya@ya.ru");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainPageAdmin();

        String expectedMessage1 = "Успешно";
        String expectedMessage2 = "1. anya, anya@ya.ru, активен=true";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage1);
        assertThat(output).contains(expectedMessage2);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Администрирование: успешное удаление пользователя")
    public void adminDeleteUser() {
        String input = "4\nanya@ya.ru\n1\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        controller.setLoggedUser(service.readUserByEmail("admin@ya.ru"));

        doNothing().when(controller).showGreetingScreen();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainPageAdmin();

        String expectedMessage = "Успешно";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(service.readUserByEmail("anya@ya.ru")).isNull();
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к регистрации нового пользователя с приветственного экрана")
    public void goToRegisterNewUser() {
        String input = "1\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).registerNewUser();
        doNothing().when(controller).exitApp();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showGreetingScreen();

        String expectedMessage = "Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Вход в существующий аккаунт с приветственного экрана")
    public void goToLoginUser() {
        String input = "2\nanya@ya.ru\n1234\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        UserController controller = spy(new UserController(service));
        doNothing().when(controller).showMainPageUser();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showGreetingScreen();

        String expectedMessage = "Здравствуйте, anya!";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        assertThat(controller.getLoggedUser().getEmail()).isEqualTo("anya@ya.ru");
        assertThat(controller.getLoggedUser().getName()).isEqualTo("anya");
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Выход из программы с приветственного экрана")
    public void goToExitProgram() {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        UserController controller = spy(new UserController(service));

        doNothing().when(controller).exitApp();
        controller.showGreetingScreen();

        String expectedMessage = "Здравствуйте! Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы";
        String output = outContent.toString();
        assertThat(output).contains(expectedMessage);
        verify(controller, times(1)).exitApp();
        System.setIn(System.in);
        System.setOut(System.out);
    }
}