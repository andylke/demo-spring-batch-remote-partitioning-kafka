package com.github.andylke.demo.randomuser;

import javax.batch.runtime.JobInstance;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import com.github.andylke.demo.user.User;

public class RandomUserToUserProcessor implements ItemProcessor<RandomUser, User> {

  private StepExecution stepExecution;

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public User process(RandomUser item) throws Exception {
    final User user = new User();

    user.setUsername(item.getLogin().getUsername());
    user.setPassword(item.getLogin().getPassword());
    user.setName(
        item.getName().getTitle()
            + " "
            + item.getName().getFirst()
            + " "
            + item.getName().getLast());
    user.setEmail(item.getEmail());
    user.setNationality(item.getNat());

    final JobInstance jobInstance = stepExecution.getJobExecution().getJobInstance();
    user.setCreatedBy(jobInstance.getJobName() + "-" + jobInstance.getInstanceId());

    return user;
  }
}
