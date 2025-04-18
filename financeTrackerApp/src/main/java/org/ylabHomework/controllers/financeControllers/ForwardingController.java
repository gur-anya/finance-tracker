package org.ylabHomework.controllers.financeControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.serviceClasses.Constants;

@Tag(name = "API перенаправления на страницы, связанные с транзакциями")
@Controller
public class ForwardingController {
    @Operation(
            summary = "Показать главную страницу транзакций",
            description = "Перенаправляет на главную страницу управления транзакциями")
    @ApiResponse(responseCode = "200", description = "Страница транзакций")
    @GetMapping(value = "/main_transaction_page")
    public String showFinanceManagement() {
        return Constants.TRANSACTIONS_MAIN_JSP;
    }

    @Operation(
            summary = "Показать страницу управления транзакциями",
            description = "Перенаправляет на страницу управления транзакциями")
    @ApiResponse(responseCode = "200", description = "Страница управления транзакциями")
    @GetMapping(value = "/transactions_management_page")
    public String showFinanceMainPage() {
        return Constants.TRANSACTIONS_MANAGEMENT_JSP;
    }

    @Operation(
            summary = "Показать страницу общей статистики",
            description = "Перенаправляет на страницу общей статистики")
    @ApiResponse(responseCode = "200", description = "Страница общей статистики")
    @GetMapping(value = "/general_stats_page")
    public String showFinanceStats() {
        return Constants.SHOW_STATS_PAGE_JSP;
    }

    @Operation(
            summary = "Показать страницу создания транзакции",
            description = "Перенаправляет на страницу создания транзакции")
    @ApiResponse(responseCode = "200", description = "Страница создания транзакции")
    @GetMapping(value = "/create_transaction")
    public String showCreateTransactionPage() {
        return Constants.CREATE_TRANSACTION_JSP;
    }

    @Operation(
            summary = "Показать страницу удаления транзакции",
            description = "Перенаправляет на страницу удаления транзакции")
    @ApiResponse(responseCode = "200", description = "Страница удаления транзакции")
    @GetMapping(value = "/delete_transaction")
    public String showDeleteTransactionPage() {
        return Constants.DELETE_TRANSACTION_JSP;
    }

    @Operation(
            summary = "Показать страницу фильтрации транзакций",
            description = "Перенаправляет на страницу фильтрации транзакций")
    @ApiResponse(responseCode = "200", description = "Страница фильтрации транзакций")
    @GetMapping(value = "/show_transactions")
    public String showTransactionsFilteringPage() {
        return Constants.SHOW_TRANSACTIONS_JSP;
    }

    @Operation(
            summary = "Показать страницу обновления транзакции",
            description = "Перенаправляет на страницу обновления транзакции")
    @ApiResponse(responseCode = "200", description = "Страница обновления транзакции")
    @GetMapping(value = "/update_transaction")
    public String showUpdateTransactionPage() {
        return Constants.UPDATE_TRANSACTION_JSP;
    }

    @Operation(
            summary = "Показать страницу общего отчёта",
            description = "Перенаправляет на страницу общего отчёта")
    @ApiResponse(responseCode = "200", description = "Страница общего отчёта")
    @GetMapping(value = "/general_report")
    public String showGeneralReportPage() {
        return Constants.GENERAL_REPORT_JSP;
    }

    @Operation(
            summary = "Показать страницу проверки цели",
            description = "Перенаправляет на страницу проверки цели")
    @ApiResponse(responseCode = "200", description = "Страница проверки цели")
    @GetMapping(value = "/check_goal")
    public String showCheckGoalPage() {
        return Constants.SHOW_CHECK_GOAL_JSP;
    }

    @Operation(
            summary = "Показать страницу управления целью",
            description = "Перенаправляет на страницу управления целью")
    @ApiResponse(responseCode = "200", description = "Страница управления целью")
    @GetMapping(value = "/goal_management")
    public String showGoalManagementPage() {
        return Constants.SHOW_GOAL_MANAGEMENT_PAGE_JSP;
    }

    @Operation(
            summary = "Показать страницу обновления цели",
            description = "Перенаправляет на страницу обновления цели")
    @ApiResponse(responseCode = "200", description = "Страница обновления цели")
    @GetMapping(value = "/update_goal")
    public String showUpdateGoalPage() {
        return Constants.UPDATE_GOAL_PAGE_JSP;
    }

    @Operation(
            summary = "Показать страницу управления бюджетом",
            description = "Перенаправляет на страницу управления месячным бюджетом")
    @ApiResponse(responseCode = "200", description = "Страница управления бюджетом")
    @GetMapping(value = "/monthly_budget_management")
    public String showBudgetManagementPage() {
        return Constants.SHOW_BUDGET_MANAGEMENT_PAGE_JSP;
    }

    @Operation(
            summary = "Показать страницу проверки бюджета",
            description = "Перенаправляет на страницу проверки месячного бюджета")
    @ApiResponse(responseCode = "200", description = "Страница проверки бюджета")
    @GetMapping(value = "/check_budget")
    public String showCheckBudgetPage() {
        return Constants.SHOW_CHECK_BUDGET_JSP;
    }

    @Operation(
            summary = "Показать страницу обновления бюджета",
            description = "Перенаправляет на страницу обновления месячного бюджета")
    @ApiResponse(responseCode = "200", description = "Страница обновления бюджета")
    @GetMapping(value = "/update_budget")
    public String showUpdateBudgetPage() {
        return Constants.UPDATE_BUDGET_PAGE_JSP;
    }

    @Operation(
            summary = "Показать страницу текущего баланса",
            description = "Перенаправляет на страницу текущего баланса")
    @ApiResponse(responseCode = "200", description = "Страница текущего баланса")
    @GetMapping(value = "/current_balance")
    public String showBalancePage() {
        return Constants.BALANCE_JSP;
    }

    @Operation(
            summary = "Показать страницу расходов по категориям",
            description = "Перенаправляет на страницу анализа расходов по категориям")
    @ApiResponse(responseCode = "200", description = "Страница расходов по категориям")
    @GetMapping(value = "/summary_expenses_by_categories")
    public String showExpensesByCategoryPage() {
        return Constants.CATEGORY_EXPENSES_JSP;
    }

    @Operation(
            summary = "Показать страницу доходов и расходов",
            description = "Перенаправляет на страницу сводки доходов и расходов")
    @ApiResponse(responseCode = "200", description = "Страница доходов и расходов")
    @GetMapping(value = "/summary_income_expense")
    public String showSummaryIncomeExpensePage() {
        return Constants.SUMMARY_INCOME_EXPENSE_JSP;
    }
}
