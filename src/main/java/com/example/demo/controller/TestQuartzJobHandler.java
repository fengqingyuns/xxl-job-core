package com.example.demo.controller;

import java.util.Date;

import com.example.demo.util.DateCalcUtil;
import com.example.demo.util.DateFormatUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
@JobHander(value="testQuartzJobHandler")
@Component
public class TestQuartzJobHandler extends IJobHandler{

	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		// TODO Auto-generated method stub
		Date date = null;
		String name = null;
		if(params != null){
            if(params.length >= 1){
                date = DateFormatUtil.parseDateNoSep(params[0]);
            }
            
            if(params.length >= 2){
                if(StringUtils.isNotBlank(params[1])){
                    name = params[1].trim();
                }
            }
        }
        if(date == null){
            date = DateCalcUtil.getPreDay(new Date());
        }
     //   Object result = settleBillGenService.genSettleBill(date, name);
        
     //   XxlJobLogger.log(JsonUtil.toJson(result));
        
      /*  if(! result.isSuccess()){
            return ReturnT.FAIL;
        }*/
        
        return ReturnT.SUCCESS;
	}

}
