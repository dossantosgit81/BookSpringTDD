package com.mendessolutions.books.api.resource;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendessolutions.books.api.dto.BookDTO;
import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
	
	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		BookDTO dto = createNewBook();
		Book savedBook = Book.builder()
				.id(10l)
				.author("Artur")
				.title("As aventuras")
				.isbn("001").build();
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
			.andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
	}

	private BookDTO createNewBook() {
		return BookDTO.builder()
				.author("Artur")
				.title("As aventuras")
				.isbn("001")
					.build();
	}
	
	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro")
	public void createInvalidBookTest() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("errors",  Matchers.hasSize(3)));
			
	}
	
	@Test
	@DisplayName("Deve obter informacoes de um livro")
	public void getBookDetailsTest() throws Exception{
		//cenario (given)
		Long id = 1l;
		Book book = Book.builder()
				.id(id)
				.title(createNewBook().getTitle())
				.author(createNewBook().getAuthor())
				.isbn(createNewBook().getIsbn())
				.build();
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		//execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+id))
		.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
			.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
			.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
			
		
	}
	
	@Test
	@DisplayName("resource not found quando o livro não existir")
	public void bookNotFOundTest() throws Exception {
	
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar um notfound quando não encontrar um livro para deletar")
	public void deleteInexistenteBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() throws Exception {
		Long id = 1l;
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		
		Book updatingBook = Book.builder().title("some title").author("some author").isbn("321").build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));
		Book updateBook = Book.builder()
				.id(id)
				.author("Artur")
				.title("As aventuras")
				.isbn("321")
				.build();
		
		BDDMockito.given(service.update(updatingBook)).willReturn(updateBook);
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+1))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
			.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
			.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));
	}
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar atualizar um livro")
	public void updateInexistentBookTest() throws Exception{
	
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		
		//Book updatingBook = Book.builder().title("some title").author("some author").isbn("321").build();
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+1))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
			
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
	public void createBookWithDuplicateIsbn() throws Exception {
		BookDTO bookDto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(bookDto);
		String mensagemErro = "Isbn já cadastrado";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro));
		
	}

}
