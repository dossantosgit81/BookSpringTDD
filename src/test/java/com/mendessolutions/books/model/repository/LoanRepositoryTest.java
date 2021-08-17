package com.mendessolutions.books.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
	
	@Autowired
	private LoanRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("deve erificar se existe emprestimo não devolvido para o livro")
	public void existsByBookNotReturnedTest() {
		//Cenario
		Loan loan = createAndPersistLoan();
		
		//Execução
		boolean exists = repository.existsByBookAndNotReturned(loan.getBook());
		
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve buscar emprestimo pelo isbn do livro ou customer")
	public void findByBookIsbnOrCustomer() {
		//cenario
		Loan loan = createAndPersistLoan();
		
		//execução
		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));
		
		//Verificação
		Assertions.assertThat(result.getContent()).hasSize(1);
		Assertions.assertThat(result.getContent()).contains(loan);
		Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
	}
	
	public Loan createAndPersistLoan() {
		Book book = BookRepositoryTest.createNewBook("123");
		entityManager.persist(book);
		
		Loan loan = Loan.builder()
				.book(book)
				.customer("Fulano")
				.loanDate(LocalDate.now())
				.build();
		entityManager.persist(loan);
		
		return loan;
	}
	
}
