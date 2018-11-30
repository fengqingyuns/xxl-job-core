package com.example.demo.dao;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.entity.Employee;

public interface BaseRepository extends ElasticsearchRepository<Employee,String>{

}
