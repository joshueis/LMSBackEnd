package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.mysql.jdbc.Statement;

public class BookDAO extends BaseDAO implements ResultSetExtractor<List<Book>> {

	public void addBook(Book book) throws SQLException {
		template.update("insert into tbl_book(title) values (?)",
				new Object[] { book.getTitle() });
	}

	public Integer addBookWithID(Book book) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "insert into tbl_book(title) values (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, book.getTitle());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBook(Book book) throws SQLException {
		template.update(
				"update tbl_book set title = ?, pubId = ? where bookId = ?",
				new Object[] { book.getTitle(),
						book.getPublisher().getPublisherId(), book.getBookId() });
		// TODO: Add another template.update updating bookgenres and book
		// authors tables
	}

	public void updateBookTitle(Book book) throws SQLException {
		template.update("update tbl_book set title =? where bookId = ?",
				new Object[] { book.getTitle(), book.getBookId() });
	}

	public void updateBookPublisher(Book b) throws SQLException {
		template.update(
				"UPDATE tbl_book SET pubId = ? WHERE bookId = ?",
				new Object[] { b.getPublisher().getPublisherId(), b.getBookId() });
	}

	public void updateBookGenres(Book b) throws SQLException {
		for (Genre g : b.getGenres()) {
			template.update("INSERT INTO tbl_book_genres VALUES (?,?)",
					new Object[] { g.getGenreId(), b.getBookId() });
		}
	}

	public void updateBookAuthors(Book b) throws SQLException {
		for (Author a : b.getAuthors()) {
			template.update("INSERT INTO tbl_book_authors VALUES (?,?)",
					new Object[] { b.getBookId(), a.getAuthorId() });
		}
	}

	public void deleteBook(Book book) throws SQLException {
		template.update("delete from tbl_book where bookId = ?",
				new Object[] { book.getBookId() });
	}

	public List<Book> readAllBooks() throws SQLException {
		return template.query("select * from tbl_book", this);
	}

	public List<Book> readAllBooksByAuthorId(Integer authorId)
			throws SQLException {
		return template
				.query("select * from tbl_book where bookId in (select bookId from tbl_book_authors where authorId = ?)",
						new Object[] { authorId }, this);
	}

	public List<Book> readAllBooksByTitle(String searchString)
			throws SQLException {
		searchString = "%" + searchString + "%";
		return template.query("select * from tbl_book where title like ?",
				new Object[] { searchString }, this);
	}

	public Book readBooksByPK(Integer bookId) throws SQLException {
		List<Book> books = (List<Book>) template.query(
				"select * from tbl_book where bookId = ?",
				new Object[] { bookId }, this);
		if (books != null) {
			return books.get(0);
		}
		return null;
	}

	@Autowired
	PublisherDAO pdao;

	@Override
	public List<Book> extractData(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			b.setPublisher(pdao.readPublishersByPK(rs.getInt("pubId")));
			books.add(b);
		}
		return books;
	}

}
