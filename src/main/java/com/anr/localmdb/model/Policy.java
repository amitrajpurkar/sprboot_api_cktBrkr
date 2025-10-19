package com.anr.localmdb.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "policies")
@JsonInclude(Include.NON_NULL)
public class Policy {
    
    @Id
    @Column(name = "policy_id", nullable = false, length = 50)
    private String policyID;
    
    @Column(name = "hcc_id", length = 50)
    private String hccID;
    
    @Column(name = "group_name", length = 50)
    private String group;
    
    @Column(name = "division", length = 50)
    private String division;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "policy_start_date")
    private Date policyStartDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "policy_expiry_date")
    private Date policyExpiryDate;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Plan> plans;

    @Override
    public String toString() {
        return "Policy [hccID=" + hccID + "]";
    }

    public String getPolicyID() {
        return policyID;
    }

    public void setPolicyID(String policyID) {
        this.policyID = policyID;
    }

    public String getHccID() {
        return hccID;
    }

    public void setHccID(String hccID) {
        this.hccID = hccID;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public Date getPolicyStartDate() {
        return policyStartDate;
    }

    public void setPolicyStartDate(Date policyStartDate) {
        this.policyStartDate = policyStartDate;
    }

    public Date getPolicyExpiryDate() {
        return policyExpiryDate;
    }

    public void setPolicyExpiryDate(Date policyExpiryDate) {
        this.policyExpiryDate = policyExpiryDate;
    }
}
