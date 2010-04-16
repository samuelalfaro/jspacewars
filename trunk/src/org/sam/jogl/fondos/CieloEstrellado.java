package org.sam.jogl.fondos;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.elementos.Modificador;
import org.sam.jogl.*;
import org.sam.jogl.particulas.*;
import org.sam.util.*;

/**
 * @author samuel
 *
 */
public class CieloEstrellado implements Fondo, Modificador{
	
	private transient final Apariencia apFondo;
	private transient final float proporcionesTextura;
	private transient final Particulas[] estrellas;
	private transient float proporcionesPantalla, s1, s2;
	
	public CieloEstrellado(GL gl, String texturePath, String starstexturePath){
		apFondo = new Apariencia(); 
		apFondo.setTextura(
				new Textura(gl, Textura.Format.RGB, Imagen.cargarToBufferedImage(texturePath), true)
		);
		apFondo.setAtributosTextura(new AtributosTextura());
		apFondo.getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
		
		proporcionesTextura = apFondo.getTextura().getProporciones();

		Apariencia apEstrellas = new Apariencia();
		apEstrellas.setTextura(
				new Textura(gl, Textura.Format.ALPHA, Imagen.cargarToBufferedImage(starstexturePath), true)
		);
		apEstrellas.setAtributosTransparencia(
				new AtributosTransparencia(
						AtributosTransparencia.Equation.ADD,
						AtributosTransparencia.SrcFunc.SRC_ALPHA,
						AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA)
		);
		estrellas = new Particulas[20];
		
		Matrix4f tEmisor = new Matrix4f();
		tEmisor.setIdentity();
		tEmisor.rotZ((float)Math.PI);
		tEmisor.setTranslation(new Vector3f(2.05f,0.5f,0.0f));
		Emisor emisor = new Emisor.Cache(new Emisor.Lineal(1.0f,0.0f),tEmisor,1024);

		FactoriaDeParticulas.setOptimizedForStars(true);
		for(int i = 0, len = estrellas.length; i< len; i++){
			estrellas[i] = FactoriaDeParticulas.createParticulas((int)( Math.pow(8*(len-i)/len, 2) + 1 ));
			estrellas[i].setEmisor(emisor);
			estrellas[i].setEmision(Particulas.Emision.CONTINUA);
			estrellas[i].setRangoDeEmision(1.0f);
			estrellas[i].setRadio(0.004f + (0.012f * (i+1) )/len);
			float vel = 0.1f*i +  0.05f;
			float tVida = 2.05f/(vel*0.95f);
			estrellas[i].setTiempoVida(tVida);
			estrellas[i].setVelocidad(vel, vel*0.05f, false);
			estrellas[i].setGiroInicial(0, 180, true);
			estrellas[i].setColor(
					0.65f + (0.35f * (i+1))/len,
					0.35f + (0.65f * (i+1))/len,
					0.85f + (0.15f * (i+1))/len,
					1.0f
			);
			estrellas[i].setPertubacionColor(0.25f, false, false);
			estrellas[i].setApariencia(apEstrellas);
			estrellas[i].reset();
			estrellas[i].getModificador().modificar(tVida);
		}
		FactoriaDeParticulas.setOptimizedForStars(false);
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.fondos.Fondo#setProporcionesPantalla(float)
	 */
	public void setProporcionesPantalla(float proporcionesPantalla) {
		this.proporcionesPantalla = proporcionesPantalla;
		this.s2 = proporcionesPantalla / proporcionesTextura;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL)
	 */
	public void draw(GL gl) {
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		ObjetosOrientables.loadModelViewMatrix();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0.0, 1.0);
		
		apFondo.usar(gl);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glDepthMask(false);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(s1,0.0f);
			gl.glVertex3f(0.0f,0.0f,0.0f);
			gl.glTexCoord2f(s2 + s1,0.0f);
			gl.glVertex3f(proporcionesPantalla,0.0f,0.0f);
			gl.glTexCoord2f(s2 + s1,1.0f);
			gl.glVertex3f(proporcionesPantalla,1.0f,0.0f);
			gl.glTexCoord2f(s1,1.0f);
			gl.glVertex3f(0.0f,1.0f,0.0f);
		gl.glEnd();

		for(Particulas p:estrellas )
			p.draw(gl);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		ObjetosOrientables.loadProjectionMatrix();
		
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Modificable#getModificador()
	 */
	public Modificador getModificador() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Modificador#modificar(float)
	 */
	public boolean modificar(float steep) {
		s1 += 0.02f * steep;
		if(s1 > 1.0f)
			s1 -= 1.0f;
		for(Particulas p:estrellas )
			p.getModificador().modificar(steep);
		return false;
	}
}