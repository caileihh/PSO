
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Particle implements Cloneable, Serializable {
    private int pointNum, portN;
    private double[] x,yuanX;
    private double[] y,yuanY;
    private double[] shellX, shellY;
    private double maxX, maxY, minX, minY;
    public String Orient="R0";
    private String name;
    private String ruleName;
    public CenterPoint centerPoint = new CenterPoint(getCenterX(), getCenterY());
    ArrayList<Ports> portsArrayList = new ArrayList<>();
    private double sumF = 0;

    @Override
    public Particle clone() throws CloneNotSupportedException {
        Particle p = new Particle();
        p.name = this.name; p.ruleName=this.ruleName;
        p.Orient=this.Orient;
        p.pointNum = this.pointNum;
        p.x = this.x.clone(); p.yuanX=this.x.clone();
        p.y = this.y.clone(); p.yuanY=this.y.clone();
        p.maxX = this.maxX;
        p.maxY = this.maxY;
        p.minX = this.minX;
        p.minY = this.minY;
        p.shellX = this.shellX.clone();
        p.shellY = this.shellY.clone();
//        if (this.portsArrayList != null) p.portsArrayList = new ArrayList<>(this.portsArrayList);
//        p.portsArrayList = (ArrayList<Ports>) this.portsArrayList.clone();
//        p.portsArrayList = new ArrayList<Ports>();
        for (Ports pp : this.portsArrayList) {
            p.portsArrayList.add(pp.clone());
        }
        p.sumF = this.sumF;
        p.centerPoint = this.centerPoint.clone();
        return p;
    }

    public Particle() {
    }

    public CenterPoint getCenterPoint() {
        return centerPoint;
    }

    public double getShellX(int i) {
        while (i >= pointNum) i -= pointNum;
        return shellX[i];
    }

    public double getShellY(int i) {
        while (i >= pointNum) i -= pointNum;
        return shellY[i];
    }

    public double getX(int i) {
        while (i >= pointNum) i -= pointNum;
        return x[i];
    }

    public double getY(int i) {
        while (i >= pointNum) i -= pointNum;
        return y[i];
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setSumF(double sumF) {
        this.sumF = sumF;
    }

    public double getSumF(int x) {
        return this.sumF;
    }

    public double getSumF() {
        double sum = 0;
        for (Ports ports : portsArrayList) {
            sum += ports.sumDis;
        }
        return sum;
    }

//    public void adjustAngle(int angleFlag) {  //可优化   //逆时针为正方向
//        if (angleFlag == 0) return;
//        double x0 = getCenterX(), y0 = getCenterY();
//        double centreX = 0, centreY = 0;
//        this.maxX = Double.MIN_VALUE;
//        this.minX = Double.MAX_VALUE;
//        this.maxY = Double.MIN_VALUE;
//        this.minY = Double.MAX_VALUE;
//        if (angleFlag == 1) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = y0 - y1 + x0;
//                this.shellX[i] = y0 - y1 + x0;
//                this.y[i] = x1 - x0 + y0;
//                this.shellY[i] = x1 - x0 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum); //中心应该不会变化
//        } else if (angleFlag == 2) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = x0 - x1 + x0;
//                this.shellX[i] = x0 - x1 + x0;
//                this.y[i] = y0 - y1 + y0;
//                this.shellY[i] = y0 - y1 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        } else if (angleFlag == 3) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = y1 - y0 + x0;
//                this.shellX[i] = y1 - y0 + x0;
//                this.y[i] = x0 - x1 + y0;
//                this.shellY[i] = x0 - x1 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        } else if (angleFlag == 4) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = x0 - x1 + x0;
//                this.shellX[i] = x0 - x1 + x0;
//                this.y[i] = y1 - y0 + y0;
//                this.shellY[i] = y1 - y0 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        } else if (angleFlag == 5) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = x1 - x0 + x0;
//                this.shellX[i] = x1 - x0 + x0;
//                this.y[i] = y0 - y1 + y0;
//                this.shellY[i] = y0 - y1 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        } else if (angleFlag == 6) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = y0 - y1 + x0;
//                this.shellX[i] = y0 - y1 + x0;
//                this.y[i] = x0 - x1 + y0;
//                this.shellY[i] = x0 - x1 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        } else if (angleFlag == 7) {
//            for (int i = 0; i < pointNum; i++) {
//                double x1 = this.x[i], y1 = this.y[i];
//                this.x[i] = y1 - y0 + x0;
//                this.shellX[i] = y1 - y0 + x0;
//                this.y[i] = x1 - x0 + y0;
//                this.shellY[i] = x1 - x0 + y0;
//                centreX += this.x[i];
//                centreY += this.y[i];
//                if (x[i] > this.maxX) this.maxX = x[i];
//                else if (x[i] < this.minX) this.minX = x[i];
//                if (y[i] > this.maxY) this.maxY = y[i];
//                else if (y[i] < this.minY) this.minY = y[i];
//            }
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
//        }
//        for (Ports ports : portsArrayList) {
//            ports.adjustAngle(this.centerPoint, angleFlag);
//        }
//        resetOutShell();
//    }

    public void adjustAngle(int angleFlag) {  //可优化   //逆时针为正方向
//        if (angleFlag == 0) return;
        double x0 = getCenterX(), y0 = getCenterY();
        switch (this.Orient) {
            case "R90":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = y1 + x0 - y0;
                    this.yuanY[i] = x0 - x1 + y0;
                }
                break;
            case "R0":
                this.yuanX = this.x.clone();
                this.yuanY = this.y.clone();
                break;
            case "R180":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 * 2 - x1;
                    this.yuanY[i] = y0 * 2 - y1;
                }
                break;
            case "R270":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 + y0 - y1;
                    this.yuanY[i] = x1 + y0 - x0;
                }
                break;
            case "MX":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 * 2 - x1;
                    this.yuanY[i] =  y1;
                }
                break;
            case "MY":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x1;
                    this.yuanY[i] = y0 * 2 - y1;
                }
                break;
            case "MXR90":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = y1 + x0 - y0;
                    this.yuanY[i] = x1 + y0 - x0;
                }
                break;
            case "MYR90":
                for (int i = 0; i < pointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0+y0-y1;
                    this.yuanY[i] = y0+x0-x1;
                }
                break;
        }
        String yuanOrient=this.Orient;
        if (angleFlag==0){
            this.x=this.yuanX.clone();
            this.y=this.yuanY.clone();
            resetBoundary();
            this.Orient="R0";
        }
        else if (angleFlag == 1) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y0 - y1 + x0;
                this.shellX[i] = y0 - y1 + x0;
                this.y[i] = x1 - x0 + y0;
                this.shellY[i] = x1 - x0 + y0;


            }
            this.Orient="R90";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum); //中心应该不会变化
        } else if (angleFlag == 2) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0 - x1 + x0;
                this.shellX[i] = x0 - x1 + x0;
                this.y[i] = y0 - y1 + y0;
                this.shellY[i] = y0 - y1 + y0;

            }
            this.Orient="R180";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        } else if (angleFlag == 3) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y1 - y0 + x0;
                this.shellX[i] = y1 - y0 + x0;
                this.y[i] = x0 - x1 + y0;
                this.shellY[i] = x0 - x1 + y0;

            }
            this.Orient="R270";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        } else if (angleFlag == 4) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0 - x1 + x0;
                this.shellX[i] = x0 - x1 + x0;
                this.y[i] = y1 - y0 + y0;
                this.shellY[i] = y1 - y0 + y0;

            }
            this.Orient="MX";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        } else if (angleFlag == 5) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x1 - x0 + x0;
                this.shellX[i] = x1 - x0 + x0;
                this.y[i] = y0 - y1 + y0;
                this.shellY[i] = y0 - y1 + y0;

            }
            this.Orient="MY";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        } else if (angleFlag == 6) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0+y1-y0;
                this.shellX[i] = x0+y1-y0;
                this.y[i] = y0+x1-x0;
                this.shellY[i] = y0+x1-x0;

            }
            this.Orient="MXR90";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        } else if (angleFlag == 7) {
            for (int i = 0; i < pointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0+y0-y1;
                this.shellX[i] = x0+y0-y1;
                this.y[i] = y0+x0-x1;
                this.shellY[i] = y0+x0-x1;

            }
            this.Orient="MYR90";
//            this.centerPoint = new CenterPoint(centreX / pointNum, centreY / pointNum);
        }
        resetBoundary();
        this.centerPoint=new CenterPoint(getCenterX(),getCenterY());
        for (Ports ports : portsArrayList) {
            ports.adjustAngle(this.centerPoint, angleFlag,yuanOrient);
        }
        resetOutShell();
    }

    public void Move(double x, double y) {
        double centreX = 0, centreY = 0;
        for (int i = 0; i < pointNum; i++) {
            this.x[i] += x; this.yuanX[i]+=x;
            this.y[i] += y; this.yuanY[i]+=y;
            this.shellX[i] += x;
            this.shellY[i] += y;

            centreX += this.x[i];
            centreY += this.y[i];
        }
        this.maxX += x;
        this.minX += x;
        this.maxY += y;
        this.minY += y;

        this.centerPoint = new CenterPoint(getCenterX(), getCenterY());
        for (Ports ports : portsArrayList) {
            ports.Move(x, y);
        }
//        resetOutShell();
    }

    public void Move2(double x, double y) {
        double absX = x - this.getCenterX(), absY = y - this.getCenterY();
        double centreX = 0, centreY = 0;
        for (int i = 0; i < pointNum; i++) {
            this.x[i] += absX; this.yuanX[i]+=absX;
            this.y[i] += absY; this.yuanY[i]+=absY;
            this.shellX[i] += absX;
            this.shellY[i] += absY;

            centreX += this.x[i];
            centreY += this.y[i];
        }
        maxX += absX;
        minX += absX;
        maxY += absY;
        minY += absY;

        this.centerPoint = new CenterPoint(getCenterX(), getCenterY());
        for (Ports ports : portsArrayList) {
            ports.Move(absX, absY);
        }
//        resetOutShell();
    }

//    public Particle(Particle p){
//        this(p.pointNum,p.portN,p.x,p.y,p.name,p.centerPoint,p.portsArrayList);
//    }

    public Particle(int pointNum, int portN, double[] x, double[] y, String name, CenterPoint centerPoint, ArrayList<Ports> portsArrayList) {
        this.pointNum = pointNum;
        this.portN = portN;
        this.x = x;
        this.y = y;
        this.name = name;
        this.centerPoint = centerPoint;
        this.portsArrayList = portsArrayList;

        resetBoundary();
        resetOutShell();
    }

    public Particle(String name, int pointNum, double[] x, double[] y, String ruleName) {
        this.x = x;
        this.y = y;
        this.yuanX=x.clone();
        this.yuanY=y.clone();
        this.name = name;
        this.ruleName = ruleName;
        this.pointNum = pointNum;
        this.shellX = new double[pointNum];
        this.shellY = new double[pointNum];
        resetBoundary();
        this.centerPoint=new CenterPoint(getCenterX(),getCenterY());
        resetOutShell();
    }

    public void resetBoundary() {
        maxX = minX = x[0];
        maxY = minY = y[0];
        for (int i = 0; i < pointNum; i++) {
            if (maxX < x[i]) maxX = x[i];
            if (minX > x[i]) minX = x[i];
            if (maxY < y[i]) maxY = y[i];
            if (minY > y[i]) minY = y[i];
        }
    }

    public void resetOutShell() {


        int[] a = new int[4];            //顺时针4顶点，左上为0: 0 1 2 3
        for (int i = 0; i < pointNum; i++) {
            if (x[i] == this.maxX) {
                if (y[i] == this.maxY) a[2] = 1;
                else if (y[i] == this.minY) a[1] = 1;
            } else if (x[i] == this.minX) {
                if (y[i] == this.minY) a[0] = 1;
                else if (y[i] == this.maxY) a[3] = 1;
            }
        }
        if (a[0] * a[1] * a[2] * a[3] == 0) {
            for (int i = 0; i < pointNum; i++) {
                if (x[i] == maxX) {
                    shellX[i] = x[i] + ParticleTest.shellWidth;
                    if (y[i] == maxY) {
                        shellY[i] = y[i] + ParticleTest.shellWidth;
                    } else if (y[i] == minY) {
                        shellY[i] = y[i] - ParticleTest.shellWidth;
                    } else {
                        if (a[1] == 0) shellY[i] = y[i] - ParticleTest.shellWidth;
                        else if (a[2] == 0) shellY[i] = y[i] + ParticleTest.shellWidth;
                    }
                } else if (x[i] == minX) {
                    shellX[i] = x[i] - ParticleTest.shellWidth;
                    if (y[i] == maxY) {
                        shellY[i] = y[i] + ParticleTest.shellWidth;
                    } else if (y[i] == minY) {
                        shellY[i] = y[i] - ParticleTest.shellWidth;
                    } else {
                        if (a[0] == 0) shellY[i] = y[i] - ParticleTest.shellWidth;
                        else if (a[3] == 0) shellY[i] = y[i] + ParticleTest.shellWidth;
                    }
                } else {
                    if (y[i] == maxY) {
                        shellY[i] = y[i] + ParticleTest.shellWidth;
                        if (a[1] == 0 || a[2] == 0) shellX[i] = x[i] + ParticleTest.shellWidth;
                        else if (a[0] == 0 || a[3] == 0) shellX[i] = x[i] - ParticleTest.shellWidth;
                    } else if (y[i] == minY) {
                        shellY[i] = y[i] - ParticleTest.shellWidth;
                        if (a[0] == 0 || a[3] == 0) shellX[i] = x[i] - ParticleTest.shellWidth;
                        else if (a[1] == 0 || a[2] == 0) shellX[i] = x[i] + ParticleTest.shellWidth;
                    } else {
                        if (a[0] == 0) {
                            shellX[i] = x[i] - ParticleTest.shellWidth;
                            shellY[i] = y[i] - ParticleTest.shellWidth;
                        } else if (a[1] == 0) {
                            shellX[i] = x[i] + ParticleTest.shellWidth;
                            shellY[i] = y[i] - ParticleTest.shellWidth;
                        } else if (a[2] == 0) {
                            shellX[i] = x[i] + ParticleTest.shellWidth;
                            shellY[i] = y[i] + ParticleTest.shellWidth;
                        } else {
                            shellX[i] = x[i] - ParticleTest.shellWidth;
                            shellY[i] = y[i] + ParticleTest.shellWidth;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < pointNum; i++) {
                if (x[i] == this.maxX) {
                    shellX[i] = x[i] + ParticleTest.shellWidth;
                    if (y[i] == this.maxY) shellY[i] = y[i] + ParticleTest.shellWidth;
                    else shellY[i] = y[i] - ParticleTest.shellWidth;
                } else if (x[i] == this.minX) {
                    shellX[i] = x[i] - ParticleTest.shellWidth;
                    if (y[i] == this.minY) shellY[i] = y[i] - ParticleTest.shellWidth;
                    else shellY[i] = y[i] + ParticleTest.shellWidth;
                }
            }
        }
    }

    public double getCenterX() {
//        double sum = 0;
//        for (int i = 0; i < pointNum; i++)
//            sum += x[i];
//        return sum / pointNum;
        return (this.maxX+this.minX)/2;
    }

    public double getCenterY() {
//        double sum = 0;
//        for (int i = 0; i < pointNum; i++)
//            sum += y[i];
//        return sum / pointNum;
        return (this.maxY+this.minY)/2;
    }

    public String getName() {
        return name;
    }

    public String getRuleName() {
        return ruleName;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getCost() {
        return 0;
    }


}

class CenterPoint implements Cloneable, Serializable {
    double x, y;

    @Override
    public CenterPoint clone() throws CloneNotSupportedException {
        CenterPoint p = (CenterPoint) super.clone();
        p.x = this.x;
        p.y = this.y;
        return p;
    }

    public CenterPoint(double x, double y) {
        this.x = x;
        this.y = y;
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
