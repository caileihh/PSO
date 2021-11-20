import javax.swing.*;
import java.awt.*;

public class jpanelTest extends JPanel {
    public Particle[] particles = ParticleTest.p;
    public MyPoint[] AreaBoundary = ParticleTest.AreaBoundary;
    public int padding = 100;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        for (int i = 0; i < 3; i++) {
            g.drawLine((int) AreaBoundary[i].x + padding, (int) AreaBoundary[i].y + padding, (int) AreaBoundary[i + 1].x + padding, (int) AreaBoundary[i + 1].y + padding);
        }
        g.drawLine((int) AreaBoundary[3].x + padding, (int) AreaBoundary[3].y + padding, (int) AreaBoundary[0].x + padding, (int) AreaBoundary[0].y + padding);


        for (int i = 0; i < ParticleTest.ModuleNum; i++) {
            g.setColor(Color.YELLOW);
            for (int j = 0; j < particles[i].getPointNum(); j++) {
                g.drawLine((int) particles[i].getX(j) + padding, (int) particles[i].getY(j) + padding, (int) particles[i].getX(j + 1) + padding, (int) particles[i].getY(j + 1) + padding);
            }
            g.setColor(Color.white);
            for (int j = 0; j < particles[i].portsArrayList.size(); j++) {
                for (int k = 0; k < particles[i].portsArrayList.get(j).getPortPointNum(); k++)
                    g.drawLine((int) particles[i].portsArrayList.get(j).getX(k) + padding, (int) particles[i].portsArrayList.get(j).getY(k) + padding,
                            (int) particles[i].portsArrayList.get(j).getX(k + 1) + padding, (int) particles[i].portsArrayList.get(j).getY(k + 1) + padding);
            }
        }
    }
}
