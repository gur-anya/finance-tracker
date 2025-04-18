package TransactionControllersTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ylabHomework.Main;
import org.ylabHomework.controllers.financeControllers.ForwardingController;
import org.ylabHomework.serviceClasses.Constants;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class ForwardingControllerTests {

    private final ForwardingController controller = new ForwardingController();
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    @DisplayName("GET-запрос к /main_transaction_page")
    void testShowFinanceManagement() throws Exception {
        mockMvc.perform(get("/main_transaction_page"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.TRANSACTIONS_MAIN_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /transactions_management_page")
    void testShowFinanceMainPage() throws Exception {
        mockMvc.perform(get("/transactions_management_page"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.TRANSACTIONS_MANAGEMENT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /general_stats_page")
    void testShowFinanceStats() throws Exception {
        mockMvc.perform(get("/general_stats_page"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_STATS_PAGE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /create_transaction")
    void testShowCreateTransactionPage() throws Exception {
        mockMvc.perform(get("/create_transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.CREATE_TRANSACTION_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /delete_transaction")
    void testShowDeleteTransactionPage() throws Exception {
        mockMvc.perform(get("/delete_transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.DELETE_TRANSACTION_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /show_transactions")
    void testShowTransactionsFilteringPage() throws Exception {
        mockMvc.perform(get("/show_transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_TRANSACTIONS_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /update_transaction")
    void testShowUpdateTransactionPage() throws Exception {
        mockMvc.perform(get("/update_transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.UPDATE_TRANSACTION_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /general_report")
    void testShowGeneralReportPage() throws Exception {
        mockMvc.perform(get("/general_report"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.GENERAL_REPORT_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /check_goal")
    void testShowCheckGoalPage() throws Exception {
        mockMvc.perform(get("/check_goal"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_CHECK_GOAL_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /goal_management")
    void testShowGoalManagementPage() throws Exception {
        mockMvc.perform(get("/goal_management"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_GOAL_MANAGEMENT_PAGE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /update_goal")
    void testShowUpdateGoalPage() throws Exception {
        mockMvc.perform(get("/update_goal"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.UPDATE_GOAL_PAGE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /monthly_budget_management")
    void testShowBudgetManagementPage() throws Exception {
        mockMvc.perform(get("/monthly_budget_management"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_BUDGET_MANAGEMENT_PAGE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /check_budget")
    void testShowCheckBudgetPage() throws Exception {
        mockMvc.perform(get("/check_budget"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SHOW_CHECK_BUDGET_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /update_budget")
    void testShowUpdateBudgetPage() throws Exception {
        mockMvc.perform(get("/update_budget"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.UPDATE_BUDGET_PAGE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /current_balance")
    void testShowBalancePage() throws Exception {
        mockMvc.perform(get("/current_balance"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.BALANCE_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /summary_expenses_by_categories")
    void testShowExpensesByCategoryPage() throws Exception {
        mockMvc.perform(get("/summary_expenses_by_categories"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.CATEGORY_EXPENSES_JSP));
    }

    @Test
    @DisplayName("GET-запрос к /summary_income_expense")
    void testShowSummaryIncomeExpensePage() throws Exception {
        mockMvc.perform(get("/summary_income_expense"))
                .andExpect(status().isOk())
                .andExpect(view().name(Constants.SUMMARY_INCOME_EXPENSE_JSP));
    }
}