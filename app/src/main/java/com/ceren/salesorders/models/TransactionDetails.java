package com.ceren.salesorders.models;

public class TransactionDetails {
    private Integer ID;
    private String CreateTime, UpdateTime;
    private Double SaleAmount, CancelAmount, ReturnAmount, DiscountAmount, TotalTaxAmount;
    private Double NetSaleAmount, OBAmount, CashPaidAmount, BalanceAmount;
    private String SellerName, BillNumber;

    /*
    { "ID" : 123, "CreateTime" : "2018-05-10", "UpdateTime": "2018-05-11",
    "SaleAmount": 1080, "CancelAmount": 0, "ReturnAmount" : 0, "DiscountAmount": 0, "TotalTaxAmount" : 0,
    "NetSaleAmount": 1230, "OBAmount": 0, "CashPaidAmount": 0, "BalanceAmount": 0,
    "SellerName": "Seller name", "BillNumber": "B010" }
     */

    public TransactionDetails() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public Double getSaleAmount() {
        return SaleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        SaleAmount = saleAmount;
    }

    public Double getCancelAmount() {
        return CancelAmount;
    }

    public void setCancelAmount(Double cancelAmount) {
        CancelAmount = cancelAmount;
    }

    public Double getReturnAmount() {
        return ReturnAmount;
    }

    public void setReturnAmount(Double returnAmount) {
        ReturnAmount = returnAmount;
    }

    public Double getDiscountAmount() {
        return DiscountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        DiscountAmount = discountAmount;
    }

    public Double getTotalTaxAmount() {
        return TotalTaxAmount;
    }

    public void setTotalTaxAmount(Double totalTaxAmount) {
        TotalTaxAmount = totalTaxAmount;
    }

    public Double getNetSaleAmount() {
        return NetSaleAmount;
    }

    public void setNetSaleAmount(Double netSaleAmount) {
        NetSaleAmount = netSaleAmount;
    }

    public Double getOBAmount() {
        return OBAmount;
    }

    public void setOBAmount(Double OBAmount) {
        this.OBAmount = OBAmount;
    }

    public Double getCashPaidAmount() {
        return CashPaidAmount;
    }

    public void setCashPaidAmount(Double cashPaidAmount) {
        CashPaidAmount = cashPaidAmount;
    }

    public Double getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        BalanceAmount = balanceAmount;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        this.CreateTime = createTime;
    }

    public String getBillNumber() {
        return BillNumber;
    }

    public void setBillNumber(String billNumber) {
        BillNumber = billNumber;
    }
}
