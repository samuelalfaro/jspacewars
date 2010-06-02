package org.sam.jspacewars.elementos;

public class Misil extends Disparo {
	private transient float vTangencial;
	// TODO ajustar valores angulo
	private transient float vAngular = 0.000000005f;
	private transient float aceleracion;

	private transient Elemento objetivo;

	Misil(short code) {
		super(code);
	}

	protected Misil(Misil prototipo) {
		super(prototipo);
	}

	public Misil clone() {
		return new Misil(this);
	}

	public void setValues(float posX, float posY, float angulo, float velocidad) {
		super.setPosicion(posX, posY);
		this.angulo = angulo;
		this.vTangencial = velocidad;
		this.aceleracion = velocidad / 500000000;
	}

	public void setObjetivo(Elemento objetivo) {
		this.objetivo = objetivo;
//		objetivoAlcanzado = false;
	}

	@Override
	public boolean isDestruido() {
//		if( objetivo == null )
			return false;
//		return objetivoAlcanzado;
	}

	private static transient final float PI2 = (float) (2 * Math.PI);
	private static transient final float PI = (float) Math.PI;

	private void rotarPos(float alfa, float pX, float pY) {
		float cosAlfa = (float) Math.cos(alfa);
		float senAlfa = (float) Math.sin(alfa);

		float x = posX - pX;
		float y = posY - pY;
		posX = x * cosAlfa - y * senAlfa + pX;
		posY = x * senAlfa + y * cosAlfa + pY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {

		float vel = vTangencial * nanos;

		if( objetivo != null && !objetivo.isDestruido()){
			// w = velocidad Angular
			// v = velocidad Tangencial
			// v = w · radio
			// radio = v / w

			float radio = vTangencial / vAngular;
			float dx, dy;

			float normalX = (float) Math.sin(angulo) * radio;
			float normalY = (float) Math.cos(angulo) * radio;

			// Centro de giro 1
			float c1X = posX - normalX;
			float c1Y = posY + normalY;
			dx = objetivo.posX - c1X;
			dy = objetivo.posY - c1Y;
			float dist1 = dx * dx + dy * dy;

			// Centro de giro 2
			float c2X = posX + normalX;
			float c2Y = posY - normalY;
			dx = objetivo.posX - c2X;
			dy = objetivo.posY - c2Y;
			float dist2 = dx * dx + dy * dy;

			float iAng = vAngular * nanos;

			// Si el objetivo esta dentro del radio de uno de los centros de
			// giro, es inalcanzable por tanto se gira en el contrario.
			float r2 = radio * radio;
			if( dist1 < r2 ){
				angulo -= iAng;
				while( angulo < 0 )
					angulo += PI2;
				rotarPos(-iAng, c2X, c2Y);
				return;
			}else if( dist2 < r2 ){
				angulo += iAng;
				while( angulo > PI2 )
					angulo -= PI2;
				rotarPos(iAng, c1X, c1Y);
				return;
			}

			{ // TODO cambiar por giro y movimiento lineal tangencial a las
				// circunferencias de giro
				// El angulo al objetivo es alcanzable en este intervalo se
				// iguala y se avanza en dicha dirección
				dx = objetivo.posX - posX;
				dy = objetivo.posY - posY;

				float angOb = (float) Math.atan2(dy, dx);
				if( angOb < 0 )
					angOb = PI2 + angOb;

				float absDif = Math.abs(angOb - angulo);
				if( absDif > PI )
					absDif = PI2 - absDif;

				if( absDif < iAng ){
					angulo = angOb;
					posX += vel * (float) Math.cos(angulo);
					posY += vel * (float) Math.sin(angulo);
					vTangencial += aceleracion * nanos;
					return;
				}
			}
			// Se gira en el centro de giro más próximo al objetivo
			if( dist1 < dist2 ){
				angulo += iAng;
				while( angulo > PI2 )
					angulo -= PI2;
				rotarPos(iAng, c1X, c1Y);
			}else{
				angulo -= iAng;
				while( angulo < 0 )
					angulo += PI2;
				rotarPos(-iAng, c2X, c2Y);
			}
		}else{
			posX += vel * (float) Math.cos(angulo);
			posY += vel * (float) Math.sin(angulo);
		}
	}
}