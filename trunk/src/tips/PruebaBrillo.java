package tips;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

import org.sam.util.Imagen;
import com.jhlabs.image.MotionBlurFilter;

public class PruebaBrillo extends JComponent{
	private static final long serialVersionUID = 1L;

	BufferedImage img1, img2, img3;
	ConvolveOp convolve;
	
	public PruebaBrillo(){
		img1 = Imagen.cargarToBufferedImage("resources/img/texturas/oxido.jpg");
		img2 = Imagen.toBufferedImage(Imagen.brightPass(img1,0.5f,1.0f));
		img3 = new BufferedImage(img2.getWidth(), img2.getHeight(), BufferedImage.TYPE_INT_ARGB);
//		Kernel kernel = new Kernel(3, 3, new float[]{
//				0.075114f, 0.123841f, 0.075114f,
//				0.123841f, 0.204180f, 0.123841f,
//				0.075114f, 0.123841f, 0.075114f});
//		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,	null);
		MotionBlurFilter bio = new MotionBlurFilter();
		bio.setZoom(0.5f);
		bio.setRotation(-0.2f);
		bio.filter(img2, img3);
		Graphics2D gi = (Graphics2D)img2.getGraphics();
		gi.drawImage(img1, 0, 0, null);
		gi.setComposite(Imagen.FUSIONAR_SUM);
		gi.drawImage(img3, 0, 0, null);
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.drawRect(9,9,img1.getWidth()+1,img1.getHeight()+1);
		g2.drawImage(img1,10,10,null);
		g2.drawRect(getWidth()/2-1,9,img2.getWidth()+1,img2.getHeight()+1);
		g2.drawImage(img2,getWidth()/2,10,null);
	}

	static public void main(String args[]){
		PruebaBrillo p = new PruebaBrillo();

		JFrame frame = new JFrame("Prueba Fusion");

		frame.setSize(400,400);
		frame.setDefaultCloseOperation(3);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(p),BorderLayout.CENTER);
		frame.setVisible(true);
	}
}