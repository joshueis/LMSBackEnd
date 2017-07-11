package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.BranchBook;
import com.mysql.jdbc.Statement;

public class BranchDAO extends BaseDAO implements
		ResultSetExtractor<List<Branch>> {

	public void addBranch(Branch branch) throws SQLException {
		template.update(
				"INSERT INTO tbl_library_branch(branchName, branchAddress) values (?,?)",
				new Object[] { branch.getBranchName(),
						branch.getBranchAddress() });
	}

	public Integer addBranchWithID(Branch branch) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_library_branch(branchName, branchAddress) values (?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, branch.getBranchName());
				ps.setString(2, branch.getBranchAddress());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBranch(Branch branch) throws SQLException {
		template.update(
				"UPDATE tbl_library_branch set branchName = ?, branchAddress = ? WHERE branchId = ?",
				new Object[] { branch.getBranchName(),
						branch.getBranchAddress(), branch.getBranchId() });
	}

	public void updateBranchName(Branch branch) throws SQLException {
		template.update(
				"UPDATE tbl_library_branch set branchName = ? WHERE branchId = ?",
				new Object[] { branch.getBranchName(), branch.getBranchId() });
	}

	public void updateBranchAddress(Branch branch) throws SQLException {
		template.update(
				"UPDATE tbl_library_branch set branchAddress = ? WHERE branchId = ?",
				new Object[] { branch.getBranchAddress(), branch.getBranchId() });

	}

	public void updateBranchCopies(BranchBook bBook) throws SQLException {
		template.update(
				"UPDATE tbl_book_copies set noOfCopies = ? WHERE branchId = ? AND bookId = ?",
				new Object[] { bBook.getNoOfCopies(), bBook.getBranchId(),
						bBook.getBookId() });
	}

	// public void decBranchCopies(Branch branch, Book book) throws SQLException
	// {
	// template.update(
	// "UPDATE tbl_book_copies set noOfCopies = noOfCopies-1 WHERE branchId = ? AND bookId = ?",
	// new Object[] { branch.getBranchId(), book.getBookId() });
	// }
	//
	// public void incBranchCopies(Branch branch, Book book) throws SQLException
	// {
	// template.update(
	// "UPDATE tbl_book_copies set noOfCopies = noOfCopies+1 WHERE branchId = ? AND bookId = ?",
	// new Object[] { branch.getBranchId(), book.getBookId() });
	// }

	public void addBookEntry(Branch branch, Book book) throws SQLException {
		template.update("INSERT INTO tbl_book_copies VALUES(?,?,0)",
				new Object[] { book.getBookId(), branch.getBranchId() });
	}

	public void deleteBranch(Branch branch) throws SQLException {
		template.update("delete FROM tbl_library_branch WHERE branchId = ?",
				new Object[] { branch.getBranchId() });
	}

	public List<Branch> readAllBranches() throws SQLException {
		return (List<Branch>) template.query(
				"SELECT * FROM tbl_library_branch", this);
	}

	public List<Branch> readAllBranchesByName(String searchString)
			throws SQLException {
		searchString = "%" + searchString + "%";
		return (List<Branch>) template.query(
				"SELECT * FROM tbl_library_branch WHERE branchName like ?",
				new Object[] { searchString }, this);
	}

	public Branch readBranchesByPK(Integer branchId) throws SQLException {
		List<Branch> branches = (List<Branch>) template.query(
				"SELECT * FROM tbl_library_branch WHERE branchId = ?",
				new Object[] { branchId }, this);
		if (branches.size() > 0) {
			return branches.get(0);
		}
		return null;
	}

	public List<BranchBook> readAllBranchBooks(Integer branchId, Boolean isAv)
			throws SQLException {
		List<BranchBook> branchBooks = (List<BranchBook>) template
				.query("SELECT * FROM tbl_book_copies bc"
						+ " JOIN tbl_library_branch lb ON lb.branchId = bc.branchID"
						+ " JOIN tbl_book b ON bc.bookId = b.bookId"
						+ " WHERE bc.branchId = ?", new Object[] { branchId },
						new ResultSetExtractor<List<BranchBook>>() {
							public List<BranchBook> extractData(ResultSet rs)
									throws SQLException {
								List<BranchBook> books = new ArrayList<BranchBook>();
								while (rs.next()) {
									BranchBook book = new BranchBook();
									book.setTitle(rs.getString("title"));
									book.setBranchId(rs.getInt("branchId"));
									book.setBookId(rs.getInt("bookId"));
									book.setNoOfCopies(rs.getInt("noOfCopies"));
									if (isAv) {
										if (book.getNoOfCopies() > 0)
											books.add(book);
									}else
										books.add(book);
								}
								return books;
							}
						});
		return branchBooks;
	}

	public BranchBook readBranchBooksByPK(Integer branchId, Integer bookId)
			throws SQLException {
		List<BranchBook> branchBooks = (List<BranchBook>) template
				.query("SELECT * FROM tbl_book_copies bc"
						+ " JOIN tbl_library_branch lb ON lb.branchId = bc.branchID"
						+ " JOIN tbl_book b ON bc.bookId = b.bookId"
						+ " WHERE bc.branchId = ? AND bc.bookId = ?",
						new Object[] { branchId, bookId },
						new ResultSetExtractor<List<BranchBook>>() {
							public List<BranchBook> extractData(ResultSet rs)
									throws SQLException {
								List<BranchBook> books = new ArrayList<BranchBook>();
								while (rs.next()) {
									BranchBook book = new BranchBook();
									book.setTitle(rs.getString("title"));
									book.setBranchId(rs.getInt("branchId"));
									book.setBookId(rs.getInt("bookId"));
									book.setNoOfCopies(rs.getInt("noOfCopies"));
									books.add(book);
								}
								return books;
							}
						});
		if (branchBooks.size() > 0) {
			return branchBooks.get(0);
		}
		return null;
	}

	@Override
	public List<Branch> extractData(ResultSet rs) throws SQLException {
		List<Branch> branches = new ArrayList<>();
		while (rs.next()) {
			Branch b = new Branch();
			b.setBranchId(rs.getInt("branchId"));
			b.setBranchName(rs.getString("branchName"));
			b.setBranchAddress(rs.getString("branchAddress"));
			branches.add(b);
		}
		return branches;
	}

}
