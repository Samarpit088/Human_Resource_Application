package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "Locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locations {

    @Id
    @Column(name = "location_id")
    private Integer LocationId;

    @Column(name = "street_adress", length = 60)
    private String streetAddress;

    @Column(name = "postal_code", length = 12)
    private String postalCode;

    @Column(name = "city", length = 30, nullable = false)
    private String city;

    @Column(name = "state_province", length = 25)
    private String stateProvince;

    @Column(name = "country_id", length = 4)
    private String countryId;
}