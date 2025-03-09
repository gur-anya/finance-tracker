package UserTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Тесты для сервиса, работающего с пользователем")
public class UserServiceTests {
    private UserRepository repo;
    private UserService service;

    @BeforeEach
    void setUp() {
        repo = new UserRepository();
        service = new UserService(repo);
    }

    @Test
    @DisplayName("Получение всех пользователей")
    public void getAllUsers() {
        service.createUser("anya1", "an1@ya.ru", "1234");
        service.createUser("anya2", "an2@ya.ru", "5678");

        List<User> actual = service.getAllUsers();
        assertThat(actual).hasSize(2);
        assertThat(actual).extracting(User::getName).containsExactlyInAnyOrder("anya1", "anya2");
    }

    @Test
    @DisplayName("Проверка пустого имени пользователя")
    public void blankName() {
        String expected = "Имя не может быть пустым! Пожалуйста, введите имя!";
        String actual = service.nameCheck("");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка приемлемого имени")
    public void checkName() {
        String expected = "anya";
        String actual = service.nameCheck("anya");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (нет @, .)")
    public void incorrectEmail1() {
        String actual = service.emailCheck("anya");
        String expected = "Пожалуйста, введите корректный email!";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (нет .)")
    public void incorrectEmail2() {
        String actual = service.emailCheck("anya@ya");
        String expected = "Пожалуйста, введите корректный email!";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (кириллицей)")
    public void incorrectEmail3() {
        String actual = service.emailCheck("аня@я.ру");
        String expected = "Пожалуйста, введите корректный email!";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (нет @)")
    public void incorrectEmail4() {
        String actual = service.emailCheck("anya.ru");
        String expected = "Пожалуйста, введите корректный email!";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (пустой email)")
    public void incorrectEmail5() {
        String actual = service.emailCheck("");
        String expected = "Пожалуйста, введите корректный email!";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка неприемлемого email (такой уже зарегистрирован)")
    public void nonUniqueEmail() {
        service.createUser("anya", "anya@ya.ru", "1234");
        String expected = "Пользователь с таким email уже зарегистрирован!";
        String actual = service.emailCheck("anya@ya.ru");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверка приемлемого email")
    public void checkEmail() {
        String expected = "anya@ya.ru";
        String actual = service.emailCheck("anya@ya.ru");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка входа в систему зарегистрированного пользователя")
    public void loginUser() {
        service.createUser("anya", "anya@ya.ru", "1234");
        Object[] expected = new Object[]{true, "anya"};
        Object[] actual = service.loginUser("anya@ya.ru", "1234");
        assertThat(actual).containsExactly(expected);
    }

    @Test
    @DisplayName("Попытка входа в систему с незарегистрированным email")
    public void loginUserUnknownEmail() {
        service.createUser("anya", "anya@ya.ru", "1234");
        Object[] expected = new Object[]{false, "unknownEmail"};
        Object[] actual = service.loginUser("anyaa@ya.ru", "1234");
        assertThat(actual).containsExactly(expected);
    }

    @Test
    @DisplayName("Попытка входа в систему с зарегистрированным email, но неподходящим паролем")
    public void loginUserWrongPass() {
        service.createUser("anya", "anya@ya.ru", "1234");
        Object[] expected = new Object[]{false, "wrongPass"};
        Object[] actual = service.loginUser("anya@ya.ru", "5678");
        assertThat(actual).containsExactly(expected);
    }

    @Test
    @DisplayName("Добавление пользователя")
    public void createUser() {
        service.createUser("newAnya", "newanya@ya.ru", "5678");
        assertThat(service.readUserByEmail("newanya@ya.ru")).isNotNull();
    }

    @Test
    @DisplayName("Обновление имени пользователя")
    public void updateName() {
        service.createUser("anya", "anya@ya.ru", "1234");
        String expected = "newAnya";
        String actual = service.updateName("newAnya", "anya@ya.ru");
        assertThat(actual).isEqualTo(expected);
        assertThat(service.readUserByEmail("anya@ya.ru").getName()).isEqualTo("newAnya");
    }

    @Test
    @DisplayName("Обновление email пользователя")
    public void updateEmail() {
        service.createUser("anya", "anya@ya.ru", "1234");
        String expected = "newanya@ya.ru";
        String actual = service.updateEmail("newanya@ya.ru", "anya@ya.ru");
        assertThat(actual).isEqualTo(expected);
        assertThat(service.readUserByEmail("newanya@ya.ru")).isNotNull();
        assertThat(service.readUserByEmail("anya@ya.ru")).isNull();
    }

    @Test
    @DisplayName("Обновление пароля пользователя")
    public void updatePassword() {
        service.createUser("anya", "anya@ya.ru", "1234");
        String oldEncryptedPass = service.readUserByEmail("anya@ya.ru").getPassword();
        service.updatePassword("5678", "anya@ya.ru");
        assertThat(service.readUserByEmail("anya@ya.ru").getPassword()).isNotEqualTo(oldEncryptedPass);
    }

    @Test
    @DisplayName("Удаление пользователя")
    public void deleteUser() {
        service.createUser("anya", "anya@ya.ru", "1234");
        service.deleteUserByEmail("anya@ya.ru");
        assertThat(service.readUserByEmail("anya@ya.ru")).isNull();
    }
}