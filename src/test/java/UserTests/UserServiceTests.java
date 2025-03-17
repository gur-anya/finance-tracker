//package UserTests;
//
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.ylabHomework.models.User;
//import org.ylabHomework.repositories.UserRepository;
//import org.ylabHomework.serviceClasses.Config;
//import org.ylabHomework.services.UserService;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@DisplayName("Тесты для сервиса, работающего с пользователем")
//public class UserServiceTests {
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
//
//
//    Connection connection;
//    UserRepository userRepository;
//    UserService service;
//
//    @BeforeAll
//    static void init() {
//        postgres.start();
//    }
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        Config config = new Config();
//        connection = config.establishConnection();
//        userRepository = new UserRepository();
//        service = new UserService(userRepository);
//    }
//    @AfterEach
//    void clear() throws SQLException {
//        connection.rollback();
//        connection.close();
//        postgres.stop();
//    }
//
//    @Test
//    @DisplayName("Получение всех пользователей")
//    public void getAllUsers() {
//        service.createUser("admin", "admin@admin.ru", "admin123");
//
//        List<User> actual = service.getAllUsers();
//        assertThat(actual).hasSize(2);
//        assertThat(actual).extracting(User::getName).containsExactlyInAnyOrder("anya", "admin");
//    }
//
//    @Test
//    @DisplayName("Проверка пустого имени пользователя")
//    public void blankName() {
//        String expected = "Имя не может быть пустым! Пожалуйста, введите имя!";
//        String actual = service.nameCheck("");
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка приемлемого имени")
//    public void checkName() {
//        String expected = "OK";
//        String actual = service.nameCheck("anya");
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (нет @, .)")
//    public void incorrectEmail1() {
//        String actual = service.emailCheck("anya");
//        String expected = "INVALID";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (нет .)")
//    public void incorrectEmail2() {
//        String actual = service.emailCheck("anya@ya");
//        String expected = "INVALID";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (кириллицей)")
//    public void incorrectEmail3() {
//        String actual = service.emailCheck("аня@я.ру");
//        String expected = "INVALID";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (нет @)")
//    public void incorrectEmail4() {
//        String actual = service.emailCheck("anya.ru");
//        String expected = "INVALID";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (пустой email)")
//    public void incorrectEmail5() {
//        String actual = service.emailCheck("");
//        String expected = "INVALID";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка неприемлемого email (такой уже зарегистрирован)")
//    public void nonUniqueEmail() {
//        String expected = "FOUND";
//        String actual = service.emailCheck("anya@ya.ru");
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Проверка приемлемого email")
//    public void checkEmail() {
//        String expected = "OK";
//        String actual = service.emailCheck("newanya@ya.ru");
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("Попытка входа в систему зарегистрированного пользователя")
//    public void loginUser() {
//        UserService.LoginResult result = service.loginUser("anya@ya.ru", "1234");
//        assertThat(result.success()).isTrue();
//        assertThat(result.user().getName()).isEqualTo("anya");
//    }
//
//    @Test
//    @DisplayName("Попытка входа в систему с незарегистрированным email")
//    public void loginUserUnknownEmail() {
//        UserService.LoginResult result = service.loginUser("anyaa@ya.ru", "1234");
//        assertThat(result.success()).isFalse();
//        assertThat(result.user()).isNull();
//    }
//
//    @Test
//    @DisplayName("Попытка входа в систему с зарегистрированным email, но неподходящим паролем")
//    public void loginUserWrongPass() {
//        UserService.LoginResult result = service.loginUser("anya@ya.ru", "5678");
//        assertThat(result.success()).isFalse();
//        assertThat(result.user()).isNull();
//    }
//
//    @Test
//    @DisplayName("Обновление имени пользователя")
//    public void updateName() {
//        String result = service.updateName("newAnya", "anya@ya.ru");
//        assertThat(result).isEqualTo("Имя успешно изменено на newAnya!");
//        assertThat(service.readUserByEmail("anya@ya.ru").getName()).isEqualTo("newAnya");
//    }
//
//    @Test
//    @DisplayName("Обновление email пользователя")
//    public void updateEmail() {
//        String result = service.updateEmail("newanya@ya.ru", "anya@ya.ru");
//        assertThat(result).isEqualTo("Адрес электронной почты обновлён на newanya@ya.ru!");
//        assertThat(service.readUserByEmail("newanya@ya.ru")).isNotNull();
//    }
//
//    @Test
//    @DisplayName("Обновление пароля пользователя")
//    public void updatePassword() {
//        String oldPassword = service.readUserByEmail("anya@ya.ru").getPassword();
//        String result = service.updatePassword("5678", "anya@ya.ru");
//        assertThat(result).isEqualTo("Пароль успешно обновлён!");
//        assertThat(service.readUserByEmail("anya@ya.ru").getPassword()).isNotEqualTo(oldPassword);
//    }
//
//    @Test
//    @DisplayName("Обновление статуса активности аккаунта пользователя")
//    public void updateActive() {
//        String result = service.blockUser("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь anya@ya.ru успешно заблокирован!");
//        assertThat(service.readUserByEmail("anya@ya.ru").isActive()).isFalse();
//    }
//
//    @Test
//    @DisplayName("Удаление пользователя")
//    public void deleteUser() {
//        String result = service.deleteUserByEmail("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь anya@ya.ru успешно удалён!");
//        assertThat(service.readUserByEmail("anya@ya.ru")).isNull();
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя")
//    public void createUser() {
//        String result = service.createUser("newAnya", "newanya@ya.ru", "5678");
//        assertThat(result).isEqualTo("Регистрация прошла успешно!");
//        assertThat(service.readUserByEmail("newanya@ya.ru")).isNotNull();
//    }
//}