package com.mendessolutions.books.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mendessolutions.books.api.dto.LoanFilterDTO;
import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.model.repository.LoanRepository;
import com.mendessolutions.books.service.LoanService;

@Service
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
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
		// TODO Auto-generated method stub
		return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		
		return repository.findByBook(book, pageable);		
	}

	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanDays = 4;
		LocalDate threDaysAgo = LocalDate.now().minusDays(loanDays);
		
		return repository.findByLoanDateLessThanAndNotReturned(threDaysAgo);
	}

}
