package com.inbank.finance.loanengine.repository;

import com.inbank.finance.loanengine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @Override
    Optional<User> findById(String s);
}
