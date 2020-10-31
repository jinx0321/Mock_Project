package com.mock.Scheduler.HostTask;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.mock.Bean.Data.EnvVar;
import com.mock.Bean.Proxy.Proxy;
import com.mock.Service.CacheService.CacheOp;

//@Configuration      //1.主要用于标记配置类，兼备Component的效果。
//@EnableScheduling
public class HostService {
	private final String windows_host_dir="C:\\Windows\\System32\\drivers\\etc\\hosts";
	private final String linux_host_dir="/etc/hosts";
	
	@Autowired
	com.mock.Utils.FileUtils FileUtils;
	
	@Autowired
	@Qualifier("CacheOpImpl_Env")
	CacheOp<EnvVar> CacheOp_Env;
	
	public static void main(String[] args){
		System.out.println(System.getProperty("os.name"));
	}
	
	
	StringBuffer hosts;
	@Scheduled(cron = "0/30 * * * * ?")
	public void HostInject(){
		System.out.println("绑定Host定时任务开始————————----");
		if(getOsHostdir()!=null){
			List<Proxy> listp=CacheOp_Env.GetCache().getProxylist();
			hosts=new StringBuffer(readHost());
			listp.forEach(e->{
				Object x=e.getIs_proxy().equals("true")?hosts.append(FileUtils.spiltno+e.getIp().trim()+" "+e.getDns().trim()):"";
				if(e.getIs_proxy().equals("false")) {
					StringBuffer copy=new StringBuffer(hosts.toString());
					hosts=new StringBuffer(copy.toString().replace(FileUtils.spiltno+e.getIp().trim()+" "+e.getDns().trim(),""));
				}
			});
			
			FileUtils.Writer(getOsHostdir(), hosts.toString(), "UTF-8");
		}	
	}
	
	
   private String readHost(){
	   return FileUtils.Read(getOsHostdir(), "UTF-8");
   }	
	
   private void UpdateHost(){
	   
	   
   }
	private String getOsHostdir() {
		if(getOsType().toLowerCase().contains("windows")) {
			return windows_host_dir;
		}else if(getOsType().toLowerCase().contains("linux")) {
			return linux_host_dir;
		}
		return null;
	}
	
	private String getOsType() {
		return System.getProperty("os.name");
	}

}
