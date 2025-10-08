package com.anr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anr.localmdb.model.InsuranceMember;
import com.anr.localmdb.repository.MemberRepository;

@Component
public class MemberService {

    @Autowired
    private MemberRepository memRepo;

    public InsuranceMember saveOne(InsuranceMember mem) {
        return memRepo.save(mem);
    }

    public void saveBatch(List<InsuranceMember> members) {
        memRepo.saveAll(members);
    }
}
