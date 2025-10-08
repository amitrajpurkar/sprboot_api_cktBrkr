package com.anr.localmdb.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
//import com.querydsl.core.annotations.QueryEntity;

//@QueryEntity
@Document(collection = "members")
@JsonInclude(Include.NON_NULL)
public class InsuranceMember {

    @Id
    private String id;
    private String partyId;
    private String firstname;
    private String lastname;
    private Date dateOfBirth;
    private List<Policy> policies;

    @Override
    public String toString() {
        return "InsuranceMember [partyId=" + partyId + ", firstname=" + firstname + ", lastname=" + lastname + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public class Policy {
        private String policyID;
        private String hccID;
        private String group;
        private String division;
        private Date policyStartDate;
        private Date policyExpiryDate;
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

    public class Plan {
        private Integer planDefinitionId;
        private String planName;
        private String planNumber;
        private String segment;
        private String planFamily;
        private String coverageType;
        private String status;

        @Override
        public String toString() {
            return "Plan [planDefinitionId=" + planDefinitionId + ", planName=" + planName + ", planNumber="
                    + planNumber + "]";
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

}
