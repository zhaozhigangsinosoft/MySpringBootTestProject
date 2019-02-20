package cn.wacai.vo;

import java.math.BigDecimal;
import java.util.Date;

public class AlipayAccountVo {
	/** 交易号 */
	private String transactionNumber;
	/** 商家订单号	 */
	private String merchantOrderNumber;
	/** 交易创建时间	 */
	private Date transactionCreationTime;
	/** 付款时间	 */
	private Date paymentTime;
	/** 最近修改时间	 */
	private Date latestRevisionTime;
	/** 交易来源地	 */
	private String sourceOfTransaction;
	/** 类型	 */
	private String type;
	/** 交易对方	 */
	private String tradingParty;
	/** 商品名称	 */
	private String tradeName;
	/** 金额（元）	 */
	private BigDecimal amount;
	/** 收/支	 */
	private String collectionOrSupport;
	/** 交易状态	 */
	private String tradingStatus;
	/** 服务费（元）	 */
	private BigDecimal serviceFee;
	/** 成功退款（元）	 */
	private BigDecimal successfulRefund;
	/** 备注 */
	private String remarks;
	/** 资金状态	 */
	private String fundStatus;
	
	public String getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getMerchantOrderNumber() {
		return merchantOrderNumber;
	}
	public void setMerchantOrderNumber(String merchantOrderNumber) {
		this.merchantOrderNumber = merchantOrderNumber;
	}
	public Date getTransactionCreationTime() {
		return transactionCreationTime;
	}
	public void setTransactionCreationTime(Date transactionCreationTime) {
		this.transactionCreationTime = transactionCreationTime;
	}
	public Date getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}
	public Date getLatestRevisionTime() {
		return latestRevisionTime;
	}
	public void setLatestRevisionTime(Date latestRevisionTime) {
		this.latestRevisionTime = latestRevisionTime;
	}
	public String getSourceOfTransaction() {
		return sourceOfTransaction;
	}
	public void setSourceOfTransaction(String sourceOfTransaction) {
		this.sourceOfTransaction = sourceOfTransaction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTradingParty() {
		return tradingParty;
	}
	public void setTradingParty(String tradingParty) {
		this.tradingParty = tradingParty;
	}
	public String getTradeName() {
		return tradeName;
	}
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCollectionOrSupport() {
		return collectionOrSupport;
	}
	public void setCollectionOrSupport(String collectionOrSupport) {
		this.collectionOrSupport = collectionOrSupport;
	}
	public String getTradingStatus() {
		return tradingStatus;
	}
	public void setTradingStatus(String tradingStatus) {
		this.tradingStatus = tradingStatus;
	}
	public BigDecimal getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}
	public BigDecimal getSuccessfulRefund() {
		return successfulRefund;
	}
	public void setSuccessfulRefund(BigDecimal successfulRefund) {
		this.successfulRefund = successfulRefund;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getFundStatus() {
		return fundStatus;
	}
	public void setFundStatus(String fundStatus) {
		this.fundStatus = fundStatus;
	}
}
