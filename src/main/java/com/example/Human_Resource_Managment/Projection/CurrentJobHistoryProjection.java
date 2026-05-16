package com.example.Human_Resource_Managment.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CurrentJobHistoryProjection {
    
    Long getEmployeeId();
    
    String getEmployeeFirstName();
    
    String getEmployeeLastName();
    
    LocalDate getStartDate();
    
    LocalDate getEndDate();
    
    String getJobId();
    
    String getJobTitle();
    
    Long getDepartmentId();
    
    String getDepartmentName();
    
    BigDecimal getSalary();
    
    default String getEmployeeFullName() {
        return getEmployeeFirstName() + " " + getEmployeeLastName();
    }
    
    default Boolean isCurrentlyWorking() {
        return getEndDate() == null;
    }
}
