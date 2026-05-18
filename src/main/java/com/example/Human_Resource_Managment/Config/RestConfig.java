package com.example.Human_Resource_Managment.Config;

import com.example.Human_Resource_Managment.Entity.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Configuration for Spring Data REST
 * Exposes entity IDs in JSON responses
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors
    ) {
        // Expose IDs for all entities
        config.exposeIdsFor(
                Region.class,
                Countries.class,
                Locations.class,
                Department.class,
                Job.class,
                Employees.class,
                JobHistory.class
        );
    }
}