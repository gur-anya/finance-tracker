package org.ylabHomework.serviceClasses.customExceptions;

public class NoGoalException extends IllegalArgumentException {
    public NoGoalException() {
        super("Goal has not been set");
    }
}
