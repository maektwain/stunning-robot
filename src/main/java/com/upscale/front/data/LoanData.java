package com.upscale.front.data;

import java.math.BigDecimal;

public class LoanData {

	private Long clientId;
	
	private Long productId;
	
	private String principal;
	
	//private String downPayment;
	
	private int loanTermFrequency;
	
	private int loanTermFrequencyType;
	
	private String loanType = "individual";
	
	private int numberOfRepayments;
	
	private int repaymentEvery = 1;
	
	private int repaymentFrequencyType = 1;
	
	private String interestRatePerPeriod ;
	
	private int amortizationType = 1;
	
	private int interestType = 1;

	private int interestCalculationPeriodType = 1;
	
	private int transactionProcessingStrategyId = 1;
	
	private String expectedDisbursementDate;
	
	private String submittedOnDate;
	
	private String dateFormat;

    private String locale;

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

	/*public String getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(String downPayment) {
		this.downPayment = downPayment;
	}*/

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
	
	
	
}
