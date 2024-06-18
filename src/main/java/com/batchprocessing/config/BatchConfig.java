package com.batchprocessing.config;

import com.batchprocessing.entity.Customer;

import com.batchprocessing.itemprocessor.CustomerItemProcessor;
import com.batchprocessing.itemreader.CustomerItemReader;
import com.batchprocessing.itemwriter.CustomerItemWriter;
import com.batchprocessing.tasklet.DataCleansingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Autowired
    private DataCleansingTasklet dataCleansingTasklet;

    @Autowired
    private DataSource dataSource;


    @Bean
    public Job customerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("customerJob", jobRepository)
                .start(customerStep(jobRepository, transactionManager))
                .next(dataCleansingStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step dataCleansingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dataCleansingStep", jobRepository)
                .tasklet(dataCleansingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step customerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("customerStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(new CustomerItemReader().customerFlatFileItemReader())
                .processor(new CustomerItemProcessor())
                .writer(new CustomerItemWriter(new JdbcTemplate(dataSource)))
                .build();
    }

}

