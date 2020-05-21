package com.song.docsign.util;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpUtil {

	private static final int TIMEOUT = 5000;

    public static HttpResp post(String url, List<NameValuePair> nvps){

        CloseableHttpClient httpClient = getHttpClient(null, null);

        RequestConfig requestConfig = getRequestConfig();

        HttpPost httpPost = new HttpPost(url);

        httpPost.setConfig(requestConfig);
        HttpResp httpResp = new HttpResp();
        try {
            if(nvps != null){
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpResp.setStatus(response.getStatusLine().getStatusCode());
            if (entity != null) {
                httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
            }
        } catch (Exception e) {
            log.error("url->{}",url, e);
        }finally{
            httpPost.releaseConnection();
        }
        //logger.info("[post]url="+url.replaceAll(FA_URL, "")+",resp="+resp);
        return httpResp;
    }
	
	public static HttpResp get(String url, String auth, String password){
        CloseableHttpClient httpClient = getHttpClient(auth, password);
        HttpGet httpGet = new HttpGet(url);  

        return doGet(httpClient, httpGet);
	}

    public static HttpResp get(String url){
        CloseableHttpClient httpClient = getHttpClient("", "");
        HttpGet httpGet = new HttpGet(url);

        return doGet(httpClient, httpGet);
    }

    public static HttpResp post(String url, Map<String,String> params, String auth, String password){
        //logger.info("[post]url="+url.replaceAll(FA_URL, "")+",params="+params);
        CloseableHttpClient httpClient = getHttpClient(auth, password);

        List<NameValuePair> nvps = getNvps(params);
        RequestConfig requestConfig = getRequestConfig();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json");

        httpPost.setConfig(requestConfig);
        HttpResp httpResp = new HttpResp();
        try {
            if(nvps != null){
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpResp.setStatus(response.getStatusLine().getStatusCode());
            if (entity != null) {
                httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
            }

        } catch (Exception e) {
            log.error("url->{}",url, e);
        }finally{
            httpPost.releaseConnection();
        }
        //logger.info("[post]url="+url.replaceAll(FA_URL, "")+",resp="+resp);
        return httpResp;
    }

    public static HttpResp post(String url, String jsonBody, Map<String,String> header){
        //logger.info("[post]url="+url.replaceAll(FA_URL, "")+",params="+params);
        CloseableHttpClient httpClient = getHttpClient(null, null);

        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
        httpPost.setConfig(requestConfig);
        for(Iterator<String> it = header.keySet().iterator(); it.hasNext();){
            String key = it.next();
            httpPost.addHeader(key, header.get(key));
        }

        HttpResp httpResp = new HttpResp();
        int reTryTimes = 3;
        while(reTryTimes >0 ){
            try {
                httpPost.setEntity(new StringEntity(jsonBody, Charset.forName("utf-8")));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                httpResp.setStatus(response.getStatusLine().getStatusCode());
                if (entity != null) {
                    httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
                }
                log.info("[HttpUtil.post发送完成info->{}]",JSON.toJSONString(httpResp));
                return httpResp;
            } catch (Exception e) {
                log.error("[HttpUtil.post:超时重试url->{},param info ->{},retrytimes->{}]", url,JSON.toJSONString(jsonBody), reTryTimes,e);
                reTryTimes --;
            }finally{
                httpPost.releaseConnection();
            }
        }
        //logger.info("[post]url="+url.replaceAll(FA_URL, "")+",resp="+resp);
        return httpResp;
    }

    public static HttpResp post(String url, List<NameValuePair> params, Map<String,String> header){
        CloseableHttpClient httpClient = getHttpClient(null, null);

        RequestConfig requestConfig = getRequestConfig();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        for(Iterator<String> it = header.keySet().iterator(); it.hasNext();){
            String key = it.next();
            httpPost.addHeader(key, header.get(key));
        }

        HttpResp httpResp = new HttpResp();
        int reTryTimes = 3;
        while(reTryTimes >0 ){
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.forName("utf-8")));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                httpResp.setStatus(response.getStatusLine().getStatusCode());
                if (entity != null) {
                    httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
                }
                log.info("[HttpUtil.post发送完成info->{}]",JSON.toJSONString(httpResp));
                return httpResp;
            } catch (Exception e) {
                log.error("[HttpUtil.post:超时重试url->{},param info ->{},retrytimes->{}]", url,JSON.toJSONString(params), reTryTimes,e);
                reTryTimes --;
            }finally{
                httpPost.releaseConnection();
            }
        }
        return httpResp;
    }

    public static HttpResp post(String url, String json, String auth, String password){

        CloseableHttpClient httpClient = getHttpClient(auth, password);

        RequestConfig requestConfig = getRequestConfig();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        HttpResp httpResp = new HttpResp();
        try {

            httpPost.addHeader("Content-Type","application/json");
            httpPost.setEntity(new StringEntity(json, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpResp.setStatus(response.getStatusLine().getStatusCode());
            if (entity != null) {
                httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
            }

        } catch (Exception e) {
            log.error("url->{}",url, e);
        }finally{
            httpPost.releaseConnection();
        }
        return httpResp;
    }

	private static HttpResp doGet(CloseableHttpClient httpClient, HttpGet httpGet){
        RequestConfig requestConfig = getRequestConfig();
        httpGet.setConfig(requestConfig);
        HttpResp httpResp = new HttpResp();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            httpResp.setStatus(response.getStatusLine().getStatusCode());
            if (entity != null) {
                httpResp.setBody(EntityUtils.toString(entity, "UTF-8"));
            }

        } catch (Exception e) {
            log.error("", e);
        }finally{
            httpGet.releaseConnection();
        }
        return httpResp;
    }

    private static CloseableHttpClient getHttpClient(String auth, String password){
        CloseableHttpClient httpClient = null;
        if(!StringUtils.isEmpty(auth)){
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        }else{
            httpClient = HttpClients.custom().build();
        }
        return httpClient;
    }

    private static RequestConfig getRequestConfig(){
        RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .build();
        return requestConfig;
    }

    private static List<NameValuePair> getNvps(Map<String,String> params){
        if(params == null || params.isEmpty()){
            return null;
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
            String key = it.next();
            String value = params.get(key);
            nvps.add(new BasicNameValuePair(key, value));
        }
        return nvps;
    }

    public static String httpPostWithForm(String url,  List<BasicNameValuePair> paramList) {
        HttpPost post = new HttpPost(url);

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
            post.setEntity(entity);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            CloseableHttpResponse response = httpClient.execute(post);

            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();

            String resp = inputStream2String(content);

            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpPatchWithForm(String url,  List<BasicNameValuePair> paramList) {
        HttpPatch patch = new HttpPatch(url);

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
            patch.setEntity(entity);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            CloseableHttpResponse response = httpClient.execute(patch);

            HttpEntity httpEntity = response.getEntity();
            InputStream content = httpEntity.getContent();

            String resp = inputStream2String(content);

            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String inputStream2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}
