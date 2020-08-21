package com.mock.Service.FilterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.mock.Service.CacheService.CacheRetrieve;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mock.Bean.Data.RequestData;
import com.mock.Bean.Data.RootData;
import com.mock.Bean.Data.UrlData;
import com.mock.Service.CacheService.CacheOp;
import com.mock.Utils.ControlUtils.RequestUtils;
import com.mock.Utils.ControlUtils.UrlUtils;

/**
 * 跳转服务,来判断时重定向还是跳转
 * @author jinxh29224
 *
 */
@Service
public class JumpService {
	
	@Autowired
	UrlUtils UrlUtils;
	
	@Autowired
	@Qualifier("CacheOpImpl")
	CacheOp<RootData> CacheOp;
	
	@Autowired
	RequestUtils RequestUtils;
	
	@Autowired
	CacheRetrieve CacheRetrieve;
	
	/**
	 * 跳转到本地url去获取mock数据
	 * @param httpRequest
	 * @param httpResponse
	 */
	public void forward(HttpServletRequest httpRequest,HttpServletResponse httpresponse) {
		 try { 
		     JSONObject requestdata=GetRequestData(httpRequest);
		     System.out.println(requestdata.toJSONString());
		     UrlData urldata=CacheRetrieve.GetUrlData(requestdata.getString("ReqUrl"));
			 if(urldata!=null) {
				 //若url存在则进行param对比
				 RequestData reqdata=CacheRetrieve.GetReqData(requestdata.getString("ReqUrl"), requestdata);
				 //如果搜索到缓存中的reqdata
				 if(reqdata!=null) {
					 if(reqdata.getIs_Forward().equals("true")) {
						 InjectResponse(reqdata.getData(),httpresponse);
					 }else{
						 HttpProxyRequest(); 
					 }
					 
				 }else{
					 InjectResponse(urldata.getData(),httpresponse);
				 }
			 }else {
					InjectResponse("URL不存在",httpresponse);
				 HttpProxyRequest();
			 }
			
			//httpRequest.getRequestDispatcher("/mock/data?data="+UrlUtils.UrlParserBefore(httpRequest.getRequestURI())).forward(httpRequest,httpresponse);
		} catch (Exception e) {
			InjectResponse("SERVER SRROR",httpresponse);
			e.printStackTrace();
		} 
	}
	
	/**
	 * 请求数据提取
	 * @throws IOException 
	 */
	private JSONObject GetRequestData(HttpServletRequest httpRequest) throws IOException {
		
		JSONObject json=new JSONObject();
		//取出url
		json.put("ReqUrl", httpRequest.getRequestURI());
		//取出方法
		json.put("Method", httpRequest.getMethod());
		//取出头
		json.put("Header", new JSONArray());
		Enumeration<String> element = httpRequest.getHeaderNames();
		JSONArray headers=json.getJSONArray("Header");
		while(element.hasMoreElements()) {
		    String name = element.nextElement();
			JSONObject h=new JSONObject();
			h.put("name", name);
			h.put("value", httpRequest.getHeader(name));
			headers.add(h);
		}
		System.out.println( httpRequest.getCharacterEncoding());
		
		//取出body编码
		json.put("CharacterEncoding", httpRequest.getCharacterEncoding());
		//body数据备份
		byte[] reqdata=RequestUtils.getRequestPostBytes(httpRequest);
		//取出body
	    json.put("Body", new String(reqdata!=null?reqdata:"".getBytes(), httpRequest.getCharacterEncoding()!=null? httpRequest.getCharacterEncoding():"UTF-8") ); 
	    //设置bodyparam
	    json.put("BodyParam", RequestUtils.toJsonObject_byte(reqdata, httpRequest.getCharacterEncoding()!=null? httpRequest.getCharacterEncoding():"UTF-8").toJSONString()); 
	   

	    return json;
	}

	/**
	 * http代理请求
	 */
	private void HttpProxyRequest() {
		
	}

	/**
	 * 返回数据注入
	 */
	private void InjectResponse(String returndata,HttpServletResponse httpresponse){
		try {
			httpresponse.getOutputStream().write(returndata.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 放行
	 * @param httpRequest
	 * @param httpResponse
	 * @param chain
	 */
	public void release(ServletRequest httpRequest, ServletResponse httpResponse,FilterChain chain) {
		try {
			chain.doFilter(httpRequest, httpResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
