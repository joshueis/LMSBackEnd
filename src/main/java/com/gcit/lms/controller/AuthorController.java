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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.entity.Author;

@Path("/authors")
public class AuthorController {

	@Autowired
	AuthorDAO adao;
	@Autowired
	BookDAO bdao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Author> getAuthors() throws SQLException {
		List<Author> authors;
		authors = adao.readAllAuthors();
		for(Author a: authors){
			a.setBooks(bdao.readAllBooksByAuthorId(a.getAuthorId()));
		}
		if (authors.size() < 1)
			throw new WebApplicationException(404);
		return authors;
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{authorId}")
	public Response getAuthor(@PathParam("authorId") Integer authorId)
			throws SQLException {
		Author a = adao.readAuthorsByPK(authorId);
		if (a == null)
			throw new WebApplicationException(404);
		else{
			a.setBooks(bdao.readAllBooksByAuthorId(a.getAuthorId()));
			return Response.ok(a).build();
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addAuthor(Author author) throws SQLException {
		Integer newAuthorId = adao.addAuthorWithID(author);
		author.setAuthorId(newAuthorId);
		if(author.getBooks() != null){
			adao.updateAuthorBooks(author);
		}
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newAuthorId)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{authorId}")
	public Response updateAuthor(@PathParam("authorId") Integer authorId,
			Author author) throws SQLException {
		if (adao.readAuthorsByPK(authorId) == null)
			throw new WebApplicationException(404);
		else {
			author.setAuthorId(authorId);
			adao.updateAuthor(author);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{authorId}")
	public Response deleteAuthor(@PathParam("authorId") Integer authorId)
			throws SQLException {
		if (adao.readAuthorsByPK(authorId) == null)
			throw new WebApplicationException(404);
		else {
			Author a = new Author();
			a.setAuthorId(authorId);
			adao.deleteAuthor(a);
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
