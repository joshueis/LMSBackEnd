package com.gcit.lms;

import javax.annotation.PostConstruct;

import org.apache.commons.dbcp2.BasicDataSource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.LoanDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.BranchBook;
import com.gcit.lms.service.AdminService;

@Configuration
public class LMSConfig extends ResourceConfig {

	public String driver = "com.mysql.jdbc.Driver";
	public String url = "jdbc:mysql://gcitlmsdb.c0u245fsr0ye.us-east-1.rds.amazonaws.com:3306/Library";
	public String username = "gcitlmsdb";
	public String password = "Hur373612";

	
	@Bean(destroyMethod = "")
	public BasicDataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);

		return ds;
	}

	@Bean
	public JdbcTemplate template() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public AdminService adminService() {
		return new AdminService();
	}

	@Bean
	public AuthorDAO adao() {
		return new AuthorDAO();
	}

	@Bean
	public BookDAO bkdao() {
		return new BookDAO();
	}

	@Bean
	public GenreDAO gdao() {
		return new GenreDAO();
	}

	@Bean
	public PublisherDAO pdao() {
		return new PublisherDAO();
	}

	@Bean
	public BorrowerDAO bwdao() {
		return new BorrowerDAO();
	}

	@Bean
	BranchDAO brdao() {
		return new BranchDAO();
	}

	@Bean
	LoanDAO ldao() {
		return new LoanDAO();
	}

	@Bean
	BranchBook bBook() {
		return new BranchBook();
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	//JERSEY Configuration
	@PostConstruct
	public void setUp() {
		packages("com.gcit.lms.controller");
		register(MyExceptionMapper.class);
		register(CorsResponseFilter.class);
	}
	
}
