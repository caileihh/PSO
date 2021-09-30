package Particle;

public class Ports{
    private int portPointNum;
    private double[] x=new double[portPointNum];
    private double[] y=new double[portPointNum];
    private CenterPoint centerPoint=new CenterPoint(getCenterX(),getCenterY());
    public double sumDis;

    public Ports(int portPointNum,double[] x,double[] y) {
        this.portPointNum=portPointNum;
        this.x=x;
        this.y=y;
    }


    public void Move(double x,double y){
        for(int i=0;i<portPointNum;i++)
        {
            this.x[i]+=x;
            this.y[i]+=y;
        }
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
