package com.ceren.salesorders.models;

import java.util.List;

public class UserData {
    private String SellerName, DisplayName, ContactPerson, EmailAddress, ContactNumber, Address;
    private String FirstTransactionDate, LastTransactionDate, LastUpdateDate, LastLoginDate, LastLocalUpdateDate;
    private Integer TransactionCount;
    private List<String> RecentTransactionDates;
    private Double BalanceAmount;
    private String ProductLine;

    /*
    {
        "SellerName":"Seller Name", "DisplayName":"Admin Person", "ContactPerson":"Contact Person", "EmailAddress":"Email Address",
        "ContactNumber":"1234567890", "Address":"Fill Address",
        "FirstTransactionDate":"2018-01-01", "LastTransactionDate":"2018-05-01", "LastUpdateDate":"2018-05-01",
        "LastLoginDate":"2018-04-30", "LastLocalUpdateDate":"2018-04-30",
         "TransactionCount":"100", "RecentTransactionDates":["2018-01-01", "2018-01-02"], "BalanceAmount":"10001"
    }
     */

    public UserData() {
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getFirstTransactionDate() {
        return FirstTransactionDate;
    }

    public void setFirstTransactionDate(String firstTransactionDate) {
        FirstTransactionDate = firstTransactionDate;
    }

    public String getLastTransactionDate() {
        return LastTransactionDate;
    }

    public void setLastTransactionDate(String lastTransactionDate) {
        LastTransactionDate = lastTransactionDate;
    }

    public String getLastUpdateDate() {
        return LastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        LastUpdateDate = lastUpdateDate;
    }

    public String getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        LastLoginDate = lastLoginDate;
    }

    public Integer getTransactionCount() {
        return TransactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        TransactionCount = transactionCount;
    }

    public List<String> getRecentTransactionDates() {
        return RecentTransactionDates;
    }

    public void setRecentTransactionDates(List<String> recentTransactionDates) {
        RecentTransactionDates = recentTransactionDates;
    }

    public String getLastLocalUpdateDate() {
        return LastLocalUpdateDate;
    }

    public void setLastLocalUpdateDate(String lastLocalUpdateDate) {
        LastLocalUpdateDate = lastLocalUpdateDate;
    }

    public Double getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        BalanceAmount = balanceAmount;
    }

    public String getProductLine() {
        return ProductLine;
    }

    public void setProductLine(String productLine) {
        ProductLine = productLine;
    }
}
