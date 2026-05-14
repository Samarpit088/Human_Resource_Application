package com.example.Human_Resource_Managment.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "regions")
@Data
public class Region {

    @Id
    @Column(name = "region_id")
    private int regionId;

    @Column(name = "region_name")
    private String regionName;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Countries> countries;
}