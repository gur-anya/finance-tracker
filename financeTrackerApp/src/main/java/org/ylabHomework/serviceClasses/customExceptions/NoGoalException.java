package org.ylabHomework.serviceClasses.customExceptions;

public class NoGoalException extends IllegalArgumentException {
    public NoGoalException() {
        super("Вы еще не установили финансовую цель! Установить цель можно в разделе статистики.");
    }
}
