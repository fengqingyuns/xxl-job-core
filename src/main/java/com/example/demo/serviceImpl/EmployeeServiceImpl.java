package com.example.demo.serviceImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.AnyQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.PhraseQueryNodeBuilder;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;

import com.example.demo.dao.EmployeeRepository;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
@Service
public class EmployeeServiceImpl implements EmployeeService{

	
	
	@Autowired
    ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Override
	public Employee queryEmployeeById(String any) {
		// TODO Auto-generated method stub
		Employee temp = null;
		//QueryBuilder qb = new AnyQueryNodeBuilder();
	//	Pageable pageable = new QPageRequest(0, 50);
	//	NativeSearchQueryBuilder  builder = new NativeSearchQueryBuilder().withPageable(pageable);
	//	employeeRepository.search(builder);
		//temp = employeeRepository.search(qb);
		// List<String> newIds = stockCheckTaskDetailList
		//		                 .stream().map(StockCheckTaskDetail::getStockCheckDetailId)
		//                       .collect(Collectors.toList());
		//String queryString = "li";
		QueryStringQueryBuilder builder = new QueryStringQueryBuilder(any);
		Pageable pa = new PageRequest(0, 3);
		NativeSearchQueryBuilder bu = new NativeSearchQueryBuilder().withFilter(builder);
		//QueryStringQueryBuilder builder = new QueryStringQueryBuilder(any);
		
		SearchQuery sq = bu.build();
		AggregatedPage<Employee> page = elasticsearchTemplate.queryForPage(sq,Employee.class);
		String firName = page.stream().filter(employee ->"xia".equals(employee.getFirstName()))
					.map(employee ->employee.getFirstName())
					.collect(Collectors.toList())
					.get(0);
	//	Iterable<Employee> ems = employeeRepository.search(builder, pa);
	//	List<Employee> datas = new ArrayList<Employee>();
	//	Iterator<Employee> iterator = ems.iterator();
	//	while(iterator.hasNext()) {
	//		datas.add(iterator.next());
	//	}
	//	temp = datas.stream().
	//			filter(employee ->"li".equals(employee.getFirstName()))
	//			.collect(Collectors.toList())
	//			.get(0);
		return temp;
	}

	@Override
	public void deleteEmployeeById(String id) {
		// TODO Auto-generated method stub
	Optional<Employee> em = employeeRepository.findById(id);
		if(em == null) {
			return;
		}
		employeeRepository.deleteById(id);
	}

	@Override
	public void addEmployee(Employee em) {
		// TODO Auto-generated method stub
		IndexQuery query = new IndexQueryBuilder().withId(em.getId()).withObject(em).build();
		String index = elasticsearchTemplate.index(query);
		//employeeRepository.save(em).setId("1");
	}

	
}
