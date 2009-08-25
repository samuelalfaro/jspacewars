package tips;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.*;

import org.sam.util.Tipografias;

public class PruebaHTML {
	
	static public void main(String args[]){
		Tipografias.loadFontsInDir("resources/fonts/");
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		try{
			textPane.setPage(new URL("file:"+"resources/html/prueba.html"));
		}catch(Exception e){
			System.out.println("Error cargando pagina");
			e.printStackTrace();
		}

		JScrollPane sp = new JScrollPane(textPane,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JFrame frame = new JFrame("Prueba HTML");

		frame.setSize(400,400);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(sp,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}
