package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Loan;

public class LoanDAO extends BaseDAO implements ResultSetExtractor<List<Loan>> {

	// public void addLoan(Loan loan) throws SQLException {
	// template.update(
	// "INSERT INTO tbl_book_loans(bookId, branchId, cardNo, dateOut, dueDate, dateIn) VALUES (?,?,?, now(), DATE_ADD(now(),INTERVAL 1 WEEK), null)",
	// new Object[] { loan.getBook().getBookId(),
	// loan.getBranch().getBranchId(),
	// loan.getBorrower().getCardNo() });
	// }

	public HashMap<String, Object> addLoanWithID(Loan loan) throws SQLException {
		HashMap<String, Object> hM = new HashMap<String, Object>();
		hM.put("cardNo", loan.getBorrower().getCardNo());
		hM.put("branchId", loan.getBranch().getBranchId());
		hM.put("bookId", loan.getBook().getBookId());
		hM.put("dateOut", loan.getDateOut());
		template.update(
				"INSERT INTO tbl_book_loans(bookId, branchId, cardNo, dateOut, dueDate, dateIn) VALUES (?,?,?,?,?, null)",
				new Object[] { loan.getBook().getBookId(),
						loan.getBranch().getBranchId(),
						loan.getBorrower().getCardNo(), loan.getDateOut(), loan.getDueDate()});
		return hM;
	}

	public void updateLoan(Loan loan) {
		template.update(
				"UPDATE tbl_book_loans SET dueDate = ?, dateIn = ?  WHERE dateOut = ? AND cardNo = ? AND branchId = ? AND bookId = ?",
				new Object[] { loan.getDueDate(), loan.getDateIn(),
						loan.getDateOut(), loan.getBorrower().getCardNo(),
						loan.getBranch().getBranchId(),
						loan.getBook().getBookId() });
	}

	public void updateDueDate(Loan loan) throws SQLException {
		template.update(
				"UPDATE tbl_book_loans SET dueDate = ? WHERE dateOut = ? AND cardNo = ? AND branchId = ? AND bookId = ?",
				new Object[] { loan.getDueDate(), loan.getDateOut(),
						loan.getBorrower().getCardNo(),
						loan.getBranch().getBranchId(),
						loan.getBook().getBookId() });
	}

	public void updateDateIn(Loan loan) throws SQLException {
		template.update("UPDATE tbl_book_loans SET dateIn = now() "
				+ "WHERE cardNo = ? " + "AND branchId = ? " + "AND bookId = ? "
				+ "AND dateOut = ?", new Object[] {
				loan.getBorrower().getCardNo(), loan.getBranch().getBranchId(),
				loan.getBook().getBookId(), loan.getDateOut() });
	}

	public List<Loan> readAllLoans() throws SQLException {
		return (List<Loan>) template
				.query("SELECT * FROM tbl_book_loans", this);
	}

	public List<Loan> readUserLoans(Borrower b) throws SQLException {
		return (List<Loan>) template
				.query("SELECT * FROM tbl_book_loans WHERE cardNo = ? AND dateIn IS null",
						new Object[] { b.getCardNo() }, this);
	}

	public Loan readLoansByPK(String dateOut, Integer cardNo, Integer bookId,
			Integer branchId) throws SQLException {
		List<Loan> loans = (List<Loan>) template
				.query("SELECT * FROM tbl_book_loans WHERE dateOut = ? AND cardNo = ? AND bookId = ? AND branchId = ?",
						new Object[] { dateOut, cardNo, bookId, branchId },
						this);
		if (loans != null) {
			return loans.get(0);
		}
		return null;
	}

	public void deleteLoan(Loan l) throws SQLException {
		template.update(
				"DELETE FROM tbl_book_loans WHERE cardNo = ? AND branchId = ? AND bookId = ? AND dateOut = ?",
				new Object[] { l.getBorrower().getCardNo(),
						l.getBranch().getBranchId(), l.getBook().getBookId(),
						l.getDateOut() });
	}

	@Autowired
	BorrowerDAO bwdao;
	@Autowired
	BookDAO bdao;
	@Autowired
	BranchDAO brdao;

	@Override
	public List<Loan> extractData(ResultSet rs) throws SQLException {
		List<Loan> loans = new ArrayList<>();
		while (rs.next()) {
			Loan l = new Loan();
			l.setBorrower(bwdao.readBorrowersByPK(rs.getInt("cardNo")));
			l.setBranch(brdao.readBranchesByPK(rs.getInt("branchId")));
			l.setBook(brdao.readBranchBooksByPK(l.getBranch().getBranchId(), rs.getInt("bookId")));
			l.setDateOut(rs.getString("dateOut"));
			l.setDueDate(rs.getString("dueDate"));
			l.setDateIn(rs.getString("dateIn"));
			loans.add(l);
		}
		return loans;
	}

}
