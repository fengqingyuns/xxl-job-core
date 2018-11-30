package com.example.demo.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.entity.Post;

public interface PostRepository extends ElasticsearchRepository<Post, String>{

}
