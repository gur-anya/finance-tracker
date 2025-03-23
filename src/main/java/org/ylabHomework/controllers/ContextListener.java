package org.ylabHomework.controllers;


import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.Config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    UserRepository userRepository;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        this.userRepository = new UserRepository();

        Config.init();

        servletContext.setAttribute("userRepository", this.userRepository);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.userRepository = null;
    }
}