package pruebas.jogl;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.*;

import org.sam.jogl.Textura;
import org.sam.util.Imagen;

/**
 * This program demonstrates drawing pixels and shows the effect of
 * glDrawPixels(), glCopyPixels(), and glPixelZoom(). Interaction: moving the
 * mouse while pressing the mouse button will copy the image in the lower-left
 * corner of the window to the mouse position, using the current pixel zoom
 * factors. There is no attempt to prevent you from drawing over the original
 * image. If you press the 'r' key, the original image and zoom factors are
 * reset. If you press the 'z' or 'Z' keys, you change the zoom factors.
 * 
 * @author Kiet Le (Java port)
 */

public class Prueba00_Raster {

	private static class Renderer implements GLEventListener{
		
		private GLU glu;
	
		private Buffer checkImage;
		
		private int canvasWidth;
		private int canvasHeight;
		
		private int checkImageWidth;
		private int checkImageHeight;
		
		private final float[] zoomFactor;
		
		Renderer( BufferedImage bf, float[] zoomFactor ){
			checkImage = Textura.Util.toByteBuffer(bf, Textura.Format.RGBA, true);
			checkImageWidth =  bf.getWidth();
			checkImageHeight = bf.getHeight();
			this.zoomFactor = zoomFactor;
		}
		
		public void init(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
			glu = new GLU();
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			//		gl.glShadeModel(GL.GL_FLAT);
			//		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		}
	
		public void display(GLAutoDrawable drawable){
			GL gl = drawable.getGL();
	
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	
			int px = ( canvasWidth - (int)(checkImageWidth*zoomFactor[0]) )/2;
			int py = ( canvasHeight - (int)(checkImageHeight*zoomFactor[0]) )/2;
			
			int h = checkImageHeight;
			
			int offset = 0;
			if(px < 0){
				offset = (int)(-px /zoomFactor[0]);
				px = 0;
			}
			if(py < 0){
				h += (int)(py /zoomFactor[0])*2;
				offset += (int)(-py /zoomFactor[0])*checkImageWidth;
				py = 0;
			}
			
			checkImage.rewind();
			checkImage.position(offset*4);

			gl.glEnable(GL.GL_BLEND);
			gl.glBlendEquation(GL.GL_FUNC_ADD);
			gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDepthMask(false);
	
			gl.glRasterPos2i( px, py);
			gl.glPixelZoom(zoomFactor[0], zoomFactor[0]);
			gl.glDrawPixels(
					checkImageWidth, h, 
					GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
					checkImage);
			
//			gl.glFlush();
//			gl.glRasterPos2i( 0, 0);
//			gl.glCopyPixels(canvasWidth/4, canvasHeight/4, canvasWidth/2, canvasHeight/2, GL.GL_COLOR);
			
			gl.glDisable(GL.GL_BLEND);
			gl.glFlush();
		}
	
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
			GL gl = drawable.getGL();
	
			canvasWidth = w;
			canvasHeight = h;
			
			gl.glViewport(0, 0, w, h);
	
			zoomFactor[0] = Math.min(((float)w)/checkImageWidth,((float)h)/checkImageHeight);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluOrtho2D(0.0, w, 0.0, h);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
	
		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
				boolean deviceChanged){
		}
	}

	private static class MyKeyListener extends KeyAdapter{
		
		private final float[] zoomFactor;
		MyKeyListener(float[] zoomFactor){
			this.zoomFactor = zoomFactor;
		}
		
		public void keyPressed(KeyEvent key){
			switch (key.getKeyChar()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case 'r':
			case 'R':
				zoomFactor[0] = 1.0f;
				System.out.println("zoomFactor reset to 1.0\n");
				break;
			case 'z':
				zoomFactor[0] *= 2.0f;
				if (zoomFactor[0] >= 8.0) zoomFactor[0] = 8.0f;
				System.out.println("zoomFactor is now " + zoomFactor[0]);
				break;
			case 'Z':
				zoomFactor[0] *= 0.5f;
				if (zoomFactor[0] <= 0.125) zoomFactor[0] = 0.125f;
				System.out.println("zoomFactor is now " + zoomFactor[0]);
				break;
	
			default:
				break;
			}
			((GLCanvas)key.getComponent()).display();
		}
	}
	
	private static class MyMouseMotionListener extends MouseMotionAdapter{

		public void mouseMoved(MouseEvent mouse){
			((GLCanvas)mouse.getComponent()).display();
		}
	}

	public static void main(String[] args){
		
		BufferedImage bf = Imagen.cargarToBufferedImage("resources/texturas/explosion.png");
		float[] zoomFactor = new float[]{1.0f};
		
		JFrame frame = new JFrame("Prueba Raster");
		frame.getContentPane().setLayout(new GridLayout(0,2));
		frame.getContentPane().setBackground(Color.BLACK);
		
		frame.getContentPane().add(new JLabel(new ImageIcon(bf)));
		
		GLCapabilities caps = new GLCapabilities();
		//caps.setDoubleBuffered(false);
		System.out.println(caps.toString());
		
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Renderer(bf, zoomFactor));
		canvas.addKeyListener(new MyKeyListener(zoomFactor));
		canvas.addMouseMotionListener(new MyMouseMotionListener());
		frame.getContentPane().add(canvas);
		
		frame.getContentPane().setPreferredSize(new Dimension(500, 250));
		frame.pack();
		frame.setLocationRelativeTo(null); // center
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas.requestFocusInWindow();
	}
}