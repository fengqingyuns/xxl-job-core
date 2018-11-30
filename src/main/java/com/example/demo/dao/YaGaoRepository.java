package com.example.demo.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.entity.YaGao;

public interface YaGaoRepository extends ElasticsearchRepository<YaGao,String>{
	
}
