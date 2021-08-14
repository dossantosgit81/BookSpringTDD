package com.mendessolutions.books.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mendessolutions.books.api.exception.BusinessException;
import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.repository.BookRepository;
import com.mendessolutions.books.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
		}
		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		// TODO Auto-generated method stub
		return this.repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("O book ou o id está nulo");
		}
		this.repository.delete(book);
		
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("O book ou id está nulo");
		}
		return this.repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example<Book> example =
				Example.of(filter, ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
		);
		
		return repository.findAll(example, pageRequest);
		
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		// TODO Auto-generated method stub
		return repository.findByIsbn(isbn);
	}

}
