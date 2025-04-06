package org.ylabHomework.DTOs.TransactionsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, использующийся для передачи из контроллера единственного параметра транзакций (например, баланс, цель пользователя).
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleParamDTO {
    private String param;
}
