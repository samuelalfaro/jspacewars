import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

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
@SuppressWarnings("serial")
public class PruebaRaster
extends JFrame
implements GLEventListener, KeyListener, MouseMotionListener
{
	private GLCapabilities caps;
	private GLCanvas canvas;
	private GLU glu;
	//
	//private ByteBuffer checkImageBuf = BufferUtil.newByteBuffer(checkImageHeight * checkImageWidth * rgb);
	
	
	private BufferedImage bf;
//	private TextureData textureData;
	private Buffer checkImage;
	
	private int checkImageWidth;
	private int checkImageHeight;

	private static float zoomFactor = 1.0f;

	//
	public PruebaRaster(){
		super("Prueba Raster");
		
		bf = Imagen.cargarToBufferedImage("resources/texturas/disparos.png");
//		textureData = new TextureData(0,0,false,bf);
		checkImage = Imagen.toBuffer(bf, true);
		
		checkImageWidth =  bf.getWidth();
		checkImageHeight = bf.getHeight();
		
		//System.out.println(textureData.getBuffer());
		//System.out.ln(checkImage);
		
		/*
		 * display mode (single buffer and RGBA)
		 */
		caps = new GLCapabilities();
		//caps.setDoubleBuffered(false);
		System.out.println(caps.toString());
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseMotionListener(this);
		//
		getContentPane().add(canvas);
	}

	/*
	 * Declare initial window size, position, and set frame's close behavior. Open
	 * window with "hello" in its title bar. Call initialization routines.
	 * Register callback function to display graphics. Enter main loop and process
	 * events.
	 */
	public void run(){
		setSize(250, 250);
		setLocationRelativeTo(null); // center
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		canvas.requestFocusInWindow();
	}

	public static void main(String[] args){
		new PruebaRaster().run();
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

		gl.glRasterPos2i(0,0);

		gl.glPixelZoom(zoomFactor, zoomFactor);
	
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glDrawPixels(
				checkImageWidth, checkImageHeight, 
				GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
//				GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
//				GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE,
				checkImage);
		
		gl.glDepthMask(false);
		gl.glFlush();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
		GL gl = drawable.getGL();

		gl.glViewport(0, 0, w, h);
		
		zoomFactor = Math.min(((float)w)/checkImageWidth,((float)h)/checkImageHeight);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0, w, 0.0, h);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged){
	}

	public void keyTyped(KeyEvent e){
	}

	public void keyPressed(KeyEvent key){
		switch (key.getKeyChar()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case 'r':
		case 'R':
			zoomFactor = 1.0f;
			System.out.println("zoomFactor reset to 1.0\n");
			break;
		case 'z':
			zoomFactor *= 2.0f;
			if (zoomFactor >= 8.0) zoomFactor = 8.0f;
			System.out.println("zoomFactor is now " + zoomFactor);
			break;
		case 'Z':
			zoomFactor *= 0.5f;
			if (zoomFactor <= 0.125) zoomFactor = 0.125f;
			System.out.println("zoomFactor is now " + zoomFactor);
			break;

		default:
			break;
		}
		canvas.display();
	}

	public void keyReleased(KeyEvent e){
	}

	public void mouseDragged(MouseEvent e){
	}

	public void mouseMoved(MouseEvent mouse){
		canvas.display();
	}
}