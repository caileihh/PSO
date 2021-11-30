import java.io.Serializable;

public class Ports implements Serializable {
    private int portPointNum;
    private String ruleName;
    private double[] x = new double[portPointNum],yuanX;
    private double[] y = new double[portPointNum],yuanY;
    private CenterPoint centerPoint = new CenterPoint(getCenterX(), getCenterY());
    public double sumDis;

    public Ports() {
    }

    @Override
    public Ports clone() throws CloneNotSupportedException {
        Ports p = new Ports();
        p.portPointNum = this.portPointNum;
        p.ruleName = this.ruleName;
        p.x = this.x.clone(); p.yuanX=this.x.clone();
        p.y = this.y.clone(); p.yuanY=this.y.clone();
        p.centerPoint = this.centerPoint.clone();
        p.sumDis = this.sumDis;
        return p;
    }
    public Ports(int portPointNum, double[] x, double[] y, String ruleName) {
        this.portPointNum = portPointNum;
        this.ruleName = ruleName;
        this.x = x;
        this.y = y;
        double centreX = 0, centreY = 0;
        for (int i = 0; i < portPointNum; i++) {
            centreX += x[i];
            centreY += y[i];
        }
        this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
    }

    public String getRuleName() {
        return ruleName;
    }

    public int getPortPointNum() {
        return portPointNum;
    }

    public double getX(int i) {
        while (i >= portPointNum) i -= portPointNum;
        return x[i];
    }

    public double getY(int i) {
        while (i >= portPointNum) i -= portPointNum;
        return y[i];
    }

    public void adjustAngle(CenterPoint center, int angleFlag,String orient) {
        double x0 = center.getX(), y0 = center.getY();
        double centreX = 0, centreY = 0;

        switch (orient) {
            case "R90":
                for (int i = 0; i < portPointNum; i++) {
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
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 * 2 - x1;
                    this.yuanY[i] = y0 * 2 - y1;
                }
                break;
            case "R270":
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 + y0 - y1;
                    this.yuanY[i] = x1 + y0 - x0;
                }
                break;
            case "MX":
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 * 2 - x1;
                    this.yuanY[i] = y1;
                }
                break;
            case "MY":
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x1;
                    this.yuanY[i] = y0 * 2 - y1;
                }
                break;
            case "MXR90":
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = x0 + y0 - y1;
                    this.yuanY[i] = x0 + y0 - x1;
                }
                break;
            case "MYR90":
                for (int i = 0; i < portPointNum; i++) {
                    double x1 = this.x[i], y1 = this.y[i];
                    this.yuanX[i] = y1 + x0 - y0;
                    this.yuanY[i] = x1 + y0 - x0;
                }
                break;
        }
        if(angleFlag==0){
            this.x=this.yuanX.clone();
            this.y=this.yuanY.clone();
            for(int i=0;i<portPointNum;i++){
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        }
        else if (angleFlag == 1) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y0 - y1 + x0;
                this.y[i] = x1 - x0 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 2) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0 - x1 + x0;
                ;
                this.y[i] = y0 - y1 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 3) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y1 - y0 + x0;
                this.y[i] = x0 - x1 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 4) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x0 - x1 + x0;
                this.y[i] = y1 - y0 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 5) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = x1 - x0 + x0;
                this.y[i] = y0 - y1 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 6) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y0 - y1 + x0;
                this.y[i] = x0 - x1 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        } else if (angleFlag == 7) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.yuanX[i], y1 = this.yuanY[i];
                this.x[i] = y1 - y0 + x0;
                this.y[i] = x1 - x0 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        }
    }


    public void Move(double x, double y) {
        double centreX = 0, centreY = 0;
        for (int i = 0; i < portPointNum; i++) {
            this.x[i] += x;
            this.y[i] += y;

            centreX += this.x[i];
            centreY += this.y[i];
        }
        this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
    }

    public double getSumDis() {
        return sumDis;
    }

    public void setSumDis(double sumDis) {
        this.sumDis = sumDis;
    }

    public CenterPoint getCenterPoint() {
        return centerPoint;
    }

    public double getCenterX() {
        double sum = 0;
        for (int i = 0; i < portPointNum; i++)
            sum += x[i];
        return sum;
    }

    public double getCenterY() {
        double sum = 0;
        for (int i = 0; i < portPointNum; i++)
            sum += y[i];
        return sum;
    }
}

