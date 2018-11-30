package com.example.demo.controller;

import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.EmployeeRepository;
import com.example.demo.entity.AJAXResult;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;

import io.netty.handler.codec.http.HttpRequest;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService service;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	/**
	 * 模糊检索
	 */
	@RequestMapping(value = "/query",method = {RequestMethod.POST})
	@ResponseBody
	public AJAXResult queryByFirstName(@RequestBody Map<String,Object> params) {
		String any = params.get("any").toString();
		AJAXResult result = new AJAXResult();
		
		Employee em = service.queryEmployeeById(any);
		if(em == null) {
			result.setMsg("employee  ...   null");
		}
		System.err.println("success.......!");
		result.setMsg("success");
		return result;
	}
	@RequestMapping(value = "/add",method = {RequestMethod.POST} )
	@ResponseBody
	public AJAXResult addEmployee(@RequestBody Employee em) {
		AJAXResult result = new AJAXResult();
		
		if(null == em) {
			result.setMsg("em ... null");
			return result;
		}
		service.addEmployee(em);
		result.setMsg("success");
		return result;
	}
	@RequestMapping("/delete")
	public AJAXResult deleteById(@RequestBody String id) {
		AJAXResult result = new AJAXResult();
		Employee em = service.queryEmployeeById(id);
		if(em == null) {
			result.setMsg("success");
			return result;
		}
		service.deleteEmployeeById(id);
		result.setMsg("success");
		return result;
	}
	
	@RequestMapping("/deleteAll")
	public AJAXResult deleteAll() {
		AJAXResult result = new AJAXResult();
		employeeRepository.deleteAll();
		result.setMsg("success");
		return result;
	}
}
