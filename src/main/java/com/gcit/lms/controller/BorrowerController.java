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

import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.entity.Borrower;

@Path("/borrowers")
public class BorrowerController {

	@Autowired
	BorrowerDAO bwdao;

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Borrower> getBorrowers() throws SQLException {
		List<Borrower> borrowers = bwdao.readAllBorrowers();
		if(borrowers.size() < 1)
			throw new WebApplicationException(404);
		return borrowers;
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{cardNo}")
	public Response getBorrower(@PathParam("cardNo") Integer cardNo)
			throws SQLException {
		Borrower bw = bwdao.readBorrowersByPK(cardNo);
		if (bw == null)
			throw new WebApplicationException(404);
		else{
			return Response.ok(bw).build();
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addBorrower(Borrower borrower) throws SQLException {
		Integer newCardNo = bwdao.addBorrowerWithID(borrower);
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newCardNo)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{cardNo}")
	public Response updateBorrower(@PathParam("cardNo") Integer cardNo,
			Borrower borrower) throws SQLException {
		if (bwdao.readBorrowersByPK(cardNo) == null)
			throw new WebApplicationException(404);
		else {
			borrower.setCardNo(cardNo);
			bwdao.updateBorrower(borrower);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{cardNo}")
	public Response deleteBorrower(@PathParam("cardNo") Integer cardNo)
			throws SQLException {
		if (bwdao.readBorrowersByPK(cardNo) == null)
			throw new WebApplicationException(404);
		else {
			Borrower bw = new Borrower();
			bw.setCardNo(cardNo);
			bwdao.deleteBorrower(bw);
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
