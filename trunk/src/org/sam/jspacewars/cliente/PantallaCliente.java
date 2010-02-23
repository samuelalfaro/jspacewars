/* 
 * PantallaCliente.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.cliente;

import java.awt.Container;

import org.sam.jspacewars.DataGame;

@Deprecated
@SuppressWarnings("serial")
public abstract class PantallaCliente extends Container {

//	private static class PantallaCliente1 extends PantallaCliente {
//
//		private static class LedsNiveles extends BarraImagenes {
//
//			private int vFijos, vActuales, vDisponibles;
//			private boolean hayCambios = false;
//
//			public LedsNiveles(double x, double y, double ancho, double alto, double bordeH, double bordeV,
//					int nValores, Image imgs[]) {
//				super(x, y, ancho, alto, bordeH, bordeV, nValores, imgs);
//			}
//
//			public void setVFijos(int fijos) {
//				if( vFijos != fijos ){
//					hayCambios = true;
//					vFijos = fijos;
//				}
//			}
//
//			public void setVActuales(int actuales) {
//				if( vActuales != actuales ){
//					hayCambios = true;
//					vActuales = actuales;
//				}
//			}
//
//			public void setVDisponibles(int disponibles) {
//				if( vDisponibles != disponibles ){
//					hayCambios = true;
//					vDisponibles = disponibles;
//				}
//			}
//
//			public boolean actualizar() {
//				if( !hayCambios )
//					return false;
//
//				hayCambios = false;
//				assert (vFijos <= vActuales && vActuales <= vDisponibles && vDisponibles <= getNValues());
//
//				for( int i = 0, len = getNValues(); i < len; i++ ){
//					if( i < vFijos )
//						setValueAt(i, 3);
//					else if( i < vActuales )
//						setValueAt(i, 2);
//					else if( i < vDisponibles )
//						setValueAt(i, 1);
//					else
//						setValueAt(i, 0);
//				}
//				return true;
//			}
//		}
//
//		private static class LedsIndicadores extends BarraImagenes {
//
//			private final LedsNiveles indicadorAsociado;
//
//			private int iluminados;
//			private boolean activo;
//
//			private boolean hayCambios = false;
//
//			public LedsIndicadores(double x, double y, double ancho, double alto, double bordeH, double bordeV,
//					Image imgs[], LedsNiveles indicadorAsociado) {
//				super(x, y, ancho, alto, bordeH, bordeV, 2, imgs);
//				this.indicadorAsociado = indicadorAsociado;
//			}
//
//			public void setIluminados(int iluminados) {
//				if( this.iluminados != iluminados ){
//					hayCambios = true;
//					this.iluminados = iluminados;
//				}
//			}
//
//			public void setActivo(boolean activo) {
//				if( this.activo != activo ){
//					hayCambios = true;
//					this.activo = activo;
//				}
//			}
//
//			private boolean seleccionable(int i) {
//				if( i == 0 )
//					return indicadorAsociado.vFijos < indicadorAsociado.vActuales;
//				return indicadorAsociado.vActuales < indicadorAsociado.vDisponibles;
//			}
//
//			public boolean actualizar() {
//				hayCambios = indicadorAsociado.actualizar() || hayCambios;
//				if( !hayCambios )
//					return false;
//				assert (iluminados <= 2);
//
//				hayCambios = false;
//				for( int i = 0; i < 2; i++ ){
//					if( i >= iluminados )
//						setValueAt(i, seleccionable(i) ? 1 : 0);
//					else if( activo && (i + 1) == iluminados )
//						setValueAt(i, seleccionable(i) ? 3 : 4);
//					else
//						setValueAt(i, 2);
//				}
//				return true;
//			}
//		}
//
//		private static class LedIndicadorGrado extends BarraImagenes {
//
//			private final LedsNiveles indicadorAsociado;
//			private boolean iluminado;
//			private boolean hayCambios = false;
//
//			public LedIndicadorGrado(double x, double y, double ancho, double alto, double bordeH, double bordeV,
//					Image imgs[], LedsNiveles indicadorAsociado) {
//				super(x, y, ancho, alto, bordeH, bordeV, 1, imgs);
//				this.indicadorAsociado = indicadorAsociado;
//			}
//
//			public void setIluminado(boolean iluminado) {
//				if( this.iluminado != iluminado ){
//					hayCambios = true;
//					this.iluminado = iluminado;
//				}
//			}
//
//			public boolean actualizar() {
//				hayCambios = indicadorAsociado.actualizar() || hayCambios;
//				if( !hayCambios )
//					return false;
//
//				hayCambios = false;
//				if( !iluminado )
//					setValueAt(0, (indicadorAsociado.vFijos < indicadorAsociado.vDisponibles) ? 1 : 0);
//				else
//					setValueAt(0, (indicadorAsociado.vFijos < indicadorAsociado.vDisponibles) ? 3 : 4);
//				return true;
//			}
//		}
//
//		private static class Superior extends JComponent {
//			private final Image img;
//			private Dimension dim;
//
//			public Superior(Image img) {
//				this.img = img;
//				this.dim = new Dimension();
//				this.setLayout(new FloatLayout());
//			}
//
//			public Dimension getMinimumSize() {
//				int ancho = getParent().getWidth();
//				int alto = (ancho * 3) >> 5;
//				dim.setSize(ancho, alto);
//				this.setSize(dim);
//				return dim;
//			}
//
//			public Dimension getPreferredSize() {
//				return getMinimumSize();
//			}
//
//			public void paintComponent(Graphics g) {
//				int ancho = (int) dim.getWidth();
//				int alto = (int) dim.getHeight();
//				g.drawImage(img, 0, 0, ancho, alto, null);
//			}
//		}
//
//		private static class Lateral extends JComponent {
//			private final Image img;
//			private final int imgW;
//			private Dimension dim;
//
//			public Lateral(Image img) {
//				this.img = img;
//				this.imgW = img.getWidth(null);
//				this.dim = new Dimension();
//			}
//
//			public Dimension getMinimumSize() {
//				int ancho = getParent().getWidth();
//				int alto = getParent().getHeight() - (ancho >> 3);
//				ancho = ancho >> 5;
//				dim.setSize(ancho, alto);
//				return dim;
//			}
//
//			public Dimension getPreferredSize() {
//				return getMinimumSize();
//			}
//
//			public void paint(Graphics g) {
//				int ancho = (int) dim.getWidth();
//				int alto = (int) dim.getHeight();
//				g.drawImage(img, 0, 0, ancho, alto, 0, 0, imgW, alto * imgW / ancho, null);
//			}
//		}
//
//		private static class Inferior extends JComponent {
//			private final Image img;
//			private Dimension dim;
//
//			public Inferior(Image img) {
//				this.img = img;
//				this.dim = new Dimension();
//			}
//
//			public Dimension getMinimumSize() {
//				int ancho = getParent().getWidth();
//				int alto = ancho >> 5;
//				dim.setSize(ancho, alto);
//				return dim;
//			}
//
//			public Dimension getPreferredSize() {
//				return getMinimumSize();
//			}
//
//			public void paint(Graphics g) {
//				int ancho = (int) dim.getWidth();
//				int alto = (int) dim.getHeight();
//				g.drawImage(img, 0, 0, ancho, alto, null);
//			}
//		}
//
//		private final transient JComponent panelSuperior;
//
//		private final transient Contador vidas;
//		private final transient Contador bombas;
//		private final transient Contador puntos;
//
//		private final transient LedsNiveles[] ledsNiveles;
//		private final transient LedsIndicadores[] ledsIndicadores;
//
//		private final transient LedsNiveles ledsGrado;
//		private final transient LedIndicadorGrado ledIndicadorGrado;
//
//		private final transient GLCanvas canvas;
//		private final transient Renderer renderer;
//
//		private final transient ClientData data;
//
//		private PantallaCliente1(DataGame dataGame, ClientData clientData) {
//
//			Image numeros = Imagen.cargarImagen("resources/img/numeros.png");
//
//			vidas = new Contador(17.0 / 256, 2.0 / 128, 3.0 / 128, 3.0 / 128, numeros);
//			vidas.setNDigitos(1);
//
//			bombas = new Contador(35.0 / 256, 2.0 / 128, 3.0 / 128, 3.0 / 128, numeros);
//			bombas.setNDigitos(1);
//
//			puntos = new Contador(7.0 / 256, 7.0 / 128, 17.0 / 128, 3.0 / 128, numeros);
//			puntos.setNDigitos(6);
//
//			panelSuperior = new Superior(Imagen.cargarImagen("resources/img/interface/if01.png"));
//
//			panelSuperior.add(vidas);
//			panelSuperior.add(bombas);
//			panelSuperior.add(puntos);
//
//			Image[] imgLeds = new Image[4];
//			// No seleccionable
//			imgLeds[0] = Imagen.cargarImagen("resources/img/leds/bt00.png");
//			// Seleccionable
//			imgLeds[1] = Imagen.cargarImagen("resources/img/leds/bt01.png");
//			// Activado
//			imgLeds[2] = Imagen.cargarImagen("resources/img/leds/bt02.png");
//			// Guardado
//			imgLeds[3] = Imagen.cargarImagen("resources/img/leds/bt03.png");
//
//			Image[] imgIndicadores = new Image[5];
//			// apagado no seleccionable
//			imgIndicadores[0] = Imagen.cargarImagen("resources/img/indicadores/mr00.png");
//			// apagado seleccionable
//			imgIndicadores[1] = Imagen.cargarImagen("resources/img/indicadores/mr01.png"); 
//			// iluminado
//			imgIndicadores[2] = Imagen.cargarImagen("resources/img/indicadores/mr02.png");
//			// actual seleccionable
//			imgIndicadores[3] = Imagen.cargarImagen("resources/img/indicadores/mr03.png");
//			// actual no seleccionable
//			imgIndicadores[4] = Imagen.cargarImagen("resources/img/indicadores/mr04.png");
//
//			ledsNiveles = new LedsNiveles[5];
//			ledsIndicadores = new LedsIndicadores[5];
//
//			ledsNiveles[0] = new LedsNiveles(24.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, 5, imgLeds);
//			ledsIndicadores[0] = new LedsIndicadores(24.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsNiveles[0]);
//
//			ledsNiveles[1] = new LedsNiveles(42.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, 5, imgLeds);
//			ledsIndicadores[1] = new LedsIndicadores(42.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsNiveles[1]);
//
//			ledsNiveles[2] = new LedsNiveles(60.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, 5, imgLeds);
//			ledsIndicadores[2] = new LedsIndicadores(60.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsNiveles[2]);
//
//			ledsNiveles[3] = new LedsNiveles(78.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, 5, imgLeds);
//			ledsIndicadores[3] = new LedsIndicadores(78.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsNiveles[3]);
//
//			ledsNiveles[4] = new LedsNiveles(96.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, 5, imgLeds);
//			ledsIndicadores[4] = new LedsIndicadores(96.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsNiveles[4]);
//
//			for( Component c: ledsNiveles )
//				panelSuperior.add(c);
//			for( Component c: ledsIndicadores )
//				panelSuperior.add(c);
//
//			ledsGrado = new LedsNiveles(114.0 / 128, 15.0 / 256, 10.0 / 128, 3.0 / 256, 1.0 / 13, 0.2, 3, imgLeds);
//			ledsGrado.setVDisponibles(3);
//
//			ledIndicadorGrado = new LedIndicadorGrado(114.0 / 128, 19.0 / 256, 10.0 / 128, 2.0 / 128, 1.0 / 21, 0.1,
//					imgIndicadores, ledsGrado);
//
//			panelSuperior.add(ledsGrado);
//			panelSuperior.add(ledIndicadorGrado);
//
//			setLayout(new BorderLayout());
//			add(panelSuperior, BorderLayout.NORTH);
//			add(new Lateral(Imagen.cargarImagen("resources/img/interface/if02.png")), BorderLayout.WEST);
//			add(new Lateral(Imagen.cargarImagen("resources/img/interface/if03.png")), BorderLayout.EAST);
//			add(new Inferior(Imagen.cargarImagen("resources/img/interface/if04.png")), BorderLayout.SOUTH);
//
//			renderer = new Renderer( dataGame.getFondo(), clientData.elementos, clientData.modificadores );
//			canvas = new GLCanvas( null, null, dataGame.getGLContext(), null );
//			canvas.setBackground(Color.BLACK);
//			canvas.setIgnoreRepaint(true);
////			canvas.removeAllGLEventListeners();
//			canvas.addGLEventListener(renderer);
//			canvas.addKeyListener(new GameKeyListener(clientData));			
//			add(canvas, BorderLayout.CENTER);
//
//			data = clientData;
//		}
//
//		private transient boolean hayCambios = false;
//
//		private void setNumVidas(int valor) {
//			if( this.vidas.getValor() != valor ){
//				this.hayCambios = true;
//				this.vidas.setValor(valor);
//			}
//		}
//
//		private void setNumBombas(int valor) {
//			if( this.bombas.getValor() != valor ){
//				this.hayCambios = true;
//				this.bombas.setValor(valor);
//			}
//		}
//
//		private void setPuntos(int valor) {
//			if( this.puntos.getValor() != valor ){
//				this.hayCambios = true;
//				this.puntos.setValor(valor);
//			}
//		}
//
//		private void setNivelesFijos(int[] valores) {
//			assert (ledsNiveles.length == valores.length);
//			for( int i = 0, len = ledsNiveles.length; i < len; i++ )
//				ledsNiveles[i].setVFijos(valores[i]);
//		}
//
//		private void setNivelesActuales(int[] valores) {
//			assert (ledsNiveles.length == valores.length);
//			for( int i = 0, len = ledsNiveles.length; i < len; i++ )
//				ledsNiveles[i].setVActuales(valores[i]);
//		}
//
//		private void setNivelesDisponibles(int[] valores) {
//			assert (ledsNiveles.length == valores.length);
//			for( int i = 0, len = ledsNiveles.length; i < len; i++ )
//				ledsNiveles[i].setVDisponibles(valores[i]);
//		}
//
//		private void setIndicador(int valor) {
//			for( int i = 0; i < ledsIndicadores.length; i++ ){
//				if( valor > 0 ){
//					valor -= 2;
//					if( valor < 0 ){
//						ledsIndicadores[i].setIluminados(1);
//						ledsIndicadores[i].setActivo(true);
//					}else if( valor == 0 ){
//						ledsIndicadores[i].setIluminados(2);
//						ledsIndicadores[i].setActivo(true);
//					}else{
//						ledsIndicadores[i].setIluminados(2);
//						ledsIndicadores[i].setActivo(false);
//					}
//				}else{
//					ledsIndicadores[i].setIluminados(0);
//					ledsIndicadores[i].setActivo(false);
//				}
//			}
//			ledIndicadorGrado.setIluminado(valor > 0);
//		}
//
//		private void setGrado(int valor) {
//			ledsGrado.setVActuales(valor);
//			ledsGrado.setVFijos(valor);
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see org.sam.gui.Marco#update()
//		 */
//		@Override
//		void update() {
////			System.out.println("Actualizando pantalla...");
//			canvas.display();
////			System.out.println("display completo");
//
//			setNumVidas(data.nVidas);
//			setNumBombas(data.nBombas);
//			setPuntos(data.nVidas);
//
//			setNivelesFijos(data.nivelesFijos);
//			setNivelesActuales(data.nivelesActuales);
//			setNivelesDisponibles(data.nivelesDisponibles);
//
//			setIndicador(data.indicador);
//			setGrado(data.grado);
//
//			for( LedsIndicadores l: ledsIndicadores )
//				hayCambios = l.actualizar() || hayCambios;
//			hayCambios = ledIndicadorGrado.actualizar() || hayCambios;
//
//			if( !hayCambios )
//				return;
//			hayCambios = false;
//			panelSuperior.repaint();
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see java.awt.Component#requestFocus()
//		 */
//		@Override
//		public void requestFocus() {
//			canvas.requestFocus();
//		}
//	}


	private PantallaCliente() {
	}

	abstract void update();

	public static PantallaCliente getNewPantalla(int type, DataGame dataGame, ClientData clientData) {
//		return new PantallaCliente1( dataGame, clientData );
		return null;
	}
}
