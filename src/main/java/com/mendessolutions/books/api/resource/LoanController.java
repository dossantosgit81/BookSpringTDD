package com.mendessolutions.books.api.resource;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mendessolutions.books.api.dto.LoanDTO;
import com.mendessolutions.books.api.dto.ReturnedLoanDTO;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.service.BookService;
import com.mendessolutions.books.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
	
	private final LoanService loanService;
	private final BookService bookService;
	
	@Autowired
	public LoanController(LoanService loanService, BookService bookService) {
		this.loanService = loanService;
		this.bookService = bookService;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
				
		Loan entity = Loan.builder()
				.book(book)
				.customer(dto.getCustomer())
				.loanDate(LocalDate.now())
				.build();
		entity = loanService.save(entity);
		
		 return entity.getId();
	}
	
	@PatchMapping("{id}")
	public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
		Loan loan = loanService.getById(id).get();
		loan.setReturned(dto.getReturned());
		loanService.update(loan);
	}
	
}
