package org.ylabHomework.controllers;

import lombok.Getter;
import lombok.Setter;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;
import org.ylabHomework.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Контроллер для сущности User и взаимодействия с приложением через консольный интерфейс.
 /**
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 07.03.2025
 * </p>
 */
@Getter
@Setter
public class UserController {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService service;
    private User loggedUser = null;
    private boolean mainPageShown = false;
    public final Map<String, Command> greetingCommands = new HashMap<>();
    private final Map<String, Command> mainPageUserCommands = new HashMap<>();
    private final Map<String, Command> personalAccountCommands = new HashMap<>();
    private final Map<String, Command> updateUserCommands = new HashMap<>();
    public final Map<String, Command> adminCommands = new HashMap<>();
    private final Map<String, Command> deleteCommands = new HashMap<>();

    /**
     * Интерфейс команды для паттерна "Команда".
     */
    public interface Command {
        void execute();
    }

    /**
     * Конструктор для создания контроллера с заданным сервисом.
     *
     * @param service сервис с бизнес-логикой для User
     */
    public UserController(UserService service) {
        this.service = service;
        initializeCommands();
    }

    /**
     * Выполняет цикл меню с заданным текстом и командами.
     *
     * @param menuText текст меню для отображения
     * @param commands команды для выполнения
     * @param errorMessage сообщение об ошибке при неверном вводе
     */
    public void executeMenu(String menuText, Map<String, Command> commands, String errorMessage) {
        while (true) {
            System.out.println(menuText);
            String input = scanner.nextLine();
            Command command = commands.get(input);
            if (command != null) {
                command.execute();
                break;
            } else {
                System.out.println(errorMessage);
            }
        }
    }

    /**
     * Инициализация всех команд для меню.
     */
    private void initializeCommands() {
        greetingCommands.put("1", this::registerNewUser);
        greetingCommands.put("2", this::loginUser);
        greetingCommands.put("3", this::goToExit);


        mainPageUserCommands.put("1", this::goToTransactionsController);
        mainPageUserCommands.put("2", this::showPersonalAccountSettings);
        mainPageUserCommands.put("3", this::goToExit);


        personalAccountCommands.put("1", this::updateUser);
        personalAccountCommands.put("2", this::logoutUser);
        personalAccountCommands.put("3", this::deleteUser);
        personalAccountCommands.put("4", this::showMainPageUser);


        updateUserCommands.put("1", this::updateName);
        updateUserCommands.put("2", this::updateEmail);
        updateUserCommands.put("3", this::updatePass);


        adminCommands.put("1", this::showUsers);
        adminCommands.put("2", this::blockUser);
        adminCommands.put("3", this::unblockUser);
        adminCommands.put("4", this::deleteUserByEmail);
        adminCommands.put("5", this::logoutUser);

        deleteCommands.put("да", new ConfirmDeleteCommand());
        deleteCommands.put("нет", this::showPersonalAccountSettings);
    }

    /**
     * Показывает приветственный экран, где выводит меню выбора действий:
     * зарегистрироваться, войти в аккаунт или выйти из программы.
     */
    public void showGreetingScreen() {
            if (!mainPageShown) {
                System.out.println("Здравствуйте! Введите 1, чтобы зарегистрироваться, 2 - чтобы войти в существующий аккаунт, 3 - чтобы выйти из программы");
                setMainPageShown(true);
            }
            executeMenu("", greetingCommands, "Пожалуйста, введите 1, чтобы зарегистрироваться, 2 - чтобы войти, 3 - чтобы выйти!");
    }

    /**
     * Регистрирует нового пользователя, запрашивая данные через консоль.
     */
    public void registerNewUser() {
        setMainPageShown(false);
        System.out.println("=РЕГИСТРАЦИЯ=");
        String name = enterNameInRegistration();
        String email = enterEmailInRegistration();
        String password = enterPasswordInRegistration();
        String res = service.createUser(name, email, password);
        System.out.println(res);
        showGreetingScreen();
    }

    /**
     * Показывает главную страницу для пользователя.
     * Можно перейти к трекингу финансов, в личный кабинет или выйти из программы.
     */
    public void showMainPageUser() {
        executeMenu("Нажмите 1, чтобы перейти к трекингу финансов, 2 - чтобы перейти в личный кабинет, 3 - чтобы выйти из программы",
                mainPageUserCommands,
                "Пожалуйста, нажмите 1, 2 или 3!");
    }

    /**
     * Показывает страницу личного кабинета.
     * Можно изменить личную информацию, выйти из аккаунта, удалить аккаунт или вернуться к выбору страницы.
     */
    public void showPersonalAccountSettings() {
        executeMenu("Введите 1, чтобы изменить личную информацию, 2 - чтобы выйти, 3 - чтобы удалить аккаунт, 4 - вернуться назад",
                personalAccountCommands,
                "Пожалуйста, введите 1, 2, 3 или 4!");
    }

    /**
     * Показывает страницу для изменения личной информации.
     * Можно поменять имя, адрес электронной почты или пароль.
     */
    public void updateUser() {
        executeMenu(Constants.UPDATE_USER_TEXT_BLOCK,
                updateUserCommands,
                "Пожалуйста, введите 1, 2 или 3!");
    }

    /**
     * Показывает главную страницу для администратора.
     * Можно просмотреть всех пользователей, заблокировать, разблокировать или удалить пользователя по его email.
     */
    public void showMainPageAdmin() {
        System.out.println("Здравствуйте, Администратор!");
        executeMenu(Constants.SHOW_ADMIN_MAIN_PAGE_TEXT_BLOCK,
                adminCommands,
                "Введите 1, 2, 3, 4 или 5.");
    }

    /**
     * Проверяет, не пусто ли имя, которое пользователь вводит при регистрации.
     * @return имя пользователя
     */
    public String enterNameInRegistration() {
        String name;
        while (true) {
            System.out.println("Введите свое имя");
            name = scanner.nextLine();
            String state = service.nameCheck(name);
            if (state.equals("OK")) {
                break;
            } else {
                System.out.println("Имя не должно быть пустым!");
            }
        }
        return name;
    }

    /**
     * Проверяет приемлемость адреса электронной почты при регистрации.
     * @return адрес электронной почты пользователя
     */
    public String enterEmailInRegistration() {
        String email;
        while (true) {
            System.out.println("Введите адрес электронной почты:");
            email = scanner.nextLine();
            String state = service.emailCheck(email);
            if (state.equals("OK"))  {
                break;
            } else if (state.equals("INVALID")){
                System.out.println("Пожалуйста, введите корректный email!");
            } else {
                System.out.println("Пользователь с таким email уже зарегистрирован, попробуйте еще раз!");
            }

        }
        return email;
    }

    /**
     * Проверяет адрес электронной почты, вводимой при логине.
     * @return адрес электронной почты
     */
    public String enterEmailInLogin() {
        String email;
        while (true) {
            System.out.println("Введите адрес электронной почты:");
            email = scanner.nextLine();
            String state = service.emailCheck(email);
            if (state.equals("FOUND")) break;
            if (state.equals("INVALID")) {
                System.out.println("Пожалуйста, введите корректный email!");
            } else {
                System.out.println("Пользователь с таким email не найден!");
                System.out.println("Нажмите 1, чтобы попробовать еще раз, 2, чтобы вернуться в меню.");
                String i = scanner.nextLine();
                if (i.equals("2")) showGreetingScreen();
            }
        }
        return email;
    }

    /**
     * Проверяет пароль, вводимый при регистрации.
     * @return пароль пользователя
     */
    public String enterPasswordInRegistration() {
        String password;
        while (true) {
            System.out.println("Введите пароль");
            password = scanner.nextLine();
            System.out.println("Повторите пароль");
            String repeatedPass = scanner.nextLine();
            if (service.checkPasswordMatch(password, repeatedPass)) break;
            System.out.println("Пароли не совпадают! Пожалуйста, повторите попытку!");
        }
        return password;
    }

    /**
     * Производит логин пользователя.
     */
    public void loginUser() {
        setMainPageShown(false);
        System.out.println("=ВХОД=");
        String email = enterEmailInLogin();
        enterPasswordInLogin(email);
        if (service.isUserActive(email)) {
            loggedUser = service.readUserByEmail(email);
            if (loggedUser.getRole() == 1) {
                System.out.println("Здравствуйте, " + loggedUser.getName() + "!");
                showMainPageUser();
            }
            else showMainPageAdmin();
        } else {
            System.out.println("К сожалению, ваш аккаунт заблокирован! Обратитесь к администратору.");
            showGreetingScreen();
        }
    }

    /**
     * Проверяет соответствие введенного при логине пароля.
     * @param email адрес электронной почты пользователя
     */
    public void enterPasswordInLogin(String email) {
        while (true) {
            System.out.println("Введите пароль");
            String password = scanner.nextLine();
            UserService.LoginResult loginResult = service.loginUser(email, password);
            if (loginResult.success()) {
                loggedUser = loginResult.user();
                System.out.println("Авторизируем...");
                break;
            }
            System.out.println("Введен неверный пароль! Нажмите 1, чтобы попробовать еще раз, 2, чтобы вернуться в меню.");
            String i = scanner.nextLine();
            if (i.equals("2")) showGreetingScreen();
        }
    }

    /**
     * Обновляет пароль пользователя.
     */
    public void updatePass() {
        System.out.println("Введите старый пароль");
        String exPass = scanner.nextLine();
        System.out.println("Введите новый пароль");
        String newPass = scanner.nextLine();
        String result = service.updatePassword(exPass, newPass, loggedUser.getEmail());
        System.out.println(result);
        showPersonalAccountSettings();
    }

    /**
     * Обновляет адрес электронной почты пользователя.
     */
    public void updateEmail() {
        System.out.println("Введите новый адрес электронной почты");
        String newEmail = scanner.nextLine();
        String result = service.updateEmail(newEmail, loggedUser.getEmail());
        System.out.println(result);
        showPersonalAccountSettings();
    }

    /**
     * Обновляет имя пользователя.
     */
    public void updateName() {
        System.out.println("Введите новое имя");
        String newName = scanner.nextLine();
        String result = service.updateName(newName, loggedUser.getEmail());
        System.out.println(result);
        showPersonalAccountSettings();
    }

    /**
     * Удаляет пользователя после подтверждения.
     */
    public void deleteUser() {
        executeMenu("Вы действительно хотите удалить свой аккаунт? да/нет",
                deleteCommands,
                "Введите да или нет.");
    }


    private class ConfirmDeleteCommand implements Command {
        @Override
        public void execute() {
            service.deleteUserByEmail(loggedUser.getEmail());
            setLoggedUser(null);
            System.out.println("Аккаунт успешно удален.");
            showGreetingScreen();
        }
    }
    public void showUsers () {
        List<User> users = service.getAllUsers();
        users.remove(service.readUserByEmail("admin@ya.ru"));
        if (!users.isEmpty()) {
            for (int i = 0; i < users.size(); i++) {
                System.out.println(i + 1 + ". " + users.get(i).getEmail() + ", " + users.get(i).getName());
            }
        } else {
            System.out.println("Пользователи не найдены!");
        }
        showMainPageAdmin();
    }

    /**
     * Блокирует пользователя по email (для админа).
     */
    public void blockUser() {
        System.out.println("Введите email пользователя для блокировки:");
        String email = scanner.nextLine();
        String result = service.blockUser(email);
        System.out.println(result);
        showMainPageAdmin();
    }

    /**
     * Разблокирует пользователя по email (для админа).
     */
    public void unblockUser() {
        System.out.println("Введите email пользователя для разблокировки:");
        String email = scanner.nextLine();
        String result = service.unblockUser(email);
        System.out.println(result);
        showMainPageAdmin();
    }

    /**
     * Удаляет пользователя по email (для админа).
     */
    public void deleteUserByEmail() {
        System.out.println("Введите email пользователя для удаления:");
        String email = scanner.nextLine();
        String result = service.deleteUserByEmail(email);
        System.out.println(result);
        showMainPageAdmin();
    }

    /**
     * Обеспечивает выход пользователя из системы.
     */
    public void logoutUser() {
        setLoggedUser(null);
        showGreetingScreen();
    }

    public void goToExit(){
        exitApp();
    }
    /**
     * Завершает выполнение программы.
     */
    public void exitApp() {
        System.exit(0);
    }

    /**
     * Переносит пользователя на страницу для управления его транзакциями.
     */
    private void goToTransactionsController() {
        TransactionRepository repo = new TransactionRepository(loggedUser);
        TransactionController transController = new TransactionController(new TransactionService(repo, loggedUser), new TransactionStatsService(repo, loggedUser),
                this);
        transController.showMainMenu();
    }
}