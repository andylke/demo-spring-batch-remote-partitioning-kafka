package com.github.andylke.demo.randomuser;

import java.time.Instant;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/" + DownloadImportRandomUserJobConfig.JOB_NAME)
public class DownloadImportRandomUserRestController {

  @Autowired private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(DownloadImportRandomUserJobConfig.JOB_NAME)
  private Job job;

  @PostMapping
  public void run()
      throws JobExecutionAlreadyRunningException, JobRestartException,
          JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    jobLauncher.run(
        job,
        new JobParametersBuilder()
            .addLong("timestamp", Instant.now().toEpochMilli())
            .toJobParameters());
  }
}
