import java.io.Serializable;
import java.util.ArrayList;

public class MyPoint implements Serializable, Cloneable {
    public double x = 0, y = 0;
    public int angleFlag = 0;//0 1 2 3: 0 90 180 270;     4 5 6 7: MX,MY,MXR90,MYR90

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint() {
        this.x = 0;
        this.y = 0;
    }
    @Override
    public MyPoint clone() throws CloneNotSupportedException {
        MyPoint p = new MyPoint();
        p.x = this.x;
        p.y = this.y;
        p.angleFlag = this.angleFlag;
        return p;
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
