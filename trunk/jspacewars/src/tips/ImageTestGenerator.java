package tips;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.*;

public class ImageTestGenerator {

	private static class ImageTest extends BufferedImage{
		
		final static Color[] rgbColors = new Color[]{
			new Color(   0,   0,   0),
			new Color(   0,   0, 255),
			new Color(   0, 255,   0),
			new Color(   0, 255, 255),

			new Color( 255,   0,   0),
			new Color( 255,   0, 255),
			new Color( 255, 255,   0),
			new Color( 255, 255, 255),
		};

		private final static float[] hueFractions = new float[]{ 0.0f/6,  1.0f/6,  2.0f/6,  3.0f/6, 4.0f/6, 5.0f/6 , 6.0f/6};
		private final static Color[] hueColors = new Color[]{
			new Color( Color.HSBtoRGB( 0.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 1.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 2.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 3.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 4.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 5.0f/6, 1.0f, 1.0f)),
			new Color( Color.HSBtoRGB( 6.0f/6, 1.0f, 1.0f))
		};

		private static Color darker(Color color){
			return new Color( color.getRed()/2, color.getGreen()/2, color.getBlue()/2 );
		}

		private static Color brighter(Color color){
			return new Color( color.getRed()/2 + 128, color.getGreen()/2 + 128, color.getBlue()/2 + 128 );
		}

		private ImageTest(int w, int h){
			super(w,h, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g2d = this.createGraphics();
			g2d.setBackground(Color.BLACK);
			g2d.clearRect(0, 0, w, h);

			g2d.setColor(Color.WHITE);
			String str = String.format("Test ( %d x %d )", w, h);
			g2d.setFont( g2d.getFont().deriveFont(17.0f));

			int strWidth = g2d.getFontMetrics().stringWidth(str);
			int strHeight = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();

			g2d.drawString(str, (w - strWidth)/ 2, (h/4 - strHeight )/2 + strHeight );

			for(int i = h/4; i < 3*h/8-1; i+= 2)
				g2d.drawLine(0, i, w, i);

			for(int i = 2; i < w-1; i+= 2)
				g2d.drawLine(i, 3*h/8, i, h/2);

			g2d.setPaint(new GradientPaint( 0, 0, Color.BLACK, w, 0, Color.RED));
			g2d.fillRect( 0,  8*h/16, w,  9*h/16 -  8*h/16);
			g2d.setPaint(new GradientPaint( 0, 0, Color.BLACK, w, 0, Color.GREEN));
			g2d.fillRect( 0,  9*h/16, w, 10*h/16 -  9*h/16);
			g2d.setPaint(new GradientPaint( 0, 0, Color.BLACK, w, 0, Color.BLUE));
			g2d.fillRect( 0, 10*h/16, w, 11*h/16 - 10*h/16);
			g2d.setPaint( new LinearGradientPaint( 0, 0, w, 0, hueFractions, hueColors ) );
			g2d.fillRect( 0, 11*h/16, w, 12*h/16 - 11*h/16);

			for(int i = 0; i < 8; i++ ){
				g2d.setColor( darker(rgbColors[i]) );
				g2d.fillRect( (i*w)/8, 12*h/16,((i+1)*w)/8 - (i*w)/8, 13*h/16 - 12*h/16 );
			}

			for(int i = 0; i < 8; i++ ){
				g2d.setColor( rgbColors[i] );
				g2d.fillRect( (i*w)/8, 13*h/16,((i+1)*w)/8 - (i*w)/8, 14*h/16 - 13*h/16 );
			}

			for(int i = 0; i < 8; i++ ){
				g2d.setColor( brighter(rgbColors[i]) );
				g2d.fillRect( (i*w)/8, 14*h/16,((i+1)*w)/8 - (i*w)/8, 15*h/16 - 14*h/16 );
			}

			g2d.setPaint(new GradientPaint( 0, 0, Color.WHITE, w, 0, Color.BLACK));
			g2d.fillRect( 0, 15*h/16, w, 16*h/16 - 15*h/16);

			g2d.setColor(Color.MAGENTA);
			g2d.drawRect(0, 0, w-1, h-1);
		}
	}

	private static void export( final int w, final int h, final String formato, final String rutaFichero) throws IOException{
		ImageTest imgTest = new ImageTest( w, h );
		if(formato.equalsIgnoreCase("jpg")){
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File( rutaFichero )));
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(imgTest);
			param.setQuality( 0.85f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(imgTest);
			out.close();
		}else{
			ImageIO.write( imgTest, formato, new File( rutaFichero ));
		}
	}

	public static void main(String args[]){
		try{
			for( int i = 20; i < 33; i++ )
				export( i*16, i*9,"jpg", String.format("test/imgTest%03d.jpg", i));
			export( 480, 240,"png", "test/imgTest480x240.png");
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
}
