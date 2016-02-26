/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户基本信息
 *
 * @author maping
 */
public class UserBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     *用户ID
     */
    private String userId;

    /*
     *IMSI号
     */
    private String imsi;
    /*
     *手机号
     */
    private String msisdn;
    /*
     *所在市
     */
    private String homeCity;
    /*
     *所在县
     */
    private String homeCountry;
    /*
     *生效时间
     */
    private Date inureTime;
    /*
     *失效时间
     */
    private Date expireTime;
    /*
     *测试标志
     */
    private String testFlag;

    public UserBaseInfo(String userId, String imsi, String msisdn, String homeCity, String homeCountry, Date inureTime, Date expireTime, String testFlag) {
        this.userId = userId;
        this.imsi = imsi;
        this.msisdn = msisdn;
        this.homeCity = homeCity;
        this.homeCountry = homeCountry;
        this.inureTime = inureTime;
        this.expireTime = expireTime;
        this.testFlag = testFlag;

    }
    
    public UserBaseInfo(String userId, String imsi, String msisdn, String homeCity, String homeCountry, String testFlag) {
        this.userId = userId;
        this.imsi = imsi;
        this.msisdn = msisdn;
        this.homeCity = homeCity;
        this.homeCountry = homeCountry;
        this.inureTime = new Date();
        this.expireTime = new Date();
        this.testFlag = testFlag;
    }

    public UserBaseInfo(String userId) {
        this.userId = userId;
        this.imsi = "0";
        this.msisdn = "0";
        this.homeCity = "0";
        this.homeCountry = "0";
        this.inureTime = new Date();
        this.expireTime = new Date();
        this.testFlag = "0";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public Date getInureTime() {
        return inureTime;
    }

    public void setInureTime(Date inureTime) {
        this.inureTime = inureTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }

    @Override
    public String toString() {
        return "UserBaseInfo{" + "userId=" + userId + ", imsi=" + imsi + ", msisdn=" + msisdn + ", homeCity=" + homeCity + ", homeCountry=" + homeCountry + ", inureTime=" + inureTime + ", expireTime=" + expireTime + ", testFlag=" + testFlag + '}';
    }

}
