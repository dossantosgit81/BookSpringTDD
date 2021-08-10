package com.mendessolutions.books.service.impl;

import java.util.Optional;

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

}
