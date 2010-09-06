package org.sam.jogl;

public interface Geometria extends Dibujable {
	
	public static final int POR_REFERENCIA		= 0x0010;
	public static final int USAR_BUFFERS		= 0x0020;
	public static final int COORDENADAS_TEXTURA	= 0x0040;
	public static final int	COLOR_3				= 0x0080;
	public static final int	COLOR_4				= 0x0081;
	public static final int NORMALES			= 0x0100;
	public static final int COORDENADAS			= 0x0200;
	public static final int ATRIBUTOS_VERTICES	= 0x0400;
	
}