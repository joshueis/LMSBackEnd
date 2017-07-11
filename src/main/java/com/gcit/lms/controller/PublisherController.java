package com.gcit.lms.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Publisher;

@Path("/publishers")
public class PublisherController {
//TODO: Add update books capability
	@Autowired
	PublisherDAO pdao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Publisher> getPublishers() throws SQLException {
		return pdao.readAllPublishers();
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{publisherId}")
	public Response getPublisher(@PathParam("publisherId") Integer publisherId)
			throws SQLException {
		Publisher p = pdao.readPublishersByPK(publisherId);
		if (p == null)
			throw new NotFoundException();
		else
			return Response.ok(p).build();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addPublisher(Publisher publisher) throws SQLException {
		Integer newPublisherId = pdao.addPublisherWithID(publisher);
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newPublisherId)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{publisherId}")
	public Response updatePublisher(@PathParam("publisherId") Integer publisherId,
			Publisher publisher) throws SQLException {
		if (pdao.readPublishersByPK(publisherId) == null)
			throw new NotFoundException();
		else {
			publisher.setPublisherId(publisherId);
			pdao.updatePublisher(publisher);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{publisherId}")
	public Response deletePublisher(@PathParam("publisherId") Integer publisherId)
			throws SQLException {
		if (pdao.readPublishersByPK(publisherId) == null)
			throw new NotFoundException();
		else {
			Publisher p = new Publisher();
			p.setPublisherId(publisherId);
			pdao.deletePublisher(p);
			return Response.ok().build();
		}
	}
	

	@DELETE
	public Response delNotAllowed() {
		return Response.status(405).entity("Operation not allowed").build();
	}

	@PUT
	public Response upNotAllowed() {
		return Response.status(405).entity("Operation not allowed").build();
	}

}
