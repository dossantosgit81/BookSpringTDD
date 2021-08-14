package com.mendessolutions.books.service;

import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.model.repository.LoanRepository;
import com.mendessolutions.books.service.impl.LoaServiceImpl;

@ExtendWith(SpringExtension.class)
public class LoanServiceTest {
	
	private LoanService loanService;
	
	@MockBean
	private LoanRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.loanService = new LoaServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um emprestimo ")
	public void saveLoanTest() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder()
			.book(Book.builder().id(1l).build())
			.customer(customer)
			.loanDate(LocalDate.now())
			.build();
		
		Loan savedLoan = Loan.builder()
				.id(1l)
				.book(book)
				.loanDate(LocalDate.now())
				.customer(customer)
				.build();
		
		Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
		
		Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);
		
		Loan loan = loanService.save(savingLoan);
		
		Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
		Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
		
	}
	
	@Test
	@DisplayName("Deve lançar erro de negocio ao salvar um emprestimo com livro já emprestado ")
	public void loanedBookSaveTest() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder()
			.book(Book.builder().id(1l).build())
			.customer(customer)
			.loanDate(LocalDate.now())
			.build();
		
		Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> loanService.save(savingLoan));
		
		Assertions.assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Book alredy loaned");
		
		Mockito.verify(repository, Mockito.never()).save(savingLoan);
		
	}
	
}
