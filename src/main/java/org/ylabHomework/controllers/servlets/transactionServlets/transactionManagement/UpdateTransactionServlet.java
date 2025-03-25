package org.ylabHomework.controllers.servlets.transactionServlets.transactionManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.ActionsWithTransactionDTO;
import org.ylabHomework.mappers.ActionsWithTransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.services.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.UPDATE_TRANSACTION_JSP;

@WebServlet(name = "UpdateTransactionServlet", urlPatterns = "/update_transaction")
public class UpdateTransactionServlet extends HttpServlet {
    TransactionService transactionService;
    ActionsWithTransactionMapper actionsMapper;

    @Override
    public void init() {
        this.actionsMapper = Mappers.getMapper(ActionsWithTransactionMapper.class);
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionService = new TransactionService(transRepo, user);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(UPDATE_TRANSACTION_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ActionsWithTransactionDTO transactionDTO = (ActionsWithTransactionDTO) req.getAttribute("DTO");
        req.removeAttribute("DTO");
        Transaction updatedTransaction = new Transaction(transactionDTO.getOriginalType(),
                transactionDTO.getOriginalSum(), transactionDTO.getOriginalCategory().toLowerCase().trim(),
                transactionDTO.getDescription().trim());
        updatedTransaction.setTimestamp(transactionDTO.getOriginalTimestamp());

        String updatedValues = transactionDTO.getUpdatedValues();
        if (updatedValues.contains("type")) {
            this.transactionService.updateTransactionType(transactionDTO.getType(), updatedTransaction);
        }
        if (updatedValues.contains("sum")) {
            this.transactionService.updateTransactionSum(String.valueOf(transactionDTO.getSum()), updatedTransaction);
        }
        if (updatedValues.contains("category")) {
            this.transactionService.updateTransactionCategory(transactionDTO.getCategory(), updatedTransaction);
        }
        if (updatedValues.contains("description")) {
            this.transactionService.updateTransactionDescription(transactionDTO.getDescription(), updatedTransaction);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(parseJsonResponse(resp));

    }

    @Override
    public void destroy() {
        this.transactionService = null;
        this.actionsMapper = null;
    }

    private String parseJsonResponse(HttpServletResponse resp) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при удалении транзакции! Попробуйте еще раз.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }
}