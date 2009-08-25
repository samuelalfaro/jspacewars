package tips;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class SplashTest extends Frame implements ActionListener {
	
	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "foo", "bar", "baz" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(130, 250, 280, 40);
		g.setPaintMode();
		g.setColor(Color.BLACK);
		g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 130, 260);
		g.fillRect(130, 270, (frame * 10) % 280, 20);
	}

	public SplashTest() {
		super("SplashScreen demo");
		setSize(500, 300);
		setLayout(new BorderLayout());

		MenuBar mb = new MenuBar();
			Menu m1 = new Menu("File");
				MenuItem mi1 = new MenuItem("Exit");
				mi1.addActionListener(this);
			m1.add(mi1);
		mb.add(m1);
		setMenuBar(mb);
		
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if( splash == null ){
			System.out.println("SplashScreen.getSplashScreen() returned null");
			return;
		}
		Graphics2D g = splash.createGraphics();
		if( g == null ){
			System.out.println("g is null");
			return;
		}
		for( int i = 0; i < 100; i++ ){
			renderSplashFrame(g, i);
			splash.update();
			try{
				Thread.sleep(200);
			}catch( InterruptedException e ){
			}
		}
		splash.close();
		setVisible(true);
		toFront();
	}

	public void actionPerformed(ActionEvent ae) {
		System.exit(0);
	}

	public static void main(String args[]) {
		new SplashTest();
	}
}