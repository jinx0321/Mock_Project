package com.mock.Service.CacheService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mock.Bean.Data.RequestData;
import com.mock.Bean.Data.RootData;
import com.mock.Bean.Data.UrlData;

/**
 * 缓存检索
 * @author jinxh29224
 *
 */
@Service
public class CacheRetrieve {
	@Autowired
	@Qualifier("CacheOpImpl")
	CacheOp<RootData> CacheOp;
	
	private UrlData urldata=null;
	public synchronized UrlData GetUrlData(String url){
		CacheOp.GetCache().getUrldata().forEach(l->{
			if(l.getUrl().equals(url)) {
				urldata=l;
			}
		});
		return urldata;
	}

	private RequestData reqdata; 
	//每次对应上就打卡true,没对对应上就打卡false
	private boolean is_flag=false;
	private JSONObject json;
	public synchronized RequestData GetReqData(String url,JSONObject request){
		json=JSON.parseObject(request.getString("BodyParam"));
		reqdata=null;
		CacheOp.GetCache().getUrldata().forEach(l->{
			if(l.getUrl().equals(url)) {
				l.getRequestData().forEach(r->{
					 is_flag=false;
					//参数实例化,第一次进来会缓存参数
					if(r.RequireParamObject().equals(null)){
						r.InjectParamObject(ReqParamParser(r.getParam()));
					}
					if(r.getIs_Disable().equals("false")) {
					List<Map<String,String[]>> params=r.RequireParamObject();
					params.forEach(p->{
						String[] key=p.get("key");
						StringBuffer value=new StringBuffer((p.get("value")[0]));
						StringBuffer jsonvalue=new StringBuffer("");
						JSONObject itorjson=new JSONObject();
						for(int i=0;i<key.length;i++) {
							if(json.containsKey(key[i])){
								itorjson=json.getJSONObject(key[i]);
								json=itorjson;
							}
						}
						if(itorjson.containsKey(key[key.length-1])) {
							if(String.valueOf(itorjson.get(key[key.length-1])).equals(value)){
								is_flag=true;
							}else {
								is_flag=false;
							}
						}else {
							is_flag=false;
						}
						
					});
					if(is_flag) {
						reqdata=r;
					}
					}
				});
			}
		});
		return reqdata;
	}
	
	/**
	 * requestdata参数解析
	 * 
	 * {
	 *   key:String[]
	 *   value:String[]
	 * }
	 */
	private List<Map<String, String[]>> ReqParamParser(String param){
		List<Map<String, String[]>> list=new LinkedList<Map<String,String[]>>();
		Map<String,String[]> map=new HashMap<String,String[]>();
		JSONObject json=new JSONObject();
		String[] params=param.split(";");
		for(int i=0;i<params.length;i++) {
			String[] p=params[i].split("=");
			StringBuffer paramkey=new StringBuffer(p[0]);
			StringBuffer paramvalue = new StringBuffer("");
			if(p.length==1) {
				paramvalue=new StringBuffer("");
			}else if(p.length==2) {
				paramvalue=new StringBuffer(p[1]);
			}else {
				for(int k=1;k<p.length;k++) {
					paramvalue.append(p[k]);
				}
			}
			String[] paramsubkeys=paramkey.toString().split("\\.");
			String[] paramssubvalues= {paramvalue.toString()};
			map.put("key", paramsubkeys);
			map.put("value", paramssubvalues);
			list.add(map);
			
		}
		return list;
	}
	
	@Test
	public void test() {
		String x= "a.b.c.d=3";
		String[] xs=x.split("\\.");
		for(int i=0;i<xs.length;i++) {
			System.out.println(xs[i]);
		}
	}
	
}
