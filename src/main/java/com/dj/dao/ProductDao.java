package com.dj.dao;

import com.dj.domain.Product;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;

public class ProductDao extends JdbcTemplate {

    public ProductDao() {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            dataSource.setJdbcUrl("jdbc:mysql://192.168.25.135:3306/spider?characterEncoding=utf-8");
            dataSource.setUser("root");
            dataSource.setPassword("root");
            setDataSource(dataSource);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }



    public void saveProduct(Product product)
    {
        String sql = "insert into jd_product(id,name,title,type,band) values (?,?,?,?,?);";
        update(sql,product.getId(),product.getName(),product.getTitle(),product.getType(),product.getBand());
    }
}
