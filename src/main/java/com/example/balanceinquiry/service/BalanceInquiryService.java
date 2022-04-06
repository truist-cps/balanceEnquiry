package com.example.balanceinquiry.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.balanceinquiry.model.Account;
import com.example.balanceinquiry.model.InsufficientAccountBalance;
import com.example.balanceinquiry.model.Model;
import com.example.balanceinquiry.repository.AccountRepository;

@Service
@Transactional
public class BalanceInquiryService {

	@Autowired
    private final AccountRepository accountRepository;

    public BalanceInquiryService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

	public boolean doInquiry(Model withdrawalModel) throws InsufficientAccountBalance {
		boolean sufficient_amt=false;
		
		Optional<Account> optionalBankAccount = accountRepository.findById(withdrawalModel.getSourceAccountNumber());
        Account account = optionalBankAccount.get();
        
        if(account.getBalance()>=withdrawalModel.getAmount()) {
        	sufficient_amt=true;
        }else {
        	throw new InsufficientAccountBalance(": "+ withdrawalModel.getSourceAccountNumber());
        }
        
		return sufficient_amt;
	}
}
