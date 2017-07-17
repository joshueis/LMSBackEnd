package com.gcit.lms.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Book;

@Path("/books")
public class BookController {

	@Autowired
	BookDAO bdao;
	@Autowired
	AuthorDAO adao;
	@Autowired
	GenreDAO gdao;
	@Autowired
	PublisherDAO pdao;

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Book> getBooks(@QueryParam("title") String title)
			throws SQLException {
		List<Book> books;
		if (title == null)
			books = bdao.readAllBooks();
		else
			books = bdao.readAllBooksByTitle(title);
		for (Book b : books) {
			b.setAuthors(adao.readAllAuthorsByBookId(b.getBookId()));
			b.setGenres(gdao.readAllGenresByBookId(b.getBookId()));
		}
		if(books.size() < 1)
			throw new WebApplicationException(404);
		return books;
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{bookId}")
	public Response getBook(@PathParam("bookId") Integer bookId)
			throws SQLException {
		Book b = bdao.readBooksByPK(bookId);
		if (b == null)
			throw new WebApplicationException(404);
		else {
			b.setAuthors(adao.readAllAuthorsByBookId(b.getBookId()));
			b.setGenres(gdao.readAllGenresByBookId(b.getBookId()));
			return Response.ok(b).build();
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addBook(Book book) throws SQLException {
		Integer newBookId = bdao.addBookWithID(book);
		book.setBookId(newBookId);
		if(book.getAuthors() != null)
			bdao.updateBookAuthors(book);
		if(book.getGenres() != null)
			bdao.updateBookGenres(book);
		if(book.getPublisher() != null)
			bdao.updateBookPublisher(book);
		return Response
				.created(URI.create("/" + uriInfo.getPath() + "/" + newBookId))
				.build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{bookId}")
	public Response updateBook(@PathParam("bookId") Integer bookId, Book book)
			throws SQLException {
		if (bdao.readBooksByPK(bookId) == null)
			throw new WebApplicationException(404);
		else {
			book.setBookId(bookId);
			bdao.updateBook(book);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{bookId}")
	public Response deleteBook(@PathParam("bookId") Integer bookId)
			throws SQLException {
		if (bdao.readBooksByPK(bookId) == null)
			throw new WebApplicationException(404);
		else {
			Book b = new Book();
			b.setBookId(bookId);
			bdao.deleteBook(b);
			return Response.ok().build();
		}
	}

	@DELETE
	public Response delNotAllowed() {
		throw new WebApplicationException(405);
	}

	@PUT
	public Response upNotAllowed() {
		throw new WebApplicationException(405);
	}

}
