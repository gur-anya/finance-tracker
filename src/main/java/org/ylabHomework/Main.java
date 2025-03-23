package org.ylabHomework;

import org.ylabHomework.controllers.UserController;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.Config;
import org.ylabHomework.services.UserService;


public class Main {
    public static void main(String[] args) {
        Config.init();


       UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);


        userController.showGreetingScreen();
    }
}