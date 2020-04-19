package com.techelevator.sports.model;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JDBCSportDao implements SportDao{
		
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public JDBCSportDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
	public List<Sport> list() {
		List<Sport> allSports = new ArrayList<>();
		
		String sql = "SELECT sport_id, sport_name FROM sports";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		
		while(results.next()) {
			Sport sport = mapResultToSport(results);
			allSports.add(sport);
		}

		return allSports;
	}
    
    private Sport mapResultToSport(SqlRowSet results) {
        Sport sport = new Sport();
        sport.setId(results.getInt("sport_id"));
        sport.setName(results.getString("sport_name"));
        return sport;
    }
}