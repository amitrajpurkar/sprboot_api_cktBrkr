package com.anr.localmdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "plans")
@JsonInclude(Include.NON_NULL)
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "plan_definition_id")
    private Integer planDefinitionId;
    
    @Column(name = "plan_name", length = 100)
    private String planName;
    
    @Column(name = "plan_number", length = 50)
    private String planNumber;
    
    @Column(name = "segment", length = 100)
    private String segment;
    
    @Column(name = "plan_family", length = 100)
    private String planFamily;
    
    @Column(name = "coverage_type", length = 50)
    private String coverageType;
    
    @Column(name = "status", length = 50)
    private String status;

    @Override
    public String toString() {
        return "Plan [planDefinitionId=" + planDefinitionId + ", planName=" + planName + ", planNumber="
                + planNumber + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlanDefinitionId() {
        return planDefinitionId;
    }

    public void setPlanDefinitionId(Integer planDefinitionId) {
        this.planDefinitionId = planDefinitionId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanNumber() {
        return planNumber;
    }

    public void setPlanNumber(String planNumber) {
        this.planNumber = planNumber;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getPlanFamily() {
        return planFamily;
    }

    public void setPlanFamily(String planFamily) {
        this.planFamily = planFamily;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
