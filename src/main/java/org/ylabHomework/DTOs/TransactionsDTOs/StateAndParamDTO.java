package org.ylabHomework.DTOs.TransactionsDTOs;

import lombok.Data;
/**
 * DTO, использующийся для передачи из контроллера единственного параметра транзакций и дополнительного сообщения.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
public class StateAndParamDTO {
    private String state;
    private String param;
}
