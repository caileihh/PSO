package Particle;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.SerializationUtils;

import javax.swing.*;

public class ParticleTest {
    public static MyPoint[] AreaBoundary=new MyPoint[4];  //顺时针为正方向
    public static final int ModuleNum=16;
    public static final String  fileNumber="16-12510";
    public static Particle[] p=new Particle[ModuleNum];
    public static MyPoint[] v=new MyPoint[ModuleNum];
    public static Particle[] pBest=new Particle[ModuleNum];   //个体最优
    public static Particle[] allBest=new Particle[ModuleNum];   //全局最优
    public static double bestF =0,bestOverlap=0,eps=1e-6; //最佳总值,总overlap
    public static double allSumUp=0,bestSumOverlap=0;//总距离，总最佳overlap
    public static double shellWidth = 17.5;
    public static Random random=new Random();
//    public static double rand=random.nextDouble()*10;
    public static double c1=3,c2=1.2,disRate=0.001,overlapRate=10000;
    public static ArrayList<ArrayList<Ports>> LinkSET=new ArrayList<>();

    public static MyPoint[] p1=new MyPoint[6],p2=new MyPoint[4];

    public static void main(String[] args) throws CloneNotSupportedException {
        Long startTime = System.currentTimeMillis();

        ReadAndInit();
        ReadConnectFile();
        Init();

//        PSO(200);
        qLearning(1);

//        TestMyself();

        OutPutResultTxtFile(Transition());
        Long endTime = System.currentTimeMillis();
        double time=((double)(endTime - startTime))/1000;
        System.out.println("花费时间" + (time) + "s");
        Draw();
    }

    private static int getRandomNumberInRange(int min, int max) { //含min和max

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void TestMyself(){
        p[13].Move(270,435);
        judgeOutOfBounds(p[13]);
    }

    public static void qLearning(int maxN){    //0-11: stay 上下左右 90 180 270 MX MY MXR90 MYR90
        double epsilon=0;
//        double StepLength=50;
        while (maxN--!=0){
            for(int i=0;i<ModuleNum;i++) p[i].Move2(400,400);
            for(int j=0;j<200;j++){
                int []randArr=randomCommon(ModuleNum,ModuleNum);
                for (int i : randArr) {  //可改为随机扰动
                    double StepLength=100*Math.random();
                    if(j<=100)
                        StepLength=100*Math.random();
                    else StepLength=300*Math.random();
                    if (Math.random() < epsilon&&j<30) {
                        int tempStep = getRandomNumberInRange(0, 11);
                        if (tempStep >= 1 && tempStep <= 4) {
                            if (tempStep == 4) p[i].Move(StepLength, 0);
                            else if (tempStep == 3) p[i].Move(-StepLength, 0);
                            else if (tempStep == 2) p[i].Move(0, -StepLength);
                            else p[i].Move(0, StepLength);
                        } else if (tempStep >= 5) {
                            p[i].adjustAngle(tempStep - 4);
                        }

                        judgeOutOfBounds(p[i]);
                    } else {
                        int bestStep = 0;
                        double tempSum = Double.MAX_VALUE;
                        double bestStepLength=StepLength;
                        for (int tempStep = 0; tempStep <= 11; tempStep++) {
//                            if(j<=100)
                                StepLength=100*Math.random();
//                            else StepLength=700*Math.random();
                            Particle tempParticle = SerializationUtils.clone(p[i]);
                            if (tempStep >= 1 && tempStep <= 4) {
                                if (tempStep == 4) tempParticle.Move(StepLength, 0);
                                else if (tempStep == 3) tempParticle.Move(-StepLength, 0);
                                else if (tempStep == 2) tempParticle.Move(0, -StepLength);
                                else tempParticle.Move(0, StepLength);
                            } else if (tempStep >= 5) {
                                tempParticle.adjustAngle(tempStep - 4);
                            }

                            judgeOutOfBounds(tempParticle);

                            double tempOverlap = 0, tempDis = 0;
                            for (int k = 0; k < ModuleNum; k++) {
                                if(k==i) continue;
                                tempOverlap += calOverlap(tempParticle, p[k]);
                            }
                            for (ArrayList<Ports> ports : LinkSET) {
                                for (int tempJ = 0; tempJ < ports.size(); tempJ++) {
                                    if (p[i].portsArrayList.contains(ports.get(tempJ))) {
                                        for (int tempI = 0; tempI < ports.size(); tempI++) {
                                            if (tempI == tempJ) continue;
                                            tempDis += getDist(tempParticle.portsArrayList.get(p[i].portsArrayList.indexOf(ports.get(tempJ))).getCenterPoint(), ports.get(tempI).getCenterPoint());
                                        }
                                    }
                                }
                            }
                            if (tempDis * disRate + tempOverlap * overlapRate < tempSum) {
                                bestStep = tempStep;
                                tempSum = tempDis * disRate + tempOverlap * overlapRate;
                                bestStepLength=StepLength;
                            }
                        }
                        if (bestStep >= 1 && bestStep <= 4) {
                            if (bestStep == 4) p[i].Move(bestStepLength, 0);   //StepLength's Problem
                            else if (bestStep == 3) p[i].Move(-bestStepLength, 0);
                            else if (bestStep == 2) p[i].Move(0, -bestStepLength);
                            else p[i].Move(0, bestStepLength);
                        } else if (bestStep >= 5) {
                            p[i].adjustAngle(bestStep - 4);
                        }
                        judgeOutOfBounds(p[i]);
                    }
                }
                System.out.println(j);
            }

            shellWidth=12.5;
            for(int i=0;i<ModuleNum;i++) p[i].resetOutShell();

            for(int j=0;j<10;j++){
                int []randArr=randomCommon(ModuleNum,ModuleNum);
                for (int i : randArr) {  //可改为随机扰动
                    int bestStep = 0,bestAngle=0;
                    double StepLength=1000*Math.random();
                    double bestStepLength=StepLength;
//                    if(j<=50)
                        StepLength=100*Math.random();
//                    else StepLength=300*Math.random();
                    if (Math.random() < epsilon) {
                        int tempStep = getRandomNumberInRange(0, 11);
                        if (tempStep >= 1 && tempStep <= 4) {
                            if (tempStep == 4) p[i].Move(StepLength, 0);
                            else if (tempStep == 3) p[i].Move(-StepLength, 0);
                            else if (tempStep == 2) p[i].Move(0, -StepLength);
                            else p[i].Move(0, StepLength);
                        } else if (tempStep >= 5) {
                            p[i].adjustAngle(tempStep - 4);
                        }

                        judgeOutOfBounds(p[i]);
                    } else {

                        double tempSum = Double.MAX_VALUE;

                        for (int tempStep = 0; tempStep <= 4; tempStep++) {
                            for (int angle = 0; angle <= 7; angle++) {
//                                if (j <= 50)
                                    StepLength = 100 * Math.random();
//                                else StepLength = 700 * Math.random();
                                Particle tempParticle = SerializationUtils.clone(p[i]);
                                if (tempStep >= 1) {
                                    if (tempStep == 4) tempParticle.Move(StepLength, 0);
                                    else if (tempStep == 3) tempParticle.Move(-StepLength, 0);
                                    else if (tempStep == 2) tempParticle.Move(0, -StepLength);
                                    else tempParticle.Move(0, StepLength);
                                    judgeOutOfBounds(tempParticle);
                                }
                                tempParticle.adjustAngle(angle);

                                judgeOutOfBounds(tempParticle);

                                double tempOverlap = 0, tempDis = 0;
                                for (int k = 0; k < ModuleNum; k++) {
                                    if (k == i) continue;
                                    tempOverlap += calOverlap(tempParticle, p[k]);
                                }
                                for (ArrayList<Ports> ports : LinkSET) {
                                    for (int tempJ = 0; tempJ < ports.size(); tempJ++) {
                                        if (p[i].portsArrayList.contains(ports.get(tempJ))) {
                                            for (int tempI = 0; tempI < ports.size(); tempI++) {
                                                if (tempI == tempJ) continue;
                                                tempDis += getDist(tempParticle.portsArrayList.get(p[i].portsArrayList.indexOf(ports.get(tempJ))).getCenterPoint(), ports.get(tempI).getCenterPoint());
                                            }
                                        }
                                    }
                                }
                                if (tempDis * disRate + tempOverlap * overlapRate < tempSum) {
                                    bestStep = tempStep;
                                    bestAngle=angle;
                                    tempSum = tempDis * disRate + tempOverlap * overlapRate;
                                    bestStepLength = StepLength;
                                }
                            }
                        }
                        if (bestStep >= 1) {
                            if (bestStep == 4) p[i].Move(bestStepLength, 0);   //StepLength's Problem
                            else if (bestStep == 3) p[i].Move(-bestStepLength, 0);
                            else if (bestStep == 2) p[i].Move(0, -bestStepLength);
                            else p[i].Move(0, bestStepLength);
                            judgeOutOfBounds(p[i]);
                        }
                        p[i].adjustAngle(bestAngle);
                        judgeOutOfBounds(p[i]);
                    }
                }
                System.out.println(j);
            }

            System.out.println(maxN);

            for (ArrayList<Ports> ports : LinkSET) {
                for (int j = 0; j < ports.size(); j++) {
                    ports.get(j).setSumDis(0);
                    for (int k = 0; k < ports.size(); k++) {
                        if (k == j) continue;
                        ports.get(j).sumDis += getDist(ports.get(j).getCenterPoint(), ports.get(k).getCenterPoint());
                    }
                }
            }

            fitnessFunction();
            double tempSum=0;
            for(int j=0;j<ModuleNum;j++){  //更新个体最优
                if(p[j].getSumF()<pBest[j].getSumF()) {
                    pBest[j] = (Particle) SerializationUtils.clone(p[j]);
//                    System.out.println("Changed:"+j);
                }
                tempSum+=p[j].getSumF(1);
            }

            double tempOverlap=0;
            for(int k=0;k<ModuleNum;k++)
                for(int j=0;j<ModuleNum;j++){
                    if(j!=k){
                        tempOverlap+=(calOverlap(p[k],p[j]));
                    }
                }

            if(bestF >tempSum*disRate+tempOverlap*overlapRate) {
                bestF = tempSum*disRate+tempOverlap*overlapRate;
                bestOverlap=tempOverlap;
                System.arraycopy(p, 0, allBest, 0, ModuleNum);
            }
        }
    }

    public static void judgeOutOfBounds(Particle par){
        double vx = 0, vy = 0;  //出界判断
        if (par.getMaxX() > AreaBoundary[2].getX()-shellWidth) {
            vx = AreaBoundary[2].getX() - par.getMaxX()-shellWidth;
        } else if (par.getMinX() < AreaBoundary[0].getX()+shellWidth) {
            vx = AreaBoundary[0].getX() - par.getMinX()+shellWidth;
        }
        if (par.getMaxY() > AreaBoundary[2].getY()-shellWidth) {
            vy = AreaBoundary[2].getY() - par.getMaxY()-shellWidth;
        } else if (par.getMinY() < AreaBoundary[0].getY()+shellWidth) {
            vy = AreaBoundary[0].getY() - par.getMinY()+shellWidth;
        }
        par.Move(vx, vy);
    }

    public static int[] randomCommon(int max, int n){
        if(max<0||n<=0) return new int[1];
        Random random=new Random();
        int[] result = new int[n],flag=new int[n];
        for(int i=0;i<result.length;i++) {
            int x = random.nextInt(n);
            while (flag[x]==1) x=random.nextInt(n);
            result[i]=x;
            flag[x]=1;
        }
        return result;
    }

    public static void PSO(int max) {
        for(int i=0;i<max;i++){
            double w=0.3;
            for(int j=0;j<ModuleNum;j++){
                //速度更新 注意界限
//                double vx=w*v[j].x+c1*random.nextDouble()*10*(pBest[j].getCenterX()-p[j].getCenterX())+c2*random.nextDouble()*10*(allBest[j].getCenterX()-p[j].getCenterX());
//                double vy=w*v[j].y+c1*random.nextDouble()*10*(pBest[j].getCenterY()-p[j].getCenterY())+c2*random.nextDouble()*10*(allBest[j].getCenterY()-p[j].getCenterY());
                double vx=random.nextDouble(),vy=random.nextDouble();
                if(random.nextDouble()>=0.5)
                    vx *= random.nextDouble()*100;
                else
                    vx*=-100*random.nextDouble();
                if(random.nextDouble()>=0.5)
                    vy *= 100*random.nextDouble();
                else
                    vy*=-100*random.nextDouble();
                vx/=10;vy/=10;

                //出界限制
                if(vx>AreaBoundary[2].getX()) vx=AreaBoundary[2].getX();
                else if(vx<AreaBoundary[0].getX()) vx=AreaBoundary[0].getX();
                if(vy>AreaBoundary[2].getY()) vy=AreaBoundary[2].getY();
                else if(vx<AreaBoundary[0].getY()) vy=AreaBoundary[0].getY();
                v[j]=new MyPoint(vx,vy);

//                v[j].angleFlag=getRandomNumberInRange(0,7);
//                p[j].adjustAngle(v[j].angleFlag);

                p[j].Move(vx,vy);

                boolean moveAble1=false,moveAble2=false;  //正确性存疑
                if(p[j].getMaxX()>AreaBoundary[2].getX()) {
                    vx = AreaBoundary[2].getX();
                    moveAble1 = true;
                }
                else if(p[j].getMinX()<AreaBoundary[0].getX()) {
                    vx = AreaBoundary[0].getX();
                    moveAble1 = true;
                }
                if(p[j].getMaxY()>AreaBoundary[2].getY()) {
                    vy = AreaBoundary[2].getY();
                    moveAble2=true;
                }
                else if(p[j].getMinY()<AreaBoundary[0].getY()) {
                    vy = AreaBoundary[0].getY();
                    moveAble2=true;
                }
                if(moveAble1&&moveAble2) p[j].Move2(vx,vy);
                else if(moveAble1) p[j].Move2(vx,p[j].getCenterY());
                else if(moveAble2) p[j].Move2(p[j].getCenterX(),vy);
            }


            for (ArrayList<Ports> ports : LinkSET) {
                for (int j = 0; j < ports.size(); j++) {
                    ports.get(j).setSumDis(0);
                    for (int k = 0; k < ports.size(); k++) {
                        if (k == j) continue;
                        ports.get(j).sumDis += getDist(ports.get(j).getCenterPoint(), ports.get(k).getCenterPoint());
                    }
                }
            }

            fitnessFunction();
            double tempSum=0;
            for(int j=0;j<ModuleNum;j++){  //更新个体最优
                if(p[j].getSumF()<pBest[j].getSumF()) { ///浅拷贝问题！！！！！！！！！！
                    pBest[j] = (Particle) SerializationUtils.clone(p[j]);
//                    System.out.println("Changed:"+j);
                }
                tempSum+=p[j].getSumF(1);
            }

            double tempOverlap=0;
            for(int k=0;k<ModuleNum;k++)
                for(int j=0;j<ModuleNum;j++){
                    if(j==k) continue;
                    else{
                        tempOverlap+=(calOverlap(p[k],p[j]));
                    }
                }

            if(bestF >tempSum+tempOverlap) {
                bestF = tempSum+tempOverlap;
                bestOverlap=tempOverlap;
                for(int j=0;j<ModuleNum;j++)
                    allBest[j]=(Particle) SerializationUtils.clone(p[j]);
            }
            System.out.println("==="+i+"==="+ bestF);
        }
        for(int i=0;i<ModuleNum;i++){
            System.out.println(i+":"+p[i].getMaxY()+"==="+p[i].getMaxY());
        }
        System.out.println(bestOverlap);
    }

    public static void Init() throws CloneNotSupportedException {
        for(int i=0;i<ModuleNum;i++){
            v[i]=new MyPoint(random.nextDouble()*10,random.nextDouble()*10);
        }
        for (ArrayList<Ports> ports : LinkSET) {
            for (int j = 0; j < ports.size(); j++) {
                for (int k = 0; k < ports.size(); k++) {
                    if (k == j) continue;
                    ports.get(j).sumDis += getDist(ports.get(j).getCenterPoint(), ports.get(k).getCenterPoint());
                }
            }
        }
        fitnessFunction();
        for(int i=0;i<ModuleNum;i++)
            for(int j=0;j<ModuleNum;j++){
                if(j==i) continue;
                bestSumOverlap+=calOverlap(p[i],p[j]);
            }
        for(int i=0;i<ModuleNum;i++)  //↑
        {
            pBest[i]=(Particle) SerializationUtils.clone(p[i]);
            allBest[i]=(Particle) SerializationUtils.clone(p[i]);
        }
        bestF =allSumUp*disRate+bestSumOverlap*overlapRate;
    }

    public static void fitnessFunction(){
        allSumUp=0;
        double tempSumOverlap=0;
        for(int i=0;i<ModuleNum;i++){
            double x=p[i].getSumF();
            p[i].setSumF(x);
            allSumUp+=x;
        }
    }

    public static double getDist(CenterPoint p1, CenterPoint p2){
        return Math.hypot(p1.getX()-p2.getX(),p1.getY()-p2.getY());
    }

    public static double calOverlap(Particle p1,Particle p2){
//        if(p1.getMaxX()<p2.getMinX()||p1.getMinX()>p2.getMaxX()||
//                p1.getMaxY()<p2.getMinY()||p1.getMinY()>p2.getMaxY()) return 0;
//        if(p1.getMaxX()<=p2.getMaxX()&&p1.getMaxY()<=p2.getMaxY()) return 10000;
//        if(p1.getMinX()>=p2.getMinY()&&p1.getMinY()>=p2.getMinY()) return 10000;
//        return 0;
        MyPoint[] myPoint1=new MyPoint[p1.getPointNum()],myPoint2=new MyPoint[p2.getPointNum()];
        for(int i=0;i<p1.getPointNum();i++) myPoint1[i]=new MyPoint(p1.getShellX(i),p1.getShellY(i));
        for(int i=0;i<p2.getPointNum();i++) myPoint2[i]=new MyPoint(p2.getShellX(i),p2.getShellY(i));
        return Math.abs(SPIA(myPoint1,myPoint2,p1.getPointNum(),p2.getPointNum()));
    }

    private static final Pattern pattern = Pattern.compile("-?[0-9]+(.[0-9]+)?");
    private static boolean isNumber(String str) {
        // 通过Matcher进行字符串匹配
        Matcher m = pattern.matcher(str);
        // 如果正则匹配通过 m.matches() 方法返回 true ，反之 false
        return m.matches();
    }


    public static java.util.List<String> Transition(){
        List<String> tempList=readFile("D:\\Chrome下载\\sample\\16ModuleCase\\"+fileNumber+"\\Module.txt");
        List<String> list = new ArrayList<>();
        for(int i=0;i<2;i++) list.add(tempList.get(i));
        for(int i=0;i<ModuleNum;i++){
            StringBuilder str= new StringBuilder("Module:");
            str = new StringBuilder(str.toString().concat(p[i].getName()));
            list.add(str.toString());
            str = new StringBuilder("Boundary:");
            for(int j=0;j<p[i].getPointNum();j++){
                str.append("(").append(String.format("%.1f",p[i].getX(j))).append(", ").append(String.format("%.1f",p[i].getY(j))).append(")");
            }
            str.append(";").append(p[i].getRuleName());
            list.add(str.toString());
            for(int j=0;j<p[i].portsArrayList.size();j++){
                str=new StringBuilder("Port:");
                for(int k=0;k<p[i].portsArrayList.get(j).getPortPointNum();k++) {
                    str.append("(").append(String.format("%.1f",p[i].portsArrayList.get(j).getX(k))).append(", ").append(String.format("%.1f",p[i].portsArrayList.get(j).getY(k))).append(")");
                }
                str.append(";").append(p[i].portsArrayList.get(j).getRuleName());
                list.add(str.toString());
            }
        }
        return list;
    }
    public static void OutPutResultTxtFile(List<String> list){
        try {
            String content = "测试使用字符串";
            File file = new File("D:\\Chrome下载\\sample\\16ModuleCase\\"+fileNumber+"\\result.txt");
            //文件不存在时候，主动创建文件。
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,false);  //上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : list) {
                bw.write(s + "\n");
            }
//            bw.write(content);
            bw.close(); fw.close();
            System.out.println("test done!");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void ReadConnectFile(){
        List<String> list=readFile("D:\\Chrome下载\\sample\\16ModuleCase\\"+fileNumber+"\\connect_1.txt");
        for(int line=0;line<list.size();line++){
            String temp=list.get(line);
            System.out.println(temp);
            if(temp.matches("Link(.*)")){
                temp=list.get(++line);
                String[] ModuleList=temp.split(" ");
                String[] PortList=list.get(++line).split(" ");
                ArrayList<Ports> linkArr=new ArrayList<Ports>();
                for(int i=0;i<ModuleList.length;i++){
                    int ModuleN=Integer.parseInt(ModuleList[i].substring(ModuleList[i].indexOf("M")+1));
                    int PortN=Integer.parseInt(PortList[i]);
                    linkArr.add(p[ModuleN-1].portsArrayList.get(PortN-1));
                }
                LinkSET.add(linkArr);
            }
        }
    }

    public static void ReadAndInit(){
        List<String> list=readFile("D:\\Chrome下载\\sample\\16ModuleCase\\"+fileNumber+"\\Module.txt");
        for(int i=0;i<list.size();i++)
        {
            String temp=list.get(i);
            System.out.println(temp);
            if(temp.matches("Area:(.*)")){
                String[] templist=temp.replace("Area:","").trim().split("\\(");
                int j=0;
                for(String str:templist){
                    if(str.equals("")) continue;
                    str=str.replace(")","");
                    double tempX=Double.parseDouble(str.substring(0,str.indexOf(",")));
                    double tempY=Double.parseDouble(str.substring(str.indexOf(" ")+1));
                    AreaBoundary[j]=new MyPoint(tempX,tempY);
                    j++;
                }
            }
            if(temp.matches("Module:(.*)")){
                int ModuleX=Integer.parseInt(temp.substring(temp.indexOf(":M")+2));
                String strName=temp.substring(temp.indexOf(":")+1);
                temp=list.get(++i);
                String[] tempList=temp.replace("Boundary:","").trim().split("\\(");
                ArrayList<Double> x=new ArrayList<>(),y=new ArrayList<>();
                String ruleNa = null;
                for(String str:tempList){
                    if(str.equals("")) continue;
                    if(str.contains(");")) {
                        ruleNa=str.substring(str.indexOf(";")+1);
                        str = str.substring(0, str.indexOf(")"));
                    }
                    str=str.replace(")","");
                    double tempX=Double.parseDouble(str.substring(0,str.indexOf(",")));
                    double tempY=Double.parseDouble(str.substring(str.indexOf(" ")+1));
                    x.add(tempX);y.add(tempY);
                }
                double []x0=new double[x.size()],y0=new double[y.size()];
                for(int j=0;j<x.size();j++){
                    x0[j]=x.get(j);y0[j]=y.get(j);
                }
                p[ModuleX-1]=new Particle(strName,x.size(),x0,y0,ruleNa);

                while (list.get(i+1).contains("Port:")) {

                    temp = list.get(++i);
                    tempList=temp.replace("Port:","").trim().split("\\(");
                    x.clear();y.clear();
                    for(String str:tempList){
                        if(str.equals("")) continue;
                        if(str.contains(");")) {
                            ruleNa=str.substring(str.indexOf(";")+1);
                            str = str.substring(0, str.indexOf(")"));
                        }
                        str=str.replace(")","");
                        double tempX=Double.parseDouble(str.substring(0,str.indexOf(",")));
                        double tempY=Double.parseDouble(str.substring(str.indexOf(" ")+1));
                        x.add(tempX);y.add(tempY);
                    }
                    double []x1=new double[x.size()],y1=new double[y.size()];
                    for(int j=0;j<x.size();j++){
                        x1[j]=x.get(j);y1[j]=y.get(j);
                    }
                    Ports tempPort=new Ports(x.size(),x1,y1,ruleNa);
                    p[ModuleX-1].portsArrayList.add(tempPort);
                    if(i+1==list.size()) break;
                }
            }
        }
    }

    public static java.util.List<String> readFile(String filepath) {
        List<String> list = new ArrayList<String>();
        try {
            String encoding = "UTF-8";
            File file = new File(filepath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader br = new BufferedReader(read);
                String linetxt = null;
                while ((linetxt = br.readLine()) != null) list.add(linetxt);
                br.close();
                read.close();
            } else System.out.println("Can't find file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int dcmp(double x){
        if(x>eps) return 1;
        return x<-eps? -1:0;
    }
    public static double cross(MyPoint a,MyPoint b,MyPoint c){
        return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
    }
    public static MyPoint intersection(MyPoint a,MyPoint b,MyPoint c,MyPoint d){
        MyPoint p=(MyPoint) SerializationUtils.clone(a);
        double t = ((a.x - c.x) * (c.y - d.y) - (a.y - c.y) * (c.x - d.x)) / ((a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x));
        p.x += (b.x - a.x) * t;
        p.y += (b.y - a.y) * t;
        return p;
    }
    public static double PolygonArea(MyPoint[] p, int n){
        if (n < 3) return 0.0;
        double s = p[0].y * (p[n - 1].x - p[1].x);
        for (int i = 1; i < n - 1; ++i) {
            s += p[i].y * (p[i - 1].x - p[i + 1].x);
        }
        s += p[n - 1].y * (p[n - 2].x - p[0].x);
        return Math.abs(s * 0.5);
    }
    public static double CPIA(MyPoint[] a,MyPoint[] b,int na,int nb){
        MyPoint []p=new MyPoint[20]; MyPoint []tmp=new MyPoint[20];
        int tn, sflag, eflag;

        p=(MyPoint[]) SerializationUtils.clone(b);           /////////

        for (int i = 0; i < na && nb > 2; i++)
        {
            if (i == na - 1) {
                sflag = dcmp(cross(a[0], p[0], a[i]));
            }
            else {
                sflag = dcmp(cross(a[i + 1], p[0], a[i]));
            }
            for (int j = tn = 0; j < nb; j++, sflag = eflag)
            {
                if (sflag >= 0) {
                    tmp[tn++] = (MyPoint) SerializationUtils.clone(p[j]);
                }
                if (i == na - 1) {
                    if (j == nb - 1) {
                        eflag = dcmp(cross(a[0], p[0], a[i]));
                    }
                    else {
                        eflag = dcmp(cross(a[0], p[j + 1], a[i])); //计算下一个连续点在矢量线段的位置
                    }
                }
                else {
                    if (j == nb - 1) {
                        eflag = dcmp(cross(a[i + 1], p[0], a[i]));
                    }
                    else {
                        eflag = dcmp(cross(a[i + 1], p[j + 1], a[i]));
                    }
                }
                if ((sflag ^ eflag) == -2) {  //1和-1的异或为-2，也就是两个点分别在矢量线段的两侧
                    if (i == na - 1) {
                        if (j == nb - 1) {
                            tmp[tn++] = (MyPoint) SerializationUtils.clone(intersection(a[i], a[0], p[j], p[0])); //求交点
                        }
                        else {
                            tmp[tn++] = (MyPoint) SerializationUtils.clone(intersection(a[i], a[0], p[j], p[j + 1]));
                        }
                    }
                    else {
                        if (j == nb - 1) {
                            tmp[tn++] = (MyPoint) SerializationUtils.clone(intersection(a[i], a[i + 1], p[j], p[0]));
                        }
                        else {
                            tmp[tn++] = (MyPoint) SerializationUtils.clone(intersection(a[i], a[i + 1], p[j], p[j + 1]));
                        }
                    }
                }
            }
            p=(MyPoint[]) SerializationUtils.clone(tmp);       //////SerializationUtils.clone
            nb = tn;p[nb] = p[0];
        }
        if (nb < 3) return 0.0;
        return PolygonArea(p, nb);
    }
    public static double SPIA(MyPoint a[], MyPoint b[], int na, int nb){
        int i, j;
        MyPoint []t1=new MyPoint[na],t2=new MyPoint[nb];
        double res = 0, num1, num2;
        t1[0] = (MyPoint) SerializationUtils.clone(a[0]); t2[0] = (MyPoint) SerializationUtils.clone(b[0]);
        for(i = 2; i < na; i++)
        {
            t1[1] = (MyPoint) SerializationUtils.clone(a[i-1]); t1[2] = (MyPoint) SerializationUtils.clone(a[i]);
            num1 = dcmp(cross(t1[1], t1[2],t1[0]));  //根据差积公式来计算t1[2]在矢量线段（t1[0], t1[1]）的左侧还是右侧，
            //值为负数在矢量线段左侧，值为正数在矢量线段右侧
            if(num1 < 0) swap(t1[1], t1[2]);  // 按逆时针进行排序
            for(j = 2; j < nb; j++)
            {
                t2[1] = (MyPoint) SerializationUtils.clone(b[j - 1]); t2[2] = (MyPoint) SerializationUtils.clone(b[j]);
                num2 = dcmp(cross(t2[1], t2[2],t2[0]));
                if(num2 < 0) swap(t2[1], t2[2]);
                res += CPIA(t1, t2, 3, 3) * num1 * num2;
            }
        }
        return res;
    }
    public static void swap(MyPoint p1,MyPoint p2){   ///?
        MyPoint temp=new MyPoint(p1.getX(),p1.getY());
        p1.setX(p2.getX());p1.setY(p2.getY());
        p2.setX(temp.getX());p2.setY(temp.getY());
    }


    private static void Draw(){
        jpanelTest jpanelTest=new jpanelTest();
        jpanelTest.setBackground(Color.BLACK);
        jpanelTest.setBounds(0, 0, 1000, 1000);
        jpanelTest.setPreferredSize(new Dimension(2000, 2000));
        JFrame frame = new JFrame("PSO");
        JScrollPane jScrollPane = new JScrollPane(jpanelTest);
        jScrollPane.setBounds(100, 100, 350, 450);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        frame.getContentPane().add(jScrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1500 + 20);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

}

