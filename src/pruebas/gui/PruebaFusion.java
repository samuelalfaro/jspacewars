package pruebas.gui;

import java.awt.*;
import java.awt.image.MemoryImageSource;

import javax.swing.*;

import org.sam.util.Imagen;

public class PruebaFusion extends JComponent{
	private static final long serialVersionUID = 1L;
	
	Image img1, img2;
	Image[][] imgs;

	public PruebaFusion(){
		imgs = new Image[9][7];

		int ancho = 100;
		int alto  = 100;
		int tam   = ancho * alto;

		int intImg1[] = Imagen.toGrayPixels(Imagen.cargarImagen("resources/degradado.png"),100,100); 
		int intImg2[] = new int[tam];
		
		/* 
		 * Crea un espectro de colores en linea con la diagonal de la imagen 
		 * 
		 * Para saber la distancia (paralela al segmento de degradado) de un punto
		 * al origen de dicho sergmento.
		 * Se traslada el segmento y el punto tomando el primer punto del segmento como
		 * origen.
		 * Luego se rota el punto desde el origen el angulo inverso de la pendiente
		 * del segmento
		 * 
		 * Para ello
		 * 		rotacion P(x,y) -->
		 * 		x' = x*cos(a) - y*sen(a)
		 * 		y' = x*sen(a) + y*cos(a)
		 * 
		 * Como a es la pendiente del segmento formada por P1(x1,y1) y P2(x2,y2) -->
		 * 		sen(a) = (y2-y1)/ ((x2-x1)^2+(y2-y1)^2)^0.5		
		 * 		cos(a) = (x2-x1)/ ((x2-x1)^2+(y2-y1)^2)^0.5
		 * 
		 * Como:
		 * 		sen(-a) = -sen(a)
		 * 		cos(-a) = cos(a)
		 * 
		 * Como solo consideramos la distancia horizontal que se obtiene girando -angulo:
		 * 		d = (x-x1)*cos(a) + (y-y1)*sen(a)
		 * 		d = ( (x-x1)*(x2-x1) + (y-y1)*(y2-y1) )/ ((x2-x1)²+(y2-y1)²)^0.5
		 * 
		 * Para pasar esta distancia al rango [0..1 ] se vuelve a dividir por d(P1,P2)
		 * 		d /= d(P1,P2)
		 * 		d = ( (x-x1)*(x2-x1) + (y-y1)*(y2-y1) )/ ((x2-x1)² +(y2-y1)²)^0.5* d(P1,P2)
		 *   
		 * La distancia eculida entre el P1 y P2:
		 * 		d(P1,P2)= ( (x2-x1)² + (y2-y1)² )^0.5
		 * 
		 * 		d = ( (x-x1)*(x2-x1) + (y-y1)*(y2-y1) )/ ((x2-x1)²+(y2-y1)²)
		 */
		int indImg = 0;
		int x1 = ancho/2, y1 = 0, x2= 0, y2 =alto/2;
		int dx = (x1-x2);
		int dy = (y1-y2);
		int d2 = (dx*dx+dy*dy);

		float sat = 1.0f;
		float bri = 1.0f;
		for (int y = 0; y < alto; y++) {
			for (int x = 0; x < ancho; x++) {
				float hue = (float)( (x-x1)*dx + (y-y1)*dy ) / d2;
				int pixel1 = Color.HSBtoRGB(hue, sat, bri);
				intImg2[indImg] = pixel1;
				indImg++;
			}
		}
		img1 = this.createImage( new MemoryImageSource(	ancho, alto, intImg1, 0, ancho ) );
		img2 = this.createImage( new MemoryImageSource(	ancho, alto, intImg2, 0, ancho ) );

		System.out.println( System.getProperty("java.runtime.version") ); 
		
		for(int modo = Imagen.MODO_AND; modo<= Imagen.MODO_SUP; modo++){
			/*
			imgs[modo][0]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, Imagen.ALFA_OFF);
			imgs[modo][1]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, 0);
			imgs[modo][2]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, 64);
			imgs[modo][3]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, 128);
			imgs[modo][4]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, 192);
			imgs[modo][5]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, 255);
			imgs[modo][6]= Imagen.fusionar(intImg1, intImg2, ancho, alto, modo, Imagen.ALFA_SRC);
			/*/
			imgs[modo][0]= Imagen.fusionar(img1, img2, modo, Imagen.ALFA_OFF);
			imgs[modo][1]= Imagen.fusionar(img1, img2, modo, 0);
			imgs[modo][2]= Imagen.fusionar(img1, img2, modo, 64);
			imgs[modo][3]= Imagen.fusionar(img1, img2, modo, 128);
			imgs[modo][4]= Imagen.fusionar(img1, img2, modo, 192);
			imgs[modo][5]= Imagen.fusionar(img1, img2, modo, 255);
			imgs[modo][6]= Imagen.fusionar(img1, img2, modo, Imagen.ALFA_SRC);
			//*/

		}
		System.gc();
	}

	public void paintComponent(Graphics g){
		g.drawRect(9,9,101,101);
		g.drawImage(img1,10,10,null);
		g.drawRect(119,9,101,101);
		g.drawImage(img2,120,10,null);
		for(int f = Imagen.MODO_AND; f<= Imagen.MODO_SUP; f++)
			for(int c = 0; c < 7; c++){
				g.drawRect(c*110+9,f*110+119,101,101);
				g.drawImage(imgs[f][c],c*110+10,f*110+120,null);
			}
	}

	public Dimension getMinimumSize(){
		return new Dimension(780,1120);
	}

	public Dimension getPreferredSize(){
		return getMinimumSize();
	}

	static public void main(String args[]){
		PruebaFusion p = new PruebaFusion();
		
		JFrame frame = new JFrame("Prueba Fusion");

		frame.setSize(810,600);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(p),BorderLayout.CENTER);
		frame.setVisible(true);
	}
}