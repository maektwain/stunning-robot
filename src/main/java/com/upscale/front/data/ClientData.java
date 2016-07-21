package com.upscale.front.data;

/**
 * Created by saransh on 20/07/16.
 */
public class ClientData {


    final private Long officeId;

    final private String firstname;

    final private String lastname;

    final private String externalId;

    final private String dateFormat;

    final private String locale;

    final private String active;

    final private String activationDate;

    final private String submittedOnDate;


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






}
