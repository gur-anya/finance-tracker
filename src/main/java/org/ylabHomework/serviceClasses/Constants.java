package org.ylabHomework.serviceClasses;

/**
 * Класс, хранящий текстовые блоки для контроллеров в виде констант
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 07.03.2025
 * </p>
 */
public class Constants {
    public static final String SHOW_ADMIN_MAIN_PAGE_TEXT_BLOCK = """
            Выберите, что хотите сделать:
            1. Просмотреть пользователей
            2. Заблокировать пользователя
            3. Разблокировать пользователя
            4. Удалить пользователя
            5. Выйти из аккаунта
            """;
    /**
     * Текстовый блок для контроллера пользователя: показать меню изменения данных для пользователя.
     */
    public static final String UPDATE_USER_TEXT_BLOCK = """
            Что вы хотите изменить?
            1. Имя пользователя
            2. Адрес электронной почты
            3. Пароль""";

    public static final String TRANSACTIONS_MAIN_PAGE = """
            Выберите действие:
            1. Перейти к транзакциям
            2. Перейти в меню статистики и анализа
            3. Назад
            4. Выход из программы
            """;
    public static final String TRANSACTIONS_MANAGEMENT_MENU = """
            Выберите действие:
            1. Просмотреть транзакции
            2. Добавить транзакцию
            3. Редактировать транзакции
            4. Удалить транзакцию
            5. Назад
            """;
    public static final String STATS_AND_ANALYSIS_MENU = """
            Выберите действие:
            1. Управлять месячным бюджетом
            2. Управлять финансовой целью
            3. Вывести текущий баланс с учетом всех транзакций
            4. Рассчитать суммарный доход и расход за период
            5. Проанализировать общие расходы по категориям
            6. Сформировать отчет по финансовому состоянию
            7. Назад
            """;
    public static final String FILTER_CHOOSING_MENU = """
            Выберите фильтр для транзакций:
            1. До даты
            2. После даты
            3. По категории
            4. По типу (доход/расход)
            5. Все (без фильтра)
            6. Назад
            """;
    public static final String UPDATE_TRANSACTIONS_MENU = """
            Что хотите отредактировать:
            1. Тип
            2. Сумму
            3. Категорию
            4. Описание
            5. Назад
            """;
    public static final String MONTHLY_BUDGET_MENU = """
            Выберите действие:
            1. Изменить бюджет на месяц
            2. Проверить остаток на месяц
            3. В меню статистики и анализа
            """;
    public static final String GOAL_MENU = """
            Выберите действие:
            1. Изменить цель
            2. Проверить прогресс по цели
            3. В меню статистики и анализа
            """;


    /**
     * Запрос для поиска всех пользователей в таблице users.
     */
    public static final String FIND_ALL_USERS = "SELECT * FROM main.users";

    /**
     * Запрос для поиска всех зарегистрированных адресов электронной почты в таблице users.
     */
    public static final String FIND_ALL_EMAILS = "SELECT email FROM main.users";

    /**
     * Запрос для добавления нового пользователя в таблицу users.
     * Ожидает параметры: name, email, password, role_id, is_active.
     */
    public static final String ADD_USER = "INSERT INTO main.users (name, email, password, role_id, is_active) " +
            "VALUES (?, ?, ?, ?, ?)";

    /**
     * Запрос для поиска пользователя по адресу электронной почты в таблице users.
     * Ожидает параметр: email.
     */
    public static final String FIND_USER_BY_EMAIL = "SELECT * FROM main.users WHERE email = ?";

    /**
     * Запрос для удаления пользователя по адресу электронной почты из таблицы users.
     * Ожидает параметр: email.
     */
    public static final String DELETE_USER_BY_EMAIL = "DELETE FROM main.users WHERE email = ?";

    /**
     * Запрос для поиска идентификатора пользователя по адресу электронной почты в таблице users.
     * Ожидает параметр: email.
     */
    public static final String FIND_USER_ID_BY_EMAIL = "SELECT id FROM main.users WHERE email = ?";

    /**
     * Запрос для обновления имени пользователя в таблице users.
     * Ожидает параметры: name, email.
     */
    public static final String UPDATE_USER_NAME = "UPDATE main.users SET name = ? WHERE email = ?";

    /**
     * Запрос для обновления адреса электронной почты пользователя в таблице users.
     * Ожидает параметры: new_email, old_email.
     */
    public static final String UPDATE_USER_EMAIL = "UPDATE main.users SET email = ? WHERE email = ?";

    /**
     * Запрос для обновления пароля пользователя в таблице users.
     * Ожидает параметры: password, email.
     */
    public static final String UPDATE_USER_PASSWORD = "UPDATE main.users SET password = ? WHERE email = ?";

    /**
     * Запрос для обновления статуса активности пользователя в таблице users.
     * Ожидает параметры: is_active, email.
     */
    public static final String UPDATE_USER_ACTIVITY = "UPDATE main.users SET is_active = ? WHERE email = ?";

    /**
     * Запрос для добавления новой транзакции в таблицу transactions.
     * Ожидает параметры: type, sum, category, description, created_at, user_id.
     */
    public static final String ADD_TRANSACTION = "INSERT INTO main.transactions (type, sum, category, description, created_at, user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * Запрос для поиска всех транзакций пользователя в таблице transactions.
     * Ожидает параметр: user_id.
     */
    public static final String FIND_ALL_TRANSACTIONS_BY_USER = "SELECT * FROM main.transactions WHERE user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя по типу в таблице transactions.
     * Ожидает параметры: type, user_id.
     */
    public static final String FIND_TRANSACTIONS_BY_TYPE = "SELECT * FROM main.transactions WHERE type = ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя по категории в таблице transactions.
     * Ожидает параметры: category, user_id.
     */
    public static final String FIND_TRANSACTIONS_BY_CATEGORY = "SELECT * FROM main.transactions WHERE category = ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя, совершенных до указанного времени, в таблице transactions.
     * Ожидает параметры: created_at, user_id.
     */
    public static final String FIND_TRANSACTIONS_BEFORE_TIMESTAMP = "SELECT * FROM main.transactions WHERE created_at <= ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя, совершенных после указанного времени, в таблице transactions.
     * Ожидает параметры: created_at, user_id.
     */
    public static final String FIND_TRANSACTIONS_AFTER_TIMESTAMP = "SELECT * FROM main.transactions WHERE created_at >= ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя в заданном временном диапазоне в таблице transactions.
     * Ожидает параметры: created_at_start, created_at_end, user_id.
     */
    public static final String FIND_TRANSACTIONS_BETWEEN_TIMESTAMPS = "SELECT * FROM main.transactions WHERE created_at >= ? AND created_at <= ? AND user_id = ?";

    /**
     * Запрос для обновления типа транзакции в таблице transactions.
     * Ожидает параметры: type, created_at, user_id.
     */
    public static final String UPDATE_TRANSACTION_TYPE = "UPDATE main.transactions SET type = ? WHERE created_at = ? AND user_id = ?";

    /**
     * Запрос для обновления суммы транзакции в таблице transactions.
     * Ожидает параметры: sum, created_at, user_id.
     */
    public static final String UPDATE_TRANSACTION_SUM = "UPDATE main.transactions SET sum = ? WHERE created_at = ? AND user_id = ?";

    /**
     * Запрос для обновления категории транзакции в таблице transactions.
     * Ожидает параметры: category, created_at, user_id.
     */
    public static final String UPDATE_TRANSACTION_CATEGORY = "UPDATE main.transactions SET category = ? WHERE created_at = ? AND user_id = ?";

    /**
     * Запрос для обновления описания транзакции в таблице transactions.
     * Ожидает параметры: description, created_at, user_id.
     */
    public static final String UPDATE_TRANSACTION_DESCRIPTION = "UPDATE main.transactions SET description = ? WHERE created_at = ? AND user_id = ?";

    /**
     * Запрос для удаления транзакции из таблицы transactions.
     * Ожидает параметры: created_at, user_id.
     */
    public static final String DELETE_TRANSACTION = "DELETE FROM main.transactions WHERE created_at = ? AND user_id = ?";

    /**
     * Запрос для получения месячного бюджета пользователя из таблицы users.
     * Ожидает параметр: id.
     */
    public static final String GET_MONTHLY_BUDGET = "SELECT monthly_budget FROM main.users WHERE id = ?";

    /**
     * Запрос для установки месячного бюджета пользователя в таблице users.
     * Ожидает параметры: monthly_budget, id.
     */
    public static final String SET_MONTHLY_BUDGET = "UPDATE main.users SET monthly_budget = ? WHERE id = ?";

    /**
     * Запрос для получения финансовой цели пользователя из таблицы users.
     * Ожидает параметр: id.
     */
    public static final String GET_GOAL = "SELECT goal FROM main.users WHERE id = ?";

    /**
     * Запрос для установки финансовой цели пользователя в таблице users.
     * Ожидает параметры: goal, id.
     */
    public static final String SET_GOAL = "UPDATE main.users SET goal = ? WHERE id = ?";
}
