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

import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.entity.Genre;

@Path("/genres")
public class GenreController {
//TODO: Add update books capability
	@Autowired
	GenreDAO gdao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Genre> getGenres() throws SQLException {
		List<Genre> genres = gdao.readAllGenres();
		if(genres.size() < 1)
			throw new WebApplicationException(404);
		return genres;
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{genreId}")
	public Response getGenre(@PathParam("genreId") Integer genreId)
			throws SQLException {
		Genre g = gdao.readGenresByPK(genreId);
		if (g == null)
			throw new WebApplicationException(404);
		else
			return Response.ok(g).build();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addGenre(Genre genre) throws SQLException {
		Integer newGenreId = gdao.addGenreWithID(genre);
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newGenreId)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{genreId}")
	public Response updateGenre(@PathParam("genreId") Integer genreId,
			Genre genre) throws SQLException {
		if (gdao.readGenresByPK(genreId) == null)
			throw new WebApplicationException(404);
		else {
			genre.setGenreId(genreId);
			gdao.updateGenre(genre);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{genreId}")
	public Response deleteGenre(@PathParam("genreId") Integer genreId)
			throws SQLException {
		if (gdao.readGenresByPK(genreId) == null)
			throw new WebApplicationException(404);
		else {
			Genre g = new Genre();
			g.setGenreId(genreId);
			gdao.deleteGenre(g);
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
