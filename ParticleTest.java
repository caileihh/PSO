package Particle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.SerializationUtils;

public class ParticleTest {
    private static MyPoint[] AreaBoundary=new MyPoint[4];  //顺时针为正方向
    private static final int ModuleNum=16;
    public static Particle[] p=new Particle[ModuleNum];
    public static MyPoint[] v=new MyPoint[ModuleNum];
    public static Particle[] pBest=new Particle[ModuleNum];   //个体最优
    public static Particle[] allBest=new Particle[ModuleNum];   //全局最优
    public static double bestF =0,allOverlap=0; //最佳总值,总overlap
    public static double allSumUp=0,bestSumOverlap=0;//总距离，总最佳overlap
    public static Random random=new Random();
//    public static double rand=random.nextDouble()*10;
    public static double c1=3,c2=1.2;
    public static ArrayList<ArrayList<Ports>> LinkSET=new ArrayList<>();
    public static void main(String[] args) throws CloneNotSupportedException {
        ReadAndInit();
        ReadConnectFile();
        Init();
        PSO(2000);
    }

    public static void PSO(int max) throws CloneNotSupportedException {
        for(int i=0;i<max;i++){
            double w=0.3;
            for(int j=0;j<ModuleNum;j++){
                //速度更新 注意界限
                double vx=w*v[j].x+c1*random.nextDouble()*10*(pBest[j].getCenterX()-p[j].getCenterX())+c2*random.nextDouble()*10*(allBest[j].getCenterX()-p[j].getCenterX());
                double vy=w*v[j].y+c1*random.nextDouble()*10*(pBest[j].getCenterY()-p[j].getCenterY())+c2*random.nextDouble()*10*(allBest[j].getCenterY()-p[j].getCenterY());
                if(random.nextDouble()>=0.5)
                    vx *= random.nextDouble()*100;
                else
                    vx*=-100*random.nextDouble();
                if(random.nextDouble()>=0.5)
                    vy *= 100*random.nextDouble();
                else
                    vy*=-100*random.nextDouble();

                //出界限制
                if(vx>AreaBoundary[2].getX()) vx=AreaBoundary[2].getX();
                else if(vx<AreaBoundary[0].getX()) vx=AreaBoundary[0].getX();
                if(vy>AreaBoundary[2].getY()) vy=AreaBoundary[2].getY();
                else if(vx<AreaBoundary[0].getY()) vy=AreaBoundary[0].getY();

                v[j]=new MyPoint(vx,vy);

                p[j].Move(vx,vy);

                boolean moveAble1=false,moveAble2=false;
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
                    System.out.println("Changed:"+j);
                }
                tempSum+=p[j].getSumF(1);
            }

//            double tempOverlap=0;
//            for(int k=0;k<ModuleNum;k++)
//                for(int j=0;j<ModuleNum;j++){
//                    if(j==k) continue;
//                    else{
//                        tempOverlap+=calOverlap(p[k],p[j]);
//                    }
//                }

            if(bestF >tempSum/*+tempOverlap*/) {
                bestF = tempSum/*+tempOverlap*/;
                for(int j=0;j<ModuleNum;j++)
                    allBest[j]=(Particle) SerializationUtils.clone(p[j]);
            }
            System.out.println("==="+i+"==="+ bestF);
        }
        for(int i=0;i<ModuleNum;i++){
            System.out.println(p[i].getCenterX()+"==="+p[i].getCenterY());
        }
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
//        System.arraycopy(p, 0, pBest, 0, ModuleNum);
//        System.arraycopy(p, 0, allBest, 0, ModuleNum);
        for(int i=0;i<ModuleNum;i++)  //↑
        {
            pBest[i]=(Particle) SerializationUtils.clone(p[i]);
            allBest[i]=(Particle) SerializationUtils.clone(p[i]);
        }
        bestF =allSumUp+bestSumOverlap;
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
        if(p1.getMaxX()<p2.getMinX()||p1.getMinX()>p2.getMaxX()||
                p1.getMaxY()<p2.getMinY()||p1.getMinY()>p2.getMaxY()) return 0;
        if(p1.getMaxX()<p2.getMaxX()&&p1.getMaxY()<p2.getMaxY()) return 10000;
        if(p1.getMinX()>p2.getMinY()&&p1.getMinY()>p2.getMinY()) return 10000;
        return 0;
    }

    private static Pattern pattern = Pattern.compile("-?[0-9]+(.[0-9]+)?");
    private static boolean isNumber(String str) {
        // 通过Matcher进行字符串匹配
        Matcher m = pattern.matcher(str);
        // 如果正则匹配通过 m.matches() 方法返回 true ，反之 false
        return m.matches();
    }
    public static void ReadConnectFile(){
        List<String> list=readFile("D:\\Chrome下载\\sample\\16ModuleCase\\16-10001\\connect_1.txt");
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
        List<String> list=readFile("D:\\Chrome下载\\sample\\16ModuleCase\\16-10001\\Module.txt");
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
                for(String str:tempList){
                    if(str.equals("")) continue;
                    if(str.contains(");")) str=str.substring(0,str.indexOf(")"));
                    str=str.replace(")","");
                    double tempX=Double.parseDouble(str.substring(0,str.indexOf(",")));
                    double tempY=Double.parseDouble(str.substring(str.indexOf(" ")+1));
                    x.add(tempX);y.add(tempY);
                }
                double []x0=new double[x.size()],y0=new double[y.size()];
                for(int j=0;j<x.size();j++){
                    x0[j]=x.get(j);y0[j]=y.get(j);
                }
                p[ModuleX-1]=new Particle(strName,x.size(),x0,y0);

                while (list.get(i+1).contains("Port:")) {
                    temp = list.get(++i);
                    tempList=temp.replace("Port:","").trim().split("\\(");
                    x.clear();y.clear();
                    for(String str:tempList){
                        if(str.equals("")) continue;
                        if(str.contains(");")) str=str.substring(0,str.indexOf(")"));
                        str=str.replace(")","");
                        double tempX=Double.parseDouble(str.substring(0,str.indexOf(",")));
                        double tempY=Double.parseDouble(str.substring(str.indexOf(" ")+1));
                        x.add(tempX);y.add(tempY);
                    }
                    double []x1=new double[x.size()],y1=new double[y.size()];
                    for(int j=0;j<x.size();j++){
                        x1[j]=x.get(j);y1[j]=y.get(j);
                    }
                    Ports tempPort=new Ports(x.size(),x1,y1);
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
}

