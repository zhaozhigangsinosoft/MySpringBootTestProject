package cn.wacai.vo;

import java.math.BigDecimal;
import java.util.Date;

public class WacaiAccountVo {
	//支出大类
	private String expenditureCategories;
	//支出小类
	private String expenditureCategory;
	//账户
	private String account;
	//币种
	private String currency;
	//项目
	private String project;
	//商家
	private String business;
	//报销
	private String reimbursement;
	//消费日期
	private Date consumptionDate;
	//消费金额
	private BigDecimal consumptionAmount;
	//成员金额
	private String memberAmount;
	//备注
	private String remarks;
	//账本
	private String accountBook;
	
	//收/支
	private String collectionOrSupport;
	//交易对方
	private String tradingParty;
	//商品
	private String commodity;
	
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
	public String getExpenditureCategories() {
		return expenditureCategories;
	}
	public void setExpenditureCategories(String expenditureCategories) {
		this.expenditureCategories = expenditureCategories;
	}
	public String getExpenditureCategory() {
		return expenditureCategory;
	}
	public void setExpenditureCategory(String expenditureCategory) {
		this.expenditureCategory = expenditureCategory;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getReimbursement() {
		return reimbursement;
	}
	public void setReimbursement(String reimbursement) {
		this.reimbursement = reimbursement;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getAccountBook() {
		return accountBook;
	}
	public void setAccountBook(String accountBook) {
		this.accountBook = accountBook;
	}
	public Date getConsumptionDate() {
		return consumptionDate;
	}
	public void setConsumptionDate(Date consumptionDate) {
		this.consumptionDate = consumptionDate;
	}
	public BigDecimal getConsumptionAmount() {
		return consumptionAmount;
	}
	public void setConsumptionAmount(BigDecimal consumptionAmount) {
		this.consumptionAmount = consumptionAmount;
	}
	public String getMemberAmount() {
		return memberAmount;
	}
	public void setMemberAmount(String memberAmount) {
		this.memberAmount = memberAmount;
	}

}
