package org.sam.jogl;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

public class Shader{
	
	private static class Atributo{
		private final int id;
		private final Object value;
		
		Atributo(int id, Object value){
			this.id = id;
			this.value = value;
		}
	}
	
	private transient static Shader anterior;
	
	private transient final int programObject;
	private transient final Collection<Atributo> uniforms;
	
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
		uniforms = new ArrayList<Atributo>(0);
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
    	uniforms.add( new Atributo ( gl.glGetUniformLocation(programObject, name) , value ) );
    }
    
	public void activar(GL gl) {
		if( anterior == this )
			return;
		gl.glUseProgramObjectARB(programObject);
		for(Atributo atributo: uniforms){
			if (atributo.value instanceof Integer)
				gl.glUniform1i( atributo.id, (Integer)atributo.value );
			// TODO Resto de tipos de atributos
		}
		anterior = this;
	}
	
	public static void desactivar(GL gl) {
		anterior = null;
		gl.glUseProgramObjectARB(0);
	}
}