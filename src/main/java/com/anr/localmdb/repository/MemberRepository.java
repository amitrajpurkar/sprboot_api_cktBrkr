package com.anr.localmdb.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.anr.localmdb.model.InsuranceMember;

@Repository
public interface MemberRepository extends JpaRepository<InsuranceMember, String> {

    // JPA method name query - finds members by firstname
    List<InsuranceMember> findMembersByFirstname(String name);

    // JPA method name query - finds members by lastname
    List<InsuranceMember> findMembersByLastname(String name);

    // JPQL query - finds members within birthday range
    @Query("SELECT m FROM InsuranceMember m WHERE m.dateOfBirth BETWEEN :fromDate AND :toDate")
    List<InsuranceMember> findMembersWithinBirthdayRange(Date fromDate, Date toDate);
}
