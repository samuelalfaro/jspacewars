package org.sam.jspacewars.cliente;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.sam.colisiones.Poligono;
import org.sam.jogl.Apariencia;
import org.sam.jogl.Instancia3D;
import org.sam.jogl.Nodo;
import org.sam.jspacewars.servidor.elementos.Elemento;
import org.sam.util.Reflexion;

/**
 * 
 * @author Samuel Alfaro
 */
public class ClienteTestColisiones {
	
	static class PoligonoDibujable implements Nodo{
		
		private static Poligono getPoligono(Elemento e){
			java.lang.reflect.Field f = Reflexion.findField(Elemento.class, "forma");
			if (f == null)
				return null;
			try {
				boolean accesible = f.isAccessible();
				f.setAccessible(true);
				Poligono p = (Poligono)f.get(e);
				f.setAccessible(accesible);
				return p;
			} catch (IllegalAccessException ignorada) {
				return null;
			}
		}
		
		private static float[] getArray(Poligono p, String name){
			java.lang.reflect.Field f = Reflexion.findField(Poligono.class, name);
			try {
				boolean accesible = f.isAccessible();
				f.setAccessible(true);
				float[] array = (float[])f.get(p);
				f.setAccessible(accesible);
				return array;
			} catch (IllegalAccessException ignorada) {
				return null;
			}
		}
		
		final Poligono p;
		PoligonoDibujable(Elemento e){
			this.p = getPoligono(e);
		}

		/* (non-Javadoc)
		 * @see org.sam.jogl.Nodo#getParent()
		 */
		public Nodo getParent() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.sam.jogl.Nodo#setParent(org.sam.jogl.Nodo)
		 */
		public void setParent(Nodo parent) {
		}
		
		private transient final Nodo[] nodos = new Nodo[]{}; 
		/* (non-Javadoc)
		 * @see org.sam.jogl.Nodo#getChilds()
		 */
		public final Nodo[] getChilds(){
			return nodos;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		public Nodo clone(){
			return this;
		}
		
		@Override
		public void draw(GL gl) {
			if(p != null){
				int nLados = p.getNLados();
				float coordX[] = getArray(p, "coordX");
				float coordY[] = getArray(p, "coordY");
				
				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor3f( 0.25f, 1.0f, 0.25f );
				for(int i=0; i< nLados; i++){
					gl.glVertex3f(coordX[i],coordY[i],0);
				}
				gl.glVertex3f(coordX[0],coordY[0],0);
				gl.glEnd();
			}
		}
	}
	
	static class Renderer implements GLEventListener {

		private final transient GLU glu = new GLU();

		private final transient ClientData data;
		private final transient Apariencia apLineas;
		private final transient MarcoDeIndicadores marco;
		
		Renderer( ClientData data) {
			this.data = data;
			this.apLineas = new Apariencia();
			this.marco = MarcoDeIndicadores.getMarco(0);
		}

		private boolean iniciado = false;
		
		public void init(GLAutoDrawable drawable) {
			GL gl = drawable.getGL();
			while(!marco.isLoadComplete())
				marco.loadTexturas(gl);

//			gl.glShadeModel(GL.GL_SMOOTH);
//
//			gl.glEnable(GL.GL_DEPTH_TEST);
//			gl.glDepthFunc(GL.GL_LESS);
//			
//
//			gl.glEnable(GL.GL_CULL_FACE);

			iniciado = true;
		}

		public void display(GLAutoDrawable drawable) {
			if(!iniciado){
				reshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
				init(drawable);
			}
			GL gl = drawable.getGL();

			gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

			marco.setViewportAreaInterna(gl);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			glu.gluLookAt(0.0, 0.0, 11, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

			apLineas.usar(gl);
			for( Instancia3D elemento: data.elementos )
				elemento.draw(gl);
//		 	gl.glDepthMask(false);
			
			marco.update(data);
			marco.draw(gl);
			gl.glFlush();
		}

		public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		}

		//private transient final Rectangle areaInterna = new Rectangle();
		
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			GL gl = drawable.getGL();
			gl.glViewport(0, 0, width, height);
			marco.setBounds( 0, 0, width, height );

			
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			double near = 0.5;
			double far = 240.0;
			double a1 = 35.0; // angulo en grados
			double a2 = a1/360*Math.PI; // mitad del angulo en radianes
			double d  = near/Math.sqrt((1/Math.pow(Math.sin(a2), 2))-1);

			// Formato 4/3 centrado, panorámico a la derecha en caso contrario.
			float ratio_4_3 = marco.getAreaInternaWidth(4, 3)/marco.getAreaInternaHeight(4, 3);
			float aWidth = marco.getAreaInternaWidth( width, height );
			float aHeight =marco.getAreaInternaHeight( width, height );
			
			gl.glFrustum(-ratio_4_3 * d, ((2.0 * aWidth) / aHeight - ratio_4_3) * d, -d, d, near, far);
			
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
	}

	private final transient ClientData data;

	public ClienteTestColisiones(GLCanvas canvas) {
		this.data =  new ClientData();
				
		canvas.setBackground(Color.BLACK);
		canvas.setIgnoreRepaint(true);
		canvas.addGLEventListener( new Renderer(data) );
		canvas.addKeyListener(new GameKeyListener(data));	
	}
	
	public int getKeyState(){
		return data.key_state;
	}
	
	public void setNVidas(int nVidas){
		data.nVidas = nVidas;
	}
	
	public void setNBombas(int nBombas){
		data.nBombas = nBombas;
	}
	
	public void setPuntos(int puntos){
		data.puntos = puntos;
	}
	
	public void setNivelesFijos(int[] nivelesFijos) {
		for(int i = 0; i < nivelesFijos.length; i++ )
			data.nivelesFijos[i] = nivelesFijos[i];
	}

	public void setNivelesActuales(int[] nivelesActuales) {
		for(int i = 0; i < nivelesActuales.length; i++ )
			data.nivelesActuales[i] = nivelesActuales[i];
	}

	public void setNivelesDisponibles(int[] nivelesDisponibles) {
		for(int i = 0; i < nivelesDisponibles.length; i++ )
			data.nivelesDisponibles[i] = nivelesDisponibles[i];
	}
	
	public void setIndicador(int indicador){
		data.indicador = indicador;
	}
	
	public void setGrado(int grado){
		data.grado = grado;
	}
	
	public void clearList(){
		data.elementos.clear();
	}
	
	public void add(Elemento e){
		data.elementos.add( new Instancia3D( (short)0, new PoligonoDibujable (e)) );
	}
}