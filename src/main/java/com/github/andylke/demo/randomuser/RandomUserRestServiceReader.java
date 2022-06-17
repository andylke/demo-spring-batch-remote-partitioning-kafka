package com.github.andylke.demo.randomuser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.web.client.RestTemplate;

public class RandomUserRestServiceReader implements ItemReader<RandomUser> {

  private RestTemplate restTemplate = new RestTemplate();

  private Queue<RandomUser> randomUsers = new LinkedList<RandomUser>();

  private int currentPage;

  private final int targetPage;

  private final int pageSize;

  public RandomUserRestServiceReader(
      int startPagePerPartition, int totalPagePerPartition, int pageSize) {
    this.currentPage = startPagePerPartition;
    this.targetPage = startPagePerPartition + totalPagePerPartition;
    this.pageSize = pageSize;
  }

  @Override
  public RandomUser read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (randomUsers.isEmpty()) {
      if (currentPage >= targetPage) {
        return null;
      }
      randomUsers.addAll(getRandomUsers(currentPage++, pageSize));
    }
    return randomUsers.poll();
  }

  private List<RandomUser> getRandomUsers(int page, int size) {
    return restTemplate
        .getForEntity(
            "https://randomuser.me/api/?inc=name,gender,email,password,login,nat&page="
                + page
                + "&results="
                + size,
            RandomUserResponse.class)
        .getBody()
        .getResults();
  }
}
