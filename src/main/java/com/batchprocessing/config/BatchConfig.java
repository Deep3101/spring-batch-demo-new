package com.batchprocessing.config;

import com.batchprocessing.entity.Customer;
import com.batchprocessing.itemprocessor.CustomItemProcessor;
import com.batchprocessing.itemreader.CustomItemReader;
import com.batchprocessing.itemwriter.CustomItemWriter;
import com.batchprocessing.tasklet.DataCleansingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private DataCleansingTasklet dataCleansingTasklet;


    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("user-records", jobRepository)
                .start(dataCleansingStep(jobRepository, transactionManager))
                .next(step(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step dataCleansingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("data-cleansing-step", jobRepository)
                .tasklet(dataCleansingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("user-step", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(customItemReaderBean())
                .processor(customItemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> customItemReaderBean() {
        return new CustomItemReader().itemReader();
    }

    @Bean
    public CustomItemProcessor customItemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new CustomItemWriter();
    }
}

