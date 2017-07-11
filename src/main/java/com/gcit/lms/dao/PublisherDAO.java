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

import com.gcit.lms.entity.Publisher;
import com.mysql.jdbc.Statement;

public class PublisherDAO extends BaseDAO implements
		ResultSetExtractor<List<Publisher>> {

	public void addPublisher(Publisher publisher) throws SQLException {
		template.update(
				"INSERT INTO tbl_publisher(publisherName, publisherAddress, publisherPhone) VALUES (?,?,?)",
				new Object[] { publisher.getPublisherName(),
						publisher.getPublisherAddress(),
						publisher.getPublisherPhone() });
	}

	public Integer addPublisherWithID(Publisher publisher) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_publisher(publisherName, publisherAddress, publisherPhone) VALUES (?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, publisher.getPublisherName());
				ps.setString(2, publisher.getPublisherAddress());
				ps.setString(3, publisher.getPublisherPhone());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updatePublisher(Publisher publisher) throws SQLException {
		template.update(
				"UPDATE tbl_publisher SET publisherName = ?, publisherAddress = ?, publisherPhone = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherName(),
						publisher.getPublisherAddress(),
						publisher.getPublisherPhone(),
						publisher.getPublisherId() });
	}

	public void updatePublisherName(Publisher publisher) throws SQLException {
		template.update(
				"UPDATE tbl_publisher SET publisherName = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherName(),
						publisher.getPublisherId() });
	}

	public void updatePublisherAddress(Publisher publisher) throws SQLException {
		template.update(
				"UPDATE tbl_publisher SET publisherAddress = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherAddress(),
						publisher.getPublisherId() });
	}

	public void updatePublisherPhone(Publisher publisher) throws SQLException {
		template.update(
				"UPDATE tbl_publisher SET publisherPhone = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherPhone(),
						publisher.getPublisherId() });
	}

	public void deletePublisher(Publisher publisher) throws SQLException {
		template.update("DELETE FROM tbl_publisher WHERE publisherId = ?",
				new Object[] { publisher.getPublisherId() });
	}

	public List<Publisher> readAllPublishers() throws SQLException {
		return (List<Publisher>) template.query("SELECT * FROM tbl_publisher",
				this);
	}

	public List<Publisher> readAllPublishersByName(String searchString)
			throws SQLException {
		searchString = "%" + searchString + "%";
		return (List<Publisher>) template.query(
				"SELECT * FROM tbl_publisher WHERE publisherName LIKE ?",
				new Object[] { searchString }, this);
	}

	public Publisher readPublishersByPK(Integer publisherId)
			throws SQLException {
		List<Publisher> publishers = (List<Publisher>) template.query(
				"SELECT * FROM tbl_publisher WHERE publisherId = ?",
				new Object[] { publisherId }, this);
		if (publishers.size() != 0) {
			return publishers.get(0);
		}
		return null;
	}

	@Override
	public List<Publisher> extractData(ResultSet rs) throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		while (rs.next()) {
			Publisher b = new Publisher();
			b.setPublisherId(rs.getInt("publisherId"));
			b.setPublisherName(rs.getString("publisherName"));
			b.setPublisherAddress(rs.getString("publisherAddress"));
			b.setPublisherPhone(rs.getString("publisherPhone"));
			publishers.add(b);
		}
		return publishers;
	}

}
