package com.upscale.front.service.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExtractionUtil {

	public String extractDocumentId(String text, String type){

		if(type.equals("PANCARD")){
			Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
			String result = null;
			String[] data = org.apache.commons.lang.StringUtils.split(text, "\n");
			for(String str: data){
				Matcher matcher = pattern.matcher(str);
				if(matcher.matches())
					result = str;
			}
			return result;
		}
		else if(type.equals("VOTERID CARD")){
			Pattern pattern = Pattern.compile("[A-Z]{3}[0-9]{7}");
			String result = null;
			String[] data = org.apache.commons.lang.StringUtils.split(text, "\n");
			for(String str: data){
				Matcher matcher = pattern.matcher(str);
				if(matcher.matches())
					result = str;
			}
			return result;
		}
		else
			return "documeny type not found";
	}

	public String extractAddress(String text, String type){

		if(type.equals("DRIVING LICENSE")){
			String[] data = org.apache.commons.lang.StringUtils.split(text, "\n");
			return data[10].concat(data[11]);
		}
		else
			return null;

	}

	public String extractFatherName(String text, String type){

		if(type.equals("PANCARD")){
			String[] data = org.apache.commons.lang.StringUtils.split(text, "\n");
			return data[3];
		}
		else
			return null;

	}

	public Date extractDOB(String text, String type){

		if(type.equals("PANCARD")){
			Pattern pattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d");
			String result = null;
			String[] data = org.apache.commons.lang.StringUtils.split(text, "\n");
			for(String str: data){
				Matcher matcher = pattern.matcher(str);
				if(matcher.matches())
					result = str;
			}

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		    Date dob = new Date();
		    try {
		        dob = df.parse(result);
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
			return dob;
		}
		else
			return null;

	}
}
