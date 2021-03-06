package com.mendessolutions.books.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mendessolutions.books.api.dto.BookDTO;
import com.mendessolutions.books.api.dto.LoanDTO;
import com.mendessolutions.books.api.dto.LoanFilterDTO;
import com.mendessolutions.books.api.dto.ReturnedLoanDTO;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;
import com.mendessolutions.books.service.BookService;
import com.mendessolutions.books.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
	
	private final LoanService loanService;
	private final BookService bookService;
	private final ModelMapper modelMapper;

	
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
		Loan loan = loanService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());
		loanService.update(loan);
	}
	
	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest){
		Page<Loan> result = loanService.find(dto, pageRequest);
		List<LoanDTO> loans= result
			.getContent()
			.stream()
			.map(entity -> {
			Book book = entity.getBook();
			BookDTO bookDto = modelMapper.map(book, BookDTO.class);
			LoanDTO loanDto = modelMapper.map(entity, LoanDTO.class);
			loanDto.setBook(bookDto);
			return loanDto;
			})
			.collect(Collectors.toList());
		return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
	}
	

	
}
