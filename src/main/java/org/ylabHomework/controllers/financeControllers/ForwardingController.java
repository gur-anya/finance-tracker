package org.ylabHomework.controllers.financeControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.serviceClasses.Constants;

@Api(value = "API перенаправления на страницы, связанные с транзакциями")
@Controller
public class ForwardingController {
    @ApiOperation(value = "Показать главную страницу транзакций",
            notes = "Перенаправляет на главную страницу управления транзакциями")
    @ApiResponse(code = 200, message = "Страница транзакций")
    @GetMapping(value = "/main_transaction_page")
    public String showFinanceManagement() {
        return Constants.TRANSACTIONS_MAIN_JSP;
    }

    @ApiOperation(value = "Показать страницу управления транзакциями",
            notes = "Перенаправляет на страницу управления транзакциями")
    @ApiResponse(code = 200, message = "Страница управления транзакциями")
    @GetMapping(value = "/transactions_management_page")
    public String showFinanceMainPage() {
        return Constants.TRANSACTIONS_MANAGEMENT_JSP;
    }

    @ApiOperation(value = "Показать страницу общей статистики",
            notes = "Перенаправляет на страницу общей статистики")
    @ApiResponse(code = 200, message = "Страница общей статистики")
    @GetMapping(value = "/general_stats_page")
    public String showFinanceStats() {
        return Constants.SHOW_STATS_PAGE_JSP;
    }

    @ApiOperation(value = "Показать страницу создания транзакции",
            notes = "Перенаправляет на страницу создания транзакции")
    @ApiResponse(code = 200, message = "Страница создания транзакции")
    @GetMapping(value = "/create_transaction")
    public String showCreateTransactionPage() {
        return Constants.CREATE_TRANSACTION_JSP;
    }

    @ApiOperation(value = "Показать страницу удаления транзакции",
            notes = "Перенаправляет на страницу удаления транзакции")
    @ApiResponse(code = 200, message = "Страница удаления транзакции")
    @GetMapping(value = "/delete_transaction")
    public String showDeleteTransactionPage() {
        return Constants.DELETE_TRANSACTION_JSP;
    }

    @ApiOperation(value = "Показать страницу фильтрации транзакций",
            notes = "Перенаправляет на страницу фильтрации транзакций")
    @ApiResponse(code = 200, message = "Страница фильтрации транзакций")
    @GetMapping(value = "/show_transactions")
    public String showTransactionsFilteringPage() {
        return Constants.SHOW_TRANSACTIONS_JSP;
    }

    @ApiOperation(value = "Показать страницу обновления транзакции",
            notes = "Перенаправляет на страницу обновления транзакции")
    @ApiResponse(code = 200, message = "Страница обновления транзакции")
    @GetMapping(value = "/update_transaction")
    public String showUpdateTransactionPage() {
        return Constants.UPDATE_TRANSACTION_JSP;
    }

    @ApiOperation(value = "Показать страницу общего отчёта",
            notes = "Перенаправляет на страницу общего отчёта")
    @ApiResponse(code = 200, message = "Страница общего отчёта")
    @GetMapping(value = "/general_report")
    public String showGeneralReportPage() {
        return Constants.GENERAL_REPORT_JSP;
    }

    @ApiOperation(value = "Показать страницу проверки цели",
            notes = "Перенаправляет на страницу проверки цели")
    @ApiResponse(code = 200, message = "Страница проверки цели")
    @GetMapping(value = "/check_goal")
    public String showCheckGoalPage() {
        return Constants.SHOW_CHECK_GOAL_JSP;
    }

    @ApiOperation(value = "Показать страницу управления целью",
            notes = "Перенаправляет на страницу управления целью")
    @ApiResponse(code = 200, message = "Страница управления целью")
    @GetMapping(value = "/goal_management")
    public String showGoalManagementPage() {
        return Constants.SHOW_GOAL_MANAGEMENT_PAGE_JSP;
    }

    @ApiOperation(value = "Показать страницу обновления цели",
            notes = "Перенаправляет на страницу обновления цели")
    @ApiResponse(code = 200, message = "Страница обновления цели")
    @GetMapping(value = "/update_goal")
    public String showUpdateGoalPage() {
        return Constants.UPDATE_GOAL_PAGE_JSP;
    }

    @ApiOperation(value = "Показать страницу управления бюджетом",
            notes = "Перенаправляет на страницу управления месячным бюджетом")
    @ApiResponse(code = 200, message = "Страница управления бюджетом")
    @GetMapping(value = "/monthly_budget_management")
    public String showBudgetManagementPage() {
        return Constants.SHOW_BUDGET_MANAGEMENT_PAGE_JSP;
    }

    @ApiOperation(value = "Показать страницу проверки бюджета",
            notes = "Перенаправляет на страницу проверки месячного бюджета")
    @ApiResponse(code = 200, message = "Страница проверки бюджета")
    @GetMapping(value = "/check_budget")
    public String showCheckBudgetPage() {
        return Constants.SHOW_CHECK_BUDGET_JSP;
    }

    @ApiOperation(value = "Показать страницу обновления бюджета",
            notes = "Перенаправляет на страницу обновления месячного бюджета")
    @ApiResponse(code = 200, message = "Страница обновления бюджета")
    @GetMapping(value = "/update_budget")
    public String showUpdateBudgetPage() {
        return Constants.UPDATE_BUDGET_PAGE_JSP;
    }

    @ApiOperation(value = "Показать страницу текущего баланса",
            notes = "Перенаправляет на страницу текущего баланса")
    @ApiResponse(code = 200, message = "Страница текущего баланса")
    @GetMapping(value = "/current_balance")
    public String showBalancePage() {
        return Constants.BALANCE_JSP;
    }

    @ApiOperation(value = "Показать страницу расходов по категориям",
            notes = "Перенаправляет на страницу анализа расходов по категориям")
    @ApiResponse(code = 200, message = "Страница расходов по категориям")
    @GetMapping(value = "/summary_expenses_by_categories")
    public String showExpensesByCategoryPage() {
        return Constants.CATEGORY_EXPENSES_JSP;
    }

    @ApiOperation(value = "Показать страницу доходов и расходов",
            notes = "Перенаправляет на страницу сводки доходов и расходов")
    @ApiResponse(code = 200, message = "Страница доходов и расходов")
    @GetMapping(value = "/summary_income_expense")
    public String showSummaryIncomeExpensePage() {
        return Constants.SUMMARY_INCOME_EXPENSE_JSP;
    }
}
