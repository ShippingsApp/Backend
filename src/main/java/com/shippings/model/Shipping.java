package com.shippings.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.zip.ZipEntry;

@Entity
@Table(name = "shippings")
@Data
public class Shipping {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "driver_id")
    private long driverId;

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

    @Column(name = "plus_time")
    private int plusTime;

    @Column(name = "status")
    private boolean status;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date_start")
    private Date dateStart;

    @Column(name = "date_finish")
    private Date dateFinish;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getDriverId() {
        return driverId;
    }
    public void setDriverId(long driverId) {
        this.driverId = driverId;
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
    public int getPlusTime() {
        return plusTime;
    }
    public void setPlusTime(int plusTime) {
        this.plusTime = plusTime;
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
    public Date getDateStart() {
        return dateStart;
    }
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }
    public Date getDateFinish() {
        return dateFinish;
    }
    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }
}
