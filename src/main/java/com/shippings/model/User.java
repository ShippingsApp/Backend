package com.shippings.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "status")
    private Integer status;

    @Column(name = "rate")
    private Integer rate;
//
//    public User() {}
//
//    public User(String username, String password, String role, String realName, String mobilePhone, Integer status, Integer rate) {
//        this.username = username;
//        this.password = password;
//        this.role = role;
//        this.realName = realName;
//        this.mobilePhone = mobilePhone;
//        this.status = status;
//
//    }

    public String getUsername() {
        return username;
    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//
//    public String getRealName() {
//        return realName;
//    }
//
//    public void setRealName(String realName) {
//        this.realName = realName;
//    }

    public String getMobilePhone() {
        return mobilePhone;
    }
//
//    public void setMobilePhone(String mobilePhone) {
//        this.mobilePhone = mobilePhone;
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public Integer getRate() {
//        return rate;
//    }
//
//    public void setRate(Integer rate) {
//        this.rate = rate;
//    }
//
//
    public Long getId() {
        return this.id;
    }
}
