package com.mendessolutions.books.service;

import java.util.Optional;

import com.mendessolutions.books.model.entity.Book;

public interface BookService {
	Book save (Book any);
	
	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);
}
