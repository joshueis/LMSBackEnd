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

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.mysql.jdbc.Statement;

public class AuthorDAO extends BaseDAO implements ResultSetExtractor<List<Author>> {

	public void addAuthor(Author author) throws SQLException {
		template.update("insert into tbl_author(authorName) values (?)", new Object[] { author.getAuthorName() });
	}

	public Integer addAuthorWithID(Author author) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "insert into tbl_author(authorName) values (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, author.getAuthorName());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateAuthor(Author author) throws SQLException {
		template.update("update tbl_author set authorName = ? where authorId = ?",
				new Object[] { author.getAuthorName(), author.getAuthorId() });
	}
	
	public void updateAuthorBooks(Author a) throws SQLException {
		for (Book b : a.getBooks()) {
			template.update("INSERT INTO tbl_book_authors VALUES (?,?)",
					new Object[] { b.getBookId(), a.getAuthorId() });
		}
	}

	public void deleteAuthor(Author author) throws SQLException {
		template.update("delete from tbl_author where authorId = ?", new Object[] { author.getAuthorId() });
	}

	public List<Author> readAllAuthors() throws SQLException {
		return template.query("select * from tbl_author", this);
	}

	public List<Author> readAllAuthorsByBookId(Integer bookId) throws SQLException {
		return template.query("select * from tbl_author where authorId in (select authorId from tbl_book_authors where bookId = ?)", new Object[]{bookId}, this);
	}

	public List<Author> readAllAuthorsByName(String searchString) throws SQLException {
		searchString = "%" + searchString + "%";
		return (List<Author>) template.query("select * from tbl_author where authorName like ?",
				new Object[] { searchString }, this);
	}

	public Author readAuthorsByPK(Integer authorId) throws SQLException {
		List<Author> authors = (List<Author>) template.query("select * from tbl_author where authorId = ?",
				new Object[] { authorId }, this);
		if (authors != null) {
			return authors.get(0);
		}
		return null;
	}

	@Override
	public List<Author> extractData(ResultSet rs) throws SQLException {
		List<Author> authors = new ArrayList<>();
		while (rs.next()) {
			Author a = new Author();
			a.setAuthorId(rs.getInt("authorId"));
			a.setAuthorName(rs.getString("authorName"));
			authors.add(a);
		}
		return authors;
	}
}
