package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import org.springframework.data.elasticsearch.annotations.FieldType;
/**
 * index相当于数据库的名字，type相当于数据库中的表，Document相当于数据库中的一行
 * indexStoreType：索引文件的存储类型
 * shards：分区数
 * replicas：分区的备份数
 * refreshInterval：刷新的间隔数
 */
@Document(indexName = "megacorp",
		type = "employee",
		shards = 1,
		replicas = 0,
		refreshInterval = "-1")
public class Employee extends SerializableSerializer{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	private String firstName;
	
	private String lastName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
	
	
	
	
}
