/* 
 * ColorRamp.java
 * 
 * Copyright (c) 2009 Samuel Alfaro <samuelalfaro at gmail.com>. All rights reserved.
 * 
 * This file is part of tips.
 * 
 * tips is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * tips is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tips.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Color;

interface ColorRamp{
	
	static abstract class AbstractColorRamp implements ColorRamp{
		public final Color compute(double c){
			return new Color(computeToIntRGB(c));
		}
	}
	
	static ColorRamp GreyScale = new AbstractColorRamp(){
		public int computeToIntRGB(double c){
			int v = Math.min(Math.max(0x00, (int)( c * 0x100 + 0.5 )), 0xFF);
			return (v<<16 & 0x00FF0000) | (v<<8 & 0x0000FF00) | (v & 0x000000FF);
		}
	};
	
	static ColorRamp Multicolor = new AbstractColorRamp(){
		/* (non-Javadoc)
		 * @see ColorRamp#computeToIntRGB(double)
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
	};
	
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
		
		/* (non-Javadoc)
		 * @see ColorRamp#computeToIntRGB(double)
		 */
		@Override
		public int computeToIntRGB(double c) {
			int r = Math.min(Math.max(0x00, (int)( mR * c + nR )), 0xFF);
			int g = Math.min(Math.max(0x00, (int)( mG * c + nG )), 0xFF);
			int b = Math.min(Math.max(0x00, (int)( mB * c + nB )), 0xFF);
			return (r<<16 & 0x00FF0000) | (g<<8 & 0x0000FF00) | (b & 0x000000FF);
		}
	}
	
	static ColorRamp BlackRedYellowWhite =   new RGBColorRamp( 3,  0, 3, -1, 3, -2 );
	static ColorRamp BlackRedMagentaWhite =  new RGBColorRamp( 3,  0, 3, -2, 3, -1 );
	static ColorRamp BlackGreenYellowWhite = new RGBColorRamp( 3, -1, 3,  0, 3, -2 );
	static ColorRamp BlackGreenCyanWhite =   new RGBColorRamp( 3, -2, 3,  0, 3, -1 );
	static ColorRamp BlackBlueMagentaWhite = new RGBColorRamp( 3, -1, 3, -2, 3,  0 );
	static ColorRamp BlackBlueCyanWhite =    new RGBColorRamp( 3, -2, 3, -1, 3,  0 );
	
	public Color compute(double c);
	public int computeToIntRGB(double c);
}