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

import com.gcit.lms.entity.Genre;
import com.mysql.jdbc.Statement;

public class GenreDAO extends BaseDAO implements ResultSetExtractor<List<Genre>> {
	

	public void addGenre(Genre genre) throws SQLException{
		template.update("insert into tbl_genre(genre_name) values (?)", new Object[] {genre.getGenreName()});
	}
	
	public Integer addGenreWithID(Genre genre) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "insert into tbl_genre(genre_name) values (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, genre.getGenreName());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	public void updateGenre(Genre genre) throws SQLException{
		template.update("update tbl_genre set genre_name = ? WHERE genre_id = ?", new Object[] {genre.getGenreName(), genre.getGenreId()});
	}
	
	public void deleteGenre(Genre genre) throws SQLException{
		template.update("delete FROM tbl_genre WHERE genre_id = ?", new Object[] {genre.getGenreId()});
	}
	

	public List<Genre> readAllGenres() throws SQLException{
		return (List<Genre>) template.query("select * FROM tbl_genre", this);
	}
	
	public List<Genre> readAllGenresByBookId(Integer bookId) throws SQLException {
		return template.query("select * from tbl_genre where genre_id in (select genre_id from tbl_book_genres where bookId = ?)", new Object[]{bookId}, this);
	}

	public List<Genre> readAllGenresByName(String searchString) throws SQLException{
		searchString = "%"+searchString+"%";
		return (List<Genre>) template.query("select * FROM tbl_genre WHERE genre_name like ?", new Object[]{searchString}, this);
	}
	

	public Genre readGenresByPK(Integer genreId) throws SQLException{
		List<Genre> genres = (List<Genre>) template.query("select * FROM tbl_genre WHERE genre_id = ?", new Object[]{genreId}, this);
		if(genres.size() > 0){
			return genres.get(0);
		}
		return null;
	}

	

	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		while(rs.next()){
			Genre g = new Genre();
			g.setGenreId(rs.getInt("genre_id"));
			g.setGenreName(rs.getString("genre_name"));
			genres.add(g);
		}
		return genres;
	}
	


}
