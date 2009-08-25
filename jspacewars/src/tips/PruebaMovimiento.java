package tips;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.elementos.*;
import org.sam.interpoladores.*;
import org.sam.util.*;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class PruebaMovimiento{

	private static final int SUBE		= (1<<0);
	private static final int BAJA		= (1<<1);
	private static final int ACELERA	= (1<<2);
	private static final int FRENA		= (1<<3);
	private static final int DISPARO	= (1<<4);
	private static final int BOMBA		= (1<<5);
	private static final int UPGRADE	= (1<<6);

	private final static int K_SUBE		= KeyEvent.VK_UP;
	private final static int K_BAJA		= KeyEvent.VK_DOWN;
	private final static int K_ACELERA	= KeyEvent.VK_RIGHT;
	private final static int K_FRENA	= KeyEvent.VK_LEFT;
	private final static int K_DISPARO	= KeyEvent.VK_SHIFT;
	private final static int K_BOMBA	= KeyEvent.VK_SPACE;
	private final static int K_UPGRADE	= KeyEvent.VK_CONTROL;
	
	static class Elemento implements Prototipo<Elemento>,Cacheable{
		
		private final int code;
		protected int posX, posY;
		protected float uniX, uniY;
		protected Polygon forma;
		
		Elemento(int code, Polygon forma){
			this.code = code;
			try{
				this.forma = new Polygon(forma.xpoints,forma.ypoints,forma.npoints);
			}catch(NullPointerException formaVacia){
				this.forma = null;
			}
			this.uniX = this.posX = 0;
			this.uniY = this.posY = 0;
		}
		
		protected Elemento(Elemento prototipo){
			code = prototipo.code;
			forma = new Polygon(prototipo.forma.xpoints,prototipo.forma.ypoints,prototipo.forma.npoints);
			this.uniX = this.posX = prototipo.posX;
			this.uniY = this.posY = prototipo.posY;
		}
		
		public final int hashCode(){
			return code;
		}
		
		public Elemento clone() {
			return new Elemento(this);
		}
		
		public void setValues(int posX, int posY){
			try{
				forma.translate(posX-this.posX, posY-this.posY);
			}catch(NullPointerException formaVacia){
			}
			this.uniX = this.posX = posX;
			this.uniY = this.posY = posY;
		}
		
		public final int getX(){
			return this.posX;
		}
		
		public final int getY(){
			return this.posY;
		}
		
		public final Polygon getForma(){
			return forma;
		}

		/* (non-Javadoc)
		 * @see org.sam.util.Cacheable#reset()
		 */
		public void reset() {
		}
	}
	
	static Elemento diana = new Elemento(0,null);
	static{
		diana.setValues(400,300);
	}
	
	static abstract class Disparo extends Elemento{
		
		protected Disparo(int code, Polygon forma){
			super(code, forma);
		}
		
		protected Disparo(Disparo prototipo){
			super(prototipo);
			this.color = prototipo.color;
		}
		
		public boolean finalizado(){
			return false;
		}
		
		public abstract void actua(long milis);
		
		private Color color;

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}
		
		public abstract Disparo prototipoDerivado(int code);
		
		public double getAngulo(){
			return 0;
		}
	}
	
	static class DisparoLineal extends Disparo{
		
		private float velocidad;
		private float angulo;
		private float incX, incY;
		
		DisparoLineal(int code, Polygon forma){
			super(code, forma);
		}
		
		protected DisparoLineal(Disparo prototipo){
			super(prototipo);
		}
		
		public DisparoLineal clone() {
			return new DisparoLineal(this);
		}
		
		public void setValues(int posX, int posY, float angulo, float velocidad){
			super.setValues(posX, posY);
			this.velocidad = velocidad/1000.0f;
			this.angulo = angulo;
			this.incX = (float)Math.cos(angulo)*this.velocidad;
			this.incY = (float)Math.sin(angulo)*this.velocidad;
		}
		
		public void actua(long milis){
			uniX += incX*milis;
			uniY += incY*milis;
			int dX = (int)(uniX + 0.5f)- posX;
			int dY = (int)(uniY + 0.5f)- posY;
			forma.translate(dX, dY);
			posX +=dX;
			posY +=dY;
		}
		
		public DisparoLineal prototipoDerivado(int code){
			return new DisparoLineal(code, this.forma);
		}
		
		public double getAngulo(){
			return angulo;
		}
	}
	
	static class DisparoInterpolado extends Disparo{

		private int despX, despY;
		private double scaleX, scaleY;
		private double angulo;
		private long time;
		private final Trayectoria.Double<double[]> trayectoria;
		
		DisparoInterpolado(int code, Polygon forma, Trayectoria.Double<double[]> trayectoria){
			super(code, forma);
			this.trayectoria = trayectoria; 
		}
		
		protected DisparoInterpolado(DisparoInterpolado prototipo){
			super(prototipo);
			this.trayectoria = prototipo.trayectoria; 
		}
		
		public DisparoInterpolado clone() {
			return new DisparoInterpolado(this);
		}
		
		public void setValues(int posX, int posY, double scaleX, double scaleY){
			super.setValues(posX, posY);
			despX = posX;
			despY = posY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}
		
		public void setTime(long time){
			this.time = time;
		}
		
		public void actua(long milis){
			time += milis;
			double posicion[] = trayectoria.getPosTang(time/1000.0);
			int dX = (int)(posicion[0]*scaleX + 0.5)- posX + despX;
			int dY = (int)(posicion[1]*scaleY + 0.5)- posY + despY;
			angulo = posicion[2]*scaleY;
			forma.translate(dX, dY);
			posX +=dX;
			posY +=dY;
		}
		
		public DisparoInterpolado prototipoDerivado(int code){
			return new DisparoInterpolado(code, this.forma, this.trayectoria);
		}
		
		public double getAngulo(){
			return angulo;
		}
	}
	
	static class Misil extends Disparo{
		private double angulo, velocidad;
		private Elemento objetivo;

		Misil(int code, Polygon forma){
			super(code, forma);
		}
		
		protected Misil(Misil prototipo){
			super(prototipo);
		}
		
		public Misil clone() {
			return new Misil(this);
		}
		
		public void setValues(int posX, int posY, double angulo, double velocidad){
			super.setValues(posX, posY);
			this.angulo = angulo;
			this.velocidad = velocidad/1000.0;
		}
		
		public void setObjetivo(Elemento objetivo){
			this.objetivo = objetivo;
		}
		
		public boolean finalizado(){
			double dx = objetivo.uniX - uniX;
			double dy = objetivo.uniY - uniY;
			return (Math.sqrt(dx*dx+dy*dy) < 10);
		}
		
		private static int signo(double d){
			return d<0?-1:1;
		}	

		private static final double PI2 = 2*Math.PI;  
		
		private static double diferenciaAngulos(double a1, double a2){
			double r = a2 -a1;
			double absR = Math.abs(r);
			if(absR > Math.PI)
				r = signo(r)*(absR - PI2);
			return r;
		}	
		
		public void actua(long milis){
			try{
				double angOb = Math.atan2( (objetivo.uniY - uniY),(objetivo.uniX - uniX));
				double dif = diferenciaAngulos(angulo,angOb);
				if(Math.abs(dif) < 0.1 )
					angulo = angOb;
				else
					angulo += 0.0075*milis*signo(dif);
			}catch(NullPointerException noHayObjetivo){
			}
			
			double vAct = velocidad * milis;
			uniX += vAct * Math.cos(angulo);
			uniY += vAct * Math.sin(angulo);
			
			int dX = (int)(uniX + 0.5f)- posX;
			int dY = (int)(uniY + 0.5f)- posY;
			forma.translate(dX, dY);
			posX +=dX;
			posY +=dY;
		}
		
		public Misil prototipoDerivado(int code){
			return new Misil(code, this.forma);
		}

		public double getAngulo(){
			return angulo;
		}
	}
	
	static class Nave extends Elemento{

		private static final int I_VELOCIDAD 		= 0;
		private static final int I_NUM_CANIONES_PRI	= 1;
		private static final int I_GRA_CANIONES_PRI	= 2;
		private static final int I_NUM_CANIONES_SEC	= 3;
		private static final int I_GRA_CANIONES_SEC	= 4;

		private final int [][] limitesDeNiveles;
		private int gradoNave;

		private static final int I_ACTUAL 	= 0;
		private static final int I_GUARDADO	= 1;
		private int niveles[][];
		
		private final float [] velocidadesDisponibles;
		private float velocidad;

		private static final int I_PRIMARIO 	= 0;
		private static final int I_SECUNDARIO	= 1;
		private final Canion[][][] canionesDisponibles;
		private Canion[][] caniones;
		
		private final List<Disparo> disparos;
		
		/**
		 * 
		 * @param limitesDeNiveles
		 * @param velocidadesDisponibles
		 * @param canionesDisponibles
		 * @param forma
		 */
		public Nave(
				int tipo,
				Polygon forma,
				int[][] limitesDeNiveles,
				float[] velocidadesDisponibles,
				Canion[][][] canionesDisponibles,
				List<Disparo> disparos){
			super(tipo, forma);
			this.limitesDeNiveles = limitesDeNiveles;
			this.velocidadesDisponibles = velocidadesDisponibles;
			this.canionesDisponibles = canionesDisponibles;
			this.gradoNave = 1;
			this.niveles = new int[2][5];
			// En la posicion 0 estan almacenados los valores inciales
			System.arraycopy(this.limitesDeNiveles[0],0,this.niveles[I_ACTUAL],0,5);
			System.arraycopy(this.limitesDeNiveles[0],0,this.niveles[I_GUARDADO],0,5);
			velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];
			this.caniones = new Canion[2][];
			this.disparos = disparos;
		}

		private void setCaniones(int iCanion, int nivel){
			try{
			int len = canionesDisponibles[iCanion][nivel].length;
			caniones[iCanion] = new Canion[len];
			for(int i=0; i<len ; i++)
				caniones[iCanion][i] = canionesDisponibles[iCanion][nivel][i].clone();
			}catch(ArrayIndexOutOfBoundsException enEsteNivelNoHayValores){
			}
		}

		private void aumentarGradoCaniones(int iCanion, int incGrado){
			try{
				for (int i=0, len = caniones[iCanion].length; i<len ; i++)
					caniones[iCanion][i].aumentarGrado(incGrado);
			}catch(NullPointerException noHayCaniones){
			}
		}

		public void inciarNave(){
			//for(int i = I_VELOCIDAD; i <= I_GRA_CANIONES_SEC; i++)
			//	niveles[I_ACTUAL][i] = Math.max(limitesDeNiveles[0][i],niveles[I_GUARDADO][i]);
			System.arraycopy(niveles[I_GUARDADO],0,niveles[I_ACTUAL],0,5);

			velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]]/1000.0f;
			setCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI]);
			aumentarGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_GRA_CANIONES_PRI]);
			setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
			aumentarGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_GRA_CANIONES_SEC]);
		}
		
		public boolean nivelAumentable(int index){
			return niveles[I_ACTUAL][index] < limitesDeNiveles[gradoNave][index];
		}
		
		public void aumentarNivel(int index){
			niveles[I_ACTUAL][index]++;
			switch(index){
			case I_VELOCIDAD:
				velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];
				break;
			case I_NUM_CANIONES_PRI:
				setCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI]);
				aumentarGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_GRA_CANIONES_PRI]);
				break;
			case I_GRA_CANIONES_PRI:
				aumentarGradoCaniones(I_PRIMARIO, 1);
				break;
			case I_NUM_CANIONES_SEC:
				setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
				aumentarGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_GRA_CANIONES_SEC]);
				break;
			case I_GRA_CANIONES_SEC:
				aumentarGradoCaniones(I_SECUNDARIO, 1);
				break;
			}
		}
		
		public boolean nivelGuardable(int index){
			return niveles[I_GUARDADO][index] < niveles[I_ACTUAL][index];
		}
		
		public void guardarNivel(int index){
			niveles[I_GUARDADO][index]++;
		}

		public void aumentarGradoNave(){
			gradoNave++;
		}
		
		public void dispara(){
			Disparo disparo;
			for(int iCanion = I_PRIMARIO; iCanion <= I_SECUNDARIO; iCanion++){
				try{
					for(int i=0, len = caniones[iCanion].length; i<len ; i++){
						disparo = caniones[iCanion][i].dispara(posX,posY);
						disparos.add(disparo);
					}
				}catch(NullPointerException noHayCaniones){
				}
			}
		}
		
		private int ancho, alto;
		public void setLimitesPantalla(int ancho, int alto){
			this.ancho = ancho;
			this.alto = alto;
		}
		
		private int key_state;
		public void setKeyState(int key_state){
			this.key_state = key_state;
		}
		
		private long tiempoUltimoDisparo = 10000;
		private final long tiempoRecarga = 40;

		public void actua(long milis){
			int dirX = 0, dirY = 0;
			if((key_state & SUBE) != 0){
				if (posY > 0) dirY = -1;
			}else if ((key_state & BAJA) != 0){
				if (posY < alto-20) dirY = 1;
			}
			if((key_state & ACELERA) != 0){
				if (posX < ancho-20) dirX = 1;
			}else if ((key_state & FRENA) != 0){
				if (posX > 0) dirX = -1;
			}
			if( dirX!=0 || dirY!=0 ){
				float v = velocidad*milis; 
				uniX += v*dirX;
				uniY += v*dirY;
				int dX = (int)(uniX + 0.5f)- posX;
				int dY = (int)(uniY + 0.5f)- posY;
				posX +=dX;
				posY +=dY;
				forma.translate( dX, dY);
			}
			tiempoUltimoDisparo += milis;
			if( (key_state & DISPARO) != 0){
				if (tiempoUltimoDisparo < tiempoRecarga)
					return;
				tiempoUltimoDisparo = 0;
				dispara();
			}
		}
	}
	
	static abstract class Canion implements Cloneable{
		
		protected static Cache<Elemento> cache;

		public final static void setCache(Cache<Elemento> unaCache){
			if (cache == null)
				cache = unaCache;
		}
		
		/**
		 * Posicion relativa del cañon respecto a la nave
		 */
		protected int posX, posY;
		/**
		 * Angulo de disparo del cañon
		 */

		protected int grado;
		/**
		 * Referencia a los distintos disparos que puede lanzar un cañon dependiendo
		 * de su grado.
		 */
		protected final int[] disparos;
		
		protected Canion(int[] disparos, int grado){
			this.disparos = disparos;
			this.grado = grado;
		}

		protected Canion(Canion prototipo){
			this.disparos = prototipo.disparos;
			this.grado = prototipo.grado;
			this.posX = prototipo.posX;
			this.posY = prototipo.posY;
		}

		public abstract Canion clone();
		
		public final void setPos(int posX, int posY){
			this.posX = posX;
			this.posY = posY;
		}
		
		public abstract Disparo dispara(int posAbsX, int posAbsY);
		
		public final void setGrado(int grado) {
			this.grado = grado;
		}
		
		public final void aumentarGrado(int incGrado){
			grado += incGrado;
		}
	}
	
	static class CanionLineal extends Canion{
		/**
		 * Angulo de disparo del cañón
		 */
		private float angulo;
		
		public CanionLineal(int[] disparos, int grado){
			super(disparos,grado);
		}

		private CanionLineal(CanionLineal prototipo){
			super(prototipo);
			this.angulo = prototipo.angulo;
		}

		public Canion clone(){
			return new CanionLineal(this);
		}
		
		public void setAngulo(float angulo){
			this.angulo = (float)(angulo/180.0f * Math.PI);
		}

		public Disparo dispara(int posAbsX, int posAbsY){
			DisparoLineal disparo = (DisparoLineal)cache.newObject(disparos[grado]);
			disparo.setValues(posAbsX+posX,posAbsY+posY, angulo, 1000.0f);
			return disparo;
		}
	}
	
	static class CanionInterpolado extends Canion{
		/**
		 * Escala de la trayectoria del disparo
		 */
		private double scaleX, scaleY;

		public CanionInterpolado(int[] disparos, int grado){
			super(disparos,grado);
		}

		private CanionInterpolado(CanionInterpolado prototipo){
			super(prototipo);
			this.scaleX = prototipo.scaleX;
			this.scaleY = prototipo.scaleY;
		}

		public Canion clone(){
			return new CanionInterpolado(this);
		}
		
		public void setScales(double scaleX, double scaleY){
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}

		public Disparo dispara(int posAbsX, int posAbsY){
			DisparoInterpolado disparo = (DisparoInterpolado)cache.newObject(disparos[grado]);
			disparo.setValues(posAbsX+posX,posAbsY+posY, scaleX, scaleY);
			disparo.setTime(0);
			return disparo;
		}
	}

	static class LanzaMisiles extends Canion{
		/**
		 * Angulo de disparo del cañon
		 */
		private double angulo;
		
		public LanzaMisiles(int[] disparos, int grado){
			super(disparos,grado);
		}

		private LanzaMisiles(LanzaMisiles prototipo){
			super(prototipo);
			this.angulo = prototipo.angulo;
		}

		public Canion clone(){
			return new LanzaMisiles(this);
		}
		
		public void setAngulo(double angulo){
			this.angulo = angulo/180.0 * Math.PI;
		}

		public Disparo dispara(int posAbsX, int posAbsY){
			Misil disparo = (Misil)cache.newObject(disparos[grado]);
			disparo.setValues(posAbsX+posX,posAbsY+posY, angulo, 1000.0);
			disparo.setObjetivo(diana);
			return disparo;
		}
	}

	private static boolean pintado = true;
	
	static class Motor{
//		private final Semaforo semaforo;
		private final Cache<Elemento> cache;
		private final Nave nave;
		private final LinkedList<Disparo> disparos;
		
		Motor(Semaforo semaforo, Cache<Elemento> cache, Nave nave, LinkedList<Disparo> disparos){
//			this.semaforo = semaforo;
			this.cache = cache;
			this.nave = nave;
			this.disparos = disparos;
		}

		private int ancho, alto;
		public void setDimensiones(int ancho, int alto){
			this.ancho = ancho;
			this.alto  = alto;
		}
		
		private synchronized void actua(int keyState, long milis){
			while(!pintado)
				Thread.yield();
//			semaforo.esperar();
			//milis/=10;
			Iterator<Disparo> i = disparos.listIterator();
			while(i.hasNext()){
				Disparo d = i.next();
				if (d.finalizado() || d.getX() > ancho || d.getY() < 0 || d.getY() > alto){
					i.remove();
					cache.cached(d);
				}else
					d.actua(milis);
			}
			nave.setKeyState(keyState);
			nave.actua(milis);
			pintado = false;
//			semaforo.notificar();
		}
	}

	static class Pantalla extends JPanel{
		private static final long serialVersionUID = 1L;
		
		private BufferedImage buffer;
		
		private void createBuffer() {
			if(getWidth() == 0 || getHeight() == 0)
				return;
			if( buffer == null || buffer.getWidth() != this.getWidth()
					|| buffer.getHeight() != this.getHeight() )
				buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		
//		private final Semaforo semaforo;
//		private final Cache cache;
		private final Nave nave;
		private final LinkedList<Disparo> disparos;
		
		Pantalla(Semaforo semaforo, Cache<Elemento> cache, Nave nave, LinkedList<Disparo> disparos){
//			this.semaforo = semaforo;
//			this.cache = cache;
			this.nave = nave;
			this.disparos = disparos;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#setVisible(boolean)
		 */
		@Override
		public void setVisible(boolean visible){
			if(visible)
				createBuffer();
			super.setVisible(visible);
		}

		/* (non-Javadoc)
		 * @see java.awt.Container#doLayout()
		 */
		@Override
		public void doLayout(){
			if(isVisible())
				createBuffer();
			super.doLayout();
		}

		/* (non-Javadoc)
		 * @see java.awt.Container#validate()
		 */
		@Override
		public void validate(){
			if(isVisible())
				createBuffer();
			super.validate();
		}
		
		public void paintComponent(Graphics g){
			if (isVisible() && buffer != null) {
				Graphics2D g2 = buffer.createGraphics();
				//semaforo.esperar();
				while(pintado)
					Thread.yield();
				g2.setColor(new Color(0,0,0,16));
				g2.fillRect(0,0,getWidth(),getHeight());
				Composite oldC = g2.getComposite();

				g2.setComposite(Imagen.FUSIONAR_SUM);

				Iterator<Disparo> i = disparos.listIterator();
				while(i.hasNext()){
					Disparo d = i.next();
					g2.setColor(d.getColor());
					double angulo = d.getAngulo();
					int x = d.getX();
					int y = d.getY();
					g2.rotate(angulo,x,y);
					g2.fillPolygon(d.getForma());
					g2.rotate(-angulo,x,y);
				}
				//			g2.setColor(Color.CYAN);
				g2.setColor(new Color(4,32,32,255));
				g2.fillPolygon(nave.getForma());
				g2.setComposite(oldC);
				g2.dispose();
				g.drawImage(buffer, 0, 0, this);

				pintado = true;
				//semaforo.notificar();
			}
		}
	}
	
	static class MyKeyListener implements KeyListener{
		
		private static Cache<Elemento> cache;
		private	int	key_state;
		
		static void setCache(Cache<Elemento> unaCache){
			cache = unaCache;
		}

		MyKeyListener(){
			key_state = 0;
		}

		public void keyPressed(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();
			switch (keyCode) {
			case K_SUBE:	key_state |= SUBE;		break;
			case K_BAJA:	key_state |= BAJA;		break;
			case K_ACELERA:	key_state |= ACELERA;	break;
			case K_FRENA:	key_state |= FRENA;		break;
			case K_DISPARO:	key_state |= DISPARO;	break;
			case K_BOMBA:	key_state |= BOMBA;		break;
			case K_UPGRADE:	key_state |= UPGRADE;	break;
			default:
			}
		}

		public void keyReleased(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();
			switch (keyCode) {
			case K_SUBE:	key_state &= ~SUBE;		break;
			case K_BAJA:	key_state &= ~BAJA;		break;
			case K_ACELERA:	key_state &= ~ACELERA;	break;
			case K_FRENA:	key_state &= ~FRENA;	break;
			case K_DISPARO:	key_state &= ~DISPARO;	break;
			case K_BOMBA:	key_state &= ~BOMBA;	break;
			case K_UPGRADE:	key_state &= ~UPGRADE;	break;
			case KeyEvent.VK_C: System.out.println(cache);
			default:
			}
		}

		public void keyTyped(KeyEvent keyEvent) {}
		
		public int getKeyState(){
			return key_state;
		}
	}
	
	static public void main(String args[]){
		
		JFrame frame = new JFrame("Prueba Movimiento");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LinkedList<Polygon> formas = new LinkedList<Polygon>();
		try{
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(new XMLHandlerForma(parser,null,formas));
			parser.parse("resources/xml/elementos.xml");
		}catch (Exception e){
			System.err.println ("Error al procesar el fichero de elementos: "+ e.getMessage());
			e.printStackTrace();
		}

		int limitesDeNiveles[][] = new int[][]{
				{0,4,3,2,0},
				{2,2,2,1,1},
				{4,4,4,2,2},
				{5,5,5,3,3}
			};
		
		float velocidadesDisponibles[] = new float[]{
			200.0f, 600.0f, 300.0f, 350.0f, 400.0f
		};
		
		Canion potrotipoCanion;

		potrotipoCanion = new CanionLineal(new int[]{1,2,3,4,5,6,7},0);
		
		CanionLineal c11 = (CanionLineal)potrotipoCanion.clone();
		c11.setGrado(2);
		c11.setPos(65, 15);
		c11.setAngulo(0.0f);
		CanionLineal c12 = (CanionLineal)potrotipoCanion.clone();
		c12.setGrado(1);
		c12.setPos(58, 10);
		c12.setAngulo(-5.0f);
		CanionLineal c13 = (CanionLineal)potrotipoCanion.clone();
		c13.setGrado(1);
		c13.setPos(58, 20);
		c13.setAngulo(5.0f);
		CanionLineal c14 = (CanionLineal)potrotipoCanion.clone();
		c14.setGrado(0);
		c14.setPos(55, 5);
		c14.setAngulo(-10.0f);
		CanionLineal c15 = (CanionLineal)potrotipoCanion.clone();
		c15.setGrado(0);
		c15.setPos(55, 25);
		c15.setAngulo(10.0f);
		potrotipoCanion = null;

		potrotipoCanion = new CanionInterpolado(new int[]{10,11,12,13},0);
		
		CanionInterpolado c21 = (CanionInterpolado)potrotipoCanion.clone();
		c21.setPos(0, 15);
		c21.setScales(1.5, 1.0);
		CanionInterpolado c22 = (CanionInterpolado)potrotipoCanion.clone();
		c22.setPos(0, 15);
		c22.setScales(1.5, -1.0);
		potrotipoCanion = null;
		
		potrotipoCanion = new LanzaMisiles(new int[]{20},0);
		
		LanzaMisiles c23 = (LanzaMisiles)potrotipoCanion.clone();
		c23.setPos(0, 5);
		c23.setAngulo(-45);
		LanzaMisiles c24 = (LanzaMisiles)potrotipoCanion.clone();
		c24.setPos(0, 25);
		c24.setAngulo(45);
		potrotipoCanion = null;
		
		Canion[][][] canionesDisponibles = new Canion[][][]{
			// Cañones primarios
			{
				{c11},
				{c12,c13},
				{c11,c12,c13},
				{c12,c13,c14,c15},
				{c11,c12,c13,c14,c15},
			},
			// Cañones secundarios
			{	
				{},
				{c21,c22},
				{c21,c22,c23,c24},
			},
		};

		Cache<Elemento> cache = new Cache<Elemento>(500);
		Canion.setCache(cache);
		LinkedList<Disparo> disparos = new LinkedList<Disparo>();
		
		Nave nave = new Nave(101,formas.removeFirst(),
				limitesDeNiveles,
				velocidadesDisponibles,
				canionesDisponibles,
				disparos);
		nave.inciarNave();
		nave.setLimitesPantalla(800,600);
		
		Polygon formaDisparo = formas.removeFirst();
		Disparo disparo = new DisparoLineal(1, formaDisparo);
		disparo.setColor(new Color(8,64,8));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);
		
		disparo = disparo.prototipoDerivado(2);
		disparo.setColor(new Color(16,64,8));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);
		
		disparo = disparo.prototipoDerivado(3);
		disparo.setColor(new Color(32,64,8));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);

		disparo = disparo.prototipoDerivado(4);
		disparo.setColor(new Color(48,64,8));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);

		disparo = disparo.prototipoDerivado(5);
		disparo.setColor(new Color(64,64,8));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);

		disparo = disparo.prototipoDerivado(6);
		disparo.setColor(new Color(64,64,16));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);
		
		disparo = disparo.prototipoDerivado(7);
		disparo.setColor(new Color(64,64,32));
//		disparo.setColor(Color.YELLOW);
		cache.addPrototipo(disparo);

		double values[][] = new double[][]{
			{0.0, 0.0},
				{-10.0, 0.0},
				{-25.0, 15.0},
			{-25.0, 25.0},
				{-25.0, 35.0},
				{-10.0, 50.0},
			{0.0, 50.0},
				{25.0, 50.0},
				{75.0, -25.0},
			{100.0, -25.0},
				{125.0, -25.0},
				{175.0, 25.0},
			{200.0, 25.0},
				{225.0, 25.0},
				{275.0, -25.0},
			{300.0, -25.0},
				{325.0, -25.0},
				{375.0, 25.0},
			{400.0, 25.0},
				{425.0, 25.0},
				{475.0, -25.0},
			{500.0, -25.0},
				{525.0, -25.0},
				{575.0, 25.0},
			{600.0, 25.0},
				{625.0, 25.0},
				{675.0, -25.0},
			{700.0, -25.0}
		};

		Trayectoria.Double<double[]> trayectoria = (Trayectoria.Double<double[]>)GettersFactory.Double.create(Keys.PROPORCIONALES, 2.5, 0.0, values, MetodoDeInterpolacion.Predefinido.BEZIER);
		
		disparo = new DisparoInterpolado(10,formaDisparo,trayectoria);
		disparo.setColor(new Color(64,16,8));
//		disparo.setColor(Color.ORANGE);
		cache.addPrototipo(disparo);
		
		disparo = new Misil(20,formaDisparo);
		disparo.setColor(new Color(64,32,64));
//		disparo.setColor(Color.WHITE);
		cache.addPrototipo(disparo);

		Semaforo semaforo = new Semaforo(1);
		final MyKeyListener myKeyListener = new MyKeyListener();
		MyKeyListener.setCache(cache);
		final Pantalla pantalla = new Pantalla(semaforo,cache, nave, disparos);
		frame.addKeyListener(myKeyListener);
		frame.setFocusable(true);
		frame.setFocusTraversalKeysEnabled(false);
		final Motor motor = new Motor(semaforo,cache, nave, disparos);
		motor.setDimensiones(800,600);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pantalla,BorderLayout.CENTER);
		frame.setVisible(true);
		
		Thread animador = new Thread(){
			public void run(){
				long tAnterior = System.currentTimeMillis();
				while(true){
					long tActual = System.currentTimeMillis();
					motor.actua(myKeyListener.getKeyState(),tActual-tAnterior);
					tAnterior = tActual;
					pantalla.repaint();
//					try{
//						Thread.sleep(0);
//						Thread.yield();
//					}catch(InterruptedException igonada){
//					}
				}
			}
		};
		animador.start();
	}
}