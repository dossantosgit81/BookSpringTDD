package com.mendessolutions.books.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.repository.BookRepository;
import com.mendessolutions.books.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	@MockBean
	BookRepository repository;

	BookService service;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {

		// cenario
		Book book = createdValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(11l).isbn("123").author("Fulano").title("As aventuras").build());

		// Execução
		Book savedBook = service.save(book);

		// verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");

	}

	private Book createdValidBook() {
		return Book.builder().author("Fulano").isbn("123").title("As aventuras").build();
	}

	@Test
	@DisplayName("Deve obter um livro por id")
	public void getByIdTest() {
		Long id = 1l;
		Book book = createdValidBook();
		book.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

		// Execução
		Optional<Book> foundBook = service.getById(id);

		// Verificações
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

	}

	@Test
	@DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existe no banco")
	public void BookNotFoundByIdTest() {
		Long id = 1l;

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execução
		Optional<Book> book = service.getById(id);

		// Verificações
		assertThat(book.isPresent()).isFalse();

	}

	@Test
	@DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicate() {
		// cenario
		Book book = createdValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		// execuçao
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		// verificações
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");

		Mockito.verify(repository, Mockito.never()).save(book);
	}

	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		// Cenario
		Book book = Book.builder().id(1l).build();

		// Execução
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

		// Verificações
		Mockito.verify(repository, Mockito.times(1)).delete(book);

	}

	@Test
	@DisplayName("Deve ocorrer ao tentar deletar um livro inexistente")
	public void deleteInvalidBookTest() {
		// Cenario
		Book book = new Book();

		// Execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

		// verificação
		Mockito.verify(repository, Mockito.never()).delete(book);
	}

	@Test
	@DisplayName("Deve deletar um livro")
	public void updateInvalidBookTest() {
		// Cenario
		Book book = new Book();

		// Execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

		// verificação
		Mockito.verify(repository, Mockito.never()).save(book);

	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() {
		//Cenario
		long id = 1l;
		
		//Livro a atualizar
		Book updatingBook = Book.builder().id(id).build();
		
		//Simulação de livro atualizado
		Book updatedBook = createdValidBook();
		updatedBook.setId(id);
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		//Execução
		Book book = service.update(updatedBook);
		
		//Verificações
		Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
		Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
		Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
		Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
		
		
		
	}

}
