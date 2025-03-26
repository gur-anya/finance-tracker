//package UserTests;
//
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.ylabHomework.models.User;
//import org.ylabHomework.repositories.UserRepository;
//import org.ylabHomework.serviceClasses.Config;
//import org.ylabHomework.services.UserService;
//
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@DisplayName("Тесты для сервиса, работающего с пользователем")
//public class UserServiceTests {
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
//
//
//   static Connection connection;
//    UserRepository userRepository;
//    UserService service;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        postgres.start();
//        Config config = new Config();
//        connection = config.establishConnection();
//        userRepository = new UserRepository();
//        service = new UserService(userRepository);
//    }
//
//    @AfterEach
//    void clear() throws SQLException {
//        connection.rollback();
//        connection.close();
//        postgres.stop();
//    }
//
//
//    @Test
//    @DisplayName("Получение всех пользователей")
//    public void getAllUsers() {
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
//    }
//
//    @Test
//    @DisplayName("Обновление email пользователя")
//    public void updateEmail() {
//        String result = service.updateEmail("newanya@ya.ru", "anya@ya.ru");
//        assertThat(result).isEqualTo("Адрес электронной почты обновлён на newanya@ya.ru!");
//    }
//
//    @Test
//    @DisplayName("Обновление пароля пользователя")
//    public void updatePassword() {
//        String result = service.updatePassword("5678", "anya@ya.ru");
//        assertThat(result).isEqualTo("Пароль успешно обновлён!");
//    }
//
//    @Test
//    @DisplayName("Обновление статуса активности аккаунта пользователя")
//    public void updateActive() {
//        String result = service.blockUser("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь anya@ya.ru успешно заблокирован!");
//    }
//
//    @Test
//    @DisplayName("Удаление пользователя")
//    public void deleteUser() {
//        String result = service.deleteUserByEmail("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь anya@ya.ru успешно удалён!");
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя")
//    public void createUser() {
//        String result = service.createUser("newAnya", "newanya@ya.ru", "5678");
//        assertThat(result).isEqualTo("Регистрация прошла успешно!");
//    }
//
//    @Test
//    @DisplayName("Попытка создать пользователя с уже существующим email")
//    public void createUserWithExistingEmail() {
//        String result = service.createUser("anya", "anya@ya.ru", "1234");
//        assertThat(result).isEqualTo("Пользователь с таким email уже существует! Попробуйте ещё раз!");
//    }
//
//    @Test
//    @DisplayName("Попытка создать пользователя с некорректным email")
//    public void createUserWithInvalidEmail() {
//        String result = service.createUser("anya", "newanya", "1234");
//        assertThat(result).isEqualTo("Пожалуйста, введите корректный email! Попробуйте ещё раз!");
//    }
//
//    @Test
//    @DisplayName("Попытка создать пользователя с пустым паролем")
//    public void createUserWithEmptyPassword() {
//        String result = service.createUser("anya", "newanya@ya.ru", "   ");
//        assertThat(result).isEqualTo("Пароль не может быть пустым! Попробуйте ещё раз!");
//    }
//
//    @Test
//    @DisplayName("Попытка создать пользователя с null паролем")
//    public void createUserWithNullPassword() {
//        String result = service.createUser("anya", "newanya@ya.ru", null);
//        assertThat(result).isEqualTo("Пароль не может быть пустым! Попробуйте ещё раз!");
//    }
//
//    @Test
//    @DisplayName("Попытка создать пользователя с некорректным именем")
//    public void createUserWithInvalidName() {
//        String result = service.createUser("", "newanya@ya.ru", "1234");
//        assertThat(result).endsWith("Попробуйте ещё раз!");
//        assertThat(result).isNotEqualTo("Регистрация прошла успешно!");
//    }
//    @Test
//    @DisplayName("Попытка обновить пароль для ненайденного пользователя")
//    public void updatePasswordNullUser() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = new UserService(repositoryMock);
//
//        when(repositoryMock.readUserByEmail(anyString())).thenReturn(null);
//        String result = service.updatePassword("1234", "5678", "anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь не найден! Попробуйте ещё раз!");
//    }
//
//    @Test
//    @DisplayName("Попытка обновить пароль, неверный повторный ввод пароля")
//    public void updatePasswordWrongSecondInput() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = spy(new UserService(repositoryMock));
//
//        User mockUser = new User("anya", "anya@ya.ru", "$2a$10$Lz/N/PPqZTdHgRQC6Wf6EeU/SZb/KxAEGm.H/MDvW315ygMq3wEwm", 1);
//        when(repositoryMock.readUserByEmail("anya@ya.ru")).thenReturn(mockUser);
//
//        when(service.comparePass(anyString(),eq(mockUser))).thenReturn(false);
//        String result = service.updatePassword("1234", "1234", "anya@ya.ru");
//        assertThat(result).isEqualTo("Неправильный старый пароль! Попробуйте ещё раз!");
//    }
//    @Test
//    @DisplayName("Успешное обновление пароля")
//    public void updatePasswordSuccess() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = spy(new UserService(repositoryMock));
//
//
//        User mockUser = new User("anya", "anya@ya.ru", "$2a$10$Lz/N/PPqZTdHgRQC6Wf6EeU/SZb/KxAEGm.H/MDvW315ygMq3wEwm", 1);
//        when(repositoryMock.readUserByEmail("anya@ya.ru")).thenReturn(mockUser);
//        when(service.comparePass(anyString(), eq(mockUser))).thenReturn(true);
//
//        String result = service.updatePassword("1234", "5678", "anya@ya.ru");
//
//        assertThat(result).isEqualTo("Пароль успешно обновлён!");
//    }
//
//
//    @Test
//    @DisplayName("Попытка разблокировать незаблокированного пользователя")
//    public void unblockUnblockedUser() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = new UserService(repositoryMock);
//        User user = new User("anya", "anya@ya.ru", "1234", 1);
//        user.setActive(true);
//
//        when(repositoryMock.readUserByEmail(anyString())).thenReturn(user);
//        String result = service.unblockUser("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь не заблокирован!");
//    }
//    @Test
//    @DisplayName("Успешная разблокировка")
//    public void unblockUser() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = new UserService(repositoryMock);
//        User user = new User("anya", "anya@ya.ru", "1234", 1);
//        user.setActive(false);
//
//        when(repositoryMock.readUserByEmail(anyString())).thenReturn(user);
//        String result = service.unblockUser("anya@ya.ru");
//        assertThat(result).isEqualTo("Пользователь anya@ya.ru успешно разблокирован!");
//    }
//    @Test
//    @DisplayName("Попытка взимодействия с закрытой базой данных")
//    public void checkDatabaseErrorCreateUser() throws SQLException {
//        UserRepository repositoryMock = mock(UserRepository.class);
//        UserService service = new UserService(repositoryMock);
//        doThrow(new SQLException("Database connection closed")).when(repositoryMock).addUser(any());
//        String result = service.createUser("anya", "newanya@ya.ru", "1234");
//        assertThat(result).startsWith("Ошибка базы данных");
//    }
//}