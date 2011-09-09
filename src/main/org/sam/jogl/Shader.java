/* 
 * Shader.java
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
package org.sam.jogl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Point4i;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple2i;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4f;
import javax.vecmath.Tuple4i;

import com.jogamp.common.nio.Buffers;

/**
 * Clase que encapsula los datos necesarios en el manejo de <i>shaders</i> y proporciona
 * los métodos para facilitar su empleo.
 */
public class Shader{

	private static abstract class Atributo <T>{
		protected final int id;
		protected T value;

		Atributo( int id ){
			this.id = id;
		}

		public abstract void setValue( Object value );

		public abstract void Uniform( GL2 gl );

		static Atributo<?> getAtributo( int id, Object value ){
			if( value instanceof Integer )
				return new AtributoEntero1( id, value );
			if( Tuple2i.class.isAssignableFrom( value.getClass() ) )
				return new AtributoEntero2( id, value );
			if( Tuple3i.class.isAssignableFrom( value.getClass() ) )
				return new AtributoEntero3( id, value );
			if( Tuple4i.class.isAssignableFrom( value.getClass() ) )
				return new AtributoEntero4( id, value );
			if( value instanceof Integer[] || value instanceof int[] ){
				switch( Array.getLength( value ) ){
				case 2:
					return new AtributoEntero2( id, value );
				case 3:
					return new AtributoEntero3( id, value );
				case 4:
					return new AtributoEntero4( id, value );
				}
			}
			if( value instanceof Float )
				return new AtributoFloat1( id, value );
			if( Tuple2f.class.isAssignableFrom( value.getClass() ) )
				return new AtributoFloat2( id, value );
			if( Tuple3f.class.isAssignableFrom( value.getClass() ) )
				return new AtributoFloat3( id, value );
			if( Tuple4f.class.isAssignableFrom( value.getClass() ) )
				return new AtributoFloat4( id, value );
			if( value instanceof Float[] || value instanceof float[] ){
				switch( Array.getLength( value ) ){
				case 2:
					return new AtributoFloat2( id, value );
				case 3:
					return new AtributoFloat3( id, value );
				case 4:
					return new AtributoFloat4( id, value );
				}
			}
			// TODO Resto de tipos de atributos
			throw new IllegalArgumentException( "Tipo no soportado" );
		}
	}

	private static class AtributoEntero1 extends Atributo<Integer>{

		AtributoEntero1( int id, Object value ){
			super( id );
			setValue( value );
		}

		public void setValue( Object value ){
			this.value = (Integer)value;
		}

		public void Uniform( GL2 gl ){
			gl.glUniform1i( id, value );
		}
	}

	private static class AtributoEntero2 extends Atributo<Tuple2i>{

		AtributoEntero2( int id, Object value ){
			super( id );
			this.value = new Point2i();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple2i.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple2i)value );
			else if( value instanceof int[] )
				this.value.set( (int[])value );
			else if( value instanceof Integer[] ){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform2i( id, value.x, value.y );
		}
	}

	private static class AtributoEntero3 extends Atributo<Tuple3i>{

		AtributoEntero3( int id, Object value ){
			super( id );
			this.value = new Point3i();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple3i.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple3i)value );
			else if( value instanceof int[] )
				this.value.set( (int[])value );
			else if( value instanceof Integer[] ){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1], v[2] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform3i( id, value.x, value.y, value.z );
		}
	}

	private static class AtributoEntero4 extends Atributo<Tuple4i>{

		AtributoEntero4( int id, Object value ){
			super( id );
			this.value = new Point4i();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple4i.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple4i)value );
			else if( value instanceof int[] )
				this.value.set( (int[])value );
			else if( value instanceof Integer[] ){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1], v[2], v[3] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform4i( id, value.x, value.y, value.z, value.w );
		}
	}

	private static class AtributoFloat1 extends Atributo<Float>{

		AtributoFloat1( int id, Object value ){
			super( id );
			setValue( value );
		}

		public void setValue( Object value ){
			this.value = (Float)value;
		}

		public void Uniform( GL2 gl ){
			gl.glUniform1f( id, value );
		}
	}

	private static class AtributoFloat2 extends Atributo<Tuple2f>{

		AtributoFloat2( int id, Object value ){
			super( id );
			this.value = new Point2f();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple2f.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple2f)value );
			else if( value instanceof float[] )
				this.value.set( (float[])value );
			else if( value instanceof Float[] ){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform2f( id, value.x, value.y );
		}
	}

	private static class AtributoFloat3 extends Atributo<Tuple3f>{

		AtributoFloat3( int id, Object value ){
			super( id );
			this.value = new Point3f();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple3f.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple3f)value );
			else if( value instanceof float[] )
				this.value.set( (float[])value );
			else if( value instanceof Float[] ){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1], v[2] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform3f( id, value.x, value.y, value.z );
		}
	}

	private static class AtributoFloat4 extends Atributo<Tuple4f>{

		AtributoFloat4( int id, Object value ){
			super( id );
			this.value = new Point4f();
			setValue( value );
		}

		public void setValue( Object value ){
			if( Tuple4f.class.isAssignableFrom( value.getClass() ) )
				this.value.set( (Tuple4f)value );
			else if( value instanceof float[] )
				this.value.set( (float[])value );
			else if( value instanceof Float[] ){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1], v[2], v[3] );
			}
		}

		public void Uniform( GL2 gl ){
			gl.glUniform4f( id, value.x, value.y, value.z, value.w );
		}
	}

	// TODO Resto de tipos de atributos

	private transient static Shader anterior;

	private transient final int programObject;

	private transient final Map<String, Atributo<? extends Object>> uniforms;

	/**
	 * Constructor que genera un {@code Shader} a partir de los datos indicados.
	 * 
	 * @param gl Contexto gráfico empleado para almacenar el <i>shader</i> en memoria de vídeo.
	 * @param vertexFile Ruta del archivo que contiene el <i>vertex shader</i>.
	 * @param fragmentFile Ruta del archivo que contiene el <i>fragment shader</i>.
	 */
	public Shader( GL2 gl, String vertexFile, String fragmentFile ){
		int vertexShader = 0;
		int fragmentShader = 0;

		boolean ok = false;

		try{

			vertexShader = gl.glCreateShaderObjectARB( GL2ES2.GL_VERTEX_SHADER );
			gl.glShaderSourceARB( vertexShader, 1, new String[] { loadShader( vertexFile ) }, (int[])null, 0 );
			gl.glCompileShaderARB( vertexShader );
			checkLogInfo( gl, vertexShader );

			fragmentShader = gl.glCreateShaderObjectARB( GL2ES2.GL_FRAGMENT_SHADER );
			gl.glShaderSourceARB( fragmentShader, 1, new String[] { loadShader( fragmentFile ) }, (int[])null, 0 );
			gl.glCompileShaderARB( fragmentShader );

			checkLogInfo( gl, fragmentShader );
			ok = true;

		}catch( FileNotFoundException e ){
			e.printStackTrace();
		}catch( IOException e ){
			e.printStackTrace();
		}

		if( ok ){
			programObject = gl.glCreateProgramObjectARB();

			gl.glAttachObjectARB( programObject, vertexShader );
			gl.glAttachObjectARB( programObject, fragmentShader );
			gl.glLinkProgramARB( programObject );

			gl.glValidateProgramARB( programObject );

			checkLogInfo( gl, programObject );

		}else{
			programObject = 0;
		}
		uniforms = new HashMap<String, Atributo<? extends Object>>( 0 );
	}

	private static String loadShader( String filename ) throws FileNotFoundException, IOException{

		BufferedReader shaderReader = new BufferedReader( new InputStreamReader( new FileInputStream( filename ) ) );
		StringWriter shaderWriter = new StringWriter();

		String line;
		while( ( line = shaderReader.readLine() ) != null ){
			shaderWriter.write( line );
			shaderWriter.write( "\n" );
		}
		return shaderWriter.getBuffer().toString();
	}

	private static void checkLogInfo( GL2 gl, int obj ){
		IntBuffer iVal = Buffers.newDirectIntBuffer( 1 );
		gl.glGetObjectParameterivARB( obj, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal );

		int length = iVal.get();

		if( length <= 1 )
			return;

		ByteBuffer infoLog = Buffers.newDirectByteBuffer( length );

		iVal.flip();
		gl.glGetInfoLogARB( obj, length, iVal, infoLog );

		byte[] infoBytes = new byte[length];
		infoLog.get( infoBytes );
		System.out.println( "GLSL Validation >> " + new String( infoBytes, 0, length - 1 ) );
	}

	/**
	 * Método que añade y asigna un valor a un atributo uniforme.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param name {@code String} que identifica el atributo uniforme añadido.
	 * @param value Valor del atributo uniforme añadido.
	 */
	public void addUniform( GL2 gl, String name, Object value ){
		int id = gl.glGetUniformLocation( programObject, name );
		uniforms.put( name, Atributo.getAtributo( id, value ) );
	}

	/**
	 * Método que modifica el valor de un atributo uniforme anteriormente añadido.
	 * @param name {@code String} que identifica el atributo uniforme asignado.
	 * @param value Valor del atributo uniforme asignado.
	 */
	public void setUniform( String name, Object value ){
		Atributo<?> att = uniforms.get( name );
		if( att != null )
			att.setValue( value );
	}

	/**
	 * Método que devuelve la posición de los atributos de vértice.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param name {@code String} que identifica los atributos de vértice ligados.
	 * @return Posición donde están ligados los atributos de vértice.
	 */
	public int getAttribLocation( GL2 gl, String name ){
		return gl.glGetAttribLocation( programObject, name );
	}

	/**
	 * Método que liga los atributos de vértice a una posición.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param index Posición donde serán ligados los atributos de vértice.
	 * @param name {@code String} que identifica los atributos de vértice ligados.
	 */
	public void bindAttribLocation( GL2 gl, int index, String name ){
		gl.glBindAttribLocation( programObject, index, name );
	}

	/**
	 * Método que activa el uso de <i>shaders</i> con los valores de
	 * este {@code Shader}.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void activar( GL2 gl ){
		if( anterior == this )
			return;
		gl.glUseProgramObjectARB( programObject );
		for( Atributo<?> atributo: uniforms.values() )
			atributo.Uniform( gl );
		anterior = this;
	}

	/**
	 * Método estático que desactiva el uso de <i>shaders</i>.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public static void desactivar( GL2 gl ){
		anterior = null;
		gl.glUseProgramObjectARB( 0 );
	}
}
