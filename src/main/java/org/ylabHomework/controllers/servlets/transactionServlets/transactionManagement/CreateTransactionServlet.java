package org.ylabHomework.controllers.servlets.transactionServlets.transactionManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.mappers.TransactionMapper;
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

import static org.ylabHomework.serviceClasses.Constants.CREATE_TRANSACTION_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может создать транзакцию.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "CreateTransactionServlet", urlPatterns = "/create_transaction")
public class CreateTransactionServlet extends HttpServlet {

    private TransactionService transactionService;
    private TransactionMapper transactionMapper;

    @Override
    public void init() throws ServletException {
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionService = new TransactionService(transRepo, user);
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(CREATE_TRANSACTION_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BasicTransactionDTO transactionDTO = (BasicTransactionDTO) req.getAttribute("DTO");
        req.removeAttribute("DTO");
        Transaction transaction = this.transactionMapper.toModel(transactionDTO);
        this.transactionService.createTransaction(transaction.getType(), String.valueOf(transaction.getSum()), transaction.getCategory(), transaction.getDescription());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(parseJsonResponse(resp));

    }

    @Override
    public void destroy() {
        this.transactionService = null;
        this.transactionMapper = null;
    }

    private String parseJsonResponse(HttpServletResponse resp) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при создании транзакции! Попробуйте еще раз.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }


}
