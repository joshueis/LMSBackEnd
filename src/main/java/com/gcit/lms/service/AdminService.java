package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;

@RestController
public class AdminService {

	@Autowired
	AuthorDAO adao;
	
	@Autowired
	BookDAO bdao;
	

	@Transactional
	@RequestMapping(value = "/saveAuthor", method = RequestMethod.POST, consumes="application/json")
	public String saveAuthor(@RequestBody Author author) throws SQLException {
		return "okay";
		/*
		if (author.getAuthorId() != null) {
			adao.updateAuthor(author);
		} else {
			adao.addAuthor(author);
		}
		*/
	}
	

	//This is a rest method
	@RequestMapping(value = "/getAuthors", method = RequestMethod.GET)
	public List<Author> getAllAuthorsREST() throws SQLException {
		List<Author> authors = new ArrayList<>();

		authors = adao.readAllAuthors();

		for(Author a: authors){
			a.setBooks(bdao.readAllBooksByAuthorId(a.getAuthorId()));
		}
		return authors;
	}
	

	@Transactional
	public void deleteAuthor(Author author) throws SQLException {
		adao.deleteAuthor(author);
	}

	public Author getAuthorByPK(Integer authorId) throws SQLException {
		return adao.readAuthorsByPK(authorId);
	}

	public List<Book> getAllBooks() throws SQLException {
		return bdao.readAllBooks();
	}
}
