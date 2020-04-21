package com.shippings.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "requests")
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "shipping_id")
    private long shippingId;

    @Column(name = "user_from_id")
    private long userFromId;

    @Column(name = "start")
    private String start;

    @Column(name = "finish")
    private String finish;

    @Column(name = "weight")
    private int weight;

    @Column(name = "height")
    private int height;

    @Column(name = "length")
    private int length;

    @Column(name = "width")
    private int width;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    private boolean status;

    @Column(name = "price")
    private int price;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getShippingId() {
        return shippingId;
    }
    public void setShippingId(long id) {
        this.shippingId = id;
    }
    public long getUserFromId() {
        return userFromId;
    }
    public void setUserFromId(long id) {
        this.userFromId = id;
    }
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public String getFinish() {
        return finish;
    }
    public void setFinish(String finish) {
        this.finish = finish;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
