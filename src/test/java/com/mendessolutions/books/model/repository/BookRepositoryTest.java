package com.mendessolutions.books.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mendessolutions.books.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
	public void returnTrueWhenIsbnExists() {
		//cenario
		String isbn = "123";
		Book book = createNewBook(isbn);
				
		entityManager.persist(book);
		
		//execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		//verificacao
		Assertions.assertThat(exists).isTrue();
	}

	private Book createNewBook(String isbn) {
		return Book.builder()
				.title("Aventuras")
				.author("Fulano")
				.isbn(isbn)
				.build();
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com isbn informado")
	public void returnFalseWhenIsbnDoensExists() {
		//cenario
		String isbn = "123";
		
		//execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		//verificacao
		Assertions.assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Deve obter um livro por id")
	public void findByTest() {
		//Cenario
		Book book = createNewBook("123");
		entityManager.persist(book);
		
		//Execução
		Optional<Book> foundBook = repository.findById(book.getId());
		
		//Verificação
		Assertions.assertThat(foundBook.isPresent()).isTrue();
	}
	
	

}
