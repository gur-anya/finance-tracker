package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO, использующийся для получения статуса по балансу или цели. Содержит поле value для передачи
 * заданного баланса/цели и поле status для передачи результата по балансу/цели.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchStatusDTO {
    double value;
    double status;
}
