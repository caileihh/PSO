package Particle;

import java.io.Serializable;

public class Ports implements Serializable {
    private int portPointNum;
    private double[] x=new double[portPointNum];
    private double[] y=new double[portPointNum];
    private CenterPoint centerPoint=new CenterPoint(getCenterX(),getCenterY());
    public double sumDis;

    public Ports(int portPointNum,double[] x,double[] y) {
        this.portPointNum=portPointNum;
        this.x=x;
        this.y=y;
        double centreX=0,centreY=0;
        for(int i=0;i<portPointNum;i++){
            centreX+=x[i];
            centreY+=y[i];
        }
        this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
    }


    public void Move(double x,double y){
        double centreX=0,centreY=0;
        for(int i=0;i<portPointNum;i++)
        {
            this.x[i]+=x;
            this.y[i]+=y;

            centreX+= this.x[i];
            centreY+= this.y[i];
        }
        this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
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

    public double getCenterX(){
        double sum=0;
        for(int i = 0; i< portPointNum; i++)
            sum+=x[i];
        return sum;
    }
    public double getCenterY(){
        double sum=0;
        for(int i = 0; i< portPointNum; i++)
            sum+=y[i];
        return sum;
    }
}
