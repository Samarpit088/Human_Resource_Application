package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Countries {

    @Id
    @Column(name = "country_id")
    @NotBlank(message = "Country ID is required")
    @Size(
            max = 4,
            message = "Country ID cannot exceed 4 characters"
    )
    private String countryId;

    @Column(name = "country_name")
    @NotBlank(message = "Country name is required")
    @Size(
            max = 60,
            message = "Country name cannot exceed 60 characters"
    )
    private String countryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @NotNull(message = "Region is required")
    private Region region;
}