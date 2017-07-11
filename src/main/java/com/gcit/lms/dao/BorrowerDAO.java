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

import com.gcit.lms.entity.Borrower;
import com.mysql.jdbc.Statement;

public class BorrowerDAO extends BaseDAO implements
		ResultSetExtractor<List<Borrower>> {

	public void addBorrower(Borrower borrower) throws SQLException {
		template.update(
				"INSERT INTO tbl_borrower(name,address,phone) VALUES (?,?,?)",
				new Object[] { borrower.getName(), borrower.getAddress(),
						borrower.getPhone() });
	}

	public Integer addBorrowerWithID(Borrower borrower) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_borrower(name,address,phone) VALUES (?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, borrower.getName());
				ps.setString(2, borrower.getAddress());
				ps.setString(3, borrower.getPhone());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBorrower(Borrower borrower) throws SQLException {
		template.update(
				"UPDATE tbl_borrower SET name = ?, address = ?, phone = ? WHERE cardNo = ?",
				new Object[] { borrower.getName(), borrower.getAddress(),
						borrower.getPhone(), borrower.getCardNo() });
	}

	public void updateBorrowerName(Borrower borrower) throws SQLException {
		template.update("UPDATE tbl_borrower SET name = ? WHERE cardNo = ?",
				new Object[] { borrower.getName(), borrower.getCardNo() });
	}

	public void updateBorrowerAddress(Borrower borrower) throws SQLException {
		template.update("UPDATE tbl_borrower SET address = ? WHERE cardNo = ?",
				new Object[] { borrower.getAddress(), borrower.getCardNo() });
	}

	public void updateBorrowerPhone(Borrower borrower) throws SQLException {
		template.update("UPDATE tbl_borrower SET phone = ? WHERE cardNo = ?",
				new Object[] { borrower.getPhone(), borrower.getCardNo() });
	}

	public void deleteBorrower(Borrower borrower) throws SQLException {
		template.update("delete FROM tbl_borrower WHERE cardNo = ?",
				new Object[] { borrower.getCardNo() });
	}

	public List<Borrower> readAllBorrowers() throws SQLException {
		return (List<Borrower>) template.query("select * FROM tbl_borrower",
				this);
	}

	public List<Borrower> readAllBorrowersByName(String searchString)
			throws SQLException {
		searchString = "%" + searchString + "%";
		return (List<Borrower>) template.query(
				"select * FROM tbl_borrower WHERE name like ?",
				new Object[] { searchString }, this);
	}

	public Borrower readBorrowersByPK(Integer cardNo) throws SQLException {
		List<Borrower> borrowers = (List<Borrower>) template.query(
				"select * FROM tbl_borrower WHERE cardNo = ?",
				new Object[] { cardNo }, this);
		if (borrowers.size() != 0) {
			return borrowers.get(0);
		}
		return null;
	}

	@Override
	public List<Borrower> extractData(ResultSet rs) throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		while (rs.next()) {
			Borrower b = new Borrower();
			b.setCardNo(rs.getInt("cardNo"));
			b.setName(rs.getString("name"));
			b.setAddress(rs.getString("address"));
			b.setPhone(rs.getString("phone"));
			borrowers.add(b);
		}
		return borrowers;
	}

}
