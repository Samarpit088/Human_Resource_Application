package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Id;
import java.util.Date;

public class JobHistory {
    @Id
    long employee_id;

    @Id
    Date start_date;

    Date end_date;

    String job_id;

    long department_id;
}
