package TransactionTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ylabHomework.controllers.TransactionController;
import org.ylabHomework.controllers.UserController;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;
import org.ylabHomework.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для контроллера транзакций")
public class TransactionControllerTests {
    private final UserRepository userRepo = new UserRepository();
    private final UserService userService = new UserService(userRepo);
    private UserController userController = new UserController(userService);
    private final User user = userService.readUserByEmail("anya@ya.ru");
    private final TransactionRepository repo = new TransactionRepository(user);
    private final TransactionService service = new TransactionService(repo, user);
    private final TransactionStatsService statsService = new TransactionStatsService(repo, user);

    @Test
    @DisplayName("Переход к управлению транзакциями с главного меню")
    @SuppressWarnings("unchecked")
    public void goToTransactionManagement() throws Exception {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController spyController = spy(new TransactionController(service, statsService, userController));

        Field mainMenuCommandsField = TransactionController.class.getDeclaredField("mainMenuCommands");
        mainMenuCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> mainMenuCommands =
                (Map<String, TransactionController.Command>) mainMenuCommandsField.get(spyController);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        mainMenuCommands.put("1", mockCommand);

        spyController.showMainMenu();

        verify(mockCommand, times(1)).execute();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к статистике с главного меню")
    @SuppressWarnings("unchecked")
    public void goToStatsAndAnalysisMenu() throws Exception {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController spyController = spy(new TransactionController(service, statsService, userController));

        Field mainMenuCommandsField = TransactionController.class.getDeclaredField("mainMenuCommands");
        mainMenuCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> mainMenuCommands =
                (Map<String, TransactionController.Command>) mainMenuCommandsField.get(spyController);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        mainMenuCommands.put("2", mockCommand);

        spyController.showMainMenu();

        verify(mockCommand, times(1)).execute();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Возврат в меню пользователя с главного меню")
    public void goToMainPageUser() {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        userController = spy(new UserController(userService));
        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        doNothing().when(userController).showMainPageUser();

        controller.showMainMenu();

        verify(userController).showMainPageUser();
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Выход из программы с главного меню")
    public void exitAppFromMainMenu() {
        String input = "4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        doNothing().when(controller).showMainMenu();

        controller.showMainMenu();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Обработка некорректного ввода на главном меню с последующим корректным выбором")
    @SuppressWarnings("unchecked")
    public void mainMenuOptionMismatch() throws Exception {
        String input = "йцукен\n1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));

        Field mainMenuCommandsField = TransactionController.class.getDeclaredField("mainMenuCommands");
        mainMenuCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> mainMenuCommands =
                (Map<String, TransactionController.Command>) mainMenuCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        mainMenuCommands.put("1", mockCommand);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        controller.showMainMenu();

        String output = outContent.toString();
        assertThat(output).contains("Пожалуйста, введите 1, 2, 3 или 4.");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к просмотру транзакций из меню управления")
    @SuppressWarnings("unchecked")
    public void goToShowTransactionsFilter() throws Exception {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);

        managementCommands.put("1", controller::showTransactionsFilter);
        doNothing().when(controller).showTransactionsFilter();

        controller.showTransactionManagement();

        verify(controller, times(1)).showTransactionsFilter();

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Создание новой транзакции из меню управления")
    @SuppressWarnings("unchecked")
    public void goToCreateTransactionAndCreateTransaction() throws Exception {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);


        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doAnswer(invocation -> {
            System.out.println("Транзакция успешно сохранена!");
            return null;
        }).when(mockCommand).execute();
        managementCommands.put("2", mockCommand);

        controller.showTransactionManagement();


        String output = outContent.toString();
        assertThat(output).contains("Транзакция успешно сохранена!");

        verify(controller, times(1)).showTransactionManagement();
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к редактированию транзакций из меню управления")
    @SuppressWarnings("unchecked")
    public void goToUpdateTransaction() throws Exception {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        managementCommands.put("3", mockCommand);

        controller.showTransactionManagement();

        verify(mockCommand, times(1)).execute();
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Переход к удалению транзакции из меню управления")
    @SuppressWarnings("unchecked")
    public void goToDeleteTransaction() throws Exception {
        String input = "4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        managementCommands.put("4", mockCommand);

        controller.showTransactionManagement();

        verify(mockCommand, times(1)).execute();
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Возврат в главное меню из меню управления")
    @SuppressWarnings("unchecked")
    public void goToMainMenuFromManagement() throws Exception {
        String input = "5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        managementCommands.put("5", mockCommand);

        controller.showTransactionManagement();

        verify(mockCommand, times(1)).execute();
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Просмотр всех транзакций через фильтр")
    @SuppressWarnings("unchecked")
    public void showAllTransactions() throws Exception {
        String input = "5\n6\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field filterCommandsField = TransactionController.class.getDeclaredField("filterCommands");
        filterCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> filterCommands =
                (Map<String, TransactionController.Command>) filterCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        doNothing().when(controller).showTransactionManagement();
        filterCommands.put("6", mockCommand);

        controller.showTransactionsFilter();

        String output = outContent.toString();
        assertThat(output).contains("2500,00 руб., Стипендия");
        assertThat(output).contains("-250,00 руб., Карамельный макиато");
        assertThat(output).contains("-50,00 руб., Булочка с сосиской");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Создание транзакции с некорректной суммой и последующим успехом")
    @SuppressWarnings("unchecked")
    public void createTransactionWithInvalidSum() throws Exception {
        String input = "2\n1\nйцукен\n100000\nМатпомощь\n\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        service.createTransaction(1, "100000.0", "Зарплата", "-");

        Field managementCommandsField = TransactionController.class.getDeclaredField("managementCommands");
        managementCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> managementCommands =
                (Map<String, TransactionController.Command>) managementCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        managementCommands.put("5", mockCommand);

        controller.showTransactionManagement();

        String output = outContent.toString();
        assertThat(output).contains("Пожалуйста, введите корректное число!");
        assertThat(output).contains("Транзакция успешно сохранена!");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Установка месячного бюджета с последующим выходом назад")
    @SuppressWarnings("unchecked")
    public void updateMonthlyBudget() throws Exception {

        String input = "1\n1\n20000\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));


        Field budgetCommandsField = TransactionController.class.getDeclaredField("budgetCommands");
        budgetCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> budgetCommands =
                (Map<String, TransactionController.Command>) budgetCommandsField.get(controller);


        TransactionController.Command mockBackCommand = mock(TransactionController.Command.class);
        doNothing().when(mockBackCommand).execute();
        budgetCommands.put("3", mockBackCommand);




        controller.showStatsAndAnalysisMenu();

        String output = outContent.toString();
        assertThat(output).contains("Новый месячный бюджет 20000,00 руб. успешно установлен!");
        verify(mockBackCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }



    @Test
    @DisplayName("Сводка за период")
    @SuppressWarnings("unchecked")
    public void getSummary() throws Exception {
        String input = "4\n01.03.2025 00:00\n31.03.2025 23:59\n7\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field statsCommandsField = TransactionController.class.getDeclaredField("statsCommands");
        statsCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> statsCommands =
                (Map<String, TransactionController.Command>) statsCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        statsCommands.put("7", mockCommand);

        controller.showStatsAndAnalysisMenu();

        String output = outContent.toString();
        assertThat(output).containsPattern("Доходы:\\s+2500,00 руб\\.");
        assertThat(output).containsPattern("Расходы:\\s+300,00 руб\\.");
        assertThat(output).containsPattern("Итоговый баланс:\\s+2200,00 руб\\.");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Анализ расходов по категориям")
    @SuppressWarnings("unchecked")
    public void showCategoryAnalysis() throws Exception {
        String input = "5\n7\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field statsCommandsField = TransactionController.class.getDeclaredField("statsCommands");
        statsCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> statsCommands =
                (Map<String, TransactionController.Command>) statsCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        statsCommands.put("7", mockCommand);

        controller.showStatsAndAnalysisMenu();

        String output = outContent.toString();
        assertThat(output).containsPattern("Карамельный макиато\\s+250,00 руб\\.");
        assertThat(output).containsPattern("Булочка с сосиской\\s+50,00 руб\\.");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Формирование финансового отчета")
    @SuppressWarnings("unchecked")
    public void showGeneralReport() throws Exception {
        String input = "6\n01.03.2025 00:00\n31.03.2025 23:59\n7\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field statsCommandsField = TransactionController.class.getDeclaredField("statsCommands");
        statsCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> statsCommands =
                (Map<String, TransactionController.Command>) statsCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        statsCommands.put("7", mockCommand);

        controller.showStatsAndAnalysisMenu();

        String output = outContent.toString();
        assertThat(output).contains("ФИНАНСОВЫЙ ОТЧЁТ");
        assertThat(output).containsPattern("Доходы:\\s+2500,00 руб.");
        assertThat(output).containsPattern("Расходы:\\s+300,00 руб.");
        assertThat(output).containsPattern("Баланс:\\s+2200,00 руб.");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Управление целью")
    @SuppressWarnings("unchecked")
    public void manageGoalUpdateGoal() throws Exception {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        TransactionController controller = spy(new TransactionController(service, statsService, userController));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Field goalCommandsField = TransactionController.class.getDeclaredField("goalCommands");
        goalCommandsField.setAccessible(true);
        Map<String, TransactionController.Command> goalCommands =
                (Map<String, TransactionController.Command>) goalCommandsField.get(controller);

        TransactionController.Command mockCommand = mock(TransactionController.Command.class);
        doNothing().when(mockCommand).execute();
        goalCommands.put("1", mockCommand);

        controller.manageGoal();

        String output = outContent.toString();
        assertThat(output).contains("Ваша установленная цель: 1000,00 руб");
        assertThat(output).contains("Выберите действие:");
        verify(mockCommand, times(1)).execute();

        System.setOut(System.out);
        System.setIn(System.in);
    }
}