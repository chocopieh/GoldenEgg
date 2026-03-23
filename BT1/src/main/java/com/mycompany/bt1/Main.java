package com.mycompany.bt1;

public class Main {
    public static void main(String[] args) {

        Point a = new Point(0, 0);
        Point b = new Point(4, 0);
        Point c = new Point(2, 3);
        Point d = new Point(4, 2);

        Line line = new Line(a, b);
        TamGiac tri = new TamGiac(a, b, c);
        HinhChuNhat rect = new HinhChuNhat(a, b, d, c);
        Point test = new Point(2, 2);
        Hinh[] ds = {line, tri, rect};
        for (Hinh h : ds) {
            System.out.println("===============");
            h.ve();
            System.out.println("Tam: " + h.layTam());
            System.out.println("Khoang cach: " + h.tinhKhoangCachDen(test));
            h.phongToThuNho(1.5);
            System.out.println("Sau phong to:");
            h.ve();
            h.xoay(30);
            System.out.println("Sau xoay:");
            h.ve();
        }
    }
}