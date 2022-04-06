package com.example.balanceinquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.balanceinquiry.model.Account;


public interface AccountRepository extends JpaRepository<Account,Long> {

}
