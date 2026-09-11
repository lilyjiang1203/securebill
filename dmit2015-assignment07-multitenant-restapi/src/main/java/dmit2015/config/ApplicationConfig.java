package dmit2015.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinitions({

	@DataSourceDefinition(
			name = "java:app/datasources/H2DatabaseDS",
			className = "org.h2.jdbcx.JdbcDataSource",
			// url="jdbc:h2:file:~/jdk/databases/h2/DMIT201CourseDB;",
			url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
			user = "securebill",
			password = "securebill-demo"),



})

@ApplicationScoped
public class ApplicationConfig {

}