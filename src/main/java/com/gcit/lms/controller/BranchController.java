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

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.BranchBook;

@Path("/branches")
public class BranchController {

	@Autowired
	BranchDAO brdao;
	@Autowired
	BookDAO bdao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Branch> getBranches() throws SQLException {
		List<Branch> branches = brdao.readAllBranches();
		if (branches.size() < 1)
			throw new WebApplicationException(404);
		return branches;
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{branchId}")
	public Response getBranch(@PathParam("branchId") Integer branchId)
			throws SQLException {
		Branch b = brdao.readBranchesByPK(branchId);
		if (b == null)
			throw new WebApplicationException(404);
		else
			return Response.ok(b).build();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addBranch(Branch branch) throws SQLException {
		Integer newBranchId = brdao.addBranchWithID(branch);
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newBranchId))
				.build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{branchId}")
	public Response updateBranch(@PathParam("branchId") Integer branchId,
			Branch branch) throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new WebApplicationException(404);
		else {
			branch.setBranchId(branchId);
			brdao.updateBranch(branch);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{branchId}")
	public Response deleteBranch(@PathParam("branchId") Integer branchId)
			throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new WebApplicationException(404);
		else {
			Branch br = new Branch();
			br.setBranchId(branchId);
			brdao.deleteBranch(br);
			return Response.ok().build();
		}
	}

	@GET
	@Produces("application/json")
	@Path("/{branchId}/branchBooks")
	public List<BranchBook> getBranchBooks(
			@PathParam("branchId") Integer branchId,
			@QueryParam("isAv") boolean isAv) throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new WebApplicationException(404);
		List<BranchBook> branchBooks = brdao.readAllBranchBooks(branchId, isAv);
		if (branchBooks.size() < 1)
			throw new WebApplicationException(404);
		return branchBooks;
	}

	@POST
	@Consumes("application/json")
	@Path("/{branchId}/branchBooks/{bookId}")
	public Response addBranchBook(@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId, BranchBook bBook)
			throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new WebApplicationException(404);
		if (bdao.readBooksByPK(bookId) == null)
			throw new WebApplicationException(404);
		bBook.setBranchId(branchId);
		bBook.setBookId(bookId);
		brdao.addBookEntry(bBook);
		return Response.ok().build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{branchId}/branchBooks/{bookId}")
	public Response updateBranchCopies(@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId, BranchBook bBook)
			throws SQLException {
		if (brdao.readBranchBooksByPK(branchId, bookId) == null)
			throw new WebApplicationException(404);
		else {
			bBook.setBranchId(branchId);
			bBook.setBookId(bookId);
			brdao.updateBranchCopies(bBook);
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

	@PUT
	@Path("{branchId}/branchBooks")
	public Response up2NotAllowed() {
		throw new WebApplicationException(405);
	}

}
