/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comm.sr.service.elasticsearch;


import com.comm.sr.common.entity.EsCommonQuery;
import com.comm.sr.service.AbstractQueryService;
import com.comm.sr.service.cache.CacheService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.StopWatch;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author jasstion
 */
public class EsQueryService extends AbstractQueryService<EsCommonQuery> {

    private Properties settings=null;
    private TransportClient client=null;


    public EsQueryService(Properties settings,CacheService<String,String> cacheService) {
        super(cacheService,settings);


            String hosts=settings.getProperty("elasticSearchHosts");
            client=TransportClient.builder().build();

            for(String host:hosts.split(";")){

                try {
                    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host.split(":")[0]), Integer.parseInt(host.split(":")[1])));
                } catch (Exception e) {
                    logger.error("error to get elasticsearch client!");
                }

            }




    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }

    @Override
    public List<Map<String, Object>> processQuery(EsCommonQuery baiheQuery) throws Exception {
        long timeTaken = 0;
        boolean cacheHit = false;
        List<Map<String, Object>> queryResult = null;
        StopWatch stopWatch = new StopWatch();

        // Start the watch, do some task and stop the watch.
        stopWatch.start();

        queryResult = this.queryCache(baiheQuery);
        if (queryResult != null && queryResult.size() > 1) {
            cacheHit = true;
            AbstractQueryService.QueryStatistics queryStatistics = new QueryStatistics(timeTaken, cacheHit);
            stopWatch.stop();
            timeTaken = stopWatch.getTime();
            this.recordQueryLog(queryStatistics);
            return queryResult;
        }
        cacheHit = false;
        queryResult = this.query(baiheQuery);
        stopWatch.stop();
        timeTaken = stopWatch.getTime();

        AbstractQueryService.QueryStatistics queryStatistics = new QueryStatistics(timeTaken, cacheHit);
        this.recordQueryLog(queryStatistics);
        this.cacheQueryResult(baiheQuery, queryResult);

        return queryResult;
    }

    @Override
    public List<Map<String, Object>> query(EsCommonQuery baiheQuery) throws Exception {
        List<Map<String, Object>> results = Lists.newArrayList();
        EsQueryGenerator.EsQueryWrapper  esQueryWrapper=new EsQueryGenerator().generateFinalQuery(baiheQuery);
        SearchResponse searchResponse=client.prepareSearch().setSource(esQueryWrapper.getSearchSourceBuilder().toString()).setIndices(esQueryWrapper.getIndexName())
                .execute().actionGet();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> values = hit.getSource();
            values.put("score",hit.getScore());



            results.add(values);

        }






        return results;

    }

}
