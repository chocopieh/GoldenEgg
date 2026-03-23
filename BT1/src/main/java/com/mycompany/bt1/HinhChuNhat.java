package com.mycompany.bt1;

public class HinhChuNhat implements Hinh {
    private Point a, b, c, d;

    public HinhChuNhat(Point a, Point b, Point c, Point d) {
        this.a = new Point(a.getX(), a.getY());
        this.b = new Point(b.getX(), b.getY());
        this.c = new Point(c.getX(), c.getY());
        this.d = new Point(d.getX(), d.getY());
    }

    @Override
    public void ve() {
        System.out.println("Hinh chu nhat: " + a + ", " + b + ", " + c + ", " + d);
    }

    @Override
    public Point layTam() {
        return new Point(
            (a.getX() + c.getX()) / 2,
            (a.getY() + c.getY()) / 2
        );
    }

    @Override
    public double tinhKhoangCachDen(Point p) {
        Line ab = new Line(a, b);
        Line bc = new Line(b, c);
        Line cd = new Line(c, d);
        Line da = new Line(d, a);

        return Math.min(
            Math.min(ab.tinhKhoangCachDen(p), bc.tinhKhoangCachDen(p)),
            Math.min(cd.tinhKhoangCachDen(p), da.tinhKhoangCachDen(p))
        );
    }

    @Override
    public void phongToThuNho(double tiLe) {
        Point tam = layTam();
        a.phongToTuTam(tam, tiLe);
        b.phongToTuTam(tam, tiLe);
        c.phongToTuTam(tam, tiLe);
        d.phongToTuTam(tam, tiLe);
    }

    @Override
    public void xoay(double gocDo) {
        Point tam = layTam();
        a.xoayTuTam(tam, gocDo);
        b.xoayTuTam(tam, gocDo);
        c.xoayTuTam(tam, gocDo);
        d.xoayTuTam(tam, gocDo);
    }
}