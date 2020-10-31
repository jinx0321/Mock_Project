package com.mock.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Component;

@Component
public class FileUtils {
	
	public static String spiltno="\r\n";
	
	
	public static void Writer(String dir,String content,String charset) {
		File file = new File(dir);
		BufferedWriter writer = null;
		FileOutputStream writerStream = null;
		try {
			writerStream = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			writer = new BufferedWriter(new OutputStreamWriter(writerStream, charset));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String Read(String dir,String charset) {
		String content="";
		FileInputStream fis;
		try {
			fis = new FileInputStream(dir);
		
		InputStreamReader isr = new InputStreamReader(fis, charset);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
	
		while ((line = br.readLine()) != null) {
		  content =content+ line;
		  content =content+ spiltno;
		}
		return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
		
	}
	
	public static void mkdir(String dir) {
	    if(new File(dir).exists()) {
	    	
	    }else {
	    	
	    	new File(dir).mkdir();
	    }
	}

}
