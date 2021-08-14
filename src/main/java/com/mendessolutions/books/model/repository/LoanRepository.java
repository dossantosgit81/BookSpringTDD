package com.mendessolutions.books.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mendessolutions.books.model.entity.Book;
import com.mendessolutions.books.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

	@Query("select case when (count(l.id) > 0) "
			+ "then true else false end from Loan l where l.book = :book and (l.returned is null or l.returned is false) ")
	boolean existsByBookAndNotReturned(@Param("book") Book book);

}
