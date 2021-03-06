package com.mendessolutions.books.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mendessolutions.books.api.dto.BookDTO;
import com.mendessolutions.books.api.dto.LoanDTO;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.service.BookService;
import com.mendessolutions.books.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@Api("Book API")
@Slf4j
public class BookController {
	
	@Autowired
	private BookService service;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("CREATE A BOOK")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Book successfully created")
	})
	public BookDTO create(@RequestBody @Valid BookDTO dto) {
		log.info("creating a book for isbn: {}", dto.getIsbn());
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		
		return modelMapper.map(entity, BookDTO.class);
	}
	
	@GetMapping("{id}")
	public BookDTO get(@PathVariable Long id) {
		return service
				.getById(id)
				.map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Book book = service.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
	}
	
	@PutMapping("{id}")
	public BookDTO update(@PathVariable Long id, BookDTO dto) {
		return service.getById(id)
				.map((book)-> {
					
					book.setAuthor(dto.getAuthor());
					book.setTitle(dto.getTitle());
					book = service.update(book);
					return modelMapper.map(book, BookDTO.class);
				})
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping
	public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		
		List<BookDTO> list = result.getContent().stream()
		.map(entity -> modelMapper.map(entity, BookDTO.class))
		.collect(Collectors.toList());
		
		return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
	}
	
	@GetMapping("{id}/loans")
	public Page<LoanDTO> loanByBook(@PathVariable Long id, Pageable pageable){
		Book book = service.getById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		Page<Loan> result =  loanService.getLoansByBook(book, pageable);
		
		List<LoanDTO> list = result.getContent()
			.stream()
			.map(loan -> {
				Book loanBook = loan.getBook();
				BookDTO bookDto = modelMapper.map(loanBook, BookDTO.class);
				LoanDTO loanDto = modelMapper.map(loan, LoanDTO.class);
				loanDto.setBook(bookDto);
				return loanDto;
			}).collect(Collectors.toList());
		
		return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
	}
	
}
