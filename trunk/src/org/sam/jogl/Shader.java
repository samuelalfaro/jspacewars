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

import com.sun.opengl.util.BufferUtil;

public class Shader{
	
	private static abstract class Atributo<T>{
		protected final int id;
		protected T value;
		
		Atributo(int id, Object value){
			this.id = id;
			setValue(value);
		}
		public abstract void setValue(Object value);
		public abstract void Uniform(GL gl);
	}
	
	private static class AtributoEntero1 extends Atributo<Integer>{
		AtributoEntero1(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			setValue((Integer)value);
		}
		public void setValue(Integer value){
			this.value = value;
		}
		public void Uniform(GL gl){
			gl.glUniform1i( id, value );
		}
	}
	
	private static class AtributoEntero2 extends Atributo<int[]>{
		AtributoEntero2(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof int[])
				setValue( (int[])value );
			else if(value instanceof Integer[])
				setValue((Integer[])value);
		}
		public void setValue(int[] value){
			this.value = value;
		}
		public void setValue(Integer[] value){
			this.value = new int[]{ value[0],  value[1] };
		}
		public void Uniform(GL gl){
			gl.glUniform2i( id, value[0], value[1] );
		}
	}
	
	private static class AtributoEntero3 extends Atributo<int[]>{
		AtributoEntero3(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof int[])
				setValue( (int[])value );
			else if(value instanceof Integer[])
				setValue((Integer[])value);
		}
		public void setValue(int[] value){
			this.value = value;
		}
		public void setValue(Integer[] value){
			this.value = new int[]{ value[0],  value[1],  value[2] };
		}
		public void Uniform(GL gl){
			gl.glUniform3i( id, value[0], value[1], value[2] );
		}
	}
	
	private static class AtributoEntero4 extends Atributo<int[]>{
		AtributoEntero4(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof int[])
				setValue( (int[])value );
			else if(value instanceof Integer[])
				setValue((Integer[])value);
		}
		public void setValue(int[] value){
			this.value = value;
		}
		public void setValue(Integer[] value){
			this.value = new int[]{ value[0],  value[1],  value[2],  value[3] };
		}
		public void Uniform(GL gl){
			gl.glUniform4i( id, value[0], value[1], value[2], value[3] );
		}
	}
	
	private static class AtributoFloat1 extends Atributo<Float>{
		AtributoFloat1(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			setValue((Float)value);
		}
		public void setValue(Float value){
			this.value = value;
		}
		public void Uniform(GL gl){
			gl.glUniform1f( id, value );
		}
	}
	
	private static class AtributoFloat2 extends Atributo<float[]>{
		AtributoFloat2(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof float[])
				setValue( (float[])value );
			else if(value instanceof Float[])
				setValue((Float[])value);
		}
		public void setValue(float[] value){
			this.value = value;
		}
		public void setValue(Float[] value){
			this.value = new float[]{ value[0],  value[1] };
		}
		public void Uniform(GL gl){
			gl.glUniform2f( id, value[0], value[1] );
		}
	}
	
	private static class AtributoFloat3 extends Atributo<float[]>{
		AtributoFloat3(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof float[])
				setValue( (float[])value );
			else if(value instanceof Float[])
				setValue((Float[])value);
		}
		public void setValue(float[] value){
			this.value = value;
		}
		public void setValue(Float[] value){
			this.value = new float[]{ value[0],  value[1],  value[2] };
		}
		public void Uniform(GL gl){
			gl.glUniform3f( id, value[0], value[1], value[2] );
		}
	}
	
	private static class AtributoFloat4 extends Atributo<float[]>{
		AtributoFloat4(int id, Object value){
			super(id, value);
		}
		public void setValue(Object value){
			if(value instanceof float[])
				setValue( (float[])value );
			else if(value instanceof Float[])
				setValue((Float[])value);
		}
		public void setValue(float[] value){
			this.value = value;
		}
		public void setValue(Float[] value){
			this.value = new float[]{ value[0],  value[1],  value[2],  value[3] };
		}
		
		public void Uniform(GL gl){
			gl.glUniform4f( id, value[0], value[1], value[2], value[3] );
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
    	if(value instanceof Integer)
    		uniforms.put( name, new AtributoEntero1 ( id, value ) );
    	else if(value instanceof Integer[] || value instanceof int[]){
    		switch(Array.getLength(value)){
    		case 2:
    			uniforms.put( name, new AtributoEntero2 ( id, value ) );
    			break;
    		case 3:
    			uniforms.put( name, new AtributoEntero3 ( id, value ) );
    			break;
    		case 4:
    			uniforms.put( name, new AtributoEntero4 ( id, value ) );
    			break;
    		}
    	}else if(value instanceof Float)
    		uniforms.put( name, new AtributoFloat1 ( id, value ) );
    	else if(value instanceof Float[] || value instanceof float[] ){
    		switch(Array.getLength(value)){
    		case 2:
    			uniforms.put( name, new AtributoFloat2 ( id, value) );
    			break;
    		case 3:
    			uniforms.put( name, new AtributoFloat3 ( id, value ) );
    			break;
    		case 4:
    			uniforms.put( name, new AtributoFloat4 ( id, value ) );
    			break;
    		}
    	}
    	// TODO Resto de tipos de atributos
    }
    
    public void setUniform(String name, Object value){
    	Atributo<? extends Object> att = uniforms.get( name );
    	if(att == null)
    		return;
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