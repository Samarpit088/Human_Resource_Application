package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Locations;
import com.example.Human_Resource_Managment.Projection.LocationDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

/**
 * Repository for Locations with projection support
 * Geographic Hierarchy: regions → countries → locations → departments → employees
 *
 * APIs for 2 Pages:
 * 1. Geographic Hierarchy Page - Browse locations by region/country
 * 2. Organization Structure Page - View locations with department/employee context
 *
 * NO @Query methods - only Spring Data JPA derived queries
 * NO Services - NO Controllers - Only Projections
 *
 * IMPORTANT: Return Page<Locations> (entity), not Page<Projection>
 * Spring Data REST will apply projections via ?projection=locationDetails parameter
 */
@RepositoryRestResource(
        path = "locations",
        excerptProjection = LocationDetailsProjection.class
)
public interface LocationsRepo extends JpaRepository<Locations, Long> {

    // ========================================
    // PAGE 1: GEOGRAPHIC HIERARCHY PAGE
    // Browse locations by geographic structure
    // ========================================

    // Find locations by region ID (regions → locations)
    Page<Locations> findByCountryRegionRegionId(Long regionId, Pageable pageable);

    // Find locations by region name (search)
    Page<Locations> findByCountryRegionRegionNameContainingIgnoreCase(String regionName, Pageable pageable);

    // Find locations by country ID (countries → locations)
    Page<Locations> findByCountryCountryId(String countryId, Pageable pageable);

    // Find locations by country name (search)
    Page<Locations> findByCountryCountryNameContainingIgnoreCase(String countryName, Pageable pageable);

    // Find locations by city (search within locations)
    Page<Locations> findByCityContainingIgnoreCase(String city, Pageable pageable);

    // Find locations by state/province
    Page<Locations> findByStateProvinceContainingIgnoreCase(String stateProvince, Pageable pageable);

    // Combined filters: region + country
    Page<Locations> findByCountryRegionRegionIdAndCountryCountryId(Long regionId, String countryId, Pageable pageable);

    // Combined filters: country + city
    Page<Locations> findByCountryCountryIdAndCityContainingIgnoreCase(String countryId, String city, Pageable pageable);

    // ========================================
    // PAGE 2: ORGANIZATION STRUCTURE PAGE
    // Statistics and counts for org structure
    // ========================================

    // Count locations by region (for dashboard/summary)
    Long countByCountryRegionRegionId(Long regionId);

    // Count locations by country (for dashboard/summary)
    Long countByCountryCountryId(String countryId);

    // Count locations by city
    Long countByCity(String city);

    // Check if location exists
    boolean existsByLocationId(Long locationId);

    // Find locations by exact city and country (for org structure drill-down)
    Page<Locations> findByCityAndCountryCountryId(String city, String countryId, Pageable pageable);

    // Find locations by postal code (for address lookup)
    Optional<Locations> findByPostalCode(String postalCode);

    Page<Locations> findByPostalCodeContaining(String postalCode, Pageable pageable);

    // Find single location by ID (for detail page)
    Optional<Locations> findByLocationId(Long locationId);
}