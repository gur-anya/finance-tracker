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

    /**
     * Ссылка на страницу с формой главной страницы транзакций main_transaction_page.jsp.
     */
    public static final String TRANSACTIONS_MAIN_JSP = "transaction_jsps/main_transaction_page.jsp";

    /**
     * Ссылка на страницу для управления транзакциями transactions_management_page.jsp.
     */
    public static final String TRANSACTIONS_MANAGEMENT_JSP = "transaction_jsps/transactions_management_page.jsp";
    /**
     * Ссылка на страницу для обновления транзакций update_transaction_page.jsp.
     */
    public static final String UPDATE_TRANSACTION_JSP = "transaction_jsps/update_transaction_page.jsp";

    /**
     * Ссылка на страницу для создания транзакций create_transactions_page.jsp.
     */
    public static final String CREATE_TRANSACTION_JSP = " transaction_jsps/create_transactions_page.jsp";
    /**
     * Ссылка на страницу для просмотра транзакций с заданным фильтром show_transactions_page.jsp.
     */
    public static final String SHOW_TRANSACTIONS_JSP ="transaction_jsps/show_transactions_page.jsp";
    /**
     * Ссылка на страницу для удаления транзакций delete_transaction_page.jsp.
     */
    public static final String DELETE_TRANSACTION_JSP ="transaction_jsps/delete_transaction_page.jsp";

    /**
     * Ссылка на главную страницу index.jsp.
     */
    public static final String INDEX_JSP = "index.jsp";
    /**
     * Ссылка на страницу с формой регистрации registration.jsp.
     */
    public static final String REGISTRATION_JSP = "user_jsps/registration.jsp";
    /**
     * Ссылка на страницу с формой логина login.jsp.
     */
    public static final String LOGIN_JSP = "user_jsps/login.jsp";

    /**
     * Ссылка на страницу с формой главной страницы пользователя main_user_page.jsp.
     */
    public static final String USER_MAIN_JSP = "user_jsps/main_user_page.jsp";
    /**
     * Ссылка на страницу с формой просмотре действий для аккаунта пользователя personal_account.jsp.
     */
    public static final String PERSONAL_ACCOUNT_JSP = "user_jsps/personal_account.jsp";
    /**
     * Ссылка на страницу с формой удаления аккаунта пользователя delete_account.jsp.
     */
    public static final String DELETE_ACCOUNT_JSP = "user_jsps/delete_account.jsp";
    /**
     * Ссылка на страницу с формой редактирования аккаунта пользователя update_account.jsp.
     */
    public static final String UPDATE_ACCOUNT_JSP = "user_jsps/update_account.jsp";
    /**
     * Запрос для поиска всех пользователей в таблице users.
     */
    public static final String FIND_ALL_USERS = "SELECT * FROM main.users";

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
     * Запрос для поиска пользователя по id в таблице users.
     * Ожидает параметр: id.
     */
    public static final String FIND_USER_BY_ID = "SELECT * FROM main.users WHERE id = ?";

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
     * Ожидает параметры: type, sum, category, description, timestamp, user_id.
     */
    public static final String ADD_TRANSACTION = "INSERT INTO main.transactions (type, sum, category, description, timestamp, user_id) " +
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
     * Ожидает параметры: timestamp, user_id.
     */
    public static final String FIND_TRANSACTIONS_BEFORE_TIMESTAMP = "SELECT * FROM main.transactions WHERE timestamp <= ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя, совершенных после указанного времени, в таблице transactions.
     * Ожидает параметры: timestamp, user_id.
     */
    public static final String FIND_TRANSACTIONS_AFTER_TIMESTAMP = "SELECT * FROM main.transactions WHERE timestamp >= ? AND user_id = ?";

    /**
     * Запрос для поиска транзакций пользователя в заданном временном диапазоне в таблице transactions.
     * Ожидает параметры: timestamp_start, timestamp_end, user_id.
     */
    public static final String FIND_TRANSACTIONS_BETWEEN_TIMESTAMPS = "SELECT * FROM main.transactions WHERE timestamp >= ? AND timestamp <= ? AND user_id = ?";

    /**
     * Запрос для обновления типа транзакции в таблице transactions.
     * Ожидает параметры: type, timestamp, user_id.
     */
    public static final String UPDATE_TRANSACTION_TYPE = "UPDATE main.transactions SET type = ? WHERE timestamp = ? AND user_id = ?";

    /**
     * Запрос для обновления суммы транзакции в таблице transactions.
     * Ожидает параметры: sum, timestamp, user_id.
     */
    public static final String UPDATE_TRANSACTION_SUM = "UPDATE main.transactions SET sum = ? WHERE timestamp = ? AND user_id = ?";

    /**
     * Запрос для обновления категории транзакции в таблице transactions.
     * Ожидает параметры: category, timestamp, user_id.
     */
    public static final String UPDATE_TRANSACTION_CATEGORY = "UPDATE main.transactions SET category = ? WHERE timestamp = ? AND user_id = ?";

    /**
     * Запрос для обновления описания транзакции в таблице transactions.
     * Ожидает параметры: description, timestamp, user_id.
     */
    public static final String UPDATE_TRANSACTION_DESCRIPTION = "UPDATE main.transactions SET description = ? WHERE timestamp = ? AND user_id = ?";

    /**
     * Запрос для удаления транзакции из таблицы transactions.
     * Ожидает параметры: user_id, type, sum, category, description, timestamp.
     */
    public static final String DELETE_TRANSACTION = "DELETE FROM main.transactions WHERE user_id = ? AND type = ? AND sum = ? AND category = ? AND description = ? AND timestamp = ?";

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
