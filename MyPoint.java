package Particle;

import java.io.Serializable;

public class MyPoint implements Serializable {
    public double x=0,y=0;

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint() {
        this.x=0;
        this.y=0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
