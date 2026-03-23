package com.mycompany.bt1;

public class TamGiac implements Hinh {
    private Point a, b, c;

    public TamGiac(Point a, Point b, Point c) {
        this.a = new Point(a.getX(), a.getY());
        this.b = new Point(b.getX(), b.getY());
        this.c = new Point(c.getX(), c.getY());
    }

    @Override
    public void ve() {
        System.out.println("Tam giac: " + a + ", " + b + ", " + c);
    }

    @Override
    public Point layTam() {
        return new Point(
            (a.getX() + b.getX() + c.getX()) / 3,
            (a.getY() + b.getY() + c.getY()) / 3
        );
    }

    @Override
    public double tinhKhoangCachDen(Point p) {
        Line ab = new Line(a, b);
        Line bc = new Line(b, c);
        Line ca = new Line(c, a);

        return Math.min(ab.tinhKhoangCachDen(p),
               Math.min(bc.tinhKhoangCachDen(p),
                        ca.tinhKhoangCachDen(p)));
    }

    @Override
    public void phongToThuNho(double tiLe) {
        Point tam = layTam();
        a.phongToTuTam(tam, tiLe);
        b.phongToTuTam(tam, tiLe);
        c.phongToTuTam(tam, tiLe);
    }

    @Override
    public void xoay(double gocDo) {
        Point tam = layTam();
        a.xoayTuTam(tam, gocDo);
        b.xoayTuTam(tam, gocDo);
        c.xoayTuTam(tam, gocDo);
    }
}