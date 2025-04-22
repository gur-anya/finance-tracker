package org.ylabHomework.controllers.financeControllers.financeStatsControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.models.User;
import org.ylabHomework.services.UserService;

@Controller
@RequiredArgsConstructor
public class ForwardingController {
    private final UserService userService;


    @GetMapping(value = "/")
    public String showMain(){
        return "index";
    }
    @GetMapping(value = "/signup")
    public String showSignup() {
        return "userHtmls/signup";
    }

    @GetMapping(value = "/login")
    public String showLogin() {
        return "userHtmls/login";
    }


    @GetMapping("/home")
    public String showHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        String username = "";
        if (user != null) {
            username = user.getName();
        }
        model.addAttribute("username", username);
        return "userHtmls/home";
    }

    @GetMapping(value = "/profile")
    public String showAccount(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        String username = "";
        if (user != null) {
            username = user.getName();
        }
        model.addAttribute("username", username);
        return "userHtmls/profile";
    }

    @GetMapping(value = "/profile/edit")
    public String showUpdateAccount(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        String username = "";
        String useremail = "";
        if (user != null) {
            username = user.getName();
            useremail = authentication.getName();
        }
        model.addAttribute("username", username);
        model.addAttribute("useremail", useremail);
        return "userHtmls/profileEdit";
    }

    @GetMapping(value = "/profile/delete")
    public String showDeleteAccount() {
        return "userHtmls/profileDelete";
    }


    @GetMapping(value = "/transactions/home")
    public String showTransactionsHome() {
        return "transactionHtmls/transactionsHome";
    }

    @GetMapping(value = "/transactions/manage")
    public String showTransactionsManagement(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        String username = "";
        if (user != null) {
            username = user.getName();
        }
        model.addAttribute("username", username);
        return "transactionHtmls/transactionsManage";
    }


    @GetMapping("/transactions/manage/all")
    public String getTransactions() {
        return "transactionHtmls/transactionsAll";
    }

    @GetMapping("/transactions/manage/new")
    public String showCreateTransaction() {
        return "transactionHtmls/transactionsNew";
    }

    @GetMapping("/transactions/manage/edit")
    public String showUpdateTransaction() {
        return "transactionHtmls/transactionsEdit";
    }

    @GetMapping("/transactions/manage/delete")
    public String showDeleteTransaction() {
        return "transactionHtmls/transactionsDelete";
    }


    @GetMapping(value = "transactions/stats/home")
    public String showStatsHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        String username = "";
        if (user != null) {
            username = user.getName();
        }
        model.addAttribute("username", username);
        return "transactionStatsHtmls/statsHome";
    }

    @GetMapping(value = "transactions/stats/report")
    public String showGeneralReport() {
        return "transactionStatsHtmls/statsReport";
    }

    @GetMapping(value = "transactions/stats/balance")
    public String showBalance() {
        return "transactionStatsHtmls/statsBalance";
    }


    @GetMapping(value = "transactions/stats/categoryExpenses")
    public String showExpensesByCategory() {
        return "transactionStatsHtmls/statsCategoryExpenses";
    }

    @GetMapping(value = "transactions/stats/incomeExpenseSummary")
    public String showSummaryIncomeExpense() {
        return "transactionStatsHtmls/statsIncomeExpenseSummary";
    }


    @GetMapping(value = "transactions/stats/goal")
    public String showGoalMenu() {
        return "transactionStatsHtmls/statsGetGoal";
    }


    @GetMapping(value = "transactions/stats/goal/status")
    public String showGoalStatus() {
        return "transactionStatsHtmls/statsGoalStatus";
    }


    @GetMapping(value = "transactions/stats/goal/update")
    public String showUpdateGoal() {
        return "transactionStatsHtmls/statsUpdateGoal";
    }


    @GetMapping(value = "transactions/stats/monthlyBudget")
    public String showBudgetMenu() {
        return "transactionStatsHtmls/statsGetMonthlyBudget";
    }


    @GetMapping(value = "transactions/stats/monthlyBudget/status")
    public String showBudgetStatus() {
        return "transactionStatsHtmls/statsMonthlyBudgetStatus";
    }

    @GetMapping(value = "transactions/stats/monthlyBudget/update")
    public String showUpdateBudget() {
        return "transactionStatsHtmls/statsUpdateMonthlyBudget";
    }
}
