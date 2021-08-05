package com.mendessolutions.books.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mendessolutions.books.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
