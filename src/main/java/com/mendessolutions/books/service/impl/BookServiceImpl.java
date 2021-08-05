package com.mendessolutions.books.service.impl;

import org.springframework.stereotype.Service;

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
		return repository.save(book);
	}
//0800 284 0011 educacao
}
