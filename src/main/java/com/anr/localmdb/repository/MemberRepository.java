package com.anr.localmdb.repository;

import java.util.List;

import org.bson.BsonDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Component;

import com.anr.localmdb.model.InsuranceMember;

@Component
public interface MemberRepository extends MongoRepository<InsuranceMember, String> {
    // can additionally extend , QuerydslPredicateExecutor<InsuranceMember>

    @Query("{'firstname': ?0 }")
    List<InsuranceMember> findMembersByFirstname(String name);

    @Query("{'lastname': ?0 }")
    List<InsuranceMember> findMembersByLastname(String name);

    @Query("{'dob': { $gte: ?0, $lte: ?1} }")
    List<InsuranceMember> findMembersWithinBirthdayRange(BsonDateTime fromDate, BsonDateTime toDate);
}
