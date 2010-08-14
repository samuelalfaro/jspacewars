package textureGenerator;
@Deprecated
public final class Noise3 {
	static int i, j, k, A[] = { 0, 0, 0 };
	static double u, v, w;

	static double noise(double x, double y, double z) {
		double s = (x + y + z) / 3.0;
		i = (int) Math.floor(x + s);
		j = (int) Math.floor(y + s);
		k = (int) Math.floor(z + s);
		s = (i + j + k) / 6.0;
		u = x - i + s;
		v = y - j + s;
		w = z - k + s;
		A[0] = A[1] = A[2] = 0;
		int hi = u >= w ? u >= v ? 0 : 1 : v >= w ? 1 : 2;
		int lo = u < w ? u < v ? 0 : 1 : v < w ? 1 : 2;
		return K(hi) + K(3 - hi - lo) + K(lo) + K(0);
	}

	static double K(int a) {
	
		double s = (A[0]+A[1]+A[2])/6.0; 
		
		double x = u-A[0]+s;
		double y = v-A[1]+s;
		double z = w-A[2]+s;
		
		double t = 2.0/3 -x*x -y*y -z*z; 
		
		if (t < 0){
			A[a]++;
			return 0;
		}
		
		int h = shuffle( i+A[0], j+A[1], k+A[2] ); 
		A[a]++;

//		int b5 = (h >> 5) & 1;
//		int b4 = (h >> 4) & 1;
//		int b3 = (h >> 3) & 1;
//		int b2 = (h >> 2) & 1;
//		int b = h & 3;
//	
//		double p = b==1? x: b==2? y: z;
//		double q = b==1? y: b==2? z: x;
//		double r = b==1? z: b==2? x: y;
//		p = b5==b3? -p: p;
//		q = b5==b4? -q: q;
//		r = b5!= (b4^b3)? -r : r;
		
		double p, q, r;
		switch( h & 0x7 ){
		case 0:  p=x; q=y; r=z; break;
		case 1:  p=y; q=z; r=0; break;
		case 2:  p=z; q=x; r=0; break;
		case 3:  p=x; q=y; r=0; break;
		case 4:  p=x; q=y; r=z; break;
		case 5:  p=y; q=0; r=x; break;
		case 6:  p=z; q=0; r=y; break;
		default: p=x; q=0; r=z; break;
		}
		
		t *= t; t *= t; t *= 8;
		
		switch( h>>3 & 0x7 ){
		case 0:  return t * (-p -q +r );
		case 1:  return t * ( p -q -r );
		case 2:  return t * (-p +q -r );
		case 3:  return t * ( p +q +r );
		case 4:  return t * ( p +q -r );
		case 5:  return t * (-p +q +r );
		case 6:  return t * ( p -q +r );
		default: return t * (-p -q -r );
		}
	}	
	
	static int shuffle(int i, int j, int k) {
		return
			b(i, j, k, 0) +
			b(j, k, i, 1) +
			b(k, i, j, 2) +
			b(i, j, k, 3) +
			b(j, k, i, 4) +
			b(k, i, j, 5) +
			b(i, j, k, 6) +
			b(j, k, i, 7) ;
		
//			b(i, j, k, 0) +
//			b(i, k, j, 5) +
//			b(j, i, k, 1) +
//			b(j, k, i, 4) +
//			b(k, i, j, 2) +
//			b(k, j, i, 3) ;
	}

	static int b(int i, int j, int k, int B) {
		return T[ ( ( (i >> B) & 0x1) << 2) | ( ( (j >> B) & 0x1) << 1) | ( (k >> B) & 0x1) ];
	}

//	static int b(int N, int B) {
//		return N >> B & 1;
//	}
	
	private static final int T[] = { 0x15, 0x38, 0x32, 0x2C, 0x0D, 0x13, 0x07, 0x2A };
	
//	private static final int T[] = new int[8];
//	static{
//		for(int i= 0; i < 8; i++)
//			T[i] = (int)(Math.random() * 255);
//	}
}