
public class Gaussian {

	static double normal(int x, double mu, double sigma ){
		double a = 1.0/Math.sqrt(2*Math.PI*sigma*sigma);
		double b = mu;
		double c = sigma;
		return a*Math.exp(-Math.pow(x-b, 2)/2*c*c);
	}
	
	public static void main(String... args){
		float valores[] = new float[19];
		float total = 0;
		for(int i= 0, x = 1; i < valores.length; i = x, x++){
			valores[i] = (float)normal(x,10,0.5);
			total += valores[i];
		}
		for(int i= 0, x = 1; i < valores.length; i = x, x++){
			valores[i] /= total;
			System.out.format("   %2d: %f\n", x, valores[i]);
		}
		/*
		long intValues[] = new long[] { 3140, 6747, 13251, 23786, 39020, 58503,
				80164, 100392, 114902, 120190, 114902, 100392, 80164, 58503,
				39020, 23786, 13251, 6747, 3140 };
		long intTotal = 0;
		for(long v: intValues){
			intTotal += v;
		}
		System.out.format("Total: %d\n", intTotal );
		*/
	}
}
