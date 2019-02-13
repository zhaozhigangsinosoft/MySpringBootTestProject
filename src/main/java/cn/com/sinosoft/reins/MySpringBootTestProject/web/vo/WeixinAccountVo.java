package cn.com.sinosoft.reins.MySpringBootTestProject.web.vo;

import java.math.BigDecimal;
import java.util.Date;

public class WeixinAccountVo {
	//交易时间
	private Date transactionTime;
	//交易类型
	private String transactionType;
	//交易对方
	private String tradingParty;
	//商品
	private String commodity;
	//收/支
	private String collectionOrSupport;
	//金额(元)
	private BigDecimal amount;
	//支付方式
	private String paymentMethod;
	//当前状态
	private String currentState;
	//交易单号
	private String transactionNumber;
	//商户单号
	private String merchantNumber;
	//备注
	private String remarks;
	
	
	public Date getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getTradingParty() {
		return tradingParty;
	}
	public void setTradingParty(String tradingParty) {
		this.tradingParty = tradingParty;
	}
	public String getCommodity() {
		return commodity;
	}
	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}
	public String getCollectionOrSupport() {
		return collectionOrSupport;
	}
	public void setCollectionOrSupport(String collectionOrSupport) {
		this.collectionOrSupport = collectionOrSupport;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getCurrentState() {
		return currentState;
	}
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	public String getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getMerchantNumber() {
		return merchantNumber;
	}
	public void setMerchantNumber(String merchantNumber) {
		this.merchantNumber = merchantNumber;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
