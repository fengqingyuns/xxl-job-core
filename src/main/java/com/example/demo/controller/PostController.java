package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.util.packed.PagedMutable;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalMappedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import com.example.demo.entity.Post;

@RestController
@RequestMapping("/post")
public class PostController {

	@Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
	
	/**1单字符串全文查询
     * 单字符串模糊查询，默认排序。将从所有字段中查找包含传来的word分词后字符串的数据集
     */
    @RequestMapping(value="/singleWord")
    public Object singleTitle(@RequestBody Map<String,Object> params, @PageableDefault(size=20) Pageable pageable) {
        //使用queryStringQuery完成单字符串查询
    	String word = params.get("word").toString();
    	
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new QueryStringQueryBuilder(word)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**1单字符串全文查询
     * 单字符串模糊查询，单字段排序。
     */
    @RequestMapping("/singleWord1")
    public Object singlePost(@RequestBody Map<String,Object> params,@PageableDefault(size=20,sort = "weight", direction = Sort.Direction.DESC) Pageable pageable) {
        //使用queryStringQuery完成单字符串查询
    	String word = params.get("word").toString();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
        		.withQuery(new QueryStringQueryBuilder(word))
        		.withPageable(pageable)
        		.build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**2某字段按字符串模糊查询
     * 查询某个字段中模糊包含目标字符串，使用matchQuery
     * 单字段对某字符串模糊查询
     */
    @RequestMapping("/singleMatch")
    public Object singleMatch(@RequestBody Map<String,Object> params, @PageableDefault(size=20) Pageable pageable) {
    	String content = params.get("word").toString();
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MatchQueryBuilder("content", content)).withPageable(pageable).build();
//        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("userId", userId)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**3 PhraseMatch查询，短语匹配
     * 单字段对某短语进行匹配查询，短语分词的顺序会影响结果
     * 和match查询类似，match_phrase查询首先解析查询字符串来产生一个词条列表。然后会搜索所有的词条，但只保留包含了所有搜索词条的文档，并且词条的位置要邻接。一个针对短语“中华共和国”的查询不会匹配“中华人民共和国”，因为没有含有邻接在一起的“中华”和“共和国”词条。
	 *	这种完全匹配比较严格，类似于数据库里的“%落日熔金%”这种，使用场景比较狭窄。如果我们希望能不那么严格，譬如搜索“中华共和国”，希望带“我爱中华人民共和国”的也能出来，就是分词后，中间能间隔几个位置的也能查出来，可以使用slop参数。
	 *	我们先来添加一个类似的数据
	 *	--------------------- 
	 *	作者：天涯泪小武 
	 *	来源：CSDN 
	 *	原文：https://blog.csdn.net/tianyaleixiaowu/article/details/77965257 
	 *	版权声明：本文为博主原创文章，转载请附上博文链接！
     */
    @RequestMapping("/singlePhraseMatch")
    public Object singlePhraseMatch(@RequestBody Map<String,Object> params, @PageableDefault(size=20) Pageable pageable) {
    	String content = params.get("word").toString();
    	SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MatchPhraseQueryBuilder("content", content)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    @RequestMapping("/add")
    public Object add() {
        Post post = new Post();
        post.setTitle("我是");
        post.setContent("我爱中华人民共和国");
        post.setWeight(1);
        post.setUserId(1);
        IndexQuery query = new IndexQueryBuilder().withObject(post).build();
		String index = elasticsearchTemplate.index(query);
		
        post = new Post();
        post.setTitle("我是");
        post.setContent("中华共和国");
        post.setWeight(2);
        post.setUserId(2);
        IndexQuery query1 = new IndexQueryBuilder().withObject(post).build();
        return elasticsearchTemplate.index(query1);
    }
    /*
     * slop参数告诉match_phrase查询词条能够相隔多远时仍然将文档视为匹配。相隔多远的意思是，你需要移动一个词条多少次来让查询和文档匹配？
     */
    @RequestMapping("/singlePhraseMatch1")
    public Object singlePhraseMatch1(@RequestBody Map<String,Object> params, @PageableDefault Pageable pageable) {
        //SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("content", content)).withPageable(pageable).build();
        //少匹配一个分词也OK、
    	String content = params.get("word").toString();
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MatchPhraseQueryBuilder("content", content).slop(2)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**这个是最严格的匹配，属于低级查询，不进行分词的，参考这篇文章http://www.cnblogs.com/muniaofeiyu/p/5616316.html
     * term匹配，即不分词匹配，你传来什么值就会拿你传的值去做完全匹配
     */
    @RequestMapping("/singleTerm")
    public Object singleTerm(@RequestBody Map<String,Object> params, @PageableDefault Pageable pageable) {
        //不对传来的值分词，去找完全匹配的
    	int userId = (int) params.get("word");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new TermQueryBuilder("userId", userId)).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    /*
     * 5 multi_match多个字段匹配某字符串
     * 如果我们希望title，content两个字段去匹配某个字符串，只要任何一个字段包括该字符串即可，就可以使用multimatch。
     */
    /**
     * 多字段匹配
     */
    @RequestMapping("/multiMatch")
    public Object singleUserId(@RequestBody Map<String,Object> params, @PageableDefault(sort = "weight", direction = Sort.Direction.DESC) Pageable pageable) {
        String title = params.get("word").toString();
    	SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MultiMatchQueryBuilder(title, "title", "content")).withPageable(pageable).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**
     * 单字段包含所有输入
     * 之前的查询中，当我们输入“我天”时，ES会把分词后所有包含“我”和“天”的都查询出来
     * ，(如果输入“声声” 则包含一个声就可以查出来)如果我们希望必须是包含了两个字的才能被查询出来，那么我们就需要设置一下Operator。
     */
    @RequestMapping("/contain")
    public Object contain(@RequestBody Map<String,Object> params) {
    	String title = params.get("word").toString();
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MatchQueryBuilder("title", title).operator(Operator.AND)).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    /**
     * 单字段包含所有输入(按比例包含)
     * 无论是matchQuery，multiMatchQuery，queryStringQuery等，都可以设置operator。默认为Or,设置为And后，就会把符合包含所有输入的才查出来。
	 *	如果是and的话，譬如用户输入了5个词，但包含了4个，也是显示不出来的。我们可以通过设置精度来控制。
     */
    @RequestMapping("/contain1")
    public Object contain1(@RequestBody Map<String,Object> params) {
    	String title = params.get("word").toString();
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(new MatchQueryBuilder("title", title).operator(Operator.AND).minimumShouldMatch("75%")).build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    
    /**
     * 合并查询即boolQuery，可以设置多个条件的查询方式。它的作用是用来组合多个Query，有四种方式来组合，must，mustnot，filter，should。
	 *	must代表返回的文档必须满足must子句的条件，会参与计算分值；
	 *	filter代表返回的文档必须满足filter子句的条件，但不会参与计算分值；
	 *	should代表返回的文档可能满足should子句的条件，也可能不满足，有多个should时满足任何一个就可以，通过minimum_should_match设置至少满足几个。
	 *	mustnot代表必须不满足子句的条件。
	 *	譬如我想查询title包含“XXX”，且userId=“1”，且weight最好小于5的结果。那么就可以使用boolQuery来组合。
     */
    /**
     * 多字段合并查询
     */
    @RequestMapping(value="/bool",method=RequestMethod.GET)
    public Object bool(String title, Integer userId, Integer weight) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
        		.withQuery(new BoolQueryBuilder()
        				.must(new TermQueryBuilder("userId", userId))
                .should(new RangeQueryBuilder("weight")
                		.lt(weight)).must(new MatchQueryBuilder("title", title)))
        		.build();
        return elasticsearchTemplate.queryForList(searchQuery, Post.class);
    }
    /**
     * field 字段需要.keyword不然查不出来
     * @param word
     * @param tags
     * @return
     */
    @RequestMapping("/yagao")
    public Map getMap(@RequestParam("word") String word,@RequestParam("tags")String tags) {
    	Map<String, Object> map = new HashMap<String,Object>();//
    	 //分组
    	 TermsAggregationBuilder tab = AggregationBuilders
    			 .terms("nums")
    			 .field("sex.keyword");
    	 //最小值
    	 MinAggregationBuilder minAggregation = AggregationBuilders
    		     .min("nums")
    		     .field(tags);
    	 //最大值
    	 MaxAggregationBuilder maxAggregation = AggregationBuilders
    			 .max("nums")
    			 .field(tags);
    	 //求和
    	 SumAggregationBuilder sumAggregation = AggregationBuilders
    			 .sum("nums")
    			 .field(tags);
    	 //平均数
    	 AvgAggregationBuilder avgAggregation = AggregationBuilders
    		     .avg("nums")
    		     .field(tags);
    	 //统计聚合，即一次性获取最小值、最小值、平均值、求和、统计聚合的集合。
    	 StatsAggregationBuilder statsAggregation = AggregationBuilders
    		     .stats("nums")
    		     .field(tags);
    	 //查询分组的top
    	 /*
    	  * from：最匹配的结果中的文档个数偏移
		  * size：top matching hits 返回的最大文档个数（default 3）
		  *	sort：最匹配的文档的排序方式
    	  */
    	 AggregationBuilder topAggregation = AggregationBuilders
    			 .terms("nums").field(tags+".keyword")
    			 .subAggregation(
    					 AggregationBuilders
    					 	.topHits("top")
    					 	.explain(true)
    					 	.size(2)
    					 	.from(8)
    			 );
    			
    	SearchQuery searchQuery = new NativeSearchQueryBuilder()
    			.withQuery(new MatchQueryBuilder("content",word))
    			.addAggregation((AbstractAggregationBuilder) topAggregation)
                .build();
    	Aggregations aggregations = elasticsearchTemplate.query(searchQuery,new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				// TODO Auto-generated method 
				return response.getAggregations();
			}
		});
    	/*Stats sum = aggregations.get("nums");
    	double avg = sum.getAvg();*/
    	
    	Terms  terms = (Terms) aggregations.asMap().get("nums");
    	List<? extends Bucket> buckets = terms.getBuckets();
    	 for (Terms.Bucket actionTypeBucket : terms.getBuckets()) {
             //actionTypeBucket.getKey().toString()聚合字段的相应名称,actionTypeBucket.getDocCount()相应聚合结果
             map.put(actionTypeBucket.getKey().toString(),
                     actionTypeBucket.getDocCount());
         }
    	
    	return map;
    }
}
