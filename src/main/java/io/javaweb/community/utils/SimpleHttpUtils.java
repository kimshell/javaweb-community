package io.javaweb.community.utils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 不依赖于任何第三方的HTTP工具类
 * Created by KevinBlandy on 2017/8/18 10:15
 */
public class SimpleHttpUtils {
	
	public static final HttpHeader APPLICATION_JSON = HttpHeader.of("contentType", "application/json");
	
	public static final HttpHeader TEXT_PLAIN = HttpHeader.of("contentType", "text/plain ");
	
	//默认编码
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	//默认连接超时 5s
	private static final Integer DETAUL_CONNECT_TIME_OUT = 3000;
	
	//默认读取超时 5s
	private static final Integer DETAUL_READ_TIME_OUT = 3000;
	
	/**
	 * GET请求,返回响应字符串
	 * @param url
	 * @param httpHeaders
	 * @return
	 * @throws IOException
	 */
	public static String getString(String url,HttpHeader... httpHeaders) throws IOException {
		return getString(url,DETAUL_CONNECT_TIME_OUT,DETAUL_READ_TIME_OUT,httpHeaders);
	}

	/**
	 * GET请求,返回响应字符串
	 * @param url
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @param httpHeaders
	 * @return
	 * @throws IOException
	 */
    public static String getString(String url,Integer connectTimeOut,Integer readTimeOut,HttpHeader... httpHeaders) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(getInputStream(url,connectTimeOut,readTimeOut,httpHeaders))){
        	byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1){
                stringBuilder.append(new String(buffer,0,len, DEFAULT_CHARSET));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * GET请求,返回InputStream流
     * @param url
     * @param connectTimeOut
     * @param readTimeOut
     * @param httpHeaders
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String url, Integer connectTimeOut, Integer readTimeOut,HttpHeader... httpHeaders) throws IOException {
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.setConnectTimeout(connectTimeOut);
        urlConnection.setReadTimeout(readTimeOut);
        if(!GeneralUtils.isEmpty(httpHeaders)){
            for(HttpHeader httpHeader : httpHeaders){
                urlConnection.addRequestProperty(httpHeader.getName(), httpHeader.getValue());
            }
        }
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
    
    /**
     * POST请求,返回响应字符串
     * @param url
     * @param requestBody
     * @param httpHeaders
     * @return
     * @throws IOException
     */
    public static String post(String url,String requestBody,HttpHeader... httpHeaders) throws IOException {
    	return post(url,requestBody,DETAUL_CONNECT_TIME_OUT,DETAUL_READ_TIME_OUT,httpHeaders);
    }
    
    /**
     * POST请求,返回响应字符串
     * @param url
     * @param requestBody
     * @param connectTimeOut
     * @param readTimeOut
     * @param httpHeaders
     * @return
     * @throws IOException
     */
    public static String post(String url, String requestBody,Integer connectTimeOut, Integer readTimeOut,HttpHeader... httpHeaders) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL address = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(connectTimeOut);
        connection.setReadTimeout(readTimeOut);
        if(!GeneralUtils.isEmpty(httpHeaders)){
            for(HttpHeader httpHeader : httpHeaders){
                connection.addRequestProperty(httpHeader.getName(), httpHeader.getValue());
            }
        }
        connection.setRequestMethod("POST");
        connection.connect();
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),DEFAULT_CHARSET))){
            writer.write(requestBody);
            writer.flush();
        }
        if(connection.getResponseCode() == 200){
        	try(BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream())){
        		 byte[] buffer = new byte[1024];
                 int len = 0;
                 while ((len = bufferedInputStream.read(buffer)) != -1){
                     stringBuilder.append(new String(buffer,0,len, DEFAULT_CHARSET));
                 }
        	}
        }
        return stringBuilder.toString();
    }

    public static class HttpHeader {

        private String name;

        private String value;

        public HttpHeader(){}

        public HttpHeader(String name,String value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static HttpHeader of(String name,String value){
            return new HttpHeader(name,value);
        }
    }
}
