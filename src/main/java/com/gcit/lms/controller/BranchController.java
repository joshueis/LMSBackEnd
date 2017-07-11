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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.BranchBook;

@Path("/branches")
public class BranchController {

	@Autowired
	BranchDAO brdao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Branch> getBranches() throws SQLException {
		return brdao.readAllBranches();
	}

	@GET
	@Produces({ "application/json" })
	@Path("/{branchId}")
	public Response getBranch(@PathParam("branchId") Integer branchId)
			throws SQLException {
		Branch b = brdao.readBranchesByPK(branchId);
		if (b == null)
			throw new NotFoundException();
		else
			return Response.ok(b).build();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addBranch(Branch branch) throws SQLException {
		Integer newBranchId = brdao.addBranchWithID(branch);
		return Response.created(
				URI.create("/" + uriInfo.getPath() + "/" + newBranchId)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{branchId}")
	public Response updateBranch(@PathParam("branchId") Integer branchId,
			Branch branch) throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new NotFoundException();
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
			throw new NotFoundException();
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
			@QueryParam("isAv") Boolean isAv) throws SQLException {
		if (brdao.readBranchesByPK(branchId) == null)
			throw new NotFoundException();
		return brdao.readAllBranchBooks(branchId, isAv);
	}
	

	@PUT
	@Consumes("application/json")
	@Path("/{branchId}/branchBooks/{bookId}")
	public Response updateBranchCopies(@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId, BranchBook bBook)
			throws SQLException {
		if (brdao.readBranchBooksByPK(branchId, bookId) == null)
			throw new NotFoundException();
		else {
			bBook.setBranchId(branchId);
			bBook.setBookId(bookId);
			brdao.updateBranchCopies(bBook);
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

	@PUT
	@Path("{branchId}/branchBooks")
	public Response up2NotAllowed() {
		return Response.status(405).entity("Operation not allowed").build();
	}

}
