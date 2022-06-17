package com.github.andylke.demo.randomuser;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class DownloadRandomUserPartitioner implements Partitioner {

  private final int totalPage;

  public DownloadRandomUserPartitioner(int totalPage) {
    this.totalPage = totalPage;
  }

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    final Map<String, ExecutionContext> partitions = new HashMap<>();
    int pageSizePerPartition = (int) Math.floor(totalPage / gridSize);

    int totalPagePerPartition = totalPage;
    int partitionNumber = 1;
    do {
      final ExecutionContext executionContext = new ExecutionContext();
      executionContext.put("partitionNumber", partitionNumber);
      executionContext.put(
          "startPagePerPartition", (partitionNumber - 1) * pageSizePerPartition + 1);
      executionContext.put(
          "totalPagePerPartition",
          totalPagePerPartition <= pageSizePerPartition
              ? totalPagePerPartition
              : pageSizePerPartition);
      partitions.put("partition-" + partitionNumber++, executionContext);

      if (totalPagePerPartition <= pageSizePerPartition) {
        break;
      }
      totalPagePerPartition -= pageSizePerPartition;
    } while (true);

    return partitions;
  }
}
