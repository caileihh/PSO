package Particle;

import javax.swing.*;
import java.awt.*;

public class jpanelTest extends JPanel {
    public Particle[] p=ParticleTest.p;
    public MyPoint[] AreaBoundary=ParticleTest.AreaBoundary;
    public int padding=100;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        for(int i=0;i<3;i++){
            g.drawLine((int)AreaBoundary[i].x+padding,(int)AreaBoundary[i].y+padding,(int)AreaBoundary[i+1].x+padding,(int)AreaBoundary[i+1].y+padding);
        }
        g.drawLine((int)AreaBoundary[3].x+padding,(int)AreaBoundary[3].y+padding,(int)AreaBoundary[0].x+padding,(int)AreaBoundary[0].y+padding);

        g.setColor(Color.YELLOW);
        for(int i=0;i<ParticleTest.ModuleNum;i++){
            for(int j=0;j<p[i].getPointNum();j++){
                g.drawLine((int)p[i].getX(j)+padding,(int)p[i].getY(j)+padding,(int)p[i].getX(j+1)+padding,(int)p[i].getY(j+1)+padding);
            }
        }

    }
}
