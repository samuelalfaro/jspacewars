package org.sam.elementos;

public class NaveUsuario extends Nave{

	private static final int I_VELOCIDAD		= 0;
	private static final int I_NUM_CANIONES_PRI	= 1;
	private static final int I_POW_CANIONES_PRI	= 2;
	private static final int I_NUM_CANIONES_SEC	= 3;
	private static final int I_POW_CANIONES_SEC	= 4;

	private final int [][] limitesDeNiveles;
	private transient int gradoNave;

	private static final int I_FIJO		= 0;
	private static final int I_ACTUAL 	= 1;
	
	private transient int niveles[][];
	
	private final float [] velocidadesDisponibles;
	private transient float velocidad;

	private static final int I_PRIMARIO 	= 0;
	private static final int I_SECUNDARIO	= 1;
	private final Canion[][][] canionesDisponibles;
	private transient Canion[][] caniones;
	
	/**
	 * @param code
	 * @param limitesDeNiveles
	 * @param velocidadesDisponibles
	 * @param canionesDisponibles
	 */
	public NaveUsuario(
			short code,
			int[][] limitesDeNiveles,
			float[] velocidadesDisponibles,
			Canion[][][] canionesDisponibles){
		super(code);
		this.limitesDeNiveles = limitesDeNiveles;
		this.velocidadesDisponibles = velocidadesDisponibles;
		this.canionesDisponibles = canionesDisponibles;
		this.gradoNave = 1;
		iniciar();
	}

	protected NaveUsuario(NaveUsuario prototipo){
		super(prototipo);
		this.limitesDeNiveles = prototipo.limitesDeNiveles;
		this.velocidadesDisponibles = prototipo.velocidadesDisponibles;
		this.canionesDisponibles = prototipo.canionesDisponibles;
		this.gradoNave = prototipo.gradoNave;
		iniciar();
	}
	
	public NaveUsuario clone() {
		return new NaveUsuario(this);
	}
	
	public void iniciar(){
		if(this.niveles == null){
			this.niveles = new int[2][5];
			// En la posicion 0 estan almacenados los valores inciales
			System.arraycopy(this.limitesDeNiveles[0],0,this.niveles[I_ACTUAL],0,5);
			System.arraycopy(this.limitesDeNiveles[0],0,this.niveles[I_FIJO],0,5);
		}else
			// En este caso reiniciamos la nave porque la han matado,
			// y copiamos los niveles guardados a los actuales.
			System.arraycopy(niveles[I_FIJO],0,niveles[I_ACTUAL],0,5);

		velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];
		
		if(this.caniones == null)
			this.caniones = new Canion[2][];
		setCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI]);
		setGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI]);
		setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
		setGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC]);
	}
	
	/**
	 * 
	 * @param iCanion Indice de origen/destino de los cañones(primarios/secundarios)
	 * @param nivel Nivel de los cañones en la tabla de cañones disponibles
	 */
	private void setCaniones(int iCanion, int nivel){
		int len = canionesDisponibles[iCanion][nivel].length;
		caniones[iCanion] = new Canion[len];
		for(int i=0; i<len ; i++)
			caniones[iCanion][i] = canionesDisponibles[iCanion][nivel][i].clone();
	}

	private void setGradoCaniones(int iCanion, int grado){
		if(caniones[iCanion] != null)
			for( Canion canion: caniones[iCanion])
				canion.setGrado(grado);
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
			setGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI]);
			break;
		case I_POW_CANIONES_PRI:
			setGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_POW_CANIONES_PRI]);
			break;
		case I_NUM_CANIONES_SEC:
			setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
			setGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC]);
			break;
		case I_POW_CANIONES_SEC:
			setGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_POW_CANIONES_SEC]);
			break;
		}
	}
	
	public boolean nivelGuardable(int index){
		return niveles[I_FIJO][index] < niveles[I_ACTUAL][index];
	}
	
	public void guardarNivel(int index){
		niveles[I_FIJO][index]++;
	}

	public void aumentarGradoNave(){
		gradoNave++;
	}
	
	private transient float ancho, alto;
	public void setLimitesPantalla(float ancho, float alto){
		this.ancho = ancho;
		this.alto = alto;
	}
	
	private transient int key_state;
	public void setKeyState(int key_state){
		this.key_state = key_state;
	}
	
	public void actua(long nanos){
		boolean anguloModificado = false; 
		
		float v = velocidad*nanos;
		float nX = posX;
		float nY = posY;
		if((key_state & KeyState.SUBE) != 0){
			posY += v;
			if (posY > alto)
				posY = alto;
			if (angulo > -0.5f)
				angulo -= 1.5e-9f*nanos;
			if(angulo < -0.5f)
				angulo = -0.5f;
			anguloModificado = true;
		}else if ((key_state & KeyState.BAJA) != 0){
			posY -= v;
			if (posY < -alto)
				posY = -alto;
			if (angulo < 0.5f)
				angulo += 1.5e-9f*nanos;
			if(angulo > 0.5f)
				angulo = 0.5f;
			anguloModificado = true;
		}
		if(!anguloModificado && angulo != 0.0f){
			if (angulo > 0.0f){
				angulo -= 3.0e-9f*nanos;
				if (angulo < 0.0f)
					angulo = 0.0f;
			}
			else{
				angulo += 3.0e-9f*nanos;
				if (angulo > 0.0f)
					angulo = 0.0f;
			}
		}
		if((key_state & KeyState.ACELERA) != 0){
			posX += v;
			if (posX > ancho)
				posX = ancho;
		}else if ((key_state & KeyState.FRENA) != 0){
			posX -= v;
			if (posX < -ancho)
				posX = -ancho;
		}
		
		if( (key_state & KeyState.DISPARO) != 0){
//			System.out.println("hola");
			float mX = (posX - nX) / nanos;
			float mY = (posY - nY) / nanos;

			for(Canion canion: caniones[I_PRIMARIO])
				canion.dispara( mX, nX, mY, nY, nanos, getDstDisparos());
			
			if(caniones[I_SECUNDARIO]!=null)
				for(Canion canion: caniones[I_SECUNDARIO])
					canion.dispara( mX, nX, mY, nY, nanos, getDstDisparos());
			
		}
	}
}