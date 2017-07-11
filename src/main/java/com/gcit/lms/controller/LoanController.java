package com.gcit.lms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
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

import com.gcit.lms.dao.LoanDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Loan;

@Path("/loans")
public class LoanController {

	@Autowired
	LoanDAO ldao;
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces("application/json")
	public List<Loan> getLoans(@QueryParam("cardNo") Integer cardNo)
			throws SQLException {
		Borrower b = new Borrower();
		b.setCardNo(cardNo);
		if(cardNo == null)
			return ldao.readAllLoans();
		return ldao.readUserLoans(b);
	}

	@GET
	@Produces("application/json")
	@Path("/{cardNo}/{branchId}/{bookId}/{dateOut}")
	public Response getLoan(@PathParam("cardNo") Integer cardNo,
			@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId,
			@PathParam("dateOut") String dateOut) throws SQLException {
		Loan l = ldao.readLoansByPK(dateOut, cardNo, bookId, branchId);
		if (l == null)
			throw new NotFoundException();
		else
			return Response.ok(l).build();
	}
	
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response addLoan(Loan loan) throws SQLException, UnsupportedEncodingException {
		HashMap<String, Object> newLoanIds = ldao.addLoanWithID(loan);
		String url = "/" + uriInfo.getPath() + "/" + newLoanIds.get("cardNo")
				+ "/" + newLoanIds.get("branchId") + "/"
				+ newLoanIds.get("bookId") + "/"
				+ URLEncoder.encode(newLoanIds.get("dateOut").toString(), "UTF-8");
		return Response.created(
				URI.create(url)).build();
	}

	@PUT
	@Consumes("application/json")
	@Path("/{cardNo}/{branchId}/{bookId}/{dateOut}")
	public Response updateLoan(@PathParam("cardNo") Integer cardNo,
			@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId,
			@PathParam("dateOut") String dateOut, Loan loan)
			throws SQLException {
		if (ldao.readLoansByPK(dateOut, cardNo, bookId, branchId) == null)
			throw new NotFoundException();
		else {
			Borrower bw = new Borrower();
			Branch br = new Branch();
			Book bk = new Book();
			bw.setCardNo(cardNo);
			br.setBranchId(branchId);
			bk.setBookId(bookId);
			loan.setBorrower(bw);
			loan.setBranch(br);
			loan.setBook(bk);
			loan.setDateOut(dateOut);
			ldao.updateLoan(loan);
			return Response.ok().build();
		}
	}

	@DELETE
	@Path("/{cardNo}")
	public Response deleteLoan(@PathParam("cardNo") Integer cardNo,
			@PathParam("branchId") Integer branchId,
			@PathParam("bookId") Integer bookId,
			@PathParam("dateOut") String dateOut)
			throws SQLException {
		if (ldao.readLoansByPK(dateOut, cardNo, bookId, branchId) == null)
			throw new NotFoundException();
		else {
			Loan loan =  new Loan();
			Borrower bw = new Borrower();
			Branch br = new Branch();
			Book bk = new Book();
			bw.setCardNo(cardNo);
			br.setBranchId(branchId);
			bk.setBookId(bookId);
			loan.setBorrower(bw);
			loan.setBranch(br);
			loan.setBook(bk);
			loan.setDateOut(dateOut);
			ldao.deleteLoan(loan);
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
