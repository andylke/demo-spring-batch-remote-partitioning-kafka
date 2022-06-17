package com.github.andylke.demo.user;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserSummaryByNationalityReader implements ItemReader<UserSummaryByNationality> {

  private JdbcTemplate jdbcTemplate;

  private Queue<UserSummaryByNationality> userSummaries;

  @Override
  public UserSummaryByNationality read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (userSummaries == null) {
      userSummaries = new LinkedList<UserSummaryByNationality>(getUserSummaryByNationality());
    }

    return userSummaries.poll();
  }

  private List<UserSummaryByNationality> getUserSummaryByNationality() {
    return jdbcTemplate.query(
        "select nationality, count(username) as count from user"
            + " group by nationality"
            + " order by nationality",
        (rs, index) ->
            new UserSummaryByNationality(rs.getString("nationality"), rs.getInt("count")));
  }

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
}
