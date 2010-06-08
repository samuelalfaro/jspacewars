package pruebas.jogl;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.media.opengl.*;
import javax.swing.JFrame;

import org.sam.jogl.*;
import org.sam.util.Imagen;
import org.sam.util.Tipografias;

import com.sun.opengl.util.Animator;

public class PruebaDots{

	private static final int screenHeight = 600;
	private static final int screenWidth = 800;
	
	static float sClamp(float value, float min, float max) {
		return value < min ? min : (value > max ? max : value);
	}

	static float sStep(float value, float stepPosition) {
		return value < stepPosition ? 0.0f : 1.0f;
	}

	static float sPulse(float value, float startPosition, float endPosition) {
		return sStep(value, startPosition) - sStep(value, endPosition);
	}

	static float sBoxStep(float value, float slopeStart, float slopeEnd) {
		float diff = slopeEnd - slopeStart;
		if (diff == 0)
			return sStep(value, slopeStart);
		return sClamp((value - slopeStart) / diff, 0, 1);
	}

	static float sBoxPulse(float value, float upSlopeStart, float upSlopeEnd, float downSlopeStart, float downSlopeEnd) {
		return sBoxStep(value, upSlopeStart, upSlopeEnd)
		- sBoxStep(value, downSlopeStart, downSlopeEnd);
	}

	static float sSmoothPulse(float value, float upSlopeStart, float upSlopeEnd, float downSlopeStart, float downSlopeEnd) {
		return sSmoothStep(value, upSlopeStart, upSlopeEnd) - sSmoothStep(value, downSlopeStart, downSlopeEnd);
	}

	static float sSmoothStep(float value, float slopeStart, float slopeEnd) {
		float diff;
		if (value < slopeStart)
			return 0;
		if (value >= slopeEnd)
			return 1;
		diff = slopeEnd - slopeStart;
		if (diff == 0)
			return sStep(value, slopeStart);
		value = (value - slopeStart) / diff; /* normalize to [0..1] */
		return value * value * (3 - 2 * value);
	}
	
	private static final Random rnd = new Random();

	static float random(float min, float max){
		return rnd.nextFloat() * (max - min) + min;	
	}

	static long millis(){
		return System.currentTimeMillis();
	}

	static float sin(float radians){
		return (float)Math.sin(radians);
	}

	static float cos(float radians){
		return (float)Math.cos(radians);
	}

	static class ImageGenerator{
		private Font font;
		
		ImageGenerator(){
			this(null);
		}
		
		ImageGenerator(Font font){
			this.font = font;
		}
		
		public BufferedImage generate( String text, int width, int height, int bordeH, int bordeV ){
			
			BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_GRAY);

			Graphics2D g2d = img.createGraphics();
			g2d.setBackground(Color.BLACK);
			g2d.clearRect(0, 0, width, height);
			g2d.setColor(Color.WHITE);

			g2d.setRenderingHint(
					RenderingHints.KEY_FRACTIONALMETRICS,
	                RenderingHints.VALUE_FRACTIONALMETRICS_ON
			);
			g2d.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			
			if(font == null)
				font = g2d.getFont();		
			FontMetrics fontMetrics = g2d.getFontMetrics(font);
			
			char chars[] = text.toCharArray();
			
			double defaultWidth = fontMetrics.charsWidth(chars, 0, chars.length);
			double defaultHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
			
			AffineTransform trasnform = new AffineTransform();
			double scaleX = ( img.getWidth() - 2 * bordeH)/defaultWidth;
			double scaleY = ( img.getHeight() - 2 * bordeV )/defaultHeight;
			trasnform.scale( scaleX, scaleY );
			Font font1 = font.deriveFont( trasnform );
			fontMetrics = g2d.getFontMetrics(font1);
			g2d.setFont(font1);
			g2d.drawString(text, bordeH, bordeV + fontMetrics.getMaxAscent());
			return img;
		}
	}
	
	static enum Comparadores implements Comparator<Point> {
		IZQUIERDA_DERECHA{
			@Override
			public int compare(Point o1, Point o2) {
				int d = o2.x - o1.x;
				return d != 0 ? d: o2.y - o1.y;
			}
		},
		DERECHA_IZQUIERDA{
			@Override
			public int compare(Point o1, Point o2) {
				int d = o1.x - o2.x;
				return d !=0 ? d: o1.y - o2.y;
			}
		},
		ARRIBA_ABAJO{
			@Override
			public int compare(Point o1, Point o2) {
				int d = o2.y - o1.y;
				return d != 0 ? d: o2.x - o1.x;
			}
		},
		ABAJO_ARRIBA{
			@Override
			public int compare(Point o1, Point o2) {
				int d = o1.y - o2.y;
				return d != 0 ? d: o1.x - o2.x;
			}
		}
	}

	static class DistanciaEuclidea implements Comparator<Point>{
		final Point center;

		DistanciaEuclidea(Point center){
			this.center = center;
		}

		int d2(Point o1){
			int dx = (o1.x - center.x);
			int dy = (o1.y - center.y);
			return dx*dx + dy*dy; 
		}

		public int compare(Point o1, Point o2) {
			int d = d2(o2) - d2(o1);
			return d != 0 ? d: Comparadores.IZQUIERDA_DERECHA.compare(o1, o2);
		}
	}

	static class DistanciaManhatan implements Comparator<Point>{
		final Point center;

		DistanciaManhatan(Point center){
			this.center = center;
		}

		int d2(Point o1){
			return Math.abs(o1.x - center.x) + Math.abs(o1.y - center.y); 
		}

		public int compare(Point o1, Point o2) {
			int d = d2(o2) - d2(o1);
			return d != 0 ? d: Comparadores.IZQUIERDA_DERECHA.compare(o1, o2);
		}
	}

	static class Inverso implements Comparator<Point>{
		final Comparator<Point> comp;

		Inverso(Comparator<Point> comp){
			this.comp = comp;
		}

		public int compare(Point o1, Point o2) {
			return -comp.compare(o1, o2);
		}
	}

	private static class Dots {
		int n;
		float[] x1, y1, x2, y2;

		public void init(Image img, int startx, int starty, int endtopleftx, int endtoplefty, float imgscale, Comparator<Point> comparator) {

			int pixels[] = Imagen.toPixels(img);
			int height = img.getHeight(null);
			int width  = img.getWidth(null);
//			System.out.println(width +" x "+ height);

			SortedSet<Point> destinos = new TreeSet<Point>(comparator);
			for (int y = 0, o = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x, ++o) {
					if ((pixels[o] & 0xffffff) == 0xffffff)
						destinos.add( new Point(x,y) );
				}
			}

			n = destinos.size();
			x1 = new float[n];
			y1 = new float[n];
			for (int a = 0; a < n; ++a) {
				x1[a] = startx;
				y1[a] = starty;
			}

			x2 = new float[n];
			y2 = new float[n];
			int px = 0;
			for(Point p: destinos){
				x2[px] = endtopleftx + p.x * imgscale;
				y2[px] = endtoplefty + p.y * imgscale;
				++px;
			}
//			System.out.println(n);
			destinos.clear();
		}
	}

	private static class Renderer implements GLEventListener{ 

		private UnidadTextura fondo;
		Dots[] dots;

		private final int START_X1 = 480;
		private final int START_Y1 = 250;
		private final int START_X2 = 360;
		private final int START_Y2 = 190;

		@Override
		public void init(GLAutoDrawable drawable) {
			GL gl = drawable.getGL();

			fondo = new UnidadTextura();
			fondo.setTextura(
					new Textura(
							gl, Textura.Format.RGB,
							Imagen.cargarToBufferedImage("resources/texturas/cielo512.jpg"),
							true
					)
			);
			fondo.setAtributosTextura(new AtributosTextura());
			fondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);

			dots = new Dots[8];
			for (int a = 0; a < dots.length; ++a) {
				dots[a] = new Dots();
			}
			
			Image img;
			ImageGenerator generator = new ImageGenerator(
					Tipografias.load("resources/fonts/smelo.ttf", Font.PLAIN, 64.0f)
			);
			
			img = generator.generate("Hola   ", 300, 100, 5, 5);
			dots[0].init(img, START_X1, START_Y1, 50, 50, 2.0f, new Inverso(new DistanciaEuclidea(new Point())));
			img = generator.generate("Alberto", 300, 100, 5, 5);
			dots[1].init(img, START_X2, START_Y2, 150, 400, 2.0f, new DistanciaEuclidea(new Point()));
			img = generator.generate("Hola ", 300, 100, 5, 5);
			dots[2].init(img, START_X1, START_Y1, 50, 50, 2.0f, Comparadores.IZQUIERDA_DERECHA);
			img = generator.generate("soy sam", 300, 100, 5, 5);
			dots[3].init(img, START_X2, START_Y2, 150, 400, 2.0f, Comparadores.DERECHA_IZQUIERDA);
			img = generator.generate("Soy", 300, 100, 5, 5);
			dots[4].init(img, START_X1, START_Y1, 50, 50, 2.0f, Comparadores.ARRIBA_ABAJO);
			img = generator.generate("Sam", 300, 100, 5, 5);
			dots[5].init(img, START_X2, START_Y2, 150, 400, 2.0f, Comparadores.ABAJO_ARRIBA);
			img = generator.generate("Y en esto he", 300, 100, 5, 5);
			dots[6].init(img, START_X1, START_Y1, 50, 50, 2.0f, new DistanciaManhatan(new Point()));
			img = generator.generate("perdido la tarde", 300, 100, 5, 5);
			dots[7].init(img, START_X2, START_Y2, 150, 400, 2.0f, new DistanciaManhatan(new Point(300,100)));
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		}

		private transient long startTime;
		private transient boolean primeraLlamada = true;
		@Override
		public void display(GLAutoDrawable drawable) {
			if (primeraLlamada) {
				startTime = millis();
				primeraLlamada = false;
			}
			long time = millis() - startTime;

			GL gl = drawable.getGL();
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);

			if (time <= 10000) {
				drawDots(gl, time, 0, 10000, dots[0], dots[1]);
				if (time <= 5000) {
					drawFade(gl, time, 0, 5000, false);
				}
			} else if (time > 10000 && time <= 20000) {
				drawDots(gl, time, 10000, 10000, dots[2], dots[3]);
			} else if (time > 20000 && time <= 30000) {
				drawDots(gl, time, 20000, 10000, dots[4], dots[5]);
			} else if (time > 30000 && time <= 45000) {
				drawDots(gl, time, 30000, 15000, dots[6], dots[7]);
				if (time >= 40000)
					drawFade(gl, time, 40000, 45000, true);
			}
			gl.glFlush();
		}

		private void drawBackground(GL gl, int imgW, int imgH){

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();

			float s = (float)(screenWidth - imgW)/(2 * imgW);
			float t = (float)(screenHeight - imgH)/(2 * imgH);
			gl.glOrtho( 0, screenWidth, 0, screenHeight, 0, 1);

			fondo.activar(gl, 0);

			gl.glDepthMask(false);
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f (-s,-t);
			gl.glVertex2f   ( 0, 0);
			gl.glTexCoord2f ( 1+s,-t);
			gl.glVertex2f   ( screenWidth, 0);
			gl.glTexCoord2f ( 1+s, 1+t);
			gl.glVertex2f   ( screenWidth, screenHeight);
			gl.glTexCoord2f (-s, 1+t);
			gl.glVertex2f   ( 0, screenHeight);
			gl.glEnd();
			gl.glDepthMask(true);

			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			UnidadTextura.desactivar(gl, 0);
		}

		/**
		 * @param timeSpan unusued 
		 */
		private void drawDots(GL gl, long time, long startTime, long timeSpan, Dots credit, Dots name) {

			drawBackground(gl, 512, 512);

			time -= startTime;

			gl.glEnable(GL.GL_BLEND);
			gl.glBlendEquation(GL.GL_FUNC_ADD);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			gl.glDepthMask(false);

			gl.glPointSize(8);
			gl.glBegin(GL.GL_POINTS);

			Dots d = credit;
			for (int a = 0; a < d.n; ++a) {
				float ct = time * 0.001f + 5.0f*a/d.n + random(0, 0.1f);
				float t = sSmoothStep(ct, 1, 7);
				float t1 = 1 - t;
				float x = d.x1[a] * t1 + d.x2[a] * t;
				float y = d.y1[a] * t1 + d.y2[a] * t;
				float fxm = sBoxPulse(ct, 1, 3, 5, 7);
				float fxt = ct * 4.5f;
				x += fxm * cos(fxt) * 150;
				y += fxm * sin(fxt * 2) * 50;
				gl.glColor4f( 0.16f*t, 0.1f, 0.25f * (1.0f-t), t*0.5f);
				gl.glVertex2f(x, y);
			}
			d = name;
			for (int a = 0; a < d.n; ++a) {
				float ct = time * 0.001f + random(4.5f, 5.0f)*a/d.n;
				float t = sSmoothStep(ct, 4, 10);
				float t1 = 1 - t;
				float x = d.x1[a] * t1 + d.x2[a] * t;
				float y = d.y1[a] * t1 + d.y2[a] * t;
				float fxm = sBoxPulse(ct, 4, 6, 8, 10);
				float fxt = ct * 1.5f;
				x += fxm * cos(fxt * 2) * 50;
				y += fxm * sin(fxt) * 50;
				gl.glColor4f( 1 - .84f*t, .5f - 0.4f*t, 0.25f * (1.0f-t), t*0.5f);
				gl.glVertex2f(x, y);
			}
			gl.glEnd();
//			gl.glEnable(GL.GL_TEXTURE_2D);
//			gl.glDisable( GL.GL_POINT_SPRITE );

			gl.glDepthMask(true);
			gl.glDisable(GL.GL_BLEND);
		}

		private void drawFade(GL gl, long time, int startTime, int stopTime, boolean toBlack) {

			float alpha = toBlack ? sBoxStep(time, startTime, stopTime) : sBoxStep(time, stopTime, startTime);

			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDepthMask(false);

			gl.glBegin(GL.GL_QUADS);
			gl.glColor4f(0, 0, 0, alpha);
			gl.glVertex2f(0, 0);
			gl.glVertex2f(0, screenHeight);
			gl.glVertex2f(screenWidth, screenHeight);
			gl.glVertex2f(screenWidth, 0);
			gl.glEnd();

			gl.glDepthMask(true);
			gl.glDisable(GL.GL_BLEND);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_TEXTURE_2D);
		}

		@Override
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			if( width != 0 && height != 0 ){
				GL gl = drawable.getGL();
				gl.glViewport( (width - screenWidth)/2, (height - screenHeight)/2, screenWidth, screenHeight);

				gl.glMatrixMode(GL.GL_PROJECTION);
				gl.glLoadIdentity();
				gl.glOrtho(0, screenWidth, screenHeight, 0, -1, 1);
				gl.glMatrixMode(GL.GL_MODELVIEW);
			}
		}
	}

	public static void main(String[] args){

		JFrame frame = new JFrame("Dots Example");
		frame.getContentPane().setPreferredSize( new Dimension (800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GLCanvas canvas = new GLCanvas(new GLCapabilities());
		canvas.setBackground(Color.BLACK);

		Renderer renderer = new Renderer();
		canvas.addGLEventListener(renderer);

		frame.getContentPane().add( canvas, BorderLayout.CENTER );

		Animator animator = new Animator();
		animator.setRunAsFastAsPossible(true);
		animator.add(canvas);
	
		frame.setVisible(true);
		animator.start();
	}
}