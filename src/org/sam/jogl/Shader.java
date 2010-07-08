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

import javax.media.opengl.GL;
import javax.vecmath.*;

import com.sun.opengl.util.BufferUtil;

public class Shader{
	
	private static abstract class Atributo<T>{
		protected final int id;
		protected T value;
		
		Atributo( int id ){
			this.id = id;
		}
		public abstract void setValue(Object value);
		public abstract void Uniform(GL gl);
		
		static Atributo<?> getAtributo( int id, Object value ){
	    	if( value instanceof Integer )
	    		return new AtributoEntero1( id, value );
	    	if( Tuple2i.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoEntero2( id, value );
	    	if( Tuple3i.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoEntero3( id, value );
	    	if( Tuple4i.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoEntero4( id, value );
	    	if( value instanceof Integer[] || value instanceof int[]){
	    		switch(Array.getLength(value)){
	    		case 2:
	    			return new AtributoEntero2 ( id, value );
	    		case 3:
	    			return new AtributoEntero3 ( id, value );
	    		case 4:
	    			return new AtributoEntero4 ( id, value );
	    		}
	    	}
	    	if( value instanceof Float )
	    		return new AtributoFloat1( id, value );
	    	if( Tuple2f.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoFloat2( id, value );
	    	if( Tuple3f.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoFloat3( id, value );
	    	if( Tuple4f.class.isAssignableFrom(value.getClass()) )
	    		return new AtributoFloat4( id, value );
	    	if( value instanceof Float[] || value instanceof float[] ){
	    		switch(Array.getLength(value)){
	    		case 2:
	    			return new AtributoFloat2 ( id, value);
	    		case 3:
	    			return new AtributoFloat3 ( id, value );
	    		case 4:
	    			return new AtributoFloat4 ( id, value );
	    		}
	    	}
	    	// TODO Resto de tipos de atributos
	    	throw new IllegalArgumentException("Tipo no soportado");
		}
	}
	
	private static class AtributoEntero1 extends Atributo<Integer>{
		
		AtributoEntero1(int id, Object value){
			super(id);
			setValue(value);
		}
		
		public void setValue(Object value){
			this.value = (Integer)value;
		}

		public void Uniform(GL gl){
			gl.glUniform1i( id, value );
		}
	}
	
	private static class AtributoEntero2 extends Atributo<Tuple2i>{
		
		AtributoEntero2(int id, Object value){
			super(id);
			this.value = new Point2i();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple2i.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple2i)value );
			else if(value instanceof int[])
				this.value.set( (int[])value );
			else if(value instanceof Integer[]){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1] );
			}
		}
			
		public void Uniform(GL gl){
			gl.glUniform2i( id, value.x, value.y );
		}
	}
	
	private static class AtributoEntero3 extends Atributo<Tuple3i>{
		
		AtributoEntero3(int id, Object value){
			super(id);
			this.value = new Point3i();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple3i.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple3i)value );
			else if(value instanceof int[])
				this.value.set( (int[])value );
			else if(value instanceof Integer[]){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1], v[2] );
			}
		}
		
		public void Uniform(GL gl){
			gl.glUniform3i( id, value.x, value.y, value.z );
		}
	}
	
	private static class AtributoEntero4 extends Atributo<Tuple4i>{
		
		AtributoEntero4(int id, Object value){
			super(id);
			this.value = new Point4i();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple4i.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple4i)value );
			else if(value instanceof int[])
				this.value.set( (int[])value );
			else if(value instanceof Integer[]){
				Integer[] v = (Integer[])value;
				this.value.set( v[0], v[1], v[2], v[3] );
			}
		}
		
		public void Uniform(GL gl){
			gl.glUniform4i( id, value.x, value.y, value.z, value.w );
		}
	}
	
	private static class AtributoFloat1 extends Atributo<Float>{
		
		AtributoFloat1(int id, Object value){
			super(id);
			setValue(value);
		}
		
		public void setValue(Object value){
			this.value = (Float)value;
		}

		public void Uniform(GL gl){
			gl.glUniform1f( id, value );
		}
	}
	
	private static class AtributoFloat2 extends Atributo<Tuple2f>{
		
		AtributoFloat2(int id, Object value){
			super(id);
			this.value = new Point2f();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple2f.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple2f)value );
			else if(value instanceof float[])
				this.value.set( (float[])value );
			else if(value instanceof Float[]){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1] );
			}
		}
		
		public void Uniform(GL gl){
			gl.glUniform2f( id, value.x, value.y );
		}
	}
	
	private static class AtributoFloat3 extends Atributo<Tuple3f>{
		
		AtributoFloat3(int id, Object value){
			super(id);
			this.value = new Point3f();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple3f.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple3f)value );
			else if(value instanceof float[])
				this.value.set( (float[])value );
			else if(value instanceof Float[]){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1], v[2] );
			}
		}
		
		public void Uniform(GL gl){
			gl.glUniform3f( id, value.x, value.y, value.z );
		}
	}
	
	private static class AtributoFloat4 extends Atributo<Tuple4f>{
		
		AtributoFloat4(int id, Object value){
			super(id);
			this.value = new Point4f();
			setValue(value);
		}
		
		public void setValue(Object value){
			if(Tuple4f.class.isAssignableFrom(value.getClass()))
				this.value.set( (Tuple4f)value );
			else if(value instanceof float[])
				this.value.set( (float[])value );
			else if(value instanceof Float[]){
				Float[] v = (Float[])value;
				this.value.set( v[0], v[1], v[2], v[3] );
			}
		}
		
		public void Uniform(GL gl){
			gl.glUniform4f( id, value.x, value.y, value.z, value.w );
		}
	}
	// TODO Resto de tipos de atributos
	
	private transient static Shader anterior;
	
	public transient final int programObject;
	private transient final Map<String,Atributo<? extends Object>> uniforms;
	
	public Shader(GL gl, String vertexFile, String fragmentFile){
		int vertexShader = 0;
		int fragmentShader = 0;
		
		boolean ok = false;
		
		try{

			vertexShader = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB);
			gl.glShaderSourceARB(vertexShader, 1, new String[]{loadShader(vertexFile)}, (int[]) null, 0);
			gl.glCompileShaderARB(vertexShader);
			checkLogInfo(gl, vertexShader);

			fragmentShader = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
			gl.glShaderSourceARB(fragmentShader, 1, new String[]{loadShader(fragmentFile)}, (int[]) null, 0);
			gl.glCompileShaderARB(fragmentShader);
			
			checkLogInfo(gl, fragmentShader);
			ok = true;

		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		if(ok){
			programObject = gl.glCreateProgramObjectARB();

			gl.glAttachObjectARB(programObject, vertexShader);
			gl.glAttachObjectARB(programObject, fragmentShader);
			gl.glLinkProgramARB(programObject);
			
			gl.glValidateProgramARB(programObject);
			
			checkLogInfo(gl, programObject);
			
		}else{
			programObject = 0;
		}
		uniforms = new HashMap<String,Atributo<? extends Object>>(0);
	}
	
	private static String loadShader( String filename ) throws FileNotFoundException, IOException{

		BufferedReader shaderReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(filename)
				)
		);
		StringWriter shaderWriter = new StringWriter();

		String line;
		while( ( line = shaderReader.readLine()) != null ){
			shaderWriter.write(line);
			shaderWriter.write("\n");
		}
		return shaderWriter.getBuffer().toString();
	}
	
    private static void checkLogInfo(GL gl, int obj) {
        IntBuffer iVal = BufferUtil.newIntBuffer(1);
        gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

        int length = iVal.get();

        if (length <= 1)
            return;

        ByteBuffer infoLog = BufferUtil.newByteBuffer(length);

        iVal.flip();
        gl.glGetInfoLogARB(obj, length, iVal, infoLog);

        byte[] infoBytes = new byte[length];
        infoLog.get(infoBytes);
        System.out.println("GLSL Validation >> " + new String(infoBytes, 0, length - 1));
    }
	
    public void addUniform(GL gl, String name, Object value){
    	int id = gl.glGetUniformLocation(programObject, name);
   		uniforms.put( name, Atributo.getAtributo(id, value) );
    }
    
    public void setUniform(String name, Object value){
    	Atributo<?> att = uniforms.get( name );
    	if( att != null)
    		att.setValue(value);
    }
    
	public void activar(GL gl) {
		if( anterior == this )
			return;
		gl.glUseProgramObjectARB(programObject);
		for(Atributo<?> atributo: uniforms.values())
			atributo.Uniform(gl);
		anterior = this;
	}
	
	public static void desactivar(GL gl) {
		anterior = null;
		gl.glUseProgramObjectARB(0);
	}
}