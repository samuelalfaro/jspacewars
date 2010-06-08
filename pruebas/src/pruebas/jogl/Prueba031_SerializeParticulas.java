package pruebas.jogl;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.sam.interpoladores.*;
import org.sam.jogl.particulas.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Prueba031_SerializeParticulas{
	public static void main(String args[]){
		Particulas particulas;
		
//		Matrix4f mt = new Matrix4f();
		
		Getter.Float<Float> iRadio;
		Getter.Float<Float> iSemiEje;
		Getter.Float<Float> iAlfa;

		particulas = FactoriaDeParticulas.createParticulas(10);
		particulas.setEmisor(new Emisor.Cache(new Emisor.Conico(0.025f,5.0f),16));
		particulas.setEmision(Particulas.Emision.CONTINUA);
		particulas.setRangoDeEmision(1.0f);
		particulas.setTiempoVida(0.0125f);
		particulas.setVelocidad(30.0f,5.0f,false);
		
		iRadio= GettersFactory.Float.create(
				new float[]{0.0f,0.2f,1.0f},
				new float[]{0.1f,0.2f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		particulas.setRadio(iRadio);
		iSemiEje= GettersFactory.Float.create(
				new float[]{0.0f,0.7f,1.0f},
				new float[]{0.01f,0.5f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);
		particulas.setSemiEje(iSemiEje);
		iAlfa = GettersFactory.Float.create(
				new float[]{0.7f,1.0f},
				new float[]{1.0f,0.0f},
				MetodoDeInterpolacion.Predefinido.LINEAL);

		particulas.setColor(0.4f,0.3f,0.2f, iAlfa);
		
		Matrix4f t3d = new Matrix4f();
		t3d.rotY((float)Math.PI/2);
		t3d.setTranslation(new Vector3f( 0.12f, -0.15f, -0.5f));
		
		XStream xStream = new XStream(new DomDriver());
		System.out.println(xStream.toXML(particulas));
		Float[] f = new Float[10];
		System.out.println(f.getClass().getName());
		try {
			Class<?> clazz = Class.forName("[Ljava.lang.Float;");
			System.out.println(clazz.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
