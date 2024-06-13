package com.batchprocessing.itemreader;

import com.batchprocessing.entity.Customer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CustomItemReader {

    @Bean
    public FlatFileItemReader<Customer> itemReader() {
       FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
       reader.setResource(new ClassPathResource("myrecords.csv"));
       reader.setName("csv-reader");
       reader.setLinesToSkip(1);   //skipping header line
       reader.setLineMapper(lineMapper()); //take each line and convert that to java object
       return reader;
   }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>(); //to map lines from csv to Customer objects

        //tokenizing data using comma as delimiters
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); //map tokenize field to the properties of customer class
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);  //if one column data value unavailable consider it as null
        lineTokenizer.setNames("id", "firstname", "lastname", "email", "profession");

        //to convert data to java object
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

}
