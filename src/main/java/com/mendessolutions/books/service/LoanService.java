package com.mendessolutions.books.service;

import java.util.Optional;

import com.mendessolutions.books.model.entity.Loan;

public interface LoanService {

	Loan save (Loan loan);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);
	
}
