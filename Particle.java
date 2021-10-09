package Particle;

import java.io.Serializable;
import java.util.ArrayList;

public class Particle implements Cloneable,Serializable{
    private int pointNum, portN;
    private double[] x;
    private double[] y;
    private double maxX,maxY,minX,minY;
    private String name;
    public CenterPoint centerPoint=new CenterPoint(getCenterX(),getCenterY());
    ArrayList<Ports> portsArrayList=new ArrayList<Ports>();
    private double sumF=0;

//    @Override
//    public Particle clone() throws CloneNotSupportedException {
//        Particle p=new Particle();
//        p.name=this.name;
//        p.pointNum=this.pointNum;
//        p.x=this.x;
//        p.y=this.y;
//        p.maxX=this.maxX;p.maxY=this.maxY;p.minX=this.minX;p.minY=this.minY;
//        if(this.portsArrayList!=null) p.portsArrayList=new ArrayList<Ports>(this.portsArrayList);
//        p.sumF=this.sumF;
//        p.centerPoint=this.centerPoint.clone();
//        return p;
//    }

    public Particle() {
    }

    public double getX(int i){
        while (i>=pointNum) i-=pointNum;
        return x[i];
    }
    public double getY(int i){
        while (i>=pointNum) i-=pointNum;
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

    public double getSumF(int x){
        return  this.sumF;
    }

    public double getSumF(){
        double sum=0;
        for (Ports ports : portsArrayList) {
            sum += ports.sumDis;
        }
        return sum;
    }

    public void adjustAngle(int angleFlag){  //可优化   //逆时针为正方向
        double x0=getCenterX(),y0=getCenterY();
        double centreX=0,centreY=0;
        if(angleFlag==1) {
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y0-y1+x0;
                this.y[i]=x1-x0+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum); //中心应该不会变化
        }
        else if(angleFlag==2){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=x0-x1+x0;
                this.y[i]=y0-y1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        else if(angleFlag==3){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y1-y0+x0;
                this.y[i]=x0-x1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        else if(angleFlag==4){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=x0-x1+x0;
                this.y[i]=y1-y0+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        else if(angleFlag==5){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=x1-x0+x0;
                this.y[i]=y0-y1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        else if(angleFlag==6){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y0-y1+x0;
                this.y[i]=x0-x1+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        else if(angleFlag==7){
            for(int i=0;i<pointNum;i++){
                double x1=this.x[i],y1=this.y[i];
                this.x[i]=y1-y0+x0;
                this.y[i]=x1-x0+y0;
                centreX+=this.x[i];
                centreY+=this.y[i];
            }
            this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        }
        for (Ports ports : portsArrayList) {
            ports.adjustAngle(this.centerPoint, angleFlag);
        }
    }

    public void Move(double x,double y){
        double centreX=0,centreY=0;
        for(int i=0;i<pointNum;i++){
            this.x[i]+=x;
            this.y[i]+=y;

            centreX+=this.x[i];
            centreY+=this.y[i];
        }
        maxX+=x;
        minX+=x;
        maxY+=y;
        minY+=y;

        this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        for (Ports ports : portsArrayList) {
            ports.Move(x, y);
        }
    }

    public void Move2(double x,double y){
        double absX=x-this.getCenterX(),absY=y-this.getCenterY();
        double centreX=0,centreY=0;
        for(int i=0;i<pointNum;i++){
            this.x[i]+=absX;
            this.y[i]+=absY;

            centreX+=this.x[i];
            centreY+=this.y[i];
        }
        maxX+=absX;
        minX+=absX;
        maxY+=absY;
        minY+=absY;

        this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        for (Ports ports : portsArrayList) {
            ports.Move(absX, absY);
        }
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
    }

    public Particle(String name, int pointNum, double []x, double []y){
        this.x = x;
        this.y = y;
        this.name = name;
        this.pointNum = pointNum;
        double centreX=0,centreY=0;
        for(int i=0;i<pointNum;i++){
            centreX+=x[i];
            centreY+=y[i];
        }
        this.centerPoint=new CenterPoint(centreX/pointNum,centreY/pointNum);
        resetBoundary();
    }

    public void resetBoundary(){
        maxX=minX=x[0];maxY=minY=y[0];
        for(int i=0;i<pointNum;i++){
            if(maxX<x[i]) maxX=x[i];
            if(minX>x[i]) minX=x[i];
            if(maxY<y[i]) maxY=y[i];
            if(minY>y[i]) minY=y[i];
        }
    }

    public double getCenterX(){
        double sum=0;
        for(int i = 0; i< pointNum; i++)
            sum+=x[i];
        return sum/pointNum;
    }
    public double getCenterY(){
        double sum=0;
        for(int i = 0; i< pointNum; i++)
            sum+=y[i];
        return sum/pointNum;
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

    public double getCost(){
        return 0;
    }


}
class CenterPoint implements Cloneable, Serializable {
    double x,y;

    @Override
    public CenterPoint clone() throws CloneNotSupportedException {
        return (CenterPoint) super.clone();
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
