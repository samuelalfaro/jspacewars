package org.sam.elementos;


public class NaveUsuario extends Nave{

	private static final int I_VELOCIDAD		= 0;
	private static final int I_NUM_CANIONES_PRI	= 1;
	private static final int I_GRA_CANIONES_PRI	= 2;
	private static final int I_NUM_CANIONES_SEC	= 3;
	private static final int I_GRA_CANIONES_SEC	= 4;

	private final int [][] limitesDeNiveles;
	private transient int gradoNave;

	private static final int I_ACTUAL 	= 0;
	private static final int I_GUARDADO	= 1;
	private transient int niveles[][];
	
	private final float [] velocidadesDisponibles;
	private transient float velocidad;

	private static final int I_PRIMARIO 	= 0;
	private static final int I_SECUNDARIO	= 1;
	private final Canion[][][] canionesDisponibles;
	private transient Canion[][] caniones;
	
	/**
	 * 
	 * @param limitesDeNiveles
	 * @param velocidadesDisponibles
	 * @param canionesDisponibles
	 * @param forma
	 */
	public NaveUsuario(
			short tipo,
			int[][] limitesDeNiveles,
			float[] velocidadesDisponibles,
			Canion[][][] canionesDisponibles){
		super(tipo);
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
			System.arraycopy(this.limitesDeNiveles[0],0,this.niveles[I_GUARDADO],0,5);
		}else
			// En este caso reiniciamos la nave porque la han matado,
			// y copiamos los niveles guardados a los actuales.
			System.arraycopy(niveles[I_GUARDADO],0,niveles[I_ACTUAL],0,5);

		velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]]/1000.0f;
		//velocidad = velocidadesDisponibles[niveles[I_ACTUAL][I_VELOCIDAD]];
		
		if(this.caniones == null)
			this.caniones = new Canion[2][];
		setCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_NUM_CANIONES_PRI]);
		aumentarGradoCaniones(I_PRIMARIO, niveles[I_ACTUAL][I_GRA_CANIONES_PRI]);
		setCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_NUM_CANIONES_SEC]);
		aumentarGradoCaniones(I_SECUNDARIO, niveles[I_ACTUAL][I_GRA_CANIONES_SEC]);
	}
	
	/**
	 * 
	 * @param iCanion Indice de origen/destino de los cañones(primarios/secundarios)
	 * @param nivel Nivel de los cañones en la tabla de cañones disponibles
	 */
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
	
	private transient float ancho, alto;
	public void setLimitesPantalla(float ancho, float alto){
		this.ancho = ancho;
		this.alto = alto;
	}
	
	private transient int key_state;
	public void setKeyState(int key_state){
		this.key_state = key_state;
	}
	
	public void actua(long milis){
		float v = velocidad*milis; 
		if((key_state & KeyState.SUBE) != 0){
			posY += v;
			if (posY > alto)
				posY = alto;
		}else if ((key_state & KeyState.BAJA) != 0){
			posY -= v;
			if (posY < -alto)
				posY = -alto;
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
			for(int iCanion = I_PRIMARIO; iCanion <= I_SECUNDARIO; iCanion++)
				if(caniones[iCanion]!=null)
					for(Canion canion: caniones[iCanion])
						canion.dispara(this,milis);
		}
	}
}