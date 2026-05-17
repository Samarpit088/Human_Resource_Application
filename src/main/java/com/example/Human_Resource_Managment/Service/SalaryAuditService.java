package com.example.Human_Resource_Managment.Service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to track salary changes using Redis cache
 * This is a workaround to store historical salary information without modifying database schema
 */
@Service
@Slf4j
public class SalaryAuditService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SALARY_AUDIT_KEY_PREFIX = "salary:audit:";

    public SalaryAuditService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Record a salary change
     */
    public void recordSalaryChange(Long employeeId, BigDecimal oldSalary, BigDecimal newSalary, LocalDate effectiveDate, String changeReason) {
        SalaryAuditRecord record = new SalaryAuditRecord(
                employeeId,
                oldSalary,
                newSalary,
                effectiveDate,
                LocalDateTime.now(),
                changeReason
        );

        String key = SALARY_AUDIT_KEY_PREFIX + employeeId;
        
        // Get existing records
        List<SalaryAuditRecord> records = getSalaryHistory(employeeId);
        
        // If this is the first record, also store the initial salary
        if (records.isEmpty()) {
            storeInitialSalary(employeeId, oldSalary);
        }
        
        records.add(record);
        
        // Store back to Redis
        redisTemplate.opsForValue().set(key, records);
        
        log.info("Salary change recorded in Redis for employee {}: {} -> {} (effective: {})", 
                employeeId, oldSalary, newSalary, effectiveDate);
    }
    
    /**
     * Store initial salary at joining
     */
    public void storeInitialSalary(Long employeeId, BigDecimal initialSalary) {
        String key = "salary:initial:" + employeeId;
        // Only store if not already present
        if (redisTemplate.opsForValue().get(key) == null) {
            redisTemplate.opsForValue().set(key, initialSalary);
            log.info("Initial salary stored in Redis for employee {}: {}", employeeId, initialSalary);
        }
    }
    
    /**
     * Get initial salary at joining
     */
    public Optional<BigDecimal> getInitialSalary(Long employeeId) {
        String key = "salary:initial:" + employeeId;
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value != null) {
            try {
                return Optional.of(new BigDecimal(value.toString()));
            } catch (Exception e) {
                log.error("Error parsing initial salary for employee {}: {}", employeeId, e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * Get all salary changes for an employee
     */
    @SuppressWarnings("unchecked")
    public List<SalaryAuditRecord> getSalaryHistory(Long employeeId) {
        String key = SALARY_AUDIT_KEY_PREFIX + employeeId;
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value == null) {
            return new ArrayList<>();
        }
        
        if (value instanceof List) {
            return ((List<?>) value).stream()
                    .filter(item -> item instanceof SalaryAuditRecord || item instanceof Map)
                    .map(item -> {
                        if (item instanceof SalaryAuditRecord) {
                            return (SalaryAuditRecord) item;
                        } else if (item instanceof Map) {
                            // Convert Map to SalaryAuditRecord (for deserialization)
                            Map<?, ?> map = (Map<?, ?>) item;
                            return mapToRecord(map);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(SalaryAuditRecord::getEffectiveDate).reversed())
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    /**
     * Convert Map to SalaryAuditRecord (helper for Redis deserialization)
     */
    private SalaryAuditRecord mapToRecord(Map<?, ?> map) {
        try {
            Long employeeId = ((Number) map.get("employeeId")).longValue();
            BigDecimal oldSalary = new BigDecimal(map.get("oldSalary").toString());
            BigDecimal newSalary = new BigDecimal(map.get("newSalary").toString());
            LocalDate effectiveDate = LocalDate.parse(map.get("effectiveDate").toString());
            LocalDateTime recordedAt = LocalDateTime.parse(map.get("recordedAt").toString());
            String changeReason = map.get("changeReason").toString();
            
            return new SalaryAuditRecord(employeeId, oldSalary, newSalary, effectiveDate, recordedAt, changeReason);
        } catch (Exception e) {
            log.error("Error converting map to SalaryAuditRecord: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get salary at a specific date
     */
    public Optional<BigDecimal> getSalaryAtDate(Long employeeId, LocalDate date) {
        return getSalaryHistory(employeeId).stream()
                .filter(record -> !record.getEffectiveDate().isAfter(date))
                .max(Comparator.comparing(SalaryAuditRecord::getEffectiveDate))
                .map(SalaryAuditRecord::getNewSalary);
    }

    /**
     * Inner class to represent a salary audit record
     */
    public static class SalaryAuditRecord {
        private final Long employeeId;
        private final BigDecimal oldSalary;
        private final BigDecimal newSalary;
        private final LocalDate effectiveDate;
        private final LocalDateTime recordedAt;
        private final String changeReason;

        @JsonCreator
        public SalaryAuditRecord(
                @JsonProperty("employeeId") Long employeeId,
                @JsonProperty("oldSalary") BigDecimal oldSalary,
                @JsonProperty("newSalary") BigDecimal newSalary,
                @JsonProperty("effectiveDate") LocalDate effectiveDate,
                @JsonProperty("recordedAt") LocalDateTime recordedAt,
                @JsonProperty("changeReason") String changeReason) {
            this.employeeId = employeeId;
            this.oldSalary = oldSalary;
            this.newSalary = newSalary;
            this.effectiveDate = effectiveDate;
            this.recordedAt = recordedAt;
            this.changeReason = changeReason;
        }

        public Long getEmployeeId() { return employeeId; }
        public BigDecimal getOldSalary() { return oldSalary; }
        public BigDecimal getNewSalary() { return newSalary; }
        public LocalDate getEffectiveDate() { return effectiveDate; }
        public LocalDateTime getRecordedAt() { return recordedAt; }
        public String getChangeReason() { return changeReason; }
        
        public BigDecimal getSalaryIncrease() {
            return newSalary.subtract(oldSalary);
        }
        
        public double getPercentageIncrease() {
            if (oldSalary.compareTo(BigDecimal.ZERO) == 0) return 0;
            return newSalary.subtract(oldSalary)
                    .divide(oldSalary, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
    }
}
