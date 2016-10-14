package com.upscale.front.data;

import java.util.List;

/**
 * 
 * @author Anurag Garg
 *
 */
import java.math.BigDecimal;

public class LoanData {

	private Long clientId;
	
	private Long productId;
	
	private String principal;
	
	/**
	 * Length of loan term
	 */
	private int loanTermFrequency;
	
	/**
	 * LoanTermFrequencyType
	 * 
	 * Values: 
	 * 		days = 0
	 * 		weeks = 1
	 * 		months = 2
	 * 		years = 3
	 */
	private int loanTermFrequencyType = 2;
		
	private String loanType = "individual";
	
	private int numberOfRepayments;
	
	/**
	 * repayment every repaymentFrequencyType
	 */
	private int repaymentEvery = 1;
	
	/**
	 * repaymentFrequencyType
	 * 
	 * Values: 
	 * 		days = 0
	 * 		weeks = 1
	 * 		months = 2
	 */
	private int repaymentFrequencyType = 2;
	
	private String interestRatePerPeriod ;
	
	/**
	 * amortizationType
	 * 
	 * Values: 
	 * 		0 = Equal principle payments
	 * 	 	1 = Equal installments
	 */
	private int amortizationType = 1;
	
	/**
	 * interestType
	 * 
	 * values:
	 * 		declining balance = 0
	 * 		flat = 1
	 */
	private int interestType = 1;

	/**
	 * interestCalculationPeriodType
	 * 
	 * Values: 
	 * 		0 = Daily
	 * 	    1 = Same as repayment period
	 */
	private int interestCalculationPeriodType = 1;
	
	/**
	 * transactionProcessingStrategyId 
	 * 
	 * An enumeration that indicates the type of transaction processing strategy to be used. 
	 * This relates to functionality that is also known as Payment Application Logic.
	 * 
	 * List of current approaches:
			1 = Mifos style (Similar to Old Mifos)
			2 = Heavensfamily (Custom MFI approach)
			3 = Creocore (Custom MFI approach)
			4 = RBI (India)
			5 = Principal Interest Penalties Fees Order
			6 = Interest Principal Penalties Fees Order
			7 = Early Payment Strategy
	 */
	private int transactionProcessingStrategyId = 1;
	
	private String expectedDisbursementDate;
	
	private String submittedOnDate;
	
	private String dateFormat = "dd MMMM yyyy";

    private String locale = "en_GB";
    
    private List<Collateral> collateral;

    
	/**
	 * 
	 */
	public LoanData() {
		super();
	}

	public LoanData(Long clientId, Long productId, String principal, int loanTermFrequency, int loanTermFrequencyType,
			String loanType, int numberOfRepayments, int repaymentEvery, int repaymentFrequencyType,
			String interestRatePerPeriod, int amortizationType, int interestType, int interestCalculationPeriodType,
			int transactionProcessingStrategyId, String expectedDisbursementDate, String submittedOnDate,
			String dateFormat, String locale, List<Collateral> collateral) {
		super();
		this.clientId = clientId;
		this.productId = productId;
		this.principal = principal;
		this.loanTermFrequency = loanTermFrequency;
		this.loanTermFrequencyType = loanTermFrequencyType;
		this.loanType = loanType;
		this.numberOfRepayments = numberOfRepayments;
		this.repaymentEvery = repaymentEvery;
		this.repaymentFrequencyType = repaymentFrequencyType;
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.amortizationType = amortizationType;
		this.interestType = interestType;
		this.interestCalculationPeriodType = interestCalculationPeriodType;
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
		this.expectedDisbursementDate = expectedDisbursementDate;
		this.submittedOnDate = submittedOnDate;
		this.dateFormat = dateFormat;
		this.locale = locale;
		this.collateral = collateral;
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

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public int getLoanTermFrequency() {
		return loanTermFrequency;
	}

	public void setLoanTermFrequency(int loanTermFrequency) {
		this.loanTermFrequency = loanTermFrequency;
	}

	public int getLoanTermFrequencyType() {
		return loanTermFrequencyType;
	}

	public void setLoanTermFrequencyType(int loanTermFrequencyType) {
		this.loanTermFrequencyType = loanTermFrequencyType;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public int getNumberOfRepayments() {
		return numberOfRepayments;
	}

	public void setNumberOfRepayments(int numberOfRepayments) {
		this.numberOfRepayments = numberOfRepayments;
	}

	public int getRepaymentEvery() {
		return repaymentEvery;
	}

	public void setRepaymentEvery(int repaymentEvery) {
		this.repaymentEvery = repaymentEvery;
	}

	public int getRepaymentFrequencyType() {
		return repaymentFrequencyType;
	}

	public void setRepaymentFrequencyType(int repaymentFrequencyType) {
		this.repaymentFrequencyType = repaymentFrequencyType;
	}

	public String getInterestRatePerPeriod() {
		return interestRatePerPeriod;
	}

	public void setInterestRatePerPeriod(String interestRatePerPeriod) {
		this.interestRatePerPeriod = interestRatePerPeriod;
	}

	public int getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(int amortizationType) {
		this.amortizationType = amortizationType;
	}

	public int getInterestType() {
		return interestType;
	}

	public void setInterestType(int interestType) {
		this.interestType = interestType;
	}

	public int getInterestCalculationPeriodType() {
		return interestCalculationPeriodType;
	}

	public void setInterestCalculationPeriodType(int interestCalculationPeriodType) {
		this.interestCalculationPeriodType = interestCalculationPeriodType;
	}

	public int getTransactionProcessingStrategyId() {
		return transactionProcessingStrategyId;
	}

	public void setTransactionProcessingStrategyId(int transactionProcessingStrategyId) {
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
	}

	public String getExpectedDisbursementDate() {
		return expectedDisbursementDate;
	}

	public void setExpectedDisbursementDate(String expectedDisbursementDate) {
		this.expectedDisbursementDate = expectedDisbursementDate;
	}

	public String getSubmittedOnDate() {
		return submittedOnDate;
	}

	public void setSubmittedOnDate(String submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}

	public List<Collateral> getCollateral() {
		return collateral;
	}

	public void setCollateral(List<Collateral> collateral) {
		this.collateral = collateral;
	}
}
