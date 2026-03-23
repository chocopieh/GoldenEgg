package com.mycompany.bt1;

public class Line implements Hinh {
    private Point p1;
    private Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = new Point(p1.getX(), p1.getY());
        this.p2 = new Point(p2.getX(), p2.getY());
    }

    @Override
    public void ve() {
        System.out.println("Doan thang: " + p1 + " -> " + p2);
    }

    @Override
    public double tinhKhoangCachDen(Point p) {
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double px = p.getX(), py = p.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return p1.tinhKhoangCachDen(p);
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double xGan = x1 + t * dx;
        double yGan = y1 + t * dy;

        double ddx = px - xGan;
        double ddy = py - yGan;

        return Math.sqrt(ddx * ddx + ddy * ddy);
    }

    @Override
    public Point layTam() {
        return new Point(
            (p1.getX() + p2.getX()) / 2.0,
            (p1.getY() + p2.getY()) / 2.0
        );
    }

    @Override
    public void phongToThuNho(double tiLe) {
        Point tam = layTam();
        p1.phongToTuTam(tam, tiLe);
        p2.phongToTuTam(tam, tiLe);
    }

    @Override
    public void xoay(double gocDo) {
        Point tam = layTam();
        p1.xoayTuTam(tam, gocDo);
        p2.xoayTuTam(tam, gocDo);
    }

    @Override
    public String toString() {
        return "Line[" + p1 + " -> " + p2 + "]";
    }
}