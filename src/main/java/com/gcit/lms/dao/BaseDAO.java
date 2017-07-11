package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDAO {
	
	//Constructor
	@Autowired
	JdbcTemplate template;

	public abstract List<?> extractData(ResultSet rs) throws SQLException;

}
