package org.ylabHomework;

import org.ylabHomework.controllers.UserController;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;


public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);


        userController.showGreetingScreen();
    }
}