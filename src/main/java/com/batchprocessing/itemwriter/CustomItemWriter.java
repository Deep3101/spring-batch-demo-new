package com.batchprocessing.itemwriter;

import com.batchprocessing.entity.Customer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class CustomItemWriter implements ItemWriter<Customer> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private boolean tableExists = false;


    private void createTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS customer (" +
                "id INT," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255)," +
                "email VARCHAR(255)," +
                "profession VARCHAR(255))";
        jdbcTemplate.execute(createTableSql);
    }

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        if (!tableExists) {
            createTable();
            tableExists = true;
        }
        for (Customer customer : chunk) {
            String sql = "INSERT INTO customer (id, first_name, last_name, email, profession) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getProfession());
        }
    }
}
