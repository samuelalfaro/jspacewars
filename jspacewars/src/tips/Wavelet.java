package tips;

/**
 * Wavelet.java performs wavelet transform and compression 
 * written by Yizong Cheng March 2002
 * Usage: java Wavelet 512 20 < lena.raw
 * 512 is the size and 20 the threshold
 */

import java.io.IOException;

public class Wavelet{
	int size;    //  size of the square image
	double[][] y;  // the image
	
	/**
	 * The constructor reads in the image from stdin
	 */    
	public Wavelet(int s){ 
		size = s; 
		y = new double[size][size];
		for (int i = 0; i < size; i++) 
			for (int j = 0; j < size; j++)
				try { 
					y[i][j] = System.in.read(); 
				}catch(IOException e){
					System.exit(1);
				}
	}
	
	public void invertir(){
		for (int i=0, len = y.length; i<len; i++)
			for (int j=0; j<len; j++){
				int val = 255 - (int)y[i][j];
				System.out.write(val);
			}
		System.out.flush();
	}
	
	/**
	 * The quadrature mirror transform, with four nonzero a row
	 */
	public void qmf(int size){
		double[] h = { 0.483, 0.837, 0.224, -0.129 }; 
		double[] g = { -0.129, -0.224, 0.837, -0.483 };
		double[] z = new double[size];  // temperary array
		int half = size / 2; 
		for (int i = 0; i < size; i++){  // row transform
			for (int w = 0; w < half; w++){ 
				z[w] = z[w + half] = 0;
				for (int j = 0; j < 4; j++){ 
					z[w] += h[j] * y[i][(2 * w + j ) % size];
					z[w + half] += g[j] * y[i][(2 * w + size - 2 + j) % size]; 
				} 
			}
			for (int w = 0; w < size; w++) 
				y[i][w] = z[w];     
		}
		for (int w = 0; w < size; w++){   // column transform
			for (int i = 0; i < half; i++){ 
				z[i] = z[i + half] = 0;
				for (int j = 0; j < 4; j++){ 
					z[i] += h[j] * y[(2 * i + j) % size][w];
					z[i + half] += g[j] * y[(2 * i + size - 2 + j) % size][w]; 
				} 
			}
			for (int i = 0; i < size; i++) 
				y[i][w] = z[i];  
		}
	}
	
	/**
	 * wavelet transform components close to zero become zero
	 * otherwise they are divided by 8
	 * stretches of zeros coded by run length up to 255
	 */
	public void shrink(int threshold){
		int[][] pixx = new int[size][size];
		for (int i = 0; i < size; i++) 
			for (int w = 0; w < size; w++){
				double c = y[i][w];
				if (c < threshold && c > -threshold) 
					pixx[i][w] = 0 ;
				else pixx[i][w]=((int)(Math.floor(c + 0.5)))>>3;
			}
		int run = 0;
		for (int i = 0; i < size; i++) 
			for (int w = 0; w < size; w++)
				if (pixx[i][w]!=0){
					if (run > 0){
						System.out.write(0);
						System.out.write(run);
						run = 0;
					}
					System.out.write(pixx[i][w]+128);
				}
				else if (run == 255){
					System.out.write(0);
					System.out.write(255);
					run = 1;
				}
				else run++; 
		if (run > 0){
			System.out.write(0);
			System.out.write(run);
		}
		System.out.flush();
	}
	
	/**
	 * The main function takes in size and threshold
	 */    
	public static void main(String[] args){
		if (args.length == 0){
			System.out.println("Usage: java Wavelet size shrink < image");
			System.exit(0);
		}
		int size = Integer.parseInt(args[0]); 
		Wavelet v = new Wavelet(size); 
		//v.qmf(size); 
		//v.qmf(size / 2); 
		//v.shrink(Integer.parseInt(args[1]));
		v.invertir();
	}
}