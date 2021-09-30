package Particle;

import java.util.ArrayList;

public class Particle {
    private int pointNum, portN;
    private double[] x;
    private double[] y;
    private double maxX,maxY,minX,minY;
    private String name;
    public CenterPoint centerPoint=new CenterPoint(getCenterX(),getCenterY());
    ArrayList<Ports> portsArrayList=new ArrayList<Ports>();
    private double sumF;

    public void setSumF(double sumF) {
        this.sumF = sumF;
    }

//    public double getSumF(int x){
//        return  this.sumF;
//    }

    public double getSumF(){
        double sum=0;
        for(int i=0;i<portsArrayList.size();i++){
            sum+=portsArrayList.get(i).sumDis;
        }
        return sum;
    }

    public void Move(double x,double y){
        for(int i=0;i<pointNum;i++){
            this.x[i]+=x;
            this.y[i]+=y;
            maxX+=x;
            minX+=x;
            maxY+=y;
            minY+=y;
        }
        for(int i=0;i<portsArrayList.size();i++){
            portsArrayList.get(i).Move(x,y);
        }
    }

    public Particle(int pointNum, int portN, double[] x, double[] y, String name, CenterPoint centerPoint, ArrayList<Ports> portsArrayList) {
        this.pointNum = pointNum;
        this.portN = portN;
        this.x = x;
        this.y = y;
        this.name = name;
        this.centerPoint = centerPoint;
        this.portsArrayList = portsArrayList;

        Resetboundary();
    }

    public Particle(String name, int pointNum, double []x, double []y){
        this.x = x;
        this.y = y;
        this.name = name;
        this.pointNum = pointNum;
        Resetboundary();
    }

    public void Resetboundary(){
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
        return sum;
    }
    public double getCenterY(){
        double sum=0;
        for(int i = 0; i< pointNum; i++)
            sum+=y[i];
        return sum;
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
class CenterPoint {
    double x,y;

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
