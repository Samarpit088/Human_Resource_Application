package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Id;

public class Jobs {
    @Id
    long job_id;

    String job_title;

    long min_salary;

    long max_salary;
}
