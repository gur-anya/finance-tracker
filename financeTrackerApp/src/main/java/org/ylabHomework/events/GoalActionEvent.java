package org.ylabHomework.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ylabHomework.models.Transaction;

@Getter
@AllArgsConstructor
public class GoalActionEvent {
    Transaction transaction;
    Long userId;
}
