package org.sam.util;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.*;

import javax.imageio.ImageIO;

public class Imagen {

	public static final int CANAL_ALFA  = 3;
	public static final int CANAL_ROJO  = 2;
	public static final int CANAL_VERDE = 1;
	public static final int CANAL_AZUL  = 0;

	public static final int MODO_AND = 0;
	public static final int MODO_OR  = 1;
	public static final int MODO_XOR = 2;
	public static final int MODO_SUM = 3;
	public static final int MODO_RES = 4;
	public static final int MODO_DIF = 5;
	/**
	 El MODO_MUL produce un efecto de oscurecimento, corresponde a la fórmula:

	 dst = ( src1 * src2)

	 los valores 1 dejan el origen como estaba
	 y los valores 0 devuelven el valor 0
	 */
	public static final int MODO_MUL = 6;
	/**
	 El MODO_DIV es el modo inverso al modoMUL produce un efecto de aclarado,
	 corresponde a la fórmula:

	 dst = ~( ~src1 * ~src2)

	 los valores 1 dejan el origen como estaba
	 y los valores 0 devuelven el valor 0
	 */
	public static final int MODO_DIV = 7;
	/**
	 El MODO_SUP es el modo de superposición de la imagen con la transparecia

	 dst = src1*(1-alfa) + src2*alfa

	 Si la componente alfa es 1, o no se aplica el resultado es src2
	 */
	public static final int MODO_SUP = 8;

	public static final int ALFA_OFF = -1;
	public static final int ALFA_SRC = 256;

	private static final int A_MASK = 0xFF000000;
	private static final int R_MASK = 0x00FF0000;
	private static final int G_MASK = 0x0000FF00;
	private static final int B_MASK = 0x000000FF;

	private static final int A_MASK_I = 0x00FFFFFF;
	private static final int R_MASK_I = 0xFF00FFFF;
	private static final int G_MASK_I = 0xFFFF00FF;
	private static final int B_MASK_I = 0xFFFFFF00;

	private static final int BYTE = 0xFF;

	private static final int FAST_DIVIDE_BY_255(int v){
		return (((v << 8) + v + BYTE) >> 16);
	}

	private static final IllegalArgumentException errorTam=
		new IllegalArgumentException("Las imagenes deben tener las mismas dimensiones");
	private static final IllegalArgumentException errorNull=
		new IllegalArgumentException("Las imagenes de origen no pueden ser null");
	private static final IllegalArgumentException errorModo=
		new IllegalArgumentException("Modo de fusión desconcido");
	private static final IllegalArgumentException errorAlfa=
		new IllegalArgumentException("Valor alfa fuera de rango: { ALFA_OFF, [0.255], ALFA_SRC }");
	private static final IllegalArgumentException errorCanal=
		new IllegalArgumentException("Valor canal fuera de rango:\n\t{ CANAL_ALFA = 3, CANAL_ROJO = 2,  CANAL_VERDE = 1, CANAL_AZUL = 0 }");
	private static final IllegalArgumentException errorRangos=
		new IllegalArgumentException("Argumento fuera del rango [0.0 .. 1.0]");
	private static final IllegalArgumentException errorMinMayorMax=
		new IllegalArgumentException("El valor mínimo no puede ser superio del valor máximo");

	public static Image cambiarCanal(int canal, Image img, int val){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int pixels[];
		switch(canal){
		case CANAL_ALFA:
			pixels = cambiarCanal(A_MASK, A_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_ROJO:
			pixels = cambiarCanal(R_MASK, R_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_VERDE:
			pixels = cambiarCanal(G_MASK, G_MASK_I, canal*8, toPixels(img),val);
			break;
		case CANAL_AZUL:
			pixels = cambiarCanal(B_MASK, B_MASK_I, canal*8, toPixels(img),val);
			break;
		default:
			throw(errorCanal);
		}
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, pixels, 0, ancho));
	}

	private static int[] cambiarCanal(int MASK, int MASK_I, int  desp, int[] img, int val){
		val = (val << desp)& MASK;
		for (int i = 0, t= img.length; i< t; i++)
			img[i] = val | (img[i] & MASK_I);
		return img;
	}

	public static Image cambiarCanal(int canal, Image img, Image nuevoCanal){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int pixels[];
		switch(canal){
		case CANAL_ALFA:
			pixels = cambiarCanal(A_MASK, A_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_ROJO:
			pixels = cambiarCanal(R_MASK, R_MASK_I, canal*8, toPixels(img),  toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_VERDE:
			pixels = cambiarCanal(G_MASK, G_MASK_I, canal*8, toPixels(img),  toGrayPixels(nuevoCanal,ancho,alto));
			break;
		case CANAL_AZUL:
			pixels = cambiarCanal(B_MASK, B_MASK_I, canal*8, toPixels(img), toGrayPixels(nuevoCanal,ancho,alto));
			break;
		default:
			throw(errorCanal);
		}
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, pixels, 0, ancho));
	}

	private static int[] cambiarCanal(int MASK, int MASK_I, int  desp, int[] img1, int[] img2){
		for (int i = 0, t= img1.length; i< t; i++)
			img1[i] = (img2[i] << desp)& MASK | (img1[i] & MASK_I);
		return img1;
	}

	public static int[] toPixels(Image img){
		int ancho = img.getWidth(null);
		int alto  = img.getHeight(null);
		int[] pix = new int[ancho * alto];

		PixelGrabber pgObj = new PixelGrabber(img, 0, 0, ancho, alto, pix, 0, ancho);
		try {
			pgObj.grabPixels();
		}catch( InterruptedException e ) {
			pix = null;
		}
		return pix;
	}

	public static int[] toPixels(Image img, int ancho, int alto){
		if (ancho != img.getWidth(null) || alto != img.getHeight(null)){
			BufferedImage bi = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(img,0,0,ancho,alto,null);
			return toPixels(bi);
		}
		return toPixels(img);
	}

	public static Image toImage(int[] img, int ancho, int alto){
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, img, 0,ancho));
	}

	private static abstract class Fusionador implements Composite, CompositeContext{
		protected final int alfa;

		Fusionador(){
			alfa = ALFA_SRC;
		}

		Fusionador(int alfa){
			if(alfa < ALFA_OFF && alfa > ALFA_SRC)
				throw(errorAlfa);
			this.alfa = alfa;
		}

		public void dispose() {
		}

		public final CompositeContext createContext(
				ColorModel srcColorModel,
				ColorModel dstColorModel,
				RenderingHints hints) {
			return this;
		}
	};

	private static int[] fusionar_AND(int[] src, int[] dstIn, int[] dstOut, int alfa){
		for (int i = 0, t= src.length; i< t; i++)
			dstOut[i] = (dstIn[i] & A_MASK) | (dstIn[i] & src[i] & A_MASK_I);
		return dstOut;
	}

	public static Composite FUSIONAR_AND = new Fusionador(){
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2,i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pixel1 = db1.getElem(i1);
					int pixel2 = db2.getElem(i2);

					int pixel3 = (pixel2 & A_MASK) | (pixel1 & pixel2 & A_MASK_I);
					db3.setElem(i3, pixel3);
				}
		}
	};

	private static int[] fusionar_OR(int[] src, int[] dstIn, int[] dstOut, int alfa){
		for (int i = 0, t= src.length; i< t; i++)
			dstOut[i] = (dstIn[i] & A_MASK) | (dstIn[i] | src[i] & A_MASK_I);
		return dstOut;
	}

	public static Composite FUSIONAR_OR = new Fusionador(){
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2,i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pixel1 = db1.getElem(i1);
					int pixel2 = db2.getElem(i2);

					int pixel3 = (pixel2 & A_MASK) | (pixel1 | pixel2 & A_MASK_I);
					db3.setElem(i3, pixel3);
				}
		}
	};

	private static int[] fusionar_XOR(int[] src, int[] dstIn, int[] dstOut, int alfa){
		for (int i = 0, t= src.length; i< t; i++)
			dstOut[i] = (dstIn[i] & A_MASK) | (dstIn[i] ^ src[i] & A_MASK_I);
		return dstOut;
	}

	public static Composite FUSIONAR_XOR = new Fusionador(){
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2,i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pixel1 = db1.getElem(i1);
					int pixel2 = db2.getElem(i2);

					int pixel3 = (pixel2 & A_MASK) | (pixel1 ^ pixel2 & A_MASK_I);
					db3.setElem(i3, pixel3);
				}
		}
	};

	private static int[] fusionar_SUM(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);
		switch(alfa){
		case ALFA_OFF:
			for (int i = 0, t= dstIn.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int r = (pDst>>16 & BYTE) + (pSrc>>16 & BYTE);
				int g = (pDst>>8 & BYTE) + (pSrc>>8 & BYTE);
				int b = (pDst & BYTE) + (pSrc & BYTE);
				dstOut[i] = (pDst & A_MASK) |
				( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
				( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
				( b > BYTE ? B_MASK : (b & B_MASK) );
			}
			break;
		case ALFA_SRC:
			for (int i = 0, t= dstIn.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = (pSrc>>24 & BYTE);
				int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
				int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
				int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
				dstOut[i] = (pDst & A_MASK) |
				( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
				( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
				( b > BYTE ? B_MASK : (b & B_MASK) );
			}
			break;
		default:
			for (int i = 0, t= dstIn.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
				int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
				int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
				dstOut[i] = (pDst & A_MASK) |
				( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
				( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
				( b > BYTE ? B_MASK : (b & B_MASK) );
			}
		}
		return dstOut;
	}

	public static class Fusionador_Suma extends Fusionador{

		public Fusionador_Suma(){
			super();
		}

		public Fusionador_Suma(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int r = (pDst>>16 & BYTE) + (pSrc>>16 & BYTE);
						int g = (pDst>>8 & BYTE) + (pSrc>>8 & BYTE);
						int b = (pDst & BYTE) + (pSrc & BYTE);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
								( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
								( b > BYTE ? B_MASK : (b & B_MASK) )
						);
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
						int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
						int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
								( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
								( b > BYTE ? B_MASK : (b & B_MASK) )
						);
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int r = (pDst>>16 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
						int g = (pDst>>8 & BYTE) + FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
						int b = (pDst & BYTE) + FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
								( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
								( b > BYTE ? B_MASK : (b & B_MASK) )
						);
					}
			}
		}
	};
	public static Composite FUSIONAR_SUM = new Fusionador_Suma();

	private static int[] fusionar_RES(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);
		switch(alfa){
		case ALFA_OFF:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int r = (pDst>>16 & BYTE) - (pSrc>>16 & BYTE);
				int g = (pDst>>8 & BYTE) - (pSrc>>8 & BYTE);
				int b = (pDst & BYTE) - (pSrc & BYTE);
				dstOut[i] = ( pDst & A_MASK) |
				( r < 0 ? 0 : (r<<16 & R_MASK) ) |
				( g < 0 ? 0 : (g<<8 & G_MASK)) |
				( b < 0 ? 0 : (b & B_MASK) );
			}
			break;
		case ALFA_SRC:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = (pSrc>>24 & BYTE);
				int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
				int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
				int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
				dstOut[i] = ( pDst & A_MASK) |
				( r < 0 ? 0 : (r<<16 & R_MASK) ) |
				( g < 0 ? 0 : (g<<8 & G_MASK)) |
				( b < 0 ? 0 : (b & B_MASK) );
			}
			break;
		default:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
				int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
				int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
				dstOut[i] = ( pDst & A_MASK) |
				( r < 0 ? 0 : (r<<16 & R_MASK) ) |
				( g < 0 ? 0 : (g<<8 & G_MASK)) |
				( b < 0 ? 0 : (b & B_MASK) );
			}
		}
		return dstOut;
	}

	public static class Fusionador_Resta extends Fusionador{

		public Fusionador_Resta(){
			super();
		}

		public Fusionador_Resta(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int r = (pDst>>16 & BYTE) - (pSrc>>16 & BYTE);
						int g = (pDst>>8 & BYTE) - (pSrc>>8 & BYTE);
						int b = (pDst & BYTE) - (pSrc & BYTE);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r < 0 ? 0 : (r<<16 & R_MASK) ) |
								( g < 0 ? 0 : (g<<8 & G_MASK)) |
								( b < 0 ? 0 : (b & B_MASK) )
						);
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
						int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
						int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r < 0 ? 0 : (r<<16 & R_MASK) ) |
								( g < 0 ? 0 : (g<<8 & G_MASK)) |
								( b < 0 ? 0 : (b & B_MASK) )
						);
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int r = (pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a);
						int g = (pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a);
						int b = (pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a);
						db3.setElem(i3,
								( pDst & A_MASK) |
								( r < 0 ? 0 : (r<<16 & R_MASK) ) |
								( g < 0 ? 0 : (g<<8 & G_MASK)) |
								( b < 0 ? 0 : (b & B_MASK) )
						);
					}
			}
		}
	};
	public static Composite FUSIONAR_RES = new Fusionador_Resta();

	private static int[] fusionar_DIF(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);

		switch(alfa){
		case ALFA_OFF:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int r = Math.abs((pDst>>16 & BYTE) - (pSrc>>16 & BYTE));
				int g = Math.abs((pDst>>8 & BYTE) - (pSrc>>8 & BYTE));
				int b = Math.abs((pDst & BYTE) - (pSrc & BYTE));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		case ALFA_SRC:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = (pSrc>>24 & BYTE);
				int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
				int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
				int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		default:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
				int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
				int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
		}
		return dstOut;
	}

	public static class Fusionador_Diferencia extends Fusionador{

		public Fusionador_Diferencia(){
			super();
		}

		public Fusionador_Diferencia(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int r = Math.abs((pDst>>16 & BYTE) - (pSrc>>16 & BYTE));
						int g = Math.abs((pDst>>8 & BYTE) - (pSrc>>8 & BYTE));
						int b = Math.abs((pDst & BYTE) - (pSrc & BYTE));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
						int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
						int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int r = Math.abs((pDst>>16 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a));
						int g = Math.abs((pDst>>8 & BYTE) - FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a));
						int b = Math.abs((pDst & BYTE) - FAST_DIVIDE_BY_255((pSrc & BYTE)*a));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
			}
		}
	};
	public static Composite FUSIONAR_DIF = new Fusionador_Diferencia();

	private static int[] fusionar_MUL(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);

		switch(alfa){
		case ALFA_OFF:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (pSrc>>16 & BYTE));
				int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (pSrc>>8 & BYTE));
				int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (pSrc & BYTE));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		case ALFA_SRC:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				/*
				 la componente alfa se aplica del siguiente modo
				 se obtiene el bit
				 rojo = pixel2 >> 16 & BYTE
				 se fusiona con un valor neutro 255 q representa el 1 en el rango [0..256)
				 rojo = rojo*alfa + in_a
				 */
				int a = (pSrc>>24 & BYTE);
				int in_a = BYTE - a;
				int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
				int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
				int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		default:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int in_a = BYTE - a;
				int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
				int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
				int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
		}
		return dstOut;
	}

	public static class Fusionador_Multiplicacion extends Fusionador{

		public Fusionador_Multiplicacion(){
			super();
		}

		public Fusionador_Multiplicacion(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (pSrc>>16 & BYTE));
						int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (pSrc>>8 & BYTE));
						int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (pSrc & BYTE));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int in_a = BYTE - a;
						int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
						int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
						int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int in_a = BYTE - a;
						int r = FAST_DIVIDE_BY_255((pDst>>16 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a) + in_a));
						int g = FAST_DIVIDE_BY_255((pDst>>8 & BYTE) * (FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a) + in_a));
						int b = FAST_DIVIDE_BY_255((pDst & BYTE) * (FAST_DIVIDE_BY_255((pSrc & BYTE)*a) + in_a));
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
			}
		}
	};
	public static Composite FUSIONAR_MUL = new Fusionador_Multiplicacion();

	private static int[] fusionar_DIV(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);
		switch(alfa){
		case ALFA_OFF:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = ~src[i];
				int pDst = ~dstIn[i];
				int r = FAST_DIVIDE_BY_255(~( (pDst>>16 & BYTE) * (pSrc>>16 & BYTE) ) );
				int g = FAST_DIVIDE_BY_255(~( (pDst>>8 & BYTE) * (pSrc>>8 & BYTE) ) );
				int b = FAST_DIVIDE_BY_255(~( (pDst & BYTE) * (pSrc & BYTE) ) );
				dstOut[i] = (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		case ALFA_SRC:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = ~dstIn[i];
				/*
				 la componente alfa se aplica del siguiente modo
				 se obtiene el bit de color
				 rojo = pixel2 >> 16 & BYTE
				 se le aplica alfa y se quita la mutiplicacion por 255 q produce la operacion
				 rojo = FAST_DIVIDE_BY_255(rojo*alfa)
				 se invierte el color y se le aplica otra vez la mascara para el elimiar
				 los bits sobrantes q tambien se habran invertido
				 rojo = ~rojo & BYTE
				 */
				int a = (pSrc>>24 & BYTE);
				int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
				int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
				int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
				dstOut[i] = (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		default:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = ~dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
				int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
				int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
				dstOut[i] = (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
		}
		return dstOut;
	}

	public static class Fusionador_Division extends Fusionador{

		public Fusionador_Division(){
			super();
		}

		public Fusionador_Division(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
					dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT ||
					dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
				throw new IllegalStateException(
						"Source and destination must store pixels as INT.");
			}
			
			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = ~db1.getElem(i1);
						int pDst = ~db2.getElem(i2);

						int r = FAST_DIVIDE_BY_255(~( (pDst>>16 & BYTE) * (pSrc>>16 & BYTE) ) );
						int g = FAST_DIVIDE_BY_255(~( (pDst>>8 & BYTE) * (pSrc>>8 & BYTE) ) );
						int b = FAST_DIVIDE_BY_255(~( (pDst & BYTE) * (pSrc & BYTE) ) );
						db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = ~db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
						int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
						int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
						db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = ~db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int r = FAST_DIVIDE_BY_255(~((pDst>>16 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>16 & BYTE)*a))&BYTE)));
						int g = FAST_DIVIDE_BY_255(~((pDst>>8 & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc>>8 & BYTE)*a))&BYTE)));
						int b = FAST_DIVIDE_BY_255(~((pDst & BYTE) * ((~FAST_DIVIDE_BY_255((pSrc & BYTE)*a))&BYTE)));
						db3.setElem(i3, (~pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK));
					}
			}
		}
	};
	public static Composite FUSIONAR_DIV = new Fusionador_Division();

	private static int[] fusionar_SUP(int[] src, int[] dstIn, int[] dstOut, int alfa){
		if(alfa < ALFA_OFF && alfa > ALFA_SRC)
			throw(errorAlfa);
		switch(alfa){
		case ALFA_OFF:
			System.arraycopy(src,0,dstOut,0,src.length);
			break;
		case ALFA_SRC:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = (pSrc>>24 & BYTE);
				/*
				 c1*(255-alfa) + c2*alfa = 255*c1 - c1*alfa + c2*alfa = (c2 - c1)*alfa + 255*c1
				 */
				int r = pDst>>16 & BYTE;
				r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
				int g = pDst>>8 & BYTE;
				g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
				int b = pDst & BYTE;
				b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
			break;
		default:
			for (int i = 0, t= src.length; i< t; i++){
				int pSrc = src[i];
				int pDst = dstIn[i];
				int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
				int r = pDst>>16 & BYTE;
				r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
				int g = pDst>>8 & BYTE;
				g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
				int b = pDst & BYTE;
				b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
				dstOut[i] = (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK);
			}
		}
		return dstOut;
	}

	public static class Fusionador_Superposicion extends Fusionador{

		public Fusionador_Superposicion(){
			super();
		}

		public Fusionador_Superposicion(int alfa){
			super(alfa);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			switch(alfa){
			case ALFA_OFF:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i3++){
						int pSrc = db1.getElem(i1);
						db3.setElem(i3, pSrc );
					}
				break;
			case ALFA_SRC:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = (pSrc>>24 & BYTE);
						int r = pDst>>16 & BYTE;
						r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
						int g = pDst>>8 & BYTE;
						g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
						int b = pDst & BYTE;
						b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
				break;
			default:
				for (int y = 0; y < dstIn.getHeight(); y++, i1 += sls1, i2 += sls2, i3 += sls3)
					for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
						int pSrc = db1.getElem(i1);
						int pDst = db2.getElem(i2);
						int a = FAST_DIVIDE_BY_255((pSrc>>24 & BYTE) *alfa);
						int r = pDst>>16 & BYTE;
						r += FAST_DIVIDE_BY_255(((pSrc>>16 & BYTE)-r)*a);
						int g = pDst>>8 & BYTE;
						g += FAST_DIVIDE_BY_255(((pSrc>>8 & BYTE)-g)*a);
						int b = pDst & BYTE;
						b += FAST_DIVIDE_BY_255(((pSrc & BYTE)-b)*a);
						db3.setElem(i3, (pDst & A_MASK) | (r<<16 & R_MASK) | (g<<8 & G_MASK) | (b & B_MASK)	);
					}
			}
		}
	};
	public static Composite FUSIONAR_SUP = new Fusionador_Superposicion();
	
	
	public static Composite ALPHA_CHANNEL = new Fusionador(){
		/*
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			Rectangle srcRect = src.getBounds();
			Rectangle dstInRect = dstIn.getBounds();
			Rectangle dstOutRect = dstOut.getBounds();
			int x = 0, y = 0;
			int w = Math.min(Math.min(srcRect.width, dstOutRect.width), dstInRect.width);
			int h = Math.min(Math.min(srcRect.height, dstOutRect.height), dstInRect.height);
			Object srcPix = null, dstPix = null;
			for (y = 0; y < h; y++)
				for (x = 0; x < w; x++) {
					srcPix = src.getDataElements(x + srcRect.x, y + srcRect.y, srcPix);
					dstPix = dstIn.getDataElements(x + dstInRect.x, y + dstInRect.y, dstPix);
					int sp = srcColorModel.getRGB(srcPix);
					int dp = dstColorModel.getRGB(dstPix);
					int rp = add(sp,dp);
					dstOut.setDataElements(x + dstOutRect.x, y + dstOutRect.y, dstColorModel.getDataElements(rp, null));
				}
		}
		/*/
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2,i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pSrc = db1.getElem(i1);
					int pDst = db2.getElem(i2);

					int pixel3 = (pSrc & A_MASK) | (pDst & A_MASK_I);
					db3.setElem(i3, pixel3);
				}
		}
		//*/
	};
	
	public static Composite ALPHA_CHANNEL_PRE = new Fusionador(){
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2, i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pSrc = db1.getElem(i1);
					int pDst = db2.getElem(i2);
					int a = (pSrc>>24 & BYTE);
					int r = FAST_DIVIDE_BY_255( (pDst>>16 & BYTE) * a );
					int g = FAST_DIVIDE_BY_255( (pDst>>8 & BYTE) * a );
					int b = FAST_DIVIDE_BY_255( (pDst & BYTE) * a );
					db3.setElem(i3,
							( pSrc & A_MASK) |
							( r > BYTE ? R_MASK : (r<<16 & R_MASK) ) |
							( g > BYTE ? G_MASK : (g<<8 & G_MASK) ) |
							( b > BYTE ? B_MASK : (b & B_MASK) )
					);
				}
		}
	};

	public static Composite ALPHA_MASK = new Fusionador(){
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int sls1 = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
			int i1 = (-src.getSampleModelTranslateY() * sls1) - src.getSampleModelTranslateX();
			sls1 -= src.getWidth();
			DataBuffer db1 = src.getDataBuffer();

			int sls2 = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
			int i2 = (-dstIn.getSampleModelTranslateY() * sls2) - dstIn.getSampleModelTranslateX();
			sls2 -= dstIn.getWidth();
			DataBuffer db2 = dstIn.getDataBuffer();

			int sls3 = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
			int i3 = (-dstOut.getSampleModelTranslateY() * sls3) - dstOut.getSampleModelTranslateX();
			sls3 -= dstOut.getWidth();
			DataBuffer db3 = dstOut.getDataBuffer();

			for(int y = 0; y < dstIn.getHeight(); y++, i1+= sls1, i2+= sls2,i3+= sls3)
				for(int x =0; x < dstIn.getWidth(); x++, i1++, i2++, i3++){
					int pSrc = db1.getElem(i1);
					int pDst = db2.getElem(i2);

					int pixel3 = ( ( FAST_DIVIDE_BY_255( (pDst>>24 & BYTE) * (pSrc>>24 & BYTE) ) << 24 )& A_MASK) | (pDst & A_MASK_I);
					db3.setElem(i3, pixel3);
				}
		}
	};

	public static Image fusionar(Image src, Image dstIn, int mode, int alfaMode){
		BufferedImage dstOut = new BufferedImage(dstIn.getWidth(null), dstIn.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dstOut.createGraphics();
		g.drawImage(dstIn,0,0,dstIn.getWidth(null), dstIn.getHeight(null), null);
		switch(mode){
		case MODO_AND:
			g.setComposite(FUSIONAR_AND);
			break;
		case MODO_OR :
			g.setComposite(FUSIONAR_OR);
			break;
		case MODO_XOR:
			g.setComposite(FUSIONAR_XOR);
			break;
		case MODO_SUM:
			g.setComposite(new Fusionador_Suma(alfaMode));
			break;
		case MODO_RES:
			g.setComposite(new Fusionador_Resta(alfaMode));
			break;
		case MODO_DIF:
			g.setComposite(new Fusionador_Diferencia(alfaMode));
			break;
		case MODO_MUL:
			g.setComposite(new Fusionador_Multiplicacion(alfaMode));
			break;
		case MODO_DIV:
			g.setComposite(new Fusionador_Division(alfaMode));
			break;
		case MODO_SUP:
			g.setComposite(new Fusionador_Superposicion(alfaMode));
			break;
		default: throw(errorModo);
		}
		g.drawImage(src,0,0,dstIn.getWidth(null), dstIn.getHeight(null), null);
		g.dispose();
		return dstOut;

//		return Toolkit.getDefaultToolkit().createImage(
//				new MemoryImageSource(ancho, alto, fusionar(
//						toPixels(img1, ancho, alto),
//						toPixels(img2, ancho, alto),
//						null, mode, alfaMode),0,ancho));
	}

	public static Image fusionar(int[] img1, int[] img2, int ancho, int alto, int mode, int alfaMode){
		return Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(ancho, alto, fusionar(img1, img2, null, mode, alfaMode),0,ancho));
	}

	public static int[] fusionar(int[] img1, int[] img2, int[] img3, int mode, int alfaMode){
		int tam;
		try{
			tam = img1.length;
			if(img2.length != tam)
				throw(errorTam);
		}catch(NullPointerException e){
			throw(errorNull);
		}
		if(img3 == null || img3.length != tam)
			img3 = new int[tam];
		switch(mode){
		case MODO_AND: return fusionar_AND(img1,img2,img3,alfaMode);
		case MODO_OR : return fusionar_OR(img1,img2,img3,alfaMode);
		case MODO_XOR: return fusionar_XOR(img1,img2,img3,alfaMode);
		case MODO_SUM: return fusionar_SUM(img1,img2,img3,alfaMode);
		case MODO_RES: return fusionar_RES(img1,img2,img3,alfaMode);
		case MODO_DIF: return fusionar_DIF(img1,img2,img3,alfaMode);
		case MODO_MUL: return fusionar_MUL(img1,img2,img3,alfaMode);
		case MODO_DIV: return fusionar_DIV(img1,img2,img3,alfaMode);
		case MODO_SUP: return fusionar_SUP(img1,img2,img3,alfaMode);
		default: throw(errorModo);
		}
	}

	// pre-computations for conversion from RGB to Luminance
	static int[][] luminanceData = new int[3][256];
	static{
		for (int i = 0; i < 256; i++)
			luminanceData[0][i] = (int)(0.299f*i +.5f);
		for (int i = 0; i < 256; i++)
			luminanceData[1][i] = (int)(0.587f*i +.5f);
		for (int i = 0; i < 256; i++)
			luminanceData[2][i] = (int)(0.114f*i +.5f);
	}

	private static byte rgb2luminance(int pixel){
		int r = pixel >> 16 & BYTE;
		int g = pixel >> 8  & BYTE;
		int b = pixel       & BYTE;

		return  (byte)(luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b]);
	}
	
	private static byte rgb2luminance(byte r, byte g, byte b){
		return  (byte)(luminanceData[0][r & BYTE] + luminanceData[1][g & BYTE ] + luminanceData[2][b & BYTE]);
	}

	private static void grayPass(int[] pixels) {
		int r, g, b;
		int luminance;

		for (int index = 0,len = pixels.length; index<len; index++){
			int pixel = pixels[index];

			r = pixel >> 16 & BYTE;
			g = pixel >> 8  & BYTE;
			b = pixel       & BYTE;

			// compute the luminance
			luminance = luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b];

			pixels[index] = (pixel & A_MASK) | (luminance<<16) | (luminance<<8) | luminance;
		}
	}

	public static int[] toGrayPixels(Image img){
		int pixels[] = toPixels(img);
		grayPass(pixels);
		return pixels;
	}

	public static int[] toGrayPixels(Image img, int ancho, int alto){
		int pixels[] = toPixels(img,ancho,alto);
		grayPass(pixels);
		return pixels;
	}

	private static void brightPass(int[] pixels, int min, int max) {
		int r, g, b;
		int luminance;

		for (int index = 0,len = pixels.length; index<len; index++){
			int pixel = pixels[index];

			r = pixel >> 16 & BYTE;
			g = pixel >> 8  & BYTE;
			b = pixel       & BYTE;

			// compute the luminance
			luminance = luminanceData[0][r] + luminanceData[1][g] + luminanceData[2][b];

			if(luminance < min)
				pixel &= A_MASK;
			else if(luminance > max)
				pixel |= A_MASK_I;

			pixels[index] = pixel;
		}
	}

	public static Image brightPass(Image image, float min, float max){
		if(min < 0.0f || min > 1.0f)
			throw errorRangos;
		if(max < 0.0f || max > 1.0f)
			throw errorRangos;
		if(min > max)
			throw errorMinMayorMax;

		int pixels[] = toPixels(image);
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		brightPass(pixels, (int)(min * 255),(int)(max * 255));
		return toImage(pixels,w,h);
	}

	public static Buffer toBuffer(BufferedImage img, boolean flipY){
		Raster     rt = img.getRaster();
		DataBuffer db = rt.getDataBuffer();
		int type = db.getDataType();
		
//		System.out.println(rt.getNumBands());
//		System.out.println(rt.getNumDataElements());
//		System.out.println(db.getNumBanks());
		
		Buffer buff = null;
		switch(type){
		case DataBuffer.TYPE_BYTE:
			ByteBuffer bb = ByteBuffer.allocateDirect(db.getSize());
			DataBufferByte dbb = (DataBufferByte)db;

			if(!flipY){
//				System.out.println("TYPE_BYTE");
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					bb.put(dbb.getData(i));
			}else if(db.getNumBanks() == 1){
//				System.out.println("TYPE_BYTE flip");
				int bytesWidth = rt.getWidth()*rt.getNumDataElements();
				for(int i = (rt.getHeight()-1)*bytesWidth; i >= 0; i-= bytesWidth)
					bb.put(dbb.getData(), i, bytesWidth);
			}else{
				throw new UnsupportedOperationException();
			}
			buff = bb;
			break;
		case DataBuffer.TYPE_INT:
			IntBuffer ib = ByteBuffer.allocateDirect(db.getSize()*4).order(ByteOrder.nativeOrder()).asIntBuffer();
			DataBufferInt dbi = (DataBufferInt)db;
			if(!flipY){
//				System.out.println("TYPE_INT");
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					ib.put(dbi.getData(i));
			}else if(db.getNumBanks() == 1){
//				System.out.println("TYPE_INT flip");
				int bytesWidth = rt.getWidth()*rt.getNumDataElements();
				for(int i = (rt.getHeight()-1)*bytesWidth; i >= 0; i-= bytesWidth)
					ib.put(dbi.getData(), i, bytesWidth);
			}else{
				throw new UnsupportedOperationException();
			}
			buff = ib;
			break;
		}
		buff.rewind();
		return buff;
	}

	public static ByteBuffer toByteBuffer(BufferedImage img, boolean flipY){
		Raster     rt = img.getRaster();
		DataBuffer db = rt.getDataBuffer();
		int type = db.getDataType();

//		System.out.println(rt.getNumBands());
//		System.out.println(rt.getNumDataElements());
//		System.out.println(db.getNumBanks());
		
		ByteBuffer bb = null;

		switch(type){

		case DataBuffer.TYPE_BYTE:
			if(rt.getNumBands() == 1){
				bb = ByteBuffer.allocateDirect(db.getSize());
				DataBufferByte dbb = (DataBufferByte)db;
				if(!flipY){
//					System.out.println("TYPE_BYTE TO TYPE_BYTE");
					for(int i = 0, len = db.getNumBanks(); i< len; i++)
						bb.put(dbb.getData(i));
				}else if(db.getNumBanks() == 1){
//					System.out.println("TYPE_BYTE TO TYPE_BYTE Flip");
					int bytesWidth = rt.getWidth()*rt.getNumDataElements();
					for(int i = (rt.getHeight()-1)*bytesWidth; i >= 0; i-= bytesWidth)
						bb.put(dbb.getData(), i, bytesWidth);
				}else{
					throw new UnsupportedOperationException();
				}
			}else if(rt.getNumDataElements() == 3){
				bb = ByteBuffer.allocateDirect(db.getSize()/3);
				DataBufferByte dbb = (DataBufferByte)db;
				if(!flipY){
//					System.out.println("3 TYPE_BYTE TO TYPE_BYTE");
					for(int i = 0, len = db.getNumBanks(); i< len; i++){
						byte dataBank[] = dbb.getData(i);
						for(int j = 0; j < dataBank.length; )
							bb.put(rgb2luminance(dataBank[j++],dataBank[j++],dataBank[j++]));
					}
				}else if(db.getNumBanks() == 1){
//					System.out.println("3 TYPE_BYTE TO TYPE_BYTE flip");
					int bytesWidth = rt.getWidth()*3;
					for(int i = (rt.getHeight()-1)*bytesWidth; i >= 0; i-= bytesWidth ){
						byte dataBank[] = dbb.getData();
						for(int j=i, len = i + bytesWidth; j < len; )
							bb.put(rgb2luminance(dataBank[j++],dataBank[j++],dataBank[j++]));
					}
				}else{
					throw new UnsupportedOperationException();
				}
			}else if(rt.getNumBands() == 4){
				bb = ByteBuffer.allocateDirect(db.getSize()/4);
				DataBufferByte dbb = (DataBufferByte)db;
				if(!flipY){
//					System.out.println("4 TYPE_BYTE TO TYPE_BYTE");
					for(int i = 0, len = db.getNumBanks(); i< len; i++){
						byte dataBank[] = dbb.getData(i);
						for(int j = 0; j < dataBank.length; j++)
							bb.put(rgb2luminance(dataBank[j++],dataBank[j++],dataBank[j++]));
					}
				}else if(db.getNumBanks() == 1){
//					System.out.println("4 TYPE_BYTE TO TYPE_BYTE flip");
					int bytesWidth = rt.getWidth()*4;
					for(int i = (rt.getHeight()-1)*bytesWidth; i >= 0; i-= bytesWidth ){
						byte dataBank[] = dbb.getData();
						for(int j=i, len = i + bytesWidth; j < len; j++)
							bb.put(rgb2luminance(dataBank[j++],dataBank[j++],dataBank[j++]));
					}
				}else{
					throw new UnsupportedOperationException();
				}
			}
			break;
		case DataBuffer.TYPE_INT:
			bb = ByteBuffer.allocateDirect(db.getSize());
			DataBufferInt dbi = (DataBufferInt)db;
			if(!flipY){
//				System.out.println("TYPE_INT TO TYPE_BYTE");
				for(int i = 0, len = db.getNumBanks(); i< len; i++)
					for(int rgb: dbi.getData(i))
						bb.put(rgb2luminance(rgb));
			}else if(db.getNumBanks() == 1){
//				System.out.println("TYPE_INT TO TYPE_BYTE flip");
				for(int i = (rt.getHeight()-1)*rt.getWidth(); i >= 0; i-= rt.getWidth()){
					int dataBank[] = dbi.getData();
					for(int j=i, len = i + rt.getWidth(); j < len; j++)
						bb.put(rgb2luminance(dataBank[j]));
				}
			}else{
				throw new UnsupportedOperationException();
			}
			break;
		}
		bb.rewind();
		return bb;
	}

	public static BufferedImage toBufferedImage(Image img){
		if(img instanceof BufferedImage)
			return (BufferedImage) img;
		return toBufferedImage(img, img.getWidth(null), img.getHeight(null));
	}
	
	public static BufferedImage toBufferedImage(Image img, int w, int h){
		BufferedImage bi;
		try{
			// Creando una imagen compatible con el sistema grafico en uso
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();

			bi = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		} catch (HeadlessException e2) {
			// No se ha podido obtener el sistema grafico en uso
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D g = bi.createGraphics();
		g.drawImage(img,0,0,w,h,null);
		return bi;
	}
	
	public static BufferedImage toBufferedImage(Image img, int type){
		BufferedImage bi = null;
		if(img instanceof BufferedImage){
			bi = (BufferedImage)img;
			if(bi.getType() == type)
				return bi;
		}
		bi = new BufferedImage(img.getWidth(null), img.getHeight(null), type);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img,0,0,null);
		g.dispose();
		return bi;
	}
	
	public static BufferedImage toBufferedImage(Image img, int width, int height, int type){
		BufferedImage bi = null;
		if(img instanceof BufferedImage){
			bi = (BufferedImage)img;
			if(bi.getWidth() == width && bi.getHeight() == height && bi.getType() == type)
				return bi;
		}
		bi = new BufferedImage(width, height, type);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage( img, 0, 0, width, height, null );
		g.dispose();
		return bi;
	}
	
	public static TexturePaint toTexturePaint(Image img){
		BufferedImage bi = toBufferedImage(img);
		Rectangle r = new Rectangle(0,0,bi.getWidth(),bi.getHeight());
		return new TexturePaint(bi,r);
	}

	private static int mediaTrackerID;
	private final static Component component = new Component() {
		private static final long serialVersionUID = 1L;
	};
	private final static MediaTracker tracker = new MediaTracker(component);

	public static Image cargarImagen(String localizacion){
		Image image = Toolkit.getDefaultToolkit().getImage(localizacion);
		if (image != null)
			try {
				loadImage(image);
			} catch (InterruptedException errorCargandoImagen) {
				image = null;
			}
		return image;
	}

	public static Image cargarImagen(java.net.URL localizacion){
		Image image = Toolkit.getDefaultToolkit().getImage(localizacion);
		if (image != null)
			try {
				loadImage(image);
			} catch (InterruptedException errorCargandoImagen) {
				image = null;
			}
		return image;
	}

	private static void loadImage(Image image) throws InterruptedException{
		synchronized(tracker) {
			int id = getNextID();
			tracker.addImage(image, id);
//			tracker.waitForID(id, 0);
			tracker.waitForAll();
			tracker.removeImage(image, id);
		}
	}

	private static int getNextID() {
		synchronized(tracker) {
			return ++mediaTrackerID;
		}
	}
	
	public static BufferedImage cargarToBufferedImage(String filename){
		BufferedImage img = null;
		try{
			img =  ImageIO.read( new File(filename) );
		}catch( IOException ignorada ){
		}
		return img;
	}
	
	public static BufferedImage cargarToBufferedImage(java.net.URL localizacion){
		BufferedImage img = null;
		try{
			img =  ImageIO.read( localizacion );
		}catch( IOException ignorada ){
		}
		return img;
	}

	/*
	public static Image getRecorte(Image img, int x, int y, int ancho, int alto){
		return Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource (img.getSource(),
					new CropImageFilter (x, y, ancho, alto)));
	}*/

	public static Image getRecorte(Image img, int x, int y, int ancho, int alto){
		int tipo;
		if (img instanceof BufferedImage)
			tipo = ((BufferedImage)img).getType();
		else
			tipo = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bi = new BufferedImage(ancho, alto, tipo);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img,0,0,ancho,alto,x,y,x+ancho,y+alto,null);
		return bi;
	}
}