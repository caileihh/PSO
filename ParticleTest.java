import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.swing.*;

public class ParticleTest {
    public static MyPoint[] AreaBoundary;  //顺时针为正方向
    public static int ModuleNum = 50,id=0;
    public static String fileNumber = "16-11004";        //总：0~20、100~120   5      13  18  20  101 102 持平：19 105  109
    public static Particle[] p  ;
    public static MyPoint[] v ;
    public static Particle[] pBest;   //个体最优
    public static Particle[] allBest;   //全局最优
    public static double bestF = 0, bestOverlap = 0, eps = 1e-6; //最佳总值,总overlap
    public static double allSumUp = 0, bestSumOverlap = 0;//总距离，总最佳overlap
    public static double shellWidth = 10,AreaMaxX=-10000,AreaMinX=10000,AreaMaxY=-10000,AreaMinY=10000;
    public static Random random = new Random();
    //    public static double rand=random.nextDouble()*10;
    public static double c1 = 3, c2 = 1.2, disRate = 0.001, overlapRate = 10000,maxScore=-1;
    public static ArrayList<ArrayList<Ports>> LinkSET = new ArrayList<>();

    public static MyPoint[] p1 = new MyPoint[6], p2 = new MyPoint[4];

    public static void main(String[] args) throws CloneNotSupportedException, IOException, NullPointerException, InterruptedException {

        StringBuilder tempArg2=new StringBuilder(args[2]);
        id=Integer.parseInt(args[0].substring(args[0].lastIndexOf("_")+1,args[0].indexOf(".")));
        String arg2=tempArg2.insert(tempArg2.lastIndexOf("_")+1,id).toString();
        String arg3="/home/eda210506/tempList/"+id+"/ModuleResult_"+id+".txt";
        File file=new File("/home/eda210506/tempList/"+id);
        if(!file.exists()) file.mkdirs();
//        System.out.println("Area file:"+args[0]);
//        System.out.println("Link file:"+args[1]);
//        System.out.println("id:"+id);

        String inputPath=args[1].substring(0,args[1].indexOf("/Ports"));
        String resultPath=arg2.substring(0,arg2.indexOf("/result_"));

        long startTime = System.currentTimeMillis();
        ReadAndInit(args[0]);
        ReadConnectFile(args[1]);
        Init();
        System.out.println("testing! Please waiting few minutes");
        do {
            qLearning(1);
            OutPutResultTxtFile(Transition(args[0]), arg3);
            judgeScore(inputPath,resultPath);
        }while (maxScore<1 &&(((double)(System.currentTimeMillis()- startTime)) / 1000 )< 240);
        OutPutResultTxtFile(Transition2(args[0]), arg3);
        System.out.println(maxScore);
        String routeRes=judgeScore(inputPath,resultPath);
        WriteResult(arg2);
        copyFile(routeRes+"/result.txt","/home/eda210506/project/route_results/routeres_"+id+".txt");
        System.out.println("test done!");

        long endTime = System.currentTimeMillis();
        double time = ((double) (endTime - startTime)) / 1000;
        System.out.println("花费时间" + (time) + "s");


//        RunRoute(args[0],args[1],args[2],args[3]);
    }

    public static void RunRoute(String arg0,String arg1,String arg2,String arg3) throws CloneNotSupportedException, IOException {
        ReadAndInit(arg0);
        ReadConnectFile(arg1);
        Init();
        TransOrient(arg2);
        id=Integer.parseInt(arg3.substring(arg3.lastIndexOf("_")+1,arg3.indexOf(".")));
        String folderPath="/home/eda210506/tempList/"+id;
        File file=new File(folderPath);
        if(!file.exists()) file.mkdir();
        OutPutResultTxtFile(Transition2(arg0), folderPath+"/ModuleResult_"+id+".txt");
        String command1 = "./conversion.exe " + folderPath + " " + folderPath +
                "/ModuleResult_"+id+".txt" + " " + arg1;
        String command2="./LineSearch.exe " + folderPath + " " + folderPath;

        try { Thread.sleep ( 20 ) ;  //注意时间
            Process process1 = Runtime.getRuntime().exec(command1);
            process1.waitFor();
            process1.destroy();
        } catch (InterruptedException | IOException ignored){}

        try { Thread.sleep ( 50 ) ;  //注意时间
            Process process2 = Runtime.getRuntime().exec(command2);
            process2.waitFor();
            process2.destroy();
        } catch (InterruptedException | IOException ignored){}
        copyFile(folderPath+"/result.txt","/home/eda210506/project/route_results/"+arg3);
    }

    public static void TransOrient(String resultFile){
        File file=new File(resultFile);
        List<String> list = new ArrayList<>();
        if(file.exists())  list= readFile(resultFile);
        String []orientList={"R0","R90","R180","R270","MX","MY","MXR90","MYR90"};
        List<String> oriList=Arrays.asList(orientList);
        for(int i=0;i<list.size();i++){
            String Line=list.get(i);
            int num=0;
            if(Line.matches("Module:(.*)")) {
                num = Integer.parseInt(Line.substring(Line.lastIndexOf("M") + 1)) - 1;
                Line=list.get(++i);
                String orient=Line.substring(Line.indexOf(":")+1);
                int angle=oriList.indexOf(orient);
                Line=list.get(++i);
                double x=Double.parseDouble(Line.substring(Line.indexOf("(")+1,Line.indexOf(",")));
                double y=Double.parseDouble(Line.substring(Line.indexOf(",")+1,Line.indexOf(")")));
                allBest[num].Move2(x,y);
                allBest[num].adjustAngle(angle);
            }
        }
    }

    public static void copyFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        if (!oldFile.exists()) {
            file.createNewFile();
        }
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);

        byte[] buffer=new byte[2097152];
        int readByte = 0;
        while((readByte = in.read(buffer)) != -1){
            out.write(buffer, 0, readByte);
        }

        in.close();
        out.close();
    }

    public static void writeRoutingRes(){
        List<String> list=readFile("/home/eda210506/result.txt");
        List<String> RoutingRes=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String Line=list.get(i);
            String name;
            if(Line.matches("LINK(.*)")) {
                Line = Line.replace("-", "_").replace("LINK","Link");
                name=Line;
                RoutingRes.add(Line);
                Line=list.get(++i);
                if(Line.matches("line(.*)")){
                    Line=Line.replace("line","path").replace(": ",":");
                    RoutingRes.add(Line);
                }
                else if(Line.matches("thread(.*)")){
                    Line=Line.replace("thread","path").replace(": ",":");
                    RoutingRes.add(Line);
                }
                else RoutingRes.add(Line);
                Line=list.get(++i);
                if(Line.matches("line(.*)")){
                    Line=Line.replace("line","path").replace(": ",":");
                    RoutingRes.add(Line);
                }
                else if(Line.matches("thread(.*)")){
                    Line=Line.replace("thread","path").replace(": ",":");
                    RoutingRes.add(Line);
                }
                else RoutingRes.add(Line);
                while (i<list.size()-1&&!list.get(i+1).matches("LINK(.*)")){
                    RoutingRes.add(name);
                    RoutingRes.add(list.get(++i));
                    Line=list.get(++i);
                    if(Line.matches("line(.*)")){
                        Line=Line.replace("line","path").replace(": ",":");
                        RoutingRes.add(Line);
                    }
                    else if(Line.matches("thread(.*)")){
                        Line=Line.replace("thread","path").replace(": ",":");
                        RoutingRes.add(Line);
                    }
                    else RoutingRes.add(Line);
                }
            }
        }
        try {
            File file = new File("/home/eda210506/RoutingRes.txt");
            //文件不存在时候，主动创建文件。
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);  //上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : RoutingRes) {
                bw.write(s + "\n");
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String judgeScore(String inputPath,String resultPath) throws IOException, InterruptedException, CloneNotSupportedException {
        String folderPath="/home/eda210506/tempList/"+id;
        String command1 = "./conversion.exe " + folderPath + " " + folderPath +
                "/ModuleResult_"+id+".txt" + " " + inputPath +
                "/Ports_link_input_"+id+".txt";
        String command2="./LineSearch.exe " + folderPath + " " + folderPath;

        try { Thread.sleep ( 20 ) ;  //注意时间
            Process process1 = Runtime.getRuntime().exec(command1);
            process1.waitFor();
            process1.destroy();
        } catch (InterruptedException ignored){}

        try { Thread.sleep ( 50 ) ;  //注意时间
            Process process2 = Runtime.getRuntime().exec(command2);
            process2.waitFor();
            process2.destroy();
        } catch (InterruptedException ignored){}
        List<String> strings0 = readFile(folderPath+"/judge.txt");
        double rate0 = Double.parseDouble(strings0.get(0).substring(strings0.get(0).indexOf(":") + 1));
        double length0 = Double.parseDouble(strings0.get(1).substring(strings0.get(1).indexOf(":") + 1));
        if(rate0>maxScore) {
            maxScore = rate0;
            for(int i=0;i<ModuleNum;i++) allBest[i]=p[i].clone();
        }
        return folderPath;
    }

    public static double ReserveFloat1(double x){
        return Double.parseDouble(String.format("%.1f",x));
    }
    public static void WriteResult(String resultPath){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < ModuleNum; i++) {
            StringBuilder str = new StringBuilder("Module:");
            str = new StringBuilder(str.toString().concat(allBest[i].getName()));
            list.add(str.toString());
            str = new StringBuilder("Orient:");
            str = new StringBuilder(str.toString().concat(allBest[i].Orient));
            list.add(str.toString());
            str = new StringBuilder("Offset:");
            str = new StringBuilder(str.toString().concat("("+ String.format("%.2f",(ReserveFloat1(allBest[i].getMaxX())+ReserveFloat1(allBest[i].getMinX()))/2)+","
                    +String.format("%.2f",(ReserveFloat1(allBest[i].getMaxY())+ReserveFloat1(allBest[i].getMinY()))/2))+")");
            list.add(str.toString());
        }
        try {
            File file = new File(resultPath);
            //文件不存在时候，主动创建文件。
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);  //上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : list) {
                bw.write(s + "\n");
            }
//            bw.write(content);
            bw.close();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static boolean cmd(String command) {
        boolean flag = false;
        try {
//            while (!flag) {
            Runtime.getRuntime().exec("cmd.exe /c " + command);
            flag = true;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    private static int getRandomNumberInRange(int min, int max) { //含min和max

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void TestMyself() {
        String command = "d: && cd D:\\1\\WeChat\\WechatDocuments\\WeChat Files\\QQ1115063309\\FileStorage\\File\\2021-11\\C5RouteTest " +
                "&& conversion.exe io D:/Chrome下载/sample/16ModuleCase/" + fileNumber + "/Module.txt D:/Chrome下载/sample/16ModuleCase/" + fileNumber + "/connect_1.txt " +
                "&& LineSearch.exe io io";
        cmd(command);
    }

    public static void qLearning(int maxN) {    //0-11: stay 上下左右 90 180 270 MX MY MXR90 MYR90
        double epsilon = 0;
//        double maxLength=Math.max(Math.abs(AreaBoundary[2].x-AreaBoundary[0].x), Math.abs(AreaBoundary[2].y-AreaBoundary[0].y));
//        double StepLength=50;
        while (maxN-- != 0) {
            for (int i = 0; i < ModuleNum; i++) p[i].Move2(400, 400);
            for (int j = 0; j < 500; j++) {
//                System.out.println(j);
                int[] randArr = randomCommon(ModuleNum, ModuleNum);
//                if(j==100) {
//                    shellWidth=17.5;
//                    for(int i=0;i<ModuleNum;i++) p[i].resetOutShell();
//                }
                for (int i : randArr) {  //可改为随机扰动
                    boolean[] isInclined = new boolean[12];
                    double StepLength = 100 * Math.random();
                    if (Math.random() < epsilon && j < 30) {
                        int tempStep = getRandomNumberInRange(0, 11);
                        if (tempStep >= 1 && tempStep <= 4) {
                            if (Math.random() < 0.5) {
                                if (tempStep == 4) p[i].Move(StepLength, 0);
                                else if (tempStep == 3) p[i].Move(-StepLength, 0);
                                else if (tempStep == 2) p[i].Move(0, -StepLength);
                                else p[i].Move(0, StepLength);
                            } else {
                                isInclined[tempStep] = true;
                                if (tempStep == 4) p[i].Move(StepLength, StepLength);
                                else if (tempStep == 3) p[i].Move(-StepLength, StepLength);
                                else if (tempStep == 2) p[i].Move(-StepLength, -StepLength);
                                else p[i].Move(StepLength, -StepLength);
                            }
                        } else if (tempStep >= 5) {
                            p[i].adjustAngle(tempStep - 4);
                        }

                        judgeOutOfBounds(p[i]);
                    } else {
                        int bestStep = 0;
                        double tempSum = Double.MAX_VALUE;
                        double bestStepLength = StepLength;
                        for (int tempStep = 0; tempStep <= 11; tempStep++) {
                            if (j < 300)
                                StepLength = 150 * Math.random();
                            else StepLength = 900 * Math.random();
//                            Particle tempParticle = SerializationUtils.clone(p[i]);
                            Particle tempParticle = null;
                            try {
                                tempParticle = p[i].clone();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                            if (tempStep >= 1 && tempStep <= 4) {
                                if (Math.random() < 0.5) {
                                    if (tempStep == 4) tempParticle.Move(StepLength, 0);
                                    else if (tempStep == 3) tempParticle.Move(-StepLength, 0);
                                    else if (tempStep == 2) tempParticle.Move(0, -StepLength);
                                    else tempParticle.Move(0, StepLength);
                                } else {
                                    isInclined[tempStep] = true;
                                    if (tempStep == 4) tempParticle.Move(StepLength, StepLength);
                                    else if (tempStep == 3) tempParticle.Move(-StepLength, StepLength);
                                    else if (tempStep == 2) tempParticle.Move(-StepLength, -StepLength);
                                    else tempParticle.Move(StepLength, -StepLength);
                                }
                            } else {
                                if (tempStep>=5)
                                    tempParticle.adjustAngle(tempStep - 4);
                                else tempParticle.adjustAngle(0);
                            }

                            judgeOutOfBounds(tempParticle);

                            double tempOverlap = 0, tempDis = 0,AreaOverlap=0;
                            for (int k = 0; k < ModuleNum; k++) {
                                if (k == i) continue;
                                tempOverlap += calOverlap(tempParticle, p[k]);
                            }
                            for (ArrayList<Ports> ports : LinkSET) {            //LinkSet可优化，不必每次读取
                                for (int tempJ = 0; tempJ < ports.size(); tempJ++) {
                                    if (p[i].portsArrayList.contains(ports.get(tempJ))) {
                                        for (int tempI = 0; tempI < ports.size(); tempI++) {
                                            if (tempI == tempJ) continue;
                                            tempDis += getDist(tempParticle.portsArrayList.get(p[i].portsArrayList.indexOf(ports.get(tempJ))).getCenterPoint(), ports.get(tempI).getCenterPoint());
                                        }
                                    }
                                }
                            }
                            AreaOverlap=calOverlap3(tempParticle);
                            if (tempDis * disRate + tempOverlap * overlapRate -AreaOverlap*overlapRate< tempSum) {
                                bestStep = tempStep;
                                tempSum = tempDis * disRate + tempOverlap * overlapRate-AreaOverlap*overlapRate;
                                bestStepLength = StepLength;
                            }
                        }
                        if (bestStep >= 1 && bestStep <= 4) {
                            if (!isInclined[bestStep]) {
                                if (bestStep == 4) p[i].Move(bestStepLength, 0);   //StepLength's Problem
                                else if (bestStep == 3) p[i].Move(-bestStepLength, 0);
                                else if (bestStep == 2) p[i].Move(0, -bestStepLength);
                                else p[i].Move(0, bestStepLength);
                            } else {
                                if (bestStep == 4) p[i].Move(bestStepLength, bestStepLength);   //StepLength's Problem
                                else if (bestStep == 3) p[i].Move(-bestStepLength, bestStepLength);
                                else if (bestStep == 2) p[i].Move(-bestStepLength, -bestStepLength);
                                else p[i].Move(bestStepLength, -bestStepLength);
                            }
                        } else {
                            if (bestStep >= 5)
                                p[i].adjustAngle(bestStep - 4);
                            else p[i].adjustAngle(0);
                        }
                        judgeOutOfBounds(p[i]);
                    }
                }
            }

//            shellWidth=20;
//            for(int i=0;i<ModuleNum;i++) p[i].resetOutShell();

            for (int j = 0; j < 10; j++) {
                int[] randArr = randomCommon(ModuleNum, ModuleNum);
                for (int i : randArr) {  //可改为随机扰动
                    int bestStep = 0, bestAngle = 0;
                    double StepLength = 100 * Math.random();
                    double bestStepLength = StepLength;
//                    if(j<=50)
                    StepLength = 600 * Math.random();
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
                                StepLength = 300 * Math.random();
//                                else StepLength = 700 * Math.random();
//                                Particle tempParticle = SerializationUtils.clone(p[i]);
                                Particle tempParticle = null;
                                try {
                                    tempParticle = p[i].clone();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                if (tempStep >= 1) {
                                    if (tempStep == 4) tempParticle.Move(StepLength, StepLength);
                                    else if (tempStep == 3) tempParticle.Move(-StepLength, StepLength);
                                    else if (tempStep == 2) tempParticle.Move(-StepLength, -StepLength);
                                    else tempParticle.Move(StepLength, -StepLength);
                                    judgeOutOfBounds(tempParticle);
                                }
                                tempParticle.adjustAngle(angle);

                                judgeOutOfBounds(tempParticle);

                                double tempOverlap = 0, tempDis = 0,AreaOverlap=0;
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
                                AreaOverlap=calOverlap3(tempParticle);
//                                for(int k=0;k<ModuleNum;k++){
//                                    if(k==i) continue;
//                                    tempDis+=getDist(tempParticle.getCenterPoint(),p[k].getCenterPoint());
//                                }
                                if (tempDis * disRate + tempOverlap * overlapRate -AreaOverlap*overlapRate< tempSum) {
                                    bestStep = tempStep;
                                    bestAngle = angle;
                                    tempSum = tempDis * disRate + tempOverlap * overlapRate-AreaOverlap*overlapRate;
                                    bestStepLength = StepLength;
                                }
                            }
                        }
                        if (bestStep >= 1) {
                            if (bestStep == 4) p[i].Move(bestStepLength, bestStepLength);   //StepLength's Problem
                            else if (bestStep == 3) p[i].Move(-bestStepLength, bestStepLength);
                            else if (bestStep == 2) p[i].Move(-bestStepLength, -bestStepLength);
                            else p[i].Move(bestStepLength, -bestStepLength);
                            judgeOutOfBounds(p[i]);
                        }
                        p[i].adjustAngle(bestAngle);
                        judgeOutOfBounds(p[i]);
                    }
                }
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
            double tempSum = 0;
            for (int j = 0; j < ModuleNum; j++) {  //更新个体最优
                if (p[j].getSumF() < pBest[j].getSumF()) {
//                    pBest[j] = (Particle) SerializationUtils.clone(p[j]);
                    try {
                        pBest[j] = p[j].clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("Changed:"+j);
                }
                tempSum += p[j].getSumF(1);
            }

            double tempOverlap = 0;
            for (int k = 0; k < ModuleNum; k++)
                for (int j = 0; j < ModuleNum; j++) {
                    if (j != k) {
                        tempOverlap += (calOverlap2(p[k], p[j]));
                    }
                }
            if(tempOverlap!=0) {
                maxN++;
                continue;
            }
            if (bestF > tempSum * disRate + tempOverlap * overlapRate) {
                bestF = tempSum * disRate + tempOverlap * overlapRate;
                bestOverlap = tempOverlap;
            }
        }
    }


    public static double calOverlap2(Particle p1, Particle p2) {
        MyPoint[] myPoint1 = new MyPoint[p1.getPointNum()], myPoint2 = new MyPoint[p2.getPointNum()];
        for (int i = 0; i < p1.getPointNum(); i++) myPoint1[i] = new MyPoint(p1.getX(i), p1.getY(i));
        for (int i = 0; i < p2.getPointNum(); i++) myPoint2[i] = new MyPoint(p2.getX(i), p2.getY(i));
        return Math.abs(SPIA(myPoint1, myPoint2, p1.getPointNum(), p2.getPointNum()));
    }

    public static void judgeOutOfBounds(Particle par) {
        double vx = 0, vy = 0;  //出界判断
        if (par.getMaxX() > AreaMaxX - 15) {
            vx = AreaMaxX - par.getMaxX() - 15;
        } else if (par.getMinX() < AreaMinX + 15) {
            vx = AreaMinX - par.getMinX() + 15;
        }
        if (par.getMaxY() > AreaMaxY - 15) {
            vy = AreaMaxY - par.getMaxY() - 15;
        } else if (par.getMinY() < AreaMinY + 15) {
            vy = AreaMinY - par.getMinY() + 15;
        }
        par.Move(vx, vy);
    }

    public static int[] randomCommon(int max, int n) {
        if (max < 0 || n <= 0) return new int[1];
        Random random = new Random();
        int[] result = new int[n], flag = new int[n];
        for (int i = 0; i < result.length; i++) {
            int x = random.nextInt(n);
            while (flag[x] == 1) x = random.nextInt(n);
            result[i] = x;
            flag[x] = 1;
        }
        return result;
    }



    public static void Init() throws CloneNotSupportedException {
        for (int i = 0; i < ModuleNum; i++) {
            v[i] = new MyPoint(random.nextDouble() * 10, random.nextDouble() * 10);
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
        for (int i = 0; i < ModuleNum; i++)
            for (int j = 0; j < ModuleNum; j++) {
                if (j == i) continue;
                bestSumOverlap += calOverlap(p[i], p[j]);
            }
        for (int i = 0; i < ModuleNum; i++)  //↑
        {
//            pBest[i] = (Particle) SerializationUtils.clone(p[i]);
//            allBest[i] = (Particle) SerializationUtils.clone(p[i]);
            pBest[i] = p[i].clone();
            allBest[i] = p[i].clone();
        }
        bestF = allSumUp * disRate + bestSumOverlap * overlapRate;
    }

    public static void fitnessFunction() {
        allSumUp = 0;
        double tempSumOverlap = 0;
        for (int i = 0; i < ModuleNum; i++) {
            double x = p[i].getSumF();
            p[i].setSumF(x);
            allSumUp += x;
        }
    }

    public static double getDist(CenterPoint p1, CenterPoint p2) {
        return Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    public static double calOverlap(Particle p1, Particle p2) {
//        if(p1.getMaxX()<p2.getMinX()||p1.getMinX()>p2.getMaxX()||
//                p1.getMaxY()<p2.getMinY()||p1.getMinY()>p2.getMaxY()) return 0;
//        if(p1.getMaxX()<=p2.getMaxX()&&p1.getMaxY()<=p2.getMaxY()) return 10000;
//        if(p1.getMinX()>=p2.getMinY()&&p1.getMinY()>=p2.getMinY()) return 10000;
//        return 0;
        MyPoint[] myPoint1 = new MyPoint[p1.getPointNum()], myPoint2 = new MyPoint[p2.getPointNum()];
        for (int i = 0; i < p1.getPointNum(); i++) myPoint1[i] = new MyPoint(p1.getShellX(i), p1.getShellY(i));
        for (int i = 0; i < p2.getPointNum(); i++) myPoint2[i] = new MyPoint(p2.getShellX(i), p2.getShellY(i));
        return Math.abs(SPIA(myPoint1, myPoint2, p1.getPointNum(), p2.getPointNum()));
    }



    public static double calOverlap3(Particle p1) {
        MyPoint[] myPoint1 = new MyPoint[p1.getPointNum()];
        for (int i = 0; i < p1.getPointNum(); i++) myPoint1[i] = new MyPoint(p1.getX(i), p1.getY(i));
//        for (int i = 0; i < p2.getPointNum(); i++) myPoint2[i] = new MyPoint(p2.getX(i), p2.getY(i));
        return Math.abs(SPIA(myPoint1, AreaBoundary, p1.getPointNum(), AreaBoundary.length));
    }




    public static List<String> Transition(String ModulePath) {
        List<String> tempList = readFile(ModulePath);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) list.add(tempList.get(i));
        for (int i = 0; i < ModuleNum; i++) {
            StringBuilder str = new StringBuilder("Module:");
            str = new StringBuilder(str.toString().concat(p[i].getName()));
            list.add(str.toString());
            str = new StringBuilder("Boundary:");
            for (int j = 0; j < p[i].getPointNum(); j++) {
                str.append("(").append(String.format("%.1f", p[i].getX(j))).append(", ").append(String.format("%.1f", p[i].getY(j))).append(")");
            }
            str.append(";").append(p[i].getRuleName());
            list.add(str.toString());
            for (int j = 0; j < p[i].portsArrayList.size(); j++) {
                str = new StringBuilder("Port:");
                for (int k = 0; k < p[i].portsArrayList.get(j).getPortPointNum(); k++) {
                    str.append("(").append(String.format("%.1f", p[i].portsArrayList.get(j).getX(k))).append(", ").append(String.format("%.1f", p[i].portsArrayList.get(j).getY(k))).append(")");
                }
                str.append(";").append(p[i].portsArrayList.get(j).getRuleName());
                list.add(str.toString());
            }
        }
        return list;
    }

    public static List<String> Transition2(String ModulePath) {
        List<String> tempList = readFile(ModulePath);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) list.add(tempList.get(i));
        for (int i = 0; i < ModuleNum; i++) {
            StringBuilder str = new StringBuilder("Module:");
            str = new StringBuilder(str.toString().concat(allBest[i].getName()));
            list.add(str.toString());
            str = new StringBuilder("Boundary:");
            for (int j = 0; j < allBest[i].getPointNum(); j++) {
                str.append("(").append(String.format("%.1f", allBest[i].getX(j))).append(", ").append(String.format("%.1f", allBest[i].getY(j))).append(")");
            }
            str.append(";").append(allBest[i].getRuleName());
            list.add(str.toString());
            for (int j = 0; j < allBest[i].portsArrayList.size(); j++) {
                str = new StringBuilder("Port:");
                for (int k = 0; k < allBest[i].portsArrayList.get(j).getPortPointNum(); k++) {
                    str.append("(").append(String.format("%.1f", allBest[i].portsArrayList.get(j).getX(k))).append(", ").append(String.format("%.1f", allBest[i].portsArrayList.get(j).getY(k))).append(")");
                }
                str.append(";").append(allBest[i].portsArrayList.get(j).getRuleName());
                list.add(str.toString());
            }
        }
        return list;
    }

    public static void OutPutResultTxtFile(List<String> list) {
        try {
            File file = new File("D:\\Chrome下载\\sample\\16ModuleCase\\" + fileNumber + "\\ModuleResult.txt");
            //文件不存在时候，主动创建文件。
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);  //上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : list) {
                bw.write(s + "\n");
            }
//            bw.write(content);
            bw.close();
            fw.close();
            System.out.println("test done!");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void OutPutResultTxtFile(List<String> list, String filepath) {
        try {
            File file = new File(filepath);
            //文件不存在时候，主动创建文件。
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, false);  //上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : list) {
                bw.write(s + "\n");
            }
//            bw.write(content);
            bw.close();
            fw.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public static void ReadConnectFile(String filepath) {
        List<String> list = readFile(filepath);
        if (list.isEmpty()) return;
        for (int line = 0; line < list.size(); line++) {
            String temp = list.get(line);
//            System.out.println(temp);
            if (temp.matches("Link(.*)")) {
                temp = list.get(++line);
                String[] ModuleList = temp.split(" ");
                String[] PortList = list.get(++line).split(" ");
                ArrayList<Ports> linkArr = new ArrayList<Ports>();
                for (int i = 0; i < ModuleList.length; i++) {
                    int ModuleN = Integer.parseInt(ModuleList[i].substring(ModuleList[i].indexOf("M") + 1).trim());
                    int PortN = Integer.parseInt(PortList[i].trim());
                    linkArr.add(p[ModuleN - 1].portsArrayList.get(PortN - 1));
                }
                LinkSET.add(linkArr);
            }
        }
    }

    public static void ReadAndInit() {
        try {
            List<String> list = readFile("D:\\Chrome下载\\sample\\16ModuleCase\\" + fileNumber + "\\Module.txt");
            if (list.isEmpty()) return;
            for (int i = 0; i < list.size(); i++) {
                String temp = list.get(i);
                System.out.println(temp);
                if (temp.matches("Area:(.*)")) {
                    String[] templist = temp.replace("Area:", "").trim().split("\\(");
                    int j = 0;
                    for (String str : templist) {
                        if (str.equals("")) continue;
                        str = str.replace(")", "");
                        double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                        double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                        AreaBoundary[j] = new MyPoint(tempX, tempY);
                        j++;
                    }
                }
                if (temp.matches("Module:(.*)")) {
                    int ModuleX = Integer.parseInt(temp.substring(temp.indexOf(":M") + 2));
                    String strName = temp.substring(temp.indexOf(":") + 1);
                    temp = list.get(++i);
                    String[] tempList = temp.replace("Boundary:", "").trim().split("\\(");
                    ArrayList<Double> x = new ArrayList<>(), y = new ArrayList<>();
                    String ruleNa = null;
                    for (String str : tempList) {
                        if (str.equals("")) continue;
                        if (str.contains(");")) {
                            ruleNa = str.substring(str.indexOf(";") + 1);
                            str = str.substring(0, str.indexOf(")"));
                        }
                        str = str.replace(")", "");
                        double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                        double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                        x.add(tempX);
                        y.add(tempY);
                    }
                    double[] x0 = new double[x.size()], y0 = new double[y.size()];
                    for (int j = 0; j < x.size(); j++) {
                        x0[j] = x.get(j);
                        y0[j] = y.get(j);
                    }
                    p[ModuleX - 1] = new Particle(strName, x.size(), x0, y0, ruleNa);

                    while (list.get(i + 1).contains("Port:")) {

                        temp = list.get(++i);
                        tempList = temp.replace("Port:", "").trim().split("\\(");
                        x.clear();
                        y.clear();
                        for (String str : tempList) {
                            if (str.equals("")) continue;
                            if (str.contains(");")) {
                                ruleNa = str.substring(str.indexOf(";") + 1);
                                str = str.substring(0, str.indexOf(")"));
                            }
                            str = str.replace(")", "");
                            double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                            double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                            x.add(tempX);
                            y.add(tempY);
                        }
                        double[] x1 = new double[x.size()], y1 = new double[y.size()];
                        for (int j = 0; j < x.size(); j++) {
                            x1[j] = x.get(j);
                            y1[j] = y.get(j);
                        }
                        Ports tempPort = new Ports(x.size(), x1, y1, ruleNa);
                        p[ModuleX - 1].portsArrayList.add(tempPort);
                        if (i + 1 == list.size()) break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public static int getOccur(String src,String find){
        int o = 0;
        int index=-1;
        while((index=src.indexOf(find,index))>-1){
            ++index;
            ++o;
        }
        return o;
    }

    public static void ReadAndInit(String filepath) {
        try {
            List<String> list = readFile(filepath);
            if (list.isEmpty()) return;
            int num=0;
            for (String s : list) {
                if (s.matches("Module:(.*)")) num++;
            }
            ModuleNum=num;
            p = new Particle[ModuleNum];
            v = new MyPoint[ModuleNum];
            pBest = new Particle[ModuleNum];   //个体最优
            allBest = new Particle[ModuleNum];   //全局最优
            for (int i = 0; i < list.size(); i++) {
                String temp = list.get(i);
//                System.out.println(temp);
                if (temp.matches("Area:(.*)")) {
                    AreaBoundary=new MyPoint[getOccur(temp,",")];
                    String[] templist = temp.replace("Area:", "").trim().split("\\(");
                    int j = 0;
                    for (String str : templist) {
                        if (str.equals("")) continue;
                        str = str.replace(")", "");
                        double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                        double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                        AreaBoundary[j] = new MyPoint(tempX, tempY);
                        if(AreaMaxX<tempX) AreaMaxX=tempX;
                        if(AreaMinX>tempX) AreaMinX=tempX;
                        if(AreaMaxY<tempY) AreaMaxY=tempY;
                        if(AreaMinY>tempY) AreaMinY=tempY;
                        j++;
                    }
                }
                if (temp.matches("Module:(.*)")) {
                    int ModuleX = Integer.parseInt(temp.substring(temp.indexOf(":M") + 2));
                    String strName = temp.substring(temp.indexOf(":") + 1);
                    temp = list.get(++i);
                    String[] tempList = temp.replace("Boundary:", "").trim().split("\\(");
                    ArrayList<Double> x = new ArrayList<>(), y = new ArrayList<>();
                    String ruleNa = null;
                    for (String str : tempList) {
                        if (str.equals("")) continue;
                        if (str.contains(");")) {
                            ruleNa = str.substring(str.indexOf(";") + 1);
                            str = str.substring(0, str.indexOf(")"));
                        }
                        str = str.replace(")", "");
                        double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                        double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                        x.add(tempX);
                        y.add(tempY);
                    }
                    double[] x0 = new double[x.size()], y0 = new double[y.size()];
                    for (int j = 0; j < x.size(); j++) {
                        x0[j] = x.get(j);
                        y0[j] = y.get(j);
                    }
                    p[ModuleX - 1] = new Particle(strName, x.size(), x0, y0, ruleNa);

                    while (list.get(i + 1).contains("Port:")) {

                        temp = list.get(++i);
                        tempList = temp.replace("Port:", "").trim().split("\\(");
                        x.clear();
                        y.clear();
                        for (String str : tempList) {
                            if (str.equals("")) continue;
                            if (str.contains(");")) {
                                ruleNa = str.substring(str.indexOf(";") + 1);
                                str = str.substring(0, str.indexOf(")"));
                            }
                            str = str.replace(")", "");
                            double tempX = Double.parseDouble(str.substring(0, str.indexOf(",")));
                            double tempY = Double.parseDouble(str.substring(str.indexOf(" ") + 1));
                            x.add(tempX);
                            y.add(tempY);
                        }
                        double[] x1 = new double[x.size()], y1 = new double[y.size()];
                        for (int j = 0; j < x.size(); j++) {
                            x1[j] = x.get(j);
                            y1[j] = y.get(j);
                        }
                        Ports tempPort = new Ports(x.size(), x1, y1, ruleNa);
                        p[ModuleX - 1].portsArrayList.add(tempPort);
                        if (i + 1 == list.size()) break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String filepath) {
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

    public static int dcmp(double x) {
        if (x > eps) return 1;
        return x < -eps ? -1 : 0;
    }

    public static double cross(MyPoint a, MyPoint b, MyPoint c) {
        return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
    }

    public static MyPoint intersection(MyPoint a, MyPoint b, MyPoint c, MyPoint d) {
        MyPoint p = null;
        try {
            p = a.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        double t = ((a.x - c.x) * (c.y - d.y) - (a.y - c.y) * (c.x - d.x)) / ((a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x));
        p.x += (b.x - a.x) * t;
        p.y += (b.y - a.y) * t;
        return p;
    }

    public static double PolygonArea(MyPoint[] p, int n) {
        if (n < 3) return 0.0;
        double s = p[0].y * (p[n - 1].x - p[1].x);
        for (int i = 1; i < n - 1; ++i) {
            s += p[i].y * (p[i - 1].x - p[i + 1].x);
        }
        s += p[n - 1].y * (p[n - 2].x - p[0].x);
        return Math.abs(s * 0.5);
    }

    public static double CPIA(MyPoint[] a, MyPoint[] b, int na, int nb) {
        MyPoint[] p = new MyPoint[20];
        MyPoint[] tmp = new MyPoint[20];
        int tn, sflag, eflag;

        p = b.clone();           /////////

        for (int i = 0; i < na && nb > 2; i++) {
            if (i == na - 1) {
                sflag = dcmp(cross(a[0], p[0], a[i]));
            } else {
                sflag = dcmp(cross(a[i + 1], p[0], a[i]));
            }
            for (int j = tn = 0; j < nb; j++, sflag = eflag) {
                if (sflag >= 0) {
                    try {
                        tmp[tn++] = p[j].clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                if (i == na - 1) {
                    if (j == nb - 1) {
                        eflag = dcmp(cross(a[0], p[0], a[i]));
                    } else {
                        eflag = dcmp(cross(a[0], p[j + 1], a[i])); //计算下一个连续点在矢量线段的位置
                    }
                } else {
                    if (j == nb - 1) {
                        eflag = dcmp(cross(a[i + 1], p[0], a[i]));
                    } else {
                        eflag = dcmp(cross(a[i + 1], p[j + 1], a[i]));
                    }
                }
                if ((sflag ^ eflag) == -2) {  //1和-1的异或为-2，也就是两个点分别在矢量线段的两侧
                    if (i == na - 1) {
                        if (j == nb - 1) {
                            try {
                                tmp[tn++] = intersection(a[i].clone(), a[0].clone(), p[j].clone(), p[0].clone()); //求交点
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                tmp[tn++] = intersection(a[i].clone(), a[0].clone(), p[j].clone(), p[j + 1].clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (j == nb - 1) {
                            try {
                                tmp[tn++] = intersection(a[i].clone(), a[i + 1].clone(), p[j].clone(), p[0].clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                tmp[tn++] = intersection(a[i].clone(), a[i + 1].clone(), p[j].clone(), p[j + 1].clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            p = tmp.clone();       //////SerializationUtils.clone
            nb = tn;
            p[nb] = p[0];
        }
        if (nb < 3) return 0.0;
        return PolygonArea(p, nb);
    }

    public static double SPIA(MyPoint a[], MyPoint b[], int na, int nb) {
        int i, j;
        MyPoint[] t1 = new MyPoint[na], t2 = new MyPoint[nb];
        double res = 0, num1, num2;
        try {
            t1[0] = a[0].clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        try {
            t2[0] = b[0].clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        for (i = 2; i < na; i++) {
            try {
                t1[1] = a[i - 1].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            try {
                t1[2] = a[i].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            num1 = dcmp(cross(t1[1], t1[2], t1[0]));  //根据差积公式来计算t1[2]在矢量线段（t1[0], t1[1]）的左侧还是右侧，
            //值为负数在矢量线段左侧，值为正数在矢量线段右侧
            if (num1 < 0) swap(t1[1], t1[2]);  // 按逆时针进行排序
            for (j = 2; j < nb; j++) {
                try {
                    t2[1] = b[j - 1].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                try {
                    t2[2] = b[j].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                num2 = dcmp(cross(t2[1], t2[2], t2[0]));
                if (num2 < 0) swap(t2[1], t2[2]);
                res += CPIA(t1, t2, 3, 3) * num1 * num2;
            }
        }
        return res;
    }

    public static void swap(MyPoint p1, MyPoint p2) {   ///?
        MyPoint temp = new MyPoint(p1.getX(), p1.getY());
        p1.setX(p2.getX());
        p1.setY(p2.getY());
        p2.setX(temp.getX());
        p2.setY(temp.getY());
    }


    private static void Draw() {
        jpanelTest jpanelTest = new jpanelTest();
        jpanelTest.setBackground(Color.BLACK);
        jpanelTest.setBounds(0, 0, 1000, 1000);
        jpanelTest.setPreferredSize(new Dimension(2000, 2000));
        JFrame frame = new JFrame(fileNumber);
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