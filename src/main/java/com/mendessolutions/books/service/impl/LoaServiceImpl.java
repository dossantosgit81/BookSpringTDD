package com.mendessolutions.books.service.impl;

import java.util.Optional;

import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.model.repository.LoanRepository;
import com.mendessolutions.books.service.LoanService;

public class LoaServiceImpl implements LoanService {
	
	private LoanRepository repository;
	
	public LoaServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("Book alredy loaned");
		}
		
		return repository.save(loan);
	}

	@Override
	public Optional<Loan> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Loan update(Loan loan) {
		// TODO Auto-generated method stub
		return null;
	}

}
