/* 
 * ColorRamp.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.tools.textureGenerator;

import java.awt.Color;

/**
 * 
 */
public interface ColorRamp{
	
	/**
	 *
	 */
	static abstract class AbstractColorRamp implements ColorRamp{
		/**
		 * @inheritDoc
		 */
		@Override
		public final Color compute(double c){
			return new Color(computeToIntRGB(c));
		}
	}
	
	/**
	 *
	 */
	static class RGBColorRamp extends AbstractColorRamp{
		
		private final double mR, nR;
		private final double mG, nG;
		private final double mB, nB;
		
		public RGBColorRamp( double mR, double nR, double mG, double nG, double mB, double nB){
			this.mR = mR * 0x100;
			this.nR = nR * 0x100 + 0.5;
			this.mG = mG * 0x100;
			this.nG = nG * 0x100 + 0.5;
			this.mB = mB * 0x100;
			this.nB = nB * 0x100 + 0.5;
		}
		
		/**
		 * @inheritDoc
		 */
		@Override
		public int computeToIntRGB(double c) {
			int r = Math.min(Math.max(0x00, (int)( mR * c + nR )), 0xFF);
			int g = Math.min(Math.max(0x00, (int)( mG * c + nG )), 0xFF);
			int b = Math.min(Math.max(0x00, (int)( mB * c + nB )), 0xFF);
			return (r<<16 & 0x00FF0000) | (g<<8 & 0x0000FF00) | (b & 0x000000FF);
		}
	}
	
	/**
	 * 
	 */
	public enum Predefinidas implements ColorRamp{
		/**
		 * 
		 */
		GreyScale( new AbstractColorRamp(){
			/**
			 * @inheritDoc
			 */
			@Override
			public int computeToIntRGB(double c){
				int v = Math.min(Math.max(0x00, (int)( c * 0x100 + 0.5 )), 0xFF);
				return (v<<16 & 0x00FF0000) | (v<<8 & 0x0000FF00) | (v & 0x000000FF);
			}
		}),
		/**
		 * 
		 */
		Multicolor( new AbstractColorRamp(){
			/**
			 * @inheritDoc
			 */
			@Override
			public int computeToIntRGB(double c){
				int r = (int)( c < 0.5 ? 512 * c : 512 * (1.0 - c))
				, g, b;
				
				if(r == 256)
					r = 255;
			
				if( c < 0.25 )
					g = (int)(512*(0.25 - c));
				else if( c < 0.75 )
					g = (int)(512*(c - 0.25));
				else
					g = (int)(512*(1.25 - c));
				if(g == 256)
					g = 255;
			
				b = 255 - r;
				
				return (r<<16 & 0x00FF0000) | (g<<8 & 0x0000FF00) | (b & 0x000000FF);
			}
		}),
		/**
		 * 
		 */
		BlackRedYellowWhite   (new RGBColorRamp( 3,  0, 3, -1, 3, -2 )),
		/**
		 * 
		 */
		BlackRedMagentaWhite  (new RGBColorRamp( 3,  0, 3, -2, 3, -1 )),
		/**
		 * 
		 */
		BlackGreenYellowWhite (new RGBColorRamp( 3, -1, 3,  0, 3, -2 )),
		/**
		 * 
		 */
		BlackGreenCyanWhite   (new RGBColorRamp( 3, -2, 3,  0, 3, -1 )),
		/**
		 * 
		 */
		BlackBlueMagentaWhite (new RGBColorRamp( 3, -1, 3, -2, 3,  0 )),
		/**
		 * 
		 */
		BlackBlueCyanWhite    (new RGBColorRamp( 3, -2, 3, -1, 3,  0 ));
		
		final ColorRamp colorRamp;
		
		Predefinidas(ColorRamp encapsulada){
			this.colorRamp = encapsulada;
		}
		
		/**
		 * @inheritDoc
		 */
		@Override
		public final Color compute(double c){
			return colorRamp.compute(c);
		}
		
		/**
		 * @inheritDoc
		 */
		@Override
		public final int computeToIntRGB(double c){
			return colorRamp.computeToIntRGB(c);
		}
	}
	
	/**
	 * @param c
	 * @return
	 */
	public Color compute(double c);
	/**
	 * @param c
	 * @return
	 */
	public int computeToIntRGB(double c);
}
