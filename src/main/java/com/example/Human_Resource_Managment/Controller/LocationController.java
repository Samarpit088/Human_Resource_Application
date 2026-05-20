package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.Entity.Locations;
import com.example.Human_Resource_Managment.Repository.LocationsRepo;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Custom controller for Location operations with server-side search
 */
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    @Autowired
    private LocationsRepo locationsRepo;

    /**
     * GET endpoint to fetch all locations with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Locations> locationsPage = locationsRepo.findAll(pageable);
        
        // Convert to DTOs
        List<Map<String, Object>> locationsList = locationsPage.getContent().stream()
                .map(this::convertLocationToMap)
                .collect(java.util.stream.Collectors.toList());
        
        // Build response
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("locationses", locationsList);
        response.put("_embedded", embedded);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", locationsPage.getSize());
        pageInfo.put("totalElements", locationsPage.getTotalElements());
        pageInfo.put("totalPages", locationsPage.getTotalPages());
        pageInfo.put("number", locationsPage.getNumber());
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint to fetch a single location
     */
    @GetMapping("/{locationId}")
    public ResponseEntity<Map<String, Object>> getLocation(@PathVariable Long locationId) {
        Locations location = locationsRepo.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

        return ResponseEntity.ok(convertLocationToMap(location));
    }

    /**
     * GET endpoint to search locations with pagination
     * Supports searching by city, street address, state/province, or location ID
     * Also supports filtering by country
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchLocations(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String countryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Get all locations
        List<Locations> allLocations = locationsRepo.findAll();
        
        // Apply filters
        java.util.stream.Stream<Locations> stream = allLocations.stream();
        
        // Search filter - matches city, address, or state starting with the search term
        if (query != null && !query.trim().isEmpty()) {
            String searchTerm = query.toLowerCase();
            stream = stream.filter(loc -> 
                (loc.getCity() != null && loc.getCity().toLowerCase().startsWith(searchTerm)) ||
                (loc.getStreetAddress() != null && loc.getStreetAddress().toLowerCase().startsWith(searchTerm)) ||
                (loc.getStateProvince() != null && loc.getStateProvince().toLowerCase().startsWith(searchTerm)) ||
                loc.getLocationId().toString().startsWith(searchTerm)
            );
        }
        
        // Country filter
        if (countryId != null && !countryId.trim().isEmpty()) {
            stream = stream.filter(loc -> 
                loc.getCountry() != null && 
                loc.getCountry().getCountryId().equals(countryId)
            );
        }
        
        // Collect filtered results
        List<Locations> filteredList = stream.collect(java.util.stream.Collectors.toList());
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, filteredList.size());
        List<Locations> pageContent = filteredList.subList(start, end);
        
        // Convert to maps
        List<Map<String, Object>> locationsList = pageContent.stream()
                .map(this::convertLocationToMap)
                .collect(java.util.stream.Collectors.toList());
        
        // Build response
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("locationses", locationsList);
        response.put("_embedded", embedded);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", size);
        pageInfo.put("totalElements", filteredList.size());
        pageInfo.put("totalPages", (int) Math.ceil((double) filteredList.size() / size));
        pageInfo.put("number", page);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Convert Location entity to Map
     */
    private Map<String, Object> convertLocationToMap(Locations location) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("locationId", location.getLocationId());
        map.put("streetAddress", location.getStreetAddress());
        map.put("city", location.getCity());
        map.put("stateProvince", location.getStateProvince());
        map.put("postalCode", location.getPostalCode());
        
        // Add country info
        if (location.getCountry() != null) {
            map.put("countryId", location.getCountry().getCountryId());
            map.put("countryName", location.getCountry().getCountryName());
        } else {
            map.put("countryId", null);
            map.put("countryName", null);
        }
        
        return map;
    }
}
