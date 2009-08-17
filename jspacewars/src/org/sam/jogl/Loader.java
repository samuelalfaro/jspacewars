package org.sam.jogl;

import java.io.*;

import javax.media.opengl.*;

import org.sam.jogl.fondos.CieloEstrellado;
import org.sam.util.ModificableBoolean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Loader implements GLEventListener{

	private final DataGame dataGame;
	private final ModificableBoolean loading;

	public Loader( DataGame dataGame, ModificableBoolean loading ){
		this.dataGame = dataGame;
		this.loading = loading;
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable){
		dataGame.setGLContext(drawable.getContext());
	}

	private transient ObjectInputStream in;
	private transient boolean eof;
	private transient float r = 1.0f, g = 0.0f, b = 0.0f;

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		
		if(dataGame.getFondo() == null)
			dataGame.setFondo( 
					new CieloEstrellado( gl, "resources/texturas/cielo1.jpg", "resources/texturas/spark.jpg" )
			);
		else
			try {
				if(in == null){
					XStream xStream = new XStream(new DomDriver());
					xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
					GrafoEscenaConverters.register(xStream);
					GrafoEscenaConverters.setReusingReferenceByXPathMarshallingStrategy(xStream);

					in = xStream.createObjectInputStream(new FileReader("instancias3D-stream.xml"));
				}
				if( !eof )
					try{
						dataGame.getCache().addPrototipo((Instancia3D) in.readObject());
					}catch( ClassNotFoundException e ){
						e.printStackTrace();
					}catch( EOFException eofEx ){
						eof = true;
						in.close();
					}
//					Instancia3D[] instancias =  (Instancia3D[])xStream.fromXML(new FileReader("instancias3D.xml"));
//					StringWriter writer = new StringWriter();
//					try{
//						ObjectOutputStream out = xStream.createObjectOutputStream(writer);
//						for(Instancia3D instancia: instancias)
//							out.writeObject(instancia);
//					}catch( IOException e ){
//						e.printStackTrace();
//					}
//					System.out.println(writer.toString());
//					for(Instancia3D instancia: instancias)
//						cache.addPrototipo( instancia );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ObjLoader.ParsingErrorException e) {
				e.printStackTrace();
			} catch (GLException e) {
				e.printStackTrace();
			}

		gl.glClearColor(r,g,b, 0.0f);
		g += 0.05f;
		if(g > 1.0f){
			g = 1.0f;
			r -= 0.05f;
			if (r < 0)
				r = 0.0f;
		}
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glFlush();

		if(eof){
			synchronized(loading){
				loading.setFalse();
				loading.notifyAll();
			}
			drawable.removeGLEventListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
	}
}