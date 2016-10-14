package com.upscale.front.data;

import java.util.List;

/**
 * 
 * @author Anurag Garg
 *
 */
public class Collateral {

	private Long type;
	
	private String value;
	
	private String description;

	
	/**
	 * 
	 */
	public Collateral() {
		super();
	}

	public Collateral(Long type, String value, String description) {
		super();
		this.type = type;
		this.value = value;
		this.description = description;
	}
	
	public Long getType() {
		return type;
	}

	public void setType(Long collateralTypeId) {
		this.type = collateralTypeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
