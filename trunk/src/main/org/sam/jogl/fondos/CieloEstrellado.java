/* 
 * CieloEstrellado.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl.fondos;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.elementos.Modificador;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.MatrixSingleton;
import org.sam.jogl.Textura;
import org.sam.jogl.particulas.Emisor;
import org.sam.jogl.particulas.FactoriaDeParticulas;
import org.sam.jogl.particulas.Particulas;
import org.sam.util.Imagen;

public class CieloEstrellado implements Fondo, Modificador{
	
	private transient final Apariencia apFondo;
	private transient final float proporcionesTextura;
	private transient final Particulas[] estrellas;
	private transient float proporcionesPantalla, s1, s2;
	
	public CieloEstrellado(GL2 gl, String texturePath, String starstexturePath){
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

		FactoriaDeParticulas.setOptimizedFor2D(true);
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
		FactoriaDeParticulas.setOptimizedFor2D(false);
	}


	/* (non-Javadoc)
	 * @see org.sam.jogl.fondos.Fondo#setProporcionesPantalla(float)
	 */
	@Override
	public void setProporcionesPantalla(float proporcionesPantalla) {
		this.proporcionesPantalla = proporcionesPantalla;
		this.s2 = proporcionesPantalla / proporcionesTextura;
	}


	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {

		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
		MatrixSingleton.loadModelViewMatrix();

		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho( 0.0, proporcionesPantalla, 0.0, 1.0, 0.0, 1.0 );

		apFondo.usar( gl );
		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
		gl.glDepthMask( false );
		gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( s1, 0.0f );
			gl.glVertex2f( 0.0f, 0.0f );
			gl.glTexCoord2f( s2 + s1, 0.0f );
			gl.glVertex2f( proporcionesPantalla, 0.0f );
			gl.glTexCoord2f( s2 + s1, 1.0f );
			gl.glVertex2f( proporcionesPantalla, 1.0f );
			gl.glTexCoord2f( s1, 1.0f );
			gl.glVertex2f( 0.0f, 1.0f );
		gl.glEnd();

		for( Particulas p: estrellas )
			p.draw( gl );

		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glPopMatrix();
		MatrixSingleton.loadProjectionMatrix();
		
	}

	/* (non-Javadoc)
	 * @see org.sam.elementos.Modificable#getModificador()
	 */
	@Override
	public Modificador getModificador() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.sam.elementos.Modificador#modificar(float)
	 */
	@Override
	public boolean modificar(float steep) {
		s1 += 0.02f * steep;
		if(s1 > 1.0f)
			s1 -= 1.0f;
		for(Particulas p:estrellas )
			p.getModificador().modificar(steep);
		return false;
	}
}
