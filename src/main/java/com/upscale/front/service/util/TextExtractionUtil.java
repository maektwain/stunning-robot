package com.upscale.front.service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class TextExtractionUtil {

	public String extractDocumentId(String text, String type){
		
		if(type.equals("PANCARD")){
			String[] data = StringUtils.split(text, "\n");
			return data[6];
		}
		else if(type.equals("VOTERID CARD")){
			String[] data = StringUtils.split(text, "\n");
			return data[2].replaceAll("\\s+", "");
		}
		else
			return "documeny type not found";
	}
	
	public String extractAddress(String text, String type){
		
		if(type.equals("DRIVING LICENSE")){
			String[] data =StringUtils.split(text, "\n");
			return data[10].concat(data[11]);
		}
		else
			return null;
			
	}
	
	public String extractFatherName(String text, String type){
		
		if(type.equals("PANCARD")){
			String[] data =StringUtils.split(text, "\n");
			return data[3];
		}
		else
			return null;
			
	}
	
	public Date extractDOB(String text, String type){
		
		if(type.equals("PANCARD")){
			String[] data =StringUtils.split(text, "\n");
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		    Date dob = new Date();
		    try {
		        dob = df.parse(data[4]);
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
			return dob;
		}
		else
			return null;
			
	}
}
