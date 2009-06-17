package org.sam.gui;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.sam.util.Imagen;

@SuppressWarnings("serial")
public abstract class Marco extends JPanel{

	private static class Marco1 extends Marco{

		private static class Superior extends JComponent{
			private final Image img;
			private Dimension dim;

			public Superior(Image img){
				this.img  = img;
				dim = new Dimension();
				setLayout(new FloatLayout());
			}

			public Dimension getMinimumSize(){
				int ancho = getParent().getWidth();
				int alto  = (ancho * 3)>>5;
				dim.setSize(ancho,alto);
				this.setSize(dim);
				return dim;
			}

			public Dimension getPreferredSize(){
				return getMinimumSize();
			}

			public void paintComponent(Graphics g){
				int ancho = (int)dim.getWidth();
				int alto  = (int)dim.getHeight();
				g.drawImage(img,0,0,ancho,alto,null);
			}
		}

		private static class Lateral extends JComponent{
			private final Image img;
			private final int imgW; 
			private Dimension dim;

			public Lateral(Image img){
				this.img  = img;
				this.imgW = img.getWidth(null);
				dim = new Dimension();
			}

			public Dimension getMinimumSize(){
				int ancho = getParent().getWidth();
				int alto  = getParent().getHeight() - (ancho >> 3);
				ancho = ancho >> 5;
				dim.setSize(ancho,alto);
				return dim;
			}

			public Dimension getPreferredSize(){
				return getMinimumSize();
			}

			public void paint(Graphics g){
				int ancho = (int)dim.getWidth();
				int alto  = (int)dim.getHeight();
				g.drawImage(img,0,0,ancho,alto,0,0,imgW,alto*imgW/ancho,null);
			}
		}

		private static class Inferior extends JComponent{
			private final Image img;
			private Dimension dim;

			public Inferior(Image img){
				this.img  = img;
				dim = new Dimension();
			}

			public Dimension getMinimumSize(){
				int ancho = getParent().getWidth();
				int alto  = ancho >>5;
				dim.setSize(ancho,alto);
				return dim;
			}

			public Dimension getPreferredSize(){
				return getMinimumSize();
			}

			public void paint(Graphics g){
				int ancho = (int)dim.getWidth();
				int alto  = (int)dim.getHeight();
				g.drawImage(img,0,0,ancho,alto,null);
			}
		}

		private final transient JComponent panelSuperior;
		
		private final transient Contador vidas;
		private final transient Contador bombas;
		private final transient Contador puntos;

		private transient int velocidadFija,			velocidadActual,  		velocidadDisponible;
		private transient int numeroDisparosFijos,	numeroDisparosActuales, numeroDisparosDisponibles;
		private transient int potenciaDisparosFija, 	potenciaDisparosActual, potenciaDisparosDisponible;
		private transient int numeroMisilesFijos,		numeroMisilesActuales,	numeroMisilesDisponibles;
		private transient int potenciaMisilesFija,	potenciaMisilesActual,	potenciaMisilesDisponible;
		private transient int nivel;
		private transient int indicador;

		private final transient int[] lvelocidad;
		private final transient int[] lnDisparos;
		private final transient int[] lpDisparos;
		private final transient int[] lnMisiles;
		private final transient int[] lpMisiles;
		private final transient int[] lupgrade;

		private final transient int[] ivelocidad;
		private final transient int[] inDisparos;
		private final transient int[] ipDisparos;
		private final transient int[] inMisiles;
		private final transient int[] ipMisiles;
		private final transient int[] iupgrade;

		private final transient int indicadoresLen;
		private final transient int[] indicadores[];

		private Marco1(LayoutManager layout){
			super(layout);

			Image numeros = Imagen.cargarImagen("resources/img/numeros.png");

			vidas = new Contador(17.0/256,2.0/128,3.0/128,3.0/128,numeros);
			vidas.setNDigitos(1);

			bombas = new Contador(35.0/256,2.0/128,3.0/128,3.0/128,numeros);
			bombas.setNDigitos(1);
			
			puntos = new Contador(7.0/256,7.0/128,17.0/128,3.0/128,numeros);
			puntos.setNDigitos(6);

			lvelocidad = new int[5];
			lnDisparos = new int[5];
			lpDisparos = new int[5];
			lnMisiles = new int[5];
			lpMisiles = new int[5];
			lupgrade = new int[3];

			ivelocidad = new int[2];
			inDisparos = new int[2];
			ipDisparos = new int[2];
			inMisiles = new int[2];
			ipMisiles = new int[2];
			iupgrade = new int[1];

			indicadoresLen = 
				ivelocidad.length +
				inDisparos.length +
				ipDisparos.length +
				inMisiles.length +
				ipMisiles.length +
				iupgrade.length;

			indicadores = new int[][]{
					ivelocidad,
					inDisparos,
					ipDisparos,
					inMisiles,
					ipMisiles,
					iupgrade
			};
			
			indicador = -1; // Valor inicial para que se actualice con 0

			panelSuperior = new Superior(Imagen.cargarImagen("resources/img/interface/if01.png"));

			panelSuperior.add(vidas);
			panelSuperior.add(bombas);
			panelSuperior.add(puntos);

			Image[] imgLeds = new Image[4];
			imgLeds[0]= Imagen.cargarImagen("resources/img/leds/bt00.png");
			imgLeds[1]= Imagen.cargarImagen("resources/img/leds/bt01.png");
			imgLeds[2]= Imagen.cargarImagen("resources/img/leds/bt02.png");
			imgLeds[3]= Imagen.cargarImagen("resources/img/leds/bt03.png");

			Image[] imgIndicadores = new Image[6];
			imgIndicadores[0]= Imagen.cargarImagen("resources/img/indicadores/mr00.png"); // inactivo no seleccionable
			imgIndicadores[1]= Imagen.cargarImagen("resources/img/indicadores/mr01.png"); // inactivo seleccionable
			imgIndicadores[2]= Imagen.cargarImagen("resources/img/indicadores/mr02.png"); // activo seleccionable
			imgIndicadores[3]= Imagen.cargarImagen("resources/img/indicadores/mr03.png"); // activo no seleccionable

			panelSuperior.add(new BarraLeds(24.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, lvelocidad, imgLeds));
			panelSuperior.add(new BarraLeds(24.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, ivelocidad, imgIndicadores));

			panelSuperior.add(new BarraLeds(42.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, lnDisparos, imgLeds));
			panelSuperior.add(new BarraLeds(42.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, inDisparos, imgIndicadores));

			panelSuperior.add(new BarraLeds(60.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, lpDisparos, imgLeds));
			panelSuperior.add(new BarraLeds(60.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, ipDisparos, imgIndicadores));

			panelSuperior.add(new BarraLeds(78.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, lnMisiles, imgLeds));
			panelSuperior.add(new BarraLeds(78.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, inMisiles, imgIndicadores));

			panelSuperior.add(new BarraLeds(96.0 / 128, 15.0 / 256, 14.0 / 128, 3.0 / 256, 1.0 / 21, 0.2, lpMisiles, imgLeds));
			panelSuperior.add(new BarraLeds(96.0 / 128, 19.0 / 256, 14.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, ipMisiles, imgIndicadores));

			panelSuperior.add(new BarraLeds(114.0 / 128, 15.0 / 256, 10.0 / 128, 3.0 / 256, 1.0 / 13, 0.2, lupgrade, imgLeds));
			panelSuperior.add(new BarraLeds(114.0 / 128, 19.0 / 256, 10.0 / 128, 2.0 / 128, 1.0 / 21, 0.1, iupgrade, imgIndicadores));

			add(panelSuperior,BorderLayout.NORTH);
			add(new Lateral(Imagen.cargarImagen("resources/img/interface/if02.png")),BorderLayout.WEST);
			add(new Lateral(Imagen.cargarImagen("resources/img/interface/if03.png")),BorderLayout.EAST);
			add(new Inferior(Imagen.cargarImagen("resources/img/interface/if04.png")),BorderLayout.SOUTH);
		}

		private transient boolean hayCambios = false;
		
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setVidas(int)
		 */
		@Override
		public void setVidas(int valor) {
			if(this.vidas.getValor() != valor){
				this.hayCambios = true;
				this.vidas.setValor(valor);
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setBombas(int)
		 */
		@Override
		public void setBombas(int valor) {
			if(this.bombas.getValor() != valor){
				this.hayCambios = true;
				this.bombas.setValor(valor);
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPuntos(int)
		 */
		@Override
		public void setPuntos(int valor) {
			if(this.puntos.getValor() != valor){
				this.hayCambios = true;
				this.puntos.setValor(valor);
			}
		}
		
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setVelocidadFija(int)
		 */
		@Override
		public void setVelocidadFija(int valor){
			if(this.velocidadFija != valor){
				this.hayCambios = true;
				this.velocidadFija = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setVelocidadActual(int)
		 */
		@Override
		public void setVelocidadActual(int valor){
			if(this.velocidadActual != valor){
				this.hayCambios = true;
				this.velocidadActual = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setVelocidadDisponible(int)
		 */
		@Override
		public void setVelocidadDisponible(int valor){
			if(this.velocidadDisponible != valor){
				this.hayCambios = true;
				this.velocidadDisponible = valor;
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroDisparosFijos(int)
		 */
		@Override
		public void setNumeroDisparosFijos(int valor){
			if(this.numeroDisparosFijos != valor){
				this.hayCambios = true;
				this.numeroDisparosFijos = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroDisparosActuales(int)
		 */
		@Override
		public void setNumeroDisparosActuales(int valor){
			if(this.numeroDisparosActuales != valor){
				this.hayCambios = true;
				this.numeroDisparosActuales = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroDisparosDisponibles(int)
		 */
		@Override
		public void setNumeroDisparosDisponibles(int valor){
			if(this.numeroDisparosDisponibles != valor){
				this.hayCambios = true;
				this.numeroDisparosDisponibles = valor;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaDisparosFija(int)
		 */
		@Override
		public void setPotenciaDisparosFija(int valor){
			if(this.potenciaDisparosFija != valor){
				this.hayCambios = true;
				this.potenciaDisparosFija = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaDisparosActual(int)
		 */
		@Override
		public void setPotenciaDisparosActual(int valor){
			if(this.potenciaDisparosActual != valor){
				this.hayCambios = true;
				this.potenciaDisparosActual = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaDisparosDisponible(int)
		 */
		@Override
		public void setPotenciaDisparosDisponible(int valor){
			if(this.potenciaDisparosDisponible != valor){
				this.hayCambios = true;
				this.potenciaDisparosDisponible = valor;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroMisilesFijos(int)
		 */
		@Override
		public void setNumeroMisilesFijos(int valor){
			if(this.numeroMisilesFijos != valor){
				this.hayCambios = true;
				this.numeroMisilesFijos = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroMisilesActuales(int)
		 */
		@Override
		public void setNumeroMisilesActuales(int valor){
			if(this.numeroMisilesActuales != valor){
				this.hayCambios = true;
				this.numeroMisilesActuales = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNumeroMisilesDisponibles(int)
		 */
		@Override
		public void setNumeroMisilesDisponibles(int valor){
			if(this.numeroMisilesDisponibles != valor){
				this.hayCambios = true;
				this.numeroMisilesDisponibles = valor;
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaMisilesFija(int)
		 */
		@Override
		public void setPotenciaMisilesFija(int valor){
			if(this.potenciaMisilesFija != valor){
				this.hayCambios = true;
				this.potenciaMisilesFija = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaMisilesActual(int)
		 */
		@Override
		public void setPotenciaMisilesActual(int valor){
			if(this.potenciaMisilesActual != valor){
				this.hayCambios = true;
				this.potenciaMisilesActual = valor;
			}
		}
		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setPotenciaMisilesDisponible(int)
		 */
		@Override
		public void setPotenciaMisilesDisponible(int valor){
			if(this.potenciaMisilesDisponible != valor){
				this.hayCambios = true;
				this.potenciaMisilesDisponible = valor;
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setNivel(int)
		 */
		@Override
		public void setNivel(int valor){
			if(this.nivel != valor){
				this.hayCambios = true;
				this.nivel = valor;
			}
		}

		private boolean seleccionable(int v){
			switch(v){
			case 0: // aumentar velocidad
				return velocidadActual < velocidadDisponible;
			case 1: // grabar velocidad
				return velocidadFija < velocidadActual;
			case 2: // aumentar disparos
				return numeroDisparosActuales < numeroDisparosDisponibles;
			case 3: // grabar disparos
				return numeroDisparosFijos < numeroDisparosActuales;
			case 4: // aumentar potencia disparos
				return potenciaDisparosActual < potenciaDisparosDisponible;
			case 5: // grabar potencia disparos
				return potenciaDisparosFija < potenciaDisparosActual;
			case 6: // aumentar misiles
				return numeroMisilesActuales < numeroMisilesDisponibles;
			case 7: // grabar misiles
				return numeroMisilesFijos < numeroMisilesActuales;
			case 8: // aumentar potencia misiles
				return potenciaMisilesActual < potenciaMisilesDisponible;
			case 9: // grabar potencia misiles
				return potenciaMisilesFija < potenciaMisilesActual;
			case 10: // aumentar nivel
				return nivel < lupgrade.length;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#setIndicador(int)
		 */
		@Override
		public void setIndicador(int valor){
			if(this.indicador != valor){
				this.hayCambios = true;
				this.indicador = valor;
				
				valor --;
				for(int i=0, j=0, k=0; i < indicadoresLen; i++){
					if( i < valor){
						indicadores[j][k] = 2;
					}else if( i== valor){
						indicadores[j][k] = seleccionable(i)?2:3;
					}else{
						indicadores[j][k] = seleccionable(i)?1:0;
					}
					if(++k == indicadores[j].length){
						k = 0;
						j++;
					}
				}
			}
		}
		
		private static void updateLeds(int fijos, int actuales, int disponibles, int[] leds){
			assert(fijos <= actuales && actuales <= disponibles && disponibles <= leds.length);
			for(int i= 0, len = leds.length; i< len; i++){
				if(i < fijos)
					leds[i] = 3;
				else if(i < actuales)
					leds[i] = 2;
				else if(i < disponibles)
					leds[i] = 1;
				else
					leds[i] = 0;
			}
		}

		/* (non-Javadoc)
		 * @see org.sam.gui.Marco#actualizar()
		 */
		@Override
		public void actualizar(){
			if(hayCambios){
				updateLeds(velocidadFija,        velocidadActual,        velocidadDisponible,        lvelocidad);
				updateLeds(numeroDisparosFijos,  numeroDisparosActuales, numeroDisparosDisponibles,  lnDisparos);
				updateLeds(potenciaDisparosFija, potenciaDisparosActual, potenciaDisparosDisponible, lpDisparos);
				updateLeds(numeroMisilesFijos,   numeroMisilesActuales,  numeroMisilesDisponibles,   lnMisiles);
				updateLeds(potenciaMisilesFija,  potenciaMisilesActual,  potenciaMisilesDisponible,  lpMisiles);
				updateLeds(nivel,                nivel,                  lupgrade.length,            lupgrade);
				panelSuperior.repaint();
			}
		}
	}

	private Marco(LayoutManager layout){
		super(layout);
	}

	public abstract void setVidas(int valor);
	public abstract void setBombas(int valor);
	public abstract void setPuntos(int valor);
	
	public abstract void setVelocidadFija(int valor);
	public abstract void setVelocidadActual(int valor);
	public abstract void setVelocidadDisponible(int valor);
	
	public abstract void setNumeroDisparosFijos(int valor);
	public abstract void setNumeroDisparosActuales(int valor);
	public abstract void setNumeroDisparosDisponibles(int valor);
	
	public abstract void setPotenciaDisparosFija(int valor);
	public abstract void setPotenciaDisparosActual(int valor);
	public abstract void setPotenciaDisparosDisponible(int valor);
	
	public abstract void setNumeroMisilesFijos(int valor);
	public abstract void setNumeroMisilesActuales(int valor);
	public abstract void setNumeroMisilesDisponibles(int valor);
	
	public abstract void setPotenciaMisilesFija(int valor);
	public abstract void setPotenciaMisilesActual(int valor);
	public abstract void setPotenciaMisilesDisponible(int valor);
	
	public abstract void setNivel(int valor);
	
	public abstract void setIndicador(int valor);
	
	public abstract void actualizar();
	
	public static Marco getNewMarco(int type){
		return new Marco1(new BorderLayout());
	}
}
