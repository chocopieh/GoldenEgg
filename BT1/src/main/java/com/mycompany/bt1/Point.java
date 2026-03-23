/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bt1;

/**
 *
 * @author Admin
 */
public class Point {
    private double x;
    private double y;
    
    public Point(double x,double y){
        this.x=x;
        this.y=y;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public void setX(double x){
        this.x=x;
    }
    public void setY(double y){
        this.y=y;
    }
    public double tinhKhoangCachDen(Point p){
        double dx=this.x-p.x;
        double dy=this.y-p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public void phongToTuTam(Point tam,double tile){
        this.x=tam.x+tile*(this.x-tam.x);
        this.y=tam.y+tile*(this.y-tam.y);
    }
    public void xoayTuTam(Point tam, double gocDo) {
        double rad = Math.toRadians(gocDo);
        double dx=this.x-tam.x;
        double dy=this.y-tam.y;
        double xMoi=dx*Math.cos(rad) - dy*Math.sin(rad);
        double yMoi=dx*Math.sin(rad) + dy*Math.cos(rad);
        this.x=tam.x+xMoi;
        this.y=tam.y+yMoi;
    }
 @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
   
