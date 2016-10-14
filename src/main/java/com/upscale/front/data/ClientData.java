package com.upscale.front.data;

/**
 * Created by saransh on 20/07/16.
 */
public class ClientData {


    private Long officeId;

    private String firstname;

    private String lastname;

    private String externalId;

    private String dateFormat;

    private String locale;

    private String active;

    private String activationDate;

    private String submittedOnDate;


    /**
     *
     * @param officeId
     * @param firstname
     * @param lastname
     * @param externalId
     * @param dateFormat
     * @param locale
     * @param active
     * @param activationDate
     * @param submittedOnDate
     *
     * Maybe we need to define custom constructor,
     */


	public ClientData() {
		super();
	}

    public ClientData(final Long officeId,final String firstname,final String lastname, final String externalId,final String dateFormat, final String locale,
                                final String active, final String activationDate, final String submittedOnDate){

        this.officeId = officeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.externalId = externalId;
        this.dateFormat = dateFormat;
        this.locale = locale;
        this.active = active;
        this.activationDate = activationDate;
        this.submittedOnDate = submittedOnDate;

    }

	public Long getOfficeId() {
		return officeId;
	}


	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getExternalId() {
		return externalId;
	}


	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}


	public String getDateFormat() {
		return dateFormat;
	}


	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}


	public String getLocale() {
		return locale;
	}


	public void setLocale(String locale) {
		this.locale = locale;
	}


	public String getActive() {
		return active;
	}


	public void setActive(String active) {
		this.active = active;
	}


	public String getActivationDate() {
		return activationDate;
	}


	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}


	public String getSubmittedOnDate() {
		return submittedOnDate;
	}


	public void setSubmittedOnDate(String submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}






}
