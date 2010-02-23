package org.sam.jspacewars.cliente;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import org.sam.interpoladores.Funcion;
import org.sam.interpoladores.GeneradorDeFunciones;
import org.sam.jogl.Apariencia;
import org.sam.jogl.AtributosTextura;
import org.sam.jogl.AtributosTransparencia;
import org.sam.jogl.Textura;
import org.sam.jogl.gui.BarraImagenes;
import org.sam.jogl.gui.Contador;
import org.sam.jogl.gui.GLComponent;
import org.sam.jogl.gui.Pixmap;
import org.sam.jogl.gui.PixmapComponent;
import org.sam.util.Imagen;

public abstract class MarcoDeIndicadores extends GLComponent{
	
	static class LedsNiveles extends BarraImagenes {

		private int vFijos, vActuales, vDisponibles;
		private boolean hayCambios = false;

		public LedsNiveles(	int nValores, Pixmap[] pixmaps ) {
			super( nValores, pixmaps );
		}

		public void setVFijos(int fijos) {
			if( vFijos != fijos ){
				hayCambios = true;
				vFijos = fijos;
			}
		}

		public void setVActuales(int actuales) {
			if( vActuales != actuales ){
				hayCambios = true;
				vActuales = actuales;
			}
		}

		public void setVDisponibles(int disponibles) {
			if( vDisponibles != disponibles ){
				hayCambios = true;
				vDisponibles = disponibles;
			}
		}

		public boolean actualizar() {
			if( !hayCambios )
				return false;

			hayCambios = false;
			assert (vFijos <= vActuales && vActuales <= vDisponibles && vDisponibles <= getNValues());

			for( int i = 0, len = getNValues(); i < len; i++ ){
				if( i < vFijos )
					setValueAt(i, 3);
				else if( i < vActuales )
					setValueAt(i, 2);
				else if( i < vDisponibles )
					setValueAt(i, 1);
				else
					setValueAt(i, 0);
			}
			return true;
		}
	}

	static class LedsIndicadores extends BarraImagenes {

		private final LedsNiveles indicadorAsociado;

		private int iluminados;
		private boolean activo;

		private boolean hayCambios = false;

		public LedsIndicadores(	int nValores, Pixmap[] pixmaps, LedsNiveles indicadorAsociado) {
			super( nValores, pixmaps );
			this.indicadorAsociado = indicadorAsociado;
		}

		public void setIluminados(int iluminados) {
			if( this.iluminados != iluminados ){
				hayCambios = true;
				this.iluminados = iluminados;
			}
		}

		public void setActivo(boolean activo) {
			if( this.activo != activo ){
				hayCambios = true;
				this.activo = activo;
			}
		}

		private boolean seleccionable(int i) {
			if( i == 0 )
				return indicadorAsociado.vFijos < indicadorAsociado.vActuales;
			return indicadorAsociado.vActuales < indicadorAsociado.vDisponibles;
		}

		public boolean actualizar() {
			hayCambios = indicadorAsociado.actualizar() || hayCambios;
			if( !hayCambios )
				return false;
			assert (iluminados <= 2);

			hayCambios = false;
			for( int i = 0; i < 2; i++ ){
				if( i >= iluminados )
					setValueAt(i, seleccionable(i) ? 1 : 0);
				else if( activo && (i + 1) == iluminados )
					setValueAt(i, seleccionable(i) ? 3 : 4);
				else
					setValueAt(i, 2);
			}
			return true;
		}
	}

	static class LedIndicadorGrado extends BarraImagenes {

		private final LedsNiveles indicadorAsociado;
		private boolean iluminado;
		private boolean hayCambios = false;

		public LedIndicadorGrado( Pixmap[] pixmaps, LedsNiveles indicadorAsociado ) {
			super( 1, pixmaps );
			this.indicadorAsociado = indicadorAsociado;
		}

		public void setIluminado(boolean iluminado) {
			if( this.iluminado != iluminado ){
				hayCambios = true;
				this.iluminado = iluminado;
			}
		}

		public boolean actualizar() {
			hayCambios = indicadorAsociado.actualizar() || hayCambios;
			if( !hayCambios )
				return false;

			hayCambios = false;
			if( !iluminado )
				setValueAt(0, (indicadorAsociado.vFijos < indicadorAsociado.vDisponibles) ? 1 : 0);
			else
				setValueAt(0, (indicadorAsociado.vFijos < indicadorAsociado.vDisponibles) ? 3 : 4);
			return true;
		}
	}
	
	private static class MarcoNave1 extends MarcoDeIndicadores{
		
		private static final String[] PATHS = new String[]{
			"resources/img/interface/InterfaceH.png",
			"resources/img/interface/InterfaceV1.png",
			"resources/img/interface/InterfaceV2.png",
			"resources/img/interface/numeros.png",
			"resources/img/interface/leds.png"
		};
		
		private static final Textura.Format[] FORMATS = new Textura.Format[]{
			Textura.Format.RGBA,
			Textura.Format.RGBA,
			Textura.Format.RGBA,
			Textura.Format.RGBA,
			Textura.Format.RGBA
		};
		
		private final Apariencia[] apariencias;
		private final PixmapComponent superior, inferior, izqSup, derSup, izqInf, derInf;
		
		private static final Pixmap[] numeros = new Pixmap[10];
		static{
			float j= 2;
			for(int i=0; i < 10; i++){
				numeros[i] = new Pixmap( j/512.0f, 6/64.0f, 50f/512.0f, 52/64.0f );
				j+= 51f;
			}
		}
		
		private static final Pixmap[] leds1 = new Pixmap[4];
		static{
			leds1[0] = new Pixmap( 0.0f, 17.0f/128, 0.4921875f, 16.0f/128 );
			leds1[1] = new Pixmap( 0.5f, 17.0f/128, 0.4921875f, 16.0f/128 );
			leds1[2] = new Pixmap( 0.0f,      0.0f, 0.4921875f, 16.0f/128 );
			leds1[3] = new Pixmap( 0.5f,      0.0f, 0.4921875f, 16.0f/128 );
		}
		
		private static final Pixmap[] leds2 = new Pixmap[5];
		static{
			leds2[0] = new Pixmap( 0.0f, 110.0f/128, 1.0f, 18.0f/128 );
			leds2[1] = new Pixmap( 0.0f,  91.0f/128, 1.0f, 18.0f/128 );
			leds2[2] = new Pixmap( 0.0f,  72.0f/128, 1.0f, 18.0f/128 );
			leds2[3] = new Pixmap( 0.0f,  53.0f/128, 1.0f, 18.0f/128 );
			leds2[4] = new Pixmap( 0.0f,  34.0f/128, 1.0f, 18.0f/128 );
		}
		
		public MarcoNave1(){
//			super(new Contador( 1, numeros), new Contador( 1, numeros), new Contador( 6, numeros), new LedsNiveles[5], new LedsIndicadores[5], new LedsNiveles( 3, leds1),  new LedIndicadorGrado( leds2, ledsGrado ));
			
			apariencias = new Apariencia[PATHS.length];
			
			superior = new PixmapComponent( new Pixmap( 0.0f, 0.25f, 1.0f, 0.75f) );
			inferior = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  1.0f, 0.25f) );
			izqSup   = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  0.5f, 1.0f ) );
			derSup   = new PixmapComponent( new Pixmap( 0.5f, 0.0f,  0.5f, 1.0f ) );
			izqInf   = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  0.5f, 1.0f ) );
			derInf   = new PixmapComponent( new Pixmap( 0.5f, 0.0f,  0.5f, 1.0f ) );

			vidas =  new Contador( 1, numeros);
			bombas = new Contador( 1, numeros);
			puntos = new Contador( 6, numeros);
			
			ledsNiveles = new LedsNiveles[5];
			for(int i = 0; i < ledsNiveles.length; i++){
				ledsNiveles[i] = new LedsNiveles( 5, leds1);
				ledsNiveles[i].setBorders( 0.025f, 0.1f);
			}
			ledsGrado = new LedsNiveles( 3, leds1);
			ledsGrado.setBorders( 0.05f, 0.1f);
			ledsGrado.setVDisponibles(3);
			
			ledsIndicadores = new LedsIndicadores[5];
			for(int i = 0; i < ledsIndicadores.length; i++){
				ledsIndicadores[i] = new LedsIndicadores( 2, leds2, ledsNiveles[i] );
				ledsIndicadores[i].setBorders( 0.04f, 0.05f);
			}
			ledIndicadorGrado = new LedIndicadorGrado( leds2, ledsGrado );
			ledIndicadorGrado.setBorders( 0.075f, 0.05f);
			
		}
		
		private transient int cont = 0;
		
		public boolean isLoadComplete(){
			return cont == apariencias.length;
		}
		
		public void loadTexturas(GL gl){
			
			if(cont >= apariencias.length)
				return;
			
			BufferedImage img = Imagen.cargarToBufferedImage(PATHS[cont]);
			apariencias[cont] = new Apariencia();
			apariencias[cont].setTextura(new Textura(gl, FORMATS[cont], img , true));
			apariencias[cont].setAtributosTextura(new AtributosTextura());
			apariencias[cont].getAtributosTextura().setMode(AtributosTextura.Mode.REPLACE);
			
			if(FORMATS[cont] == Textura.Format.RGBA)
				apariencias[cont].setAtributosTransparencia(FUSION_ALPHA);
			
			cont ++;
		}
		
		/**
		 * Funcion de mosaico/recorte de la textura del los laterales.
		 */
		private static final transient Funcion.Float f = 
			GeneradorDeFunciones.Predefinido.LINEAL.generaFuncion(6.0/32, 1, 14.0/32, 0).toFloatFunction();
		
		private transient float proporcionesPantalla;
		
		public void setBounds(float x, float y, float w, float h){
			super.setBounds(x,y,w,h);
			proporcionesPantalla = w/h;
			// guias horizontales
			float gh0, gh1, gh2, gh3, gh4;
			gh0 = 0.0f;
			gh1 = proporcionesPantalla/32;
			gh2 = 1.0f - 5 * proporcionesPantalla/32;
			gh3 = 1.0f - 3 * proporcionesPantalla/32;
			gh4 = 1.0f;
			// guias verticales
			float gv0, gv1, gv2, gv3;
			gv0 = 0.0f;
			gv1 = gh1;
			gv2 = 31 * proporcionesPantalla/32;
			gv3 = proporcionesPantalla;
			
			inferior.setCorners( gv0, gh0, gv3, gh1 );
			superior.setCorners( gv0, gh3, gv3, gh4);
			
			izqInf.setCorners( gv0, gh1, gv1, gh2 );
			derInf.setCorners( gv2, gh1, gv3, gh2 );
			
			izqInf.pixmap.v1 = derInf.pixmap.v1 = f.f(1.0f/proporcionesPantalla);
			
			izqSup.setCorners( gv0, gh2, gv1, gh3 );
			derSup.setCorners( gv2, gh2, gv3, gh3 );
			
			float x1, y1, y2, w1, h1, h2;
			
			y1 = 1.0f - 19 * proporcionesPantalla/512;
			y2 = 1.0f - 39 * proporcionesPantalla/512;
			w1 = h1 = 3 * proporcionesPantalla / 128;
			vidas .setBounds( 17 * proporcionesPantalla / 256, y1, w1, h1);
			bombas.setBounds( 35 * proporcionesPantalla / 256, y1, w1, h1);
			w1 = 17 * proporcionesPantalla / 128;
			puntos.setBounds(  7 * proporcionesPantalla / 256, y2, w1, h1);
			
			y1 = 1.0f - 71 * proporcionesPantalla/1024;
			y2 = 1.0f - 23 * proporcionesPantalla/256;
			w1 = 14 * proporcionesPantalla / 128;
			h1 =  3 * proporcionesPantalla / 256;
			h2 =  proporcionesPantalla / 64;
			
			for(int i = 0, j= 24; i < 5; i++, j+= 18){
				x1 = j * proporcionesPantalla / 128;
				ledsNiveles    [i].setBounds( x1, y1, w1, h1 );
				ledsIndicadores[i].setBounds( x1, y2, w1, h2 );
			}
			x1 = 114 * proporcionesPantalla / 128;
			w1 =  10 * proporcionesPantalla / 128;
			ledsGrado         .setBounds( x1, y1, w1, h1 );
			ledIndicadorGrado .setBounds( x1, y2, w1, h2 );
		}
		
		public void draw(GL gl){
			gl.glViewport( 0, 0, (int)( x2 - x1), (int)( y2 - y1 ) );
			
			gl.glMatrixMode( GL.GL_MODELVIEW );
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, proporcionesPantalla, 0.0, 1.0, 0, 1);
			
			apariencias[0].usar(gl);
			inferior.draw(gl);
			superior.draw(gl);
			
			apariencias[1].usar(gl);
			izqInf.draw(gl);
			derInf.draw(gl);
			
			apariencias[2].usar(gl);
			izqSup.draw(gl);
			derSup.draw(gl);
			
			apariencias[3].usar(gl);
			
			vidas.draw(gl);
			bombas.draw(gl);
			puntos.draw(gl);
			
			apariencias[4].usar(gl);
			
			for(GLComponent component: ledsNiveles)
				component.draw(gl);
			ledsGrado.draw(gl);
			
			for(GLComponent component: ledsIndicadores)
				component.draw(gl);
			ledIndicadorGrado.draw(gl);
			
			gl.glPopMatrix();
		}
	}
	
	private static final AtributosTransparencia FUSION_ALPHA = new AtributosTransparencia(
		AtributosTransparencia.Equation.ADD,
		AtributosTransparencia.SrcFunc.SRC_ALPHA,
		AtributosTransparencia.DstFunc.ONE_MINUS_SRC_ALPHA
	);
	
//	final transient Contador vidas;
//	final transient Contador bombas;
//	final transient Contador puntos;
//
//	final transient LedsNiveles[] ledsNiveles;
//	final transient LedsIndicadores[] ledsIndicadores;
//
//	final transient LedsNiveles ledsGrado;
//	final transient LedIndicadorGrado ledIndicadorGrado;
	
	transient Contador vidas;
	transient Contador bombas;
	transient Contador puntos;

	transient LedsNiveles[] ledsNiveles;
	transient LedsIndicadores[] ledsIndicadores;

	transient LedsNiveles ledsGrado;
	transient LedIndicadorGrado ledIndicadorGrado;
	
	/*
	public MarcoDeIndicadores(Contador vidas, Contador bombas, Contador puntos, LedsNiveles[] ledsNiveles, LedsIndicadores[] ledsIndicadores, LedsNiveles ledsGrado, LedIndicadorGrado ledIndicadorGrado){
		this.vidas = vidas;
		this.bombas = bombas;
		this.puntos = puntos;
		this.ledsNiveles = ledsNiveles;
		this.ledsIndicadores = ledsIndicadores;
		this.ledsGrado = ledsGrado;
		this.ledIndicadorGrado = ledIndicadorGrado;
//		apariencias = new Apariencia[PATHS.length];
//		superior = new PixmapComponent( new Pixmap( 0.0f, 0.25f, 1.0f, 0.75f) );
//		inferior = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  1.0f, 0.25f) );
//		izqSup   = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  0.5f, 1.0f ) );
//		derSup   = new PixmapComponent( new Pixmap( 0.5f, 0.0f,  0.5f, 1.0f ) );
//		izqInf   = new PixmapComponent( new Pixmap( 0.0f, 0.0f,  0.5f, 1.0f ) );
//		derInf   = new PixmapComponent( new Pixmap( 0.5f, 0.0f,  0.5f, 1.0f ) );
		
//		Pixmap[] numeros = new Pixmap[10];
//		float j= 2;
//		for(int i=0; i < 10; i++){
//			numeros[i] = new Pixmap( j/512.0f, 6/64.0f, 50f/512.0f, 52/64.0f );
//			j+= 51f;
//		}
//		vidas = new Contador( 1, numeros);
//		bombas= new Contador( 1, numeros);
//		puntos= new Contador( 6, numeros);
//		
//		Pixmap[] leds1 = new Pixmap[4];
//		leds1[0] = new Pixmap( 0.0f, 17.0f/128, 0.4921875f, 16.0f/128 );
//		leds1[1] = new Pixmap( 0.5f, 17.0f/128, 0.4921875f, 16.0f/128 );
//		leds1[2] = new Pixmap( 0.0f,      0.0f, 0.4921875f, 16.0f/128 );
//		leds1[3] = new Pixmap( 0.5f,      0.0f, 0.4921875f, 16.0f/128 );
//		
//		ledsNiveles = new LedsNiveles[5];
//		for(int i = 0; i < ledsNiveles.length; i++){
//			ledsNiveles[i] = new LedsNiveles( 5, leds1);
//			ledsNiveles[i].setBorders( 0.025f, 0.1f);
//		}
//		ledsGrado = new LedsNiveles( 3, leds1);
//		ledsGrado.setBorders( 0.05f, 0.1f);
//		ledsGrado.setVDisponibles(3);
//		
//		Pixmap[] leds2 = new Pixmap[5];
//		leds2[0] = new Pixmap( 0.0f, 110.0f/128, 1.0f, 18.0f/128 );
//		leds2[1] = new Pixmap( 0.0f,  91.0f/128, 1.0f, 18.0f/128 );
//		leds2[2] = new Pixmap( 0.0f,  72.0f/128, 1.0f, 18.0f/128 );
//		leds2[3] = new Pixmap( 0.0f,  53.0f/128, 1.0f, 18.0f/128 );
//		leds2[4] = new Pixmap( 0.0f,  34.0f/128, 1.0f, 18.0f/128 );
//		
//		ledsIndicadores = new LedsIndicadores[5];
//		for(int i = 0; i < ledsIndicadores.length; i++){
//			ledsIndicadores[i] = new LedsIndicadores( 2, leds2, ledsNiveles[i] );
//			ledsIndicadores[i].setBorders( 0.04f, 0.05f);
//		}
//		ledIndicadorGrado = new LedIndicadorGrado( leds2, ledsGrado );
//		ledIndicadorGrado.setBorders( 0.075f, 0.05f);
		
	}
	//*/
	
	public abstract boolean isLoadComplete();
	
	public abstract void loadTexturas(GL gl);
	
	private void setIndicador(int valor) {
		for( int i = 0; i < ledsIndicadores.length; i++ ){
			if( valor > 0 ){
				valor -= 2;
				if( valor < 0 ){
					ledsIndicadores[i].setIluminados(1);
					ledsIndicadores[i].setActivo(true);
				}else if( valor == 0 ){
					ledsIndicadores[i].setIluminados(2);
					ledsIndicadores[i].setActivo(true);
				}else{
					ledsIndicadores[i].setIluminados(2);
					ledsIndicadores[i].setActivo(false);
				}
			}else{
				ledsIndicadores[i].setIluminados(0);
				ledsIndicadores[i].setActivo(false);
			}
		}
		ledIndicadorGrado.setIluminado(valor > 0);
	}

	void update(ClientData data) {

		vidas.setValor(data.nVidas);
		bombas.setValor(data.nBombas);
		puntos.setValor(data.puntos);

		for( int i = 0, len = ledsNiveles.length; i < len; i++ ){
			ledsNiveles[i].setVFijos(data.nivelesFijos[i]);
			ledsNiveles[i].setVActuales(data.nivelesActuales[i]);
			ledsNiveles[i].setVDisponibles(data.nivelesDisponibles[i]);
		}
		if( this.ledsGrado.vActuales != data.grado ){
			ledsGrado.setVActuales(data.grado);
			ledsGrado.setVFijos(data.grado);
		}
			
		setIndicador(data.indicador);
		
		for( LedsIndicadores l: ledsIndicadores )
			l.actualizar();
		ledIndicadorGrado.actualizar();
	}
	
//	public abstract void setBounds( float x, float y, float w, float h );
	
	public static MarcoDeIndicadores getMarco(int type){
		return new MarcoNave1();
	}
}
	
