/* 
 * TextRenderers.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.fenggui.binding.render.ImageFont;
import org.fenggui.binding.render.text.DirectTextRenderer;
import org.fenggui.binding.render.text.ITextRenderer;

class TextRenderers{
	
	static ITextRenderer renderer1;
	static ITextRenderer renderer2;
	static ITextRenderer renderer3;
	
	static void init(){
		try{
			renderer1 = new DirectTextRenderer( new ImageFont( "font1.png", "font.xml" ) );
			renderer2 = new DirectTextRenderer( new ImageFont( "font2.png", "font.xml" ) );
			renderer3 = new DirectTextRenderer( new ImageFont( "font3.png", "font.xml" ) );
		}catch( FileNotFoundException e ){
		}catch( IOException e ){
		}
	}
}
