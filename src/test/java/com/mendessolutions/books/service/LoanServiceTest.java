package com.mendessolutions.books.service;

import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mendessolutions.books.api.dto.LoanFilterDTO;
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

		
		Loan savingLoan = createLoan();
		
		Mockito.when(repository.existsByBookAndNotReturned(createLoan().getBook())).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> loanService.save(savingLoan));
		
		Assertions.assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Book alredy loaned");
		
		Mockito.verify(repository, Mockito.never()).save(savingLoan);
		
	}
	
	@Test
	@DisplayName("Deve obter as informações de um emprestimo pelo ID")
	public void getLoanDetailsTest() {
		//Cenário
		Long id = 1l;
		
		Loan loan = createLoan();
		loan.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
		
		//Execuão
		Optional<Loan> result = loanService.getById(id);
		
		//Verificação
		Assertions.assertThat(result.isPresent()).isTrue();
		Assertions.assertThat(result.get().getId()).isEqualTo(loan.getId());
		Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());
		Assertions.assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
	
		Mockito.verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Deve atualizar um emprestimo")
	public void updateLoanTest() {
		//Cenario
		Long id = 1l;
		
		Loan loan = createLoan();
		loan.setId(id);
		loan.setReturned(true);
		
		Mockito.when(repository.save(loan)).thenReturn(loan);
		
		//Execucão
		Loan updatedLoan = loanService.update(loan);
		
		Assertions.assertThat(updatedLoan.getReturned()).isTrue();
		
		Mockito.verify(repository).save(loan);
		
	}
	
	@Test
	@DisplayName("Deve filtrar emprestimos pelas propriedades")
	public void findLoanTest() {
		//Cenario
		LoanFilterDTO loanFilterDTO = LoanFilterDTO
				.builder()
				.customer("Fulano")
				.isbn("123")
				.build();
		
		Loan loan = createLoan();
		loan.setId(1l);
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Loan> lista = Arrays.asList(loan);
		
		Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
		Mockito.when(repository.findByBookIsbnOrCustomer(
				Mockito.anyString(), 
				Mockito.anyString(),
				Mockito.any(PageRequest.class)))
			.thenReturn(page);
		
		//Execução
		Page<Loan> result = loanService.find(loanFilterDTO, pageRequest);
		
		//Verificações
		Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
		Assertions.assertThat(result.getContent()).isEqualTo(lista);
		Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
	public static Loan createLoan() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		return Loan.builder()
			.book(book)
			.customer(customer)
			.loanDate(LocalDate.now())
			.build();
		
	}
	
}
