package com.example.momentory.domain.auth.repository;

import com.example.momentory.domain.auth.entity.UserTerms;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTermsRepository extends JpaRepository<UserTerms, Long> {
    List<UserTerms> findByUser(User user);
}
