package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Job;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "jobList", types = Job.class)
public interface JobListProjection {
    String getJobId();

    String getJobTitle();

    BigDecimal getMinSalary();

    BigDecimal getMaxSalary();
}