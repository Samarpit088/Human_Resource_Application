package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    private Long locationId;

    @Column(name = "street_address", length = 40)
    @Size(max = 40, message = "Street address cannot exceed 40 characters")
    private String streetAddress;

    @Column(name = "postal_code", length = 12)
    @Size(max = 12, message = "Postal code cannot exceed 12 characters")
    private String postalCode;

    @Column(name = "city", nullable = false, length = 30)
    @NotBlank(message = "City cannot be blank")
    @Size(max = 30, message = "City cannot exceed 30 characters")
    private String city;

    @Column(name = "state_province", length = 25)
    @Size(max = 25, message = "State province cannot exceed 25 characters")
    private String stateProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Countries country;
}