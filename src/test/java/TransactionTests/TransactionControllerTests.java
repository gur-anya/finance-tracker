//package TransactionTests;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.ylabHomework.controllers.TransactionController;
//import org.ylabHomework.controllers.UserController;
//import org.ylabHomework.models.Transaction;
//import org.ylabHomework.models.User;
//import org.ylabHomework.repositories.TransactionRepository;
//import org.ylabHomework.repositories.UserRepository;
//import org.ylabHomework.services.TransactionService;
//import org.ylabHomework.services.TransactionStatsService;
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
//@DisplayName("Тесты для контроллера транзакций")
//public class TransactionControllerTests {
//    UserRepository userRepo = new UserRepository();
//    UserService userService = new UserService(userRepo);
//    UserController userController = new UserController(userService);
//    User user = userService.readUserByEmail("anya@ya.ru");
//    TransactionRepository repo = new TransactionRepository(user);
//    TransactionService service = new TransactionService(repo, user);
//    TransactionStatsService statsService = new TransactionStatsService(repo, user);
//
//    @Test
//    @DisplayName("Переход к управлению транзакциями с главного меню")
//    public void goToTransactionManagement() {
//        String input = "1\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showTransactionManagement();
//
//        controller.showMainMenu();
//
//        verify(controller).showTransactionManagement();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Переход к статистике с главного меню")
//    public void goToStatsAndAnalysisMenu() {
//        String input = "2\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//
//        controller.showMainMenu();
//
//        verify(controller).showStatsAndAnalysisMenu();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Возврат в меню пользователя с главного меню")
//    public void goToMainPageUser() {
//        String input = "3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        userController = spy(new UserController(userService));
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(userController).showMainPageUser();
//
//        controller.showMainMenu();
//
//        verify(userController).showMainPageUser();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Выход из программы с главного меню")
//    public void exitAppFromMainMenu() {
//        String input = "4\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).exitApp();
//
//        controller.showMainMenu();
//
//        verify(controller).exitApp();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Обработка некорректного ввода на главном меню с последующим корректным выбором")
//    public void mainMenuOptionMismatch() {
//        String input = "invalid\n1\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showTransactionManagement();
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        controller.showMainMenu();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пожалуйста, введите 1, 2, 3 или 4.");
//        verify(controller).showTransactionManagement();
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Переход к просмотру транзакций из меню управления")
//    public void goToShowTransactionsFilter() {
//        String input = "1\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showTransactionsFilter();
//
//        controller.showTransactionManagement();
//
//        verify(controller).showTransactionsFilter();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Создание новой транзакции из меню управления")
//    public void goToCreateTransactionAndCreateTransaction() {
//        String input = "2\n1\n1000\nЗарплата\nЗарплата за март\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Транзакция успешно сохранена!");
//        assertThat(service.getTransactionsByCategory("Зарплата")).isNotEmpty();
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Переход к редактированию транзакций из меню управления")
//    public void goToUpdateTransaction() {
//        String input = "3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).updateTransaction();
//
//        controller.showTransactionManagement();
//
//        verify(controller).updateTransaction();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Переход к удалению транзакции из меню управления")
//    public void goToDeleteTransaction() {
//        String input = "4\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).deleteTransaction();
//
//        controller.showTransactionManagement();
//
//        verify(controller).deleteTransaction();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Возврат в главное меню из меню управления")
//    public void goToMainMenuFromManagement() {
//        String input = "5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//
//        doNothing().when(controller).showMainMenu();
//
//        controller.showTransactionManagement();
//
//        verify(controller).showMainMenu();
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Просмотр всех транзакций через фильтр")
//    public void showAllTransactions() {
//        String input = "1\n5\n6\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("1. 2500,00 руб., Стипендия");
//        assertThat(output).contains("2. -250,00 руб., Карамельный макиато");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Создание транзакции с некорректной суммой и последующим успехом")
//    public void createTransactionWithInvalidSum() {
//        String input = "2\n1\nйцукен\n100000\nЗарплата\n\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        service.createTransaction(1, 100000.0, "Зарплата", "-");
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Пожалуйста, введите корректное число:");
//        assertThat(output).contains("Транзакция успешно сохранена!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Создание транзакции с категорией 'Цель' без установленной цели и отказом от установки")
//    public void createTransactionWithGoalCategoryNoGoal() {
//        String input = "2\n2\n500\nЦель\nнет\nЕда\n\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Вы еще не установили цель!");
//        assertThat(output).contains("Хотите прервать создание транзакции и создать цель прямо сейчас?");
//        assertThat(output).contains("Транзакция успешно сохранена!");
//        assertThat(service.getTransactionsByCategory("Еда")).isNotEmpty();
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Редактирование типа транзакции")
//    public void updateTransactionType() {
//        String input = "3\n1\n1\n2\n5\n5\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Успешно обновлено!");
//        assertThat(service.getTransactionsByType(1)).isEmpty();
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Удаление транзакции с подтверждением")
//    public void deleteTransactionWithConfirmation() {
//        String input = "4\n1\nда\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Удаление прошло успешно");
//        assertThat(service.getAllTransactions().size()).isEqualTo(1);
//        assertThat(service.getTransactionsByCategory("Стипендия")).isEmpty();
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Установка месячного бюджета")
//    public void updateMonthlyBudget() {
//        String input = "1\n1\n20000\n3\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Новый месячный бюджет 20000,00 руб. успешно установлен!");
//        assertThat(user.getMonthlyBudget()).isEqualTo(20000.0);
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Превышение месячного бюджета")
//    public void overgoMonthlyBudget() {
//        String input = "2\n2\n30000\nЕда\n600 булочек с сосиской\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//        user.setMonthlyBudget(20000.0);
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Внимание! Вы превысили установленный месячный бюджет на 7750,00 руб.");
//        assertThat(output).contains("Отправлено письмо на почту anya@ya.ru с содержанием: Внимание! Вы превысили установленный месячный бюджет на 7750,00 руб.");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Напоминание о приближении к лимиту расходов")
//    public void checkExpenseLimitReminderNearLimit() {
//        user.setMonthlyBudget(3000.0);
//
//
//        String input = "2\n2\n5050\nРазвлечения\nПокупка робота-пылесоса\n5\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showTransactionManagement();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Осторожно! Остаток бюджета составляет 200,00 руб. (менее 10% от лимита)");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//
//    @Test
//    @DisplayName("Проверка баланса")
//    public void showBalance() {
//        String input = "3\n3\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).containsPattern("Ваш баланс:\\s+2250,00 руб\\.");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Сводка за период")
//    public void getSummary() {
//        String input = "4\n4\n01.03.2025 00:00\n31.03.2025 23:59\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).containsPattern("Доходы:\\s+2500,00 руб\\.");
//        assertThat(output).containsPattern("Расходы:\\s+250,00 руб\\.");
//        assertThat(output).containsPattern("Итоговый баланс:\\s+2250,00 руб\\.");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Анализ расходов по категориям")
//    public void showCategoryAnalysis() {
//        String input = "5\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).containsPattern("Карамельный макиато\\s+250,00 руб\\.");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Формирование финансового отчета")
//    public void showGeneralReport() {
//        String input = "6\n01.03.2025 00:00\n31.03.2025 23:59\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).contains("=ФИНАНСОВЫЙ ОТЧЁТ=");
//        assertThat(output).contains("Общий доход: 2500,00 руб.");
//        assertThat(output).contains("Общий расход: 250,00 руб.");
//        assertThat(output).contains("Итоговый баланс: 2250,00 руб.");
//        verify(controller, times(1)).showGeneralReport();
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Формирование финансового отчета с целью и месячным бюджетом")
//    public void showGeneralReportWithGoalAndMonthlyBudget() {
//        user.setMonthlyBudget(20000.0);
//
//        user.setGoal(3000.0);
//        user.getTransactions().add(new Transaction(1, 2250.0, "Цель", ""));
//
//        String input = "6\n01.03.2025 00:00\n31.03.2025 23:59\n7\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showMainMenu();
//        controller.showStatsAndAnalysisMenu();
//
//        String output = outContent.toString();
//        assertThat(output).contains("=ФИНАНСОВЫЙ ОТЧЁТ=");
//        assertThat(output).contains("Общий доход: 4750,00 руб.");
//        assertThat(output).contains("Общий расход: 250,00 руб.");
//        assertThat(output).contains("Итоговый баланс: 4500,00 руб.");
//        assertThat(output).containsPattern("Цель:\\s+3000,00\\s+руб.");
//        assertThat(output).containsPattern("Расход по цели:\\s+0,00\\s+руб.");
//        assertThat(output).containsPattern("Накоплено:\\s+2250,00\\s+руб.");
//        assertThat(output).containsPattern("Осталось накопить:\\s+750,00\\s+руб.");
//        verify(controller, times(1)).showGeneralReport();
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка остатка бюджета при превышении лимита")
//    public void checkMonthlyBudgetLimitWhenExceeded() {
//        user.setMonthlyBudget(20000.0);
//
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//        user.getTransactions().add(new Transaction(2, 22500.0, "Еда", ""));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageMonthlyBudget();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Вы превысили лимит на месяц на 250,00 руб.!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка остатка бюджета при нулевом остатке")
//    public void checkMonthlyBudgetLimitWhenZero() {
//        user.setMonthlyBudget(2250.0);
//        user.getTransactions().add(new Transaction(2, 4500.0, "Еда", ""));
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageMonthlyBudget();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Ваш остаток: 0,00 руб.");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка остатка бюджета при положительном остатке")
//    public void checkMonthlyBudgetLimitWhenPositive() {
//        user.setMonthlyBudget(3000.0);
//        user.getTransactions().add(new Transaction(2, 4500, "Еда", "10 ведер рафа"));
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageMonthlyBudget();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Ваш остаток: 750,00 руб. Продолжайте в том же духе!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Управление целью: переход к установке новой цели")
//    public void manageGoalUpdateGoal() {
//        String input = "1\n5000\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Вы пока не установили цель. Сделайте это прямо сейчас!");
//        assertThat(output).contains("Выберите действие:");
//        assertThat(user.getGoal()).isEqualTo(5000.0);
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Управление целью: проверка прогресса по цели")
//    public void manageGoalCheckProgress() {
//        user.setGoal(3000.0);
//        user.getTransactions().add(new Transaction(1, 2250.0, "Цель", ""));
//
//
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Ваша установленная цель: 3000,00 руб.");
//        assertThat(output).contains("До цели осталось накопить 750,00 руб. Отличный результат!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Обновление цели с некорректным вводом и последующим успехом")
//    public void updateGoalWithInvalidInput() {
//        String input = "1\nqwerty\n5000\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Некорректный ввод! Введите положительное число.");
//        assertThat(output).contains("Новая цель 5000,00 руб. успешно установлена!");
//        assertThat(user.getGoal()).isEqualTo(5000.0);
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка прогресса по цели: цель превышена")
//    public void getGoalProgressWhenExceeded() {
//        user.setGoal(2000.0);
//        user.getTransactions().add(new Transaction(1, 2250.0, "Цель", ""));
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Поздравляем! Вы превысили цель на 250,00 руб.!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка прогресса по цели: цель достигнута")
//    public void getGoalProgressWhenAchieved() {
//        user.setGoal(2250.0);
//        user.getTransactions().add(new Transaction(1, 2250.0, "Цель", ""));
//
//
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//        String output = outContent.toString();
//        assertThat(output).contains("Поздравляем! Вы достигли своей цели! Может, пора установить новую? ;)");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//
//    @Test
//    @DisplayName("Проверка прогресса по цели: цель не достигнута")
//    public void getGoalProgressWhenNotAchieved() {
//        user.setGoal(3000.0);
//        user.getTransactions().add(new Transaction(1, 2250.0, "Цель", ""));
//
//
//        String input = "2\n3\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//
//        TransactionController controller = spy(new TransactionController(service, statsService, userController, user));
//        doNothing().when(controller).showStatsAndAnalysisMenu();
//        controller.manageGoal();
//
//
//        String output = outContent.toString();
//        assertThat(output).containsPattern("До цели осталось накопить 750,00 руб. Отличный результат!");
//
//        System.setOut(System.out);
//        System.setIn(System.in);
//    }
//}
