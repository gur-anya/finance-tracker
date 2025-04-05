package org.ylabHomework.serviceClasses.SpringConfigs;



import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.ylabHomework.controllers.filters.AuthFilter;
import org.ylabHomework.controllers.filters.EncodingFilter;

/**
 * Основной конфигурационный класс. Настраивает DispatcherServlet для обработки HTTP-запросов, обработку аспектов через аннотацию,
 * отображение jsp-страниц, регистрирует фильтры.
 *
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"org.ylabHomework"})
public class MainWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(@NonNull ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(MainWebAppInitializer.class);
        context.setServletContext(servletContext);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", EncodingFilter.class);
        encodingFilter.addMappingForUrlPatterns(null, false, "/*");

        FilterRegistration.Dynamic authFilter = servletContext.addFilter("authFilter", AuthFilter.class);
        authFilter.addMappingForUrlPatterns(null, false, "/*");
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}