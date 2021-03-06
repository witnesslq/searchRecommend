/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comm.sr.service;


import com.comm.sr.service.elasticsearch.EsQueryService;
import com.comm.sr.common.entity.SearchServiceRule;
import com.comm.sr.service.impl.EsTestSearchService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author jasstion
 */
public class SearchServiceFactory {


    final public static Map<String,BasedSearchService>  srServices= Maps.newHashMap();

    static {
        SearchServiceRule searchServiceRule=new SearchServiceRule();
        Properties settings=new Properties();
        try {
            settings.load(SearchServiceFactory.class.getClassLoader().getResourceAsStream("sr.properties"));
        } catch (IOException e) {
            throw new RuntimeException("error to load sr.properties, exception:"+ ExceptionUtils.getMessage(e.getCause())+"");


        }
        AbstractQueryService searchService=new EsQueryService(settings,null);

        srServices.put("esTest",new EsTestSearchService(searchService,searchServiceRule,settings));
    }

    public static void main(String[] args){
        BasedSearchService searchService=srServices.get("esTest");
        String queryStr=null;
        List<Map<String,Object>> results=(List<Map<String,Object>>)searchService.search(queryStr);







    }




}
