package org.sam.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import org.sam.colisiones.Poligono;
import org.sam.util.Consola;
import org.sam.util.Reflexion;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GeneradorDeSiluetas {
	
	@SuppressWarnings("serial")
	static public class ParsingErrorException extends RuntimeException {
		public ParsingErrorException() {
			super();
		}
		public ParsingErrorException(String s) {
			super(s);
		}
	}
	
	private static class ObjParser extends StreamTokenizer {
		
		private static final char BACKSLASH = '\\';
		
		ObjParser(Reader r) {
			super(r);
			setup();
		} 
		
		void setup() {
			resetSyntax();
			eolIsSignificant(true);
			lowerCaseMode(true);
			// Todos los caracteres ascii imprimibles
			wordChars('!', '~');
			// Comentario desde ! hasta el fin de linea
			commentChar('!');
			commentChar('#');
			whitespaceChars(' ', ' ');
			whitespaceChars('\n', '\n');
			whitespaceChars('\r', '\r');
			whitespaceChars('\t', '\t');
			// Tokens
			ordinaryChar('/');
			ordinaryChar(BACKSLASH);
		}
		
		void getToken() throws ParsingErrorException {
			try {
				while (true){
					nextToken();
					if (ttype != ObjParser.BACKSLASH)
						break;
					nextToken();
					if (ttype != StreamTokenizer.TT_EOL)
						break;
				}
			}
			catch (IOException e) {
				throw new ParsingErrorException(
						"IO error on line " + lineno() + ": " + e.getMessage());
			}
		}
		
		void skipToNextLine() throws ParsingErrorException {
			while (ttype != StreamTokenizer.TT_EOF && ttype != StreamTokenizer.TT_EOL) {
				getToken();
			}
		}
		
		float getFloat() throws ParsingErrorException {
			do{
				getToken();
			}while(ttype == StreamTokenizer.TT_EOL);
			return getLastValueAsFloat();
		}
		
		float getLastValueAsFloat() throws ParsingErrorException {
			try{
//				if(ttype == StreamTokenizer.TT_NUMBER)
//					return (float)this.nval;
				if(ttype == StreamTokenizer.TT_WORD)
					return (Double.valueOf(sval)).floatValue();
				throw new ParsingErrorException("Expected number on line " + lineno());
			}
			catch (NumberFormatException e) {
				throw new ParsingErrorException(e.getMessage());
			}
		}

		int getLastValueAsInteger() throws ParsingErrorException {
			try {
//				if(ttype == StreamTokenizer.TT_NUMBER)
//					return (int)this.nval;
				if(ttype == StreamTokenizer.TT_WORD)
					return (Integer.valueOf(sval)).intValue();
				throw new ParsingErrorException("Expected number on line " + lineno());
			}
			catch (NumberFormatException e) {
				throw new ParsingErrorException(e.getMessage());
			}
		}
	}
	
	private static class Line{
		final int p1;
		final int p2;
		Line( int p1, int p2){
			this.p1 = p1;
			this.p2 = p2;
		}
	}
	
	final List<Point3f> vertList;
	final List<Line> lines;

	private GeneradorDeSiluetas(final Reader reader) {
		this(reader, false, null);
	}
	
	private GeneradorDeSiluetas(final Reader reader, boolean resize, Matrix4d mt){
		vertList = new ArrayList<Point3f>(128);
		lines = new ArrayList<Line>(128);
		read(new ObjParser(new BufferedReader(reader)));
		
		if( mt != null || resize ){
			Matrix4d m;
			if(mt != null){
				m = new Matrix4d(mt);
				if ( resize )
					m.mul(escalarYCentrar());
			}else
				m = escalarYCentrar();
			
			for(Point3f v:vertList)
				m.transform(v);
		}
	}
	
	private transient float minX, maxX;
	private transient float minY, maxY;
	private transient float minZ, maxZ;
	
	private void read(ObjParser st) throws ParsingErrorException {
		
		minX = Float.MAX_VALUE; maxX= -Float.MAX_VALUE;
		minY = Float.MAX_VALUE; maxY= -Float.MAX_VALUE;
		minZ = Float.MAX_VALUE; maxZ= -Float.MAX_VALUE;
		
		do{
			st.getToken();
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("v"))
					readVertex(st);
				else if (st.sval.equals("vn"))
					st.skipToNextLine(); // normal
				else if (st.sval.equals("vt"))
					st.skipToNextLine(); // text coords
				else if (st.sval.equals("f")){
					readFace(st);
				}
				else if (st.sval.equals("fo")){
					readFace(st);
				}
				else if (st.sval.equals("g"))
					st.skipToNextLine();
				else if (st.sval.equals("s"))
					st.skipToNextLine();
				else if (st.sval.equals("p"))
					st.skipToNextLine();
				else if (st.sval.equals("l"))
					st.skipToNextLine();
				else if (st.sval.equals("mtllib"))
					st.skipToNextLine();
				else if (st.sval.equals("usemtl"))
					st.skipToNextLine();
				else if (st.sval.equals("maplib"))
					st.skipToNextLine();
				else if (st.sval.equals("usemap"))
					st.skipToNextLine();
				else
					throw new ParsingErrorException(
							"Unrecognized token, line " + st.lineno());
			}
			st.skipToNextLine();
		}while (st.ttype != StreamTokenizer.TT_EOF);
	}

	private void readVertex(ObjParser st) throws ParsingErrorException {
		Point3f v = new Point3f();
		v.x = st.getFloat();
		v.y = st.getFloat();
		v.z = st.getFloat();
		if(v.x < minX)
			minX = v.x;
		if(v.x > maxX)
			maxX = v.x;
		if(v.y < minY)
			minY = v.y;
		if(v.y > maxY)
			maxY = v.y;
		if(v.z < minZ)
			minZ = v.z;
		if(v.z > maxZ)
			maxZ = v.z;
		vertList.add(v);
	}
	
	private void readFace(ObjParser st) throws ParsingErrorException {
		int[] vertIndex  = new int[4];
		int count = 0;
		
		st.getToken();
		do{
			// Se lee el vertice
			if (st.ttype == StreamTokenizer.TT_WORD) {
				vertIndex[count] = st.getLastValueAsInteger() - 1;
				if (vertIndex[count] < 0)
					vertIndex[count] += vertList.size() + 1;
			}
			st.getToken();
			if (st.ttype == '/') {
				st.getToken(); 
				if (st.ttype == StreamTokenizer.TT_WORD) {
					// se lee las coordenadas de textura y el siguiente token que pueder ser el separador
					st.getToken();
				}
				if (st.ttype == '/') {
					st.getToken(); 
					if (st.ttype == StreamTokenizer.TT_WORD) {
						// se lee la normal y el el siguiente valor para continuar el bucle
						st.getToken();
					}
				}
			}
			count++;
		}while (st.ttype != StreamTokenizer.TT_EOF && st.ttype != StreamTokenizer.TT_EOL);
		
		for(int i=0; i < count-1; i++){
			lines.add(new Line(vertIndex[i],vertIndex[i+1]));
		}
		lines.add(new Line(vertIndex[count-1],vertIndex[0]));
	}
	
	private double getMaxDistance(){
		double absX = maxX - minX;
		double absY = maxY - minY;
		double absZ = maxZ - minZ;
		
		return Math.max(absX, Math.max(absY,absZ));
	}
	
	private Matrix4d escalarYCentrar(){
		double centerX = (minX + maxX)/2;
		double centerY = (minY + maxY)/2;
		double centerZ = (minZ + maxZ)/2;

		double scale = 1.0 / Math.max(1.0, getMaxDistance() );
		//System.err.println("Centro: ("+centerX+", "+centerY+", "+centerZ+")\nEscala: "+scale);
	
		Matrix4d mt = new Matrix4d();
		mt.set(scale, new Vector3d(-centerX*scale,-centerY*scale,-centerZ*scale));

		return mt;
	}
	
	private static void paintProjection(Graphics2D g2d, List<Point3f> vertList, List<Line> lines, int w, int h){
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, w, h);
		
		int min = Math.min(w,h);
		
		int off_W = w/2;
		int off_H = h/2;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(Line line: lines){
			Point3f v1 = vertList.get(line.p1);
			Point3f v2 = vertList.get(line.p2);
			g2d.setColor(Color.GRAY);
			g2d.drawRect((int)(-v1.x * min) + off_W -1, (int)(-v1.y*min) + off_H -1, 2, 2);
			g2d.drawRect((int)(-v2.x * min) + off_W -1, (int)(-v2.y*min) + off_H -1, 2, 2);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawLine((int)(-v1.x * min) + off_W, (int)(-v1.y*min) + off_H, (int)(-v2.x * min) + off_W, (int)(-v2.y*min) + off_H );
		}
	}
	
	@SuppressWarnings("serial")
	private JComponent getComponent(){
		return new JComponent(){
			public void paintComponent(Graphics g){
				paintProjection((Graphics2D)g, vertList, lines, this.getWidth(), this.getHeight());
			}
			public Dimension getMinimumSize(){
				Dimension size = super.getMinimumSize();
				size.setSize(256,256);
				return size;
			}
			public Dimension getPreferredSize() {
				return getMinimumSize();
			}
		};
	}

	private static void export(final Reader reader, final int w, final int h, final String formato, final String rutaFichero) throws ParsingErrorException, IOException{
		GeneradorDeSiluetas g = new GeneradorDeSiluetas(reader);
		BufferedImage img = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
		paintProjection(img.createGraphics(), g.vertList, g.lines, w, h);
		ImageIO.write( img, formato, new File( rutaFichero ) );
	}

	public static void export(String filename, final int w, final int h, final String formato, final String rutaFichero) throws ParsingErrorException, IOException{
		export(new FileReader(filename), w, h, formato, rutaFichero);
	}
	
	private static class S{
		
		private static final String Poligono = "Poligono";
		private static final String nLados = "nLados";
		private static final String coordX = "coordX";
		private static final String coordY = "coordY";

		private static final String Punto2F = "Punto2F";
		private static final String x = "x";
		private static final String y = "y";
		
		private static final String unchecked = "unchecked";
	}
	
	private static float[] getArray(Poligono p, String name){
		java.lang.reflect.Field f = Reflexion.findField(Poligono.class, name);
		float[] array = null;
		try {
			boolean accesible = f.isAccessible();
			f.setAccessible(true);
			array = (float[])f.get(p);
			f.setAccessible(accesible);
			return array;
		} catch (IllegalAccessException ignorada) {
			return null;
		}
	}
	
	private static class PoligonoConverter implements Converter {
		@SuppressWarnings(S.unchecked)
		public boolean canConvert(Class clazz) {
			return Poligono.class == clazz;
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Poligono p = (Poligono)value;
			int nLados = p.getNLados();
			writer.addAttribute(S.nLados,( (Integer)nLados ).toString() );
			float coordX[] = getArray(p, S.coordX);
			float coordY[] = getArray(p, S.coordY);
			for(int i = 0; i < nLados; i++){
				writer.startNode(S.Punto2F);
				writer.addAttribute(S.x, ((Float) coordX[i]).toString());
				writer.addAttribute(S.y, ((Float) coordY[i]).toString());
				writer.endNode();
			}
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			int nLados = Integer.parseInt(reader.getAttribute(S.nLados));
			float coordX[] = new float[nLados];
			float coordY[] = new float[nLados];
			int i = 0;
			while( i < nLados && reader.hasMoreChildren() ){
				reader.moveDown();
				coordX[i] = Float.parseFloat(reader.getAttribute(S.x));
				coordY[i] = Float.parseFloat(reader.getAttribute(S.y));
				reader.moveUp();
				i++;
			}
			return new Poligono( coordX, coordY );
		}
	}
	
	private static JComponent getPuntos(JComponent c, Poligono p){
		float coordX[] = getArray(p, S.coordX);
		float coordY[] = getArray(p, S.coordY);
		if(c == null){
			c = new JPanel();
			c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		}
		
		Dimension minSize = new Dimension(5, 20);
		Dimension minSizeT = new Dimension(10, 20);
		Dimension prefSize = new Dimension(10, 20);
		Dimension prefSizeT = new Dimension(20, 20);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 20);
		
		for(int i = 0; i < p.getNLados(); i++){
			JLabel label;
			JTextField textField;
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			
			label = new JLabel("X:");
			label.setMinimumSize(minSizeT);
			label.setMinimumSize(maxSize);
			panel.add(label);
			panel.add(new Box.Filler(minSize, prefSize, maxSize));
			
			textField = new JTextField(Float.toString(coordX[i]),2);
			textField.setMinimumSize(minSizeT);
			textField.setPreferredSize(prefSizeT);
			textField.setMinimumSize(maxSize);
			panel.add(textField);
			panel.add(new Box.Filler(minSize, prefSize, maxSize));
			
			label = new JLabel("Y:");
			label.setMinimumSize(minSizeT);
			label.setMinimumSize(maxSize);
			panel.add(label);
			panel.add(new Box.Filler(minSize, prefSize, maxSize));
			
			textField = new JTextField(Float.toString(coordY[i]),2);
			textField.setMinimumSize(minSizeT);
			textField.setPreferredSize(prefSizeT);
			textField.setMinimumSize(maxSize);
			panel.add(textField);
			
			//panel.add(Box.createHorizontalGlue());
			
			c.add(panel);
		}
		c.add(Box.createVerticalGlue());
		c.setMinimumSize(new Dimension(5, 100));
		c.setPreferredSize(new Dimension(200, 100));
		return c;
	}
	
	
	@SuppressWarnings("serial")
	public static void main(String... args){
		final Poligono poligono = new Poligono( new float[]{-1,-1,1,1}, new float[]{-1,1,1,-1});
		
//		Matrix4d mt = new Matrix4d(
//			1.0, 0.0, 0.0, 0.0,
//			0.0, 1.0, 0.0, 0.0,
//		    0.0, 0.0, 1.0, 0.0,
//			0.0, 0.0, 0.0, 1.0
//		);
		final Matrix4d mt = new Matrix4d(
			0.0, 0.0, 1.0, 0.0,
			0.0, 1.0, 0.0, 0.0,
		   -1.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 1.0
		);
//		Matrix4d mt = new Matrix4d(
//			0.0, 0.0, 1.0, 0.0,
//			1.0, 0.0, 0.0, 0.0,
//		    0.0, 1.0, 0.0, 0.0,
//			0.0, 0.0, 0.0, 1.0
//		);
		
		final JFrame frame = new JFrame("Generador de Siluetas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		final JScrollPane area = new JScrollPane(
				null,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		area.getViewport().setBackground(Color.BLACK);
		gbc.insets.set(5,5,5,5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		frame.getContentPane().add(area, gbc);
		
		JScrollPane scrollPane;
		scrollPane = new JScrollPane(
				getPuntos(null,poligono),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		gbc.insets.set(5,5,5,5);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		frame.getContentPane().add(scrollPane, gbc);
		
		JTextPane tLog = new JTextPane(new DefaultStyledDocument());
		tLog.setEditable(false);
		final PrintStream log1 = Consola.getLog(tLog);
		
		scrollPane = new JScrollPane(
				tLog,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		gbc.insets.set(5,5,5,5);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		frame.getContentPane().add(scrollPane, gbc);
		
		JMenuBar menubar = new JMenuBar();
		
		final JFileChooser chooser = new JFileChooser();
		final File workDir = new File("resources/obj3d/");
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Wavefront .obj","obj"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		JMenu m = new JMenu("Archivo");
		m.add(			
			new AbstractAction("Abrir"){
				@Override
				public void actionPerformed(ActionEvent e) {
					chooser.setCurrentDirectory(workDir);
					chooser.setSelectedFile(new File(""));
					
					int returnVal = chooser.showOpenDialog(frame);
					if( returnVal == JFileChooser.APPROVE_OPTION ){
						File f = chooser.getSelectedFile();
						if(f.getName().toLowerCase().endsWith("obj")){
							try {
								area.setViewportView(new GeneradorDeSiluetas(new FileReader(f), true, mt).getComponent());
							} catch (FileNotFoundException ignorada) {
							}
						}
					}
				}
			}
		);
		m.addSeparator();
		m.add(
			new AbstractAction("Salir"){
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			}
		);

		menubar.add(m);
		
		frame.setJMenuBar(menubar);
		
		frame.setSize(800,450);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		XStream xStream = new XStream(new DomDriver());
		xStream.registerConverter( new PoligonoConverter() );
		xStream.alias(S.Poligono, Poligono.class);
		
		log1.println(xStream.toXML(poligono));

	}
}
