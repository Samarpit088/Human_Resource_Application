package com.example.Human_Resource_Managment.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regions")
@Getter
@Setter
public class Region {

    @Id
    @Column(name = "region_id")
    @NotNull(message = "Region ID is required")
    private Long regionId;

    @Column(name = "region_name")
    @NotBlank(message = "Region name is required")
    @Size( min=2,
            max = 25,
            message = "Region name cannot exceed 25 characters"
    )
    private String regionName;
}