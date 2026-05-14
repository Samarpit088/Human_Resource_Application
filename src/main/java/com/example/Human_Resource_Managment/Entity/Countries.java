package com.example.Human_Resource_Managment.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Countries {
    @Id
    String country_Id;
    String country_Name;
}
