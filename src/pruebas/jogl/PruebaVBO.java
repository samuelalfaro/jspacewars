package pruebas.jogl;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.*;

import javax.media.opengl.*;
import javax.swing.JFrame;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/**
 */
@SuppressWarnings("serial")
public class PruebaVBO
extends JFrame
implements GLEventListener, KeyListener, MouseListener, MouseMotionListener{
	
	private static class VBO{
		int idBuff;
		ByteBuffer byteBuffer;
		FloatBuffer floatBuffer;
		
		int count = 0;
		
		FloatBuffer get(GL gl, int tam){
			count ++;
			if(byteBuffer == null){
				int[] tmp = new int[1];
				gl.glGenBuffers(1, tmp, 0);
				idBuff = tmp[0];
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, idBuff);
				gl.glBufferData(GL.GL_ARRAY_BUFFER, tam * BufferUtil.SIZEOF_FLOAT, null, GL.GL_STREAM_DRAW);
				byteBuffer = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY);
				if (byteBuffer == null) {
					throw new RuntimeException("Unable to map vertex buffer object");
				}
				floatBuffer = byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
				System.err.println("new " + count + ":"+ byteBuffer);
			}else{
				ByteBuffer tmp;
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, idBuff);
				tmp = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY);
				if (tmp == null) {
					throw new RuntimeException("Unable to map vertex buffer object");
				}
				if(tmp != byteBuffer){
					byteBuffer = tmp;
					floatBuffer = byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
					System.err.println("cambio " + count + ":\n" + byteBuffer);
				}
			}
			gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			return floatBuffer;
		}
	}
	
	private GLCanvas canvas;
	private Texture texture1;

	private VBO posVBO;
	private FloatBuffer pos;
	private VBO colVBO;
	private FloatBuffer col;
	private VBO texVBO;
	private FloatBuffer tex;
	
	static class Animador extends Thread{
		private GLCanvas canvas;
		
		Animador(GLCanvas canvas){
			this.canvas = canvas;
		}
		
		public void run(){
			while(true){
				canvas.display();
				try{
					Thread.sleep(5);
				}catch( InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public PruebaVBO(){
		super("PruebaVBO");
		
		posVBO = new VBO();
		colVBO = new VBO();
		texVBO = new VBO();

		canvas = new GLCanvas(new GLCapabilities());
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		getContentPane().add(canvas);
	}

	public void run(){
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.requestFocusInWindow();
		canvas.getContext().getGL();

		setVisible(true);
		new Animador(canvas).start();
	}

	public static void main(String[] args){
		new PruebaVBO().run();
	}

	public void init(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		gl.glClearColor(0.0f,0.25f,0.25f,0.0f);
		
		try {
			texture1 = TextureIO.newTexture(new File("resources/obj3d/formas/nave03/t01.jpg"), true);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	private double alfa = 0.0f;
	
	public void display(GLAutoDrawable drawable){

		GL gl = drawable.getGL();
		
		float cosA = (float)(Math.cos(alfa)*0.5);
		float senA = (float)(Math.sin(alfa)*0.5);
		float p1 = cosA + senA;
		float p2 = cosA - senA;
		
		alfa += 0.01;
		
		pos = posVBO.get(gl, 4 * 3);
		pos.rewind();
		pos.put( - p2); pos.put( - p1); pos.put(0.0f);
		pos.put( + p1); pos.put( - p2); pos.put(0.0f);
		pos.put( + p2); pos.put( + p1); pos.put(0.0f);
		pos.put( - p1); pos.put( + p2); pos.put(0.0f);
		pos.rewind();

		col = colVBO.get(gl, 4 * 4);
		col.rewind();
		col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
		col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
		col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
		col.put(1.0f);col.put(1.0f);col.put(1.0f);col.put(1.0f);
//		col.put(0.5f+cosA); col.put(0.5f+senA); col.put(0.5f+senA); col.put(0.25f);
//		col.put(0.5f+senA); col.put(0.5f+senA); col.put(0.5f+senA); col.put(0.0f);
//		col.put(0.5f+senA); col.put(0.5f+cosA); col.put(0.5f+senA); col.put(0.25f);
//		col.put(0.5f+senA); col.put(0.5f+senA); col.put(0.5f+cosA); col.put(1.0f);
		col.rewind();

		tex = texVBO.get(gl, 4 * 2);
		tex.rewind();
		tex.put(cosA); tex.put(senA);
		tex.put(cosA); tex.put(1.0f+cosA);
		tex.put(1.0f+senA); tex.put(1.0f+cosA);
		tex.put(1.0f+senA); tex.put(senA);
		tex.rewind();
		
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glPushMatrix();
		gl.glRotatef( -g_fSpinY, 1.0f, 0.0f, 0.0f );
		gl.glRotatef( -g_fSpinX, 0.0f, 1.0f, 0.0f );

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		
		texture1.bind();


		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);	
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, posVBO.idBuff);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
		//gl.glVertexPointer(3, GL.GL_FLOAT, 0, posVBO.floatBuffer);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colVBO.idBuff);
		gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
		//gl.glColorPointer(4, GL.GL_FLOAT, 0, colVBO.floatBuffer);

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texVBO.idBuff);
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
		//gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texVBO.floatBuffer);
		
		gl.glDrawArrays(GL.GL_QUADS, 0, 4);

		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY); 

		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glPopMatrix();

		gl.glFlush();

	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
//		if(w!= 0 && h!= 0 ){
//			GL gl = drawable.getGL();
//			gl.glViewport(0, 0, w, h);
//
//			gl.glMatrixMode(GL.GL_PROJECTION);
//			gl.glLoadIdentity();
//			double near = 0.001;
//			double far  = 30.0;
//			double a1 = 30.0;			// angulo en grados
//			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
//			double d  = near/Math.sqrt( (1 / Math.pow(Math.sin(a2), 2) )-1);
//
//			double ratio = (double)w/h;
//			if(ratio>1)
//				gl.glFrustum(-d, d, -d/ratio, d/ratio, near, far);
//			else
//				gl.glFrustum(-d*ratio, d*ratio, -d, d, near, far);
//			
//			gl.glMatrixMode(GL.GL_MODELVIEW);
//		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){
	}

	public void keyTyped(KeyEvent key){
	}

	public void keyPressed(KeyEvent key){
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent key){
	}
	
	Point ptCurrentMousePosit, ptLastMousePosit;
	float g_fSpinX=0.0f;
	float g_fSpinY=0.0f;

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		ptLastMousePosit= e.getPoint();
	}

	public void mouseReleased(MouseEvent e) {}
	
	public void mouseDragged(MouseEvent e) {
		ptCurrentMousePosit = e.getPoint();
		
		g_fSpinX -= (ptCurrentMousePosit.x - ptLastMousePosit.x);
		g_fSpinY -= (ptCurrentMousePosit.y - ptLastMousePosit.y);

		ptLastMousePosit = ptCurrentMousePosit;
	}

	public void mouseMoved(MouseEvent e) {}
}