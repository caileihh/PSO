package Particle;

import java.io.Serializable;

public class Ports implements Serializable {
    private int portPointNum;
    private String ruleName;
    private double[] x=new double[portPointNum];
    private double[] y=new double[portPointNum];
    private CenterPoint centerPoint=new CenterPoint(getCenterX(),getCenterY());
    public double sumDis;

    public Ports(int portPointNum,double[] x,double[] y,String ruleName) {
        this.portPointNum=portPointNum;
        this.ruleName=ruleName;
        this.x=x;
        this.y=y;
        double centreX=0,centreY=0;
        for(int i=0;i<portPointNum;i++){
            centreX+=x[i];
            centreY+=y[i];
        }
        this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
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

    public void adjustAngle(CenterPoint center,int angleFlag){
        double x0=center.getX(),y0=center.getY();
        double centreX=0,centreY=0;
        if(angleFlag==1) {
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.x[i], y1 = this.y[i];
                this.x[i] = y0 - y1 + x0;
                this.y[i] = x1 - x0 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        }
        else if(angleFlag==2){
            for (int i = 0; i < portPointNum; i++) {
                double x1 = this.x[i], y1 = this.y[i];
                this.x[i] = x0 - x1 + x0;;
                this.y[i] = y0 - y1 + y0;
                centreX += this.x[i];
                centreY += this.y[i];
            }
            this.centerPoint = new CenterPoint(centreX / portPointNum, centreY / portPointNum);
        }
        else if(angleFlag==3){
            for(int i=0;i<portPointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y1-y0+x0;
                this.y[i]=x0-x1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
        }
        else if(angleFlag==4){
            for(int i=0;i<portPointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=x0-x1+x0;
                this.y[i]=y1-y0+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
        }
        else if(angleFlag==5){
            for(int i=0;i<portPointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=x1-x0+x0;
                this.y[i]=y0-y1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
        }
        else if(angleFlag==6){
            for(int i=0;i<portPointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y0-y1+x0;
                this.y[i]=x0-x1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
        }
        else if(angleFlag==7){
            for(int i=0;i<portPointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y1-y0+x0;
                this.y[i]=x1-x0+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/portPointNum,centreY/portPointNum);
        }
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
