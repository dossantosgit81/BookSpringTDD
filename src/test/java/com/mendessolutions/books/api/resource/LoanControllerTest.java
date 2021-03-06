package com.mendessolutions.books.api.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendessolutions.books.api.dto.LoanDTO;
import com.mendessolutions.books.api.dto.LoanFilterDTO;
import com.mendessolutions.books.api.dto.ReturnedLoanDTO;
import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.service.BookService;
import com.mendessolutions.books.service.LoanService;
import com.mendessolutions.books.service.LoanServiceTest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
	
	static final String LOAN_API = "/api/loans";
	
	@MockBean
	private BookService bookService;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	LoanService loanService;
	
	@Test
	@DisplayName("Deve realizar um emprestimo")
	public void createLoanTest() throws Exception {
		LoanDTO dto = LoanDTO.builder().email("customer@email.com").isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
		
		Loan loan = Loan.builder().id(1l).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(LOAN_API)
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
		.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.content().string("1"));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado")
	public void invalidIsbnCreateLoanTest() throws Exception{
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
	
		BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
		.willThrow(new BusinessException("Book alredy loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
				
				mvc.perform(request)
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
					.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book alredy loaned"));
					
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
	public void loanedBookErrorOnCreateIsbnCreateLoanTest() throws Exception{
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());
	
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
				
				mvc.perform(request)
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
					.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"));
					
	}
	
	@Test
	@DisplayName("Deve retornar um livro")
	public void returnedBookTest() throws Exception{
		//Cenario returned true
		Loan loan = Loan.builder().id(1l).build();
		ReturnedLoanDTO loanDto = ReturnedLoanDTO.builder().returned(true).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
		.willReturn(Optional.of(loan));
		
		String json = new ObjectMapper().writeValueAsString(loanDto);
		
		mvc.perform(
				patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		).andExpect(MockMvcResultMatchers.status().isOk());
		Mockito.verify(loanService, Mockito.times(1)).update(loan);
		
	}
	
	@Test
	@DisplayName("Deve retornar 404 livro quando tentar devolver um livro inexistente")
	public void returnedInexistenteBookTest() throws Exception{
		//Cenario returned true
		
		ReturnedLoanDTO loanDto = ReturnedLoanDTO.builder().returned(true).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
		.willReturn(Optional.empty());		
		String json = new ObjectMapper().writeValueAsString(loanDto);
		
		mvc.perform(
				patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		).andExpect(MockMvcResultMatchers.status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Deve filtrar emprestimos")
	public void findLoansTest() throws Exception {
		//cenario
		Long id = 1l;
		
		Loan loan = LoanServiceTest.createLoan();
		loan.setId(id);
		Book book = Book.builder().id(id).isbn("123").build();
		loan.setBook(book);

        BDDMockito.given( loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>( Arrays.asList(loan), PageRequest.of(0,100), 1 )   );
	

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                book.getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
            .perform( request )
            .andExpect( status().isOk() )
            .andExpect( jsonPath("content", Matchers.hasSize(1)))
            .andExpect( jsonPath("totalElements").value(1) )
            .andExpect( jsonPath("pageable.pageSize").value(100) )
            .andExpect( jsonPath("pageable.pageNumber").value(0))
            ;
	}
	
}
