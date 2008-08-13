package org.sam.pruebas.j3d;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pruebas{
	
	public static void main(String[] args) {
		JFrame ventana = new JFrame( "Pruebas Gráficos 3D" );
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setSize( 800,600 );
		
		JPanel panel = new JPanel(new BorderLayout());
		
		final CardLayout layout = new CardLayout();
		final JPanel escenario = new JPanel(layout);
		
		/* Pruebas */
//		escenario.add(new PruebaFondo(),"1");
//		escenario.add(new PruebaFondo2(),"2");
//		escenario.add(new PruebaTexturasPNG(),"3");
		escenario.add(new PruebaParticulas(),"4");
//		escenario.add(new PruebaCubo(),"5");
//		escenario.add(new J3DBumpDemo(),"6");
//		escenario.add(new PruebaMultitexturas(),"7");
		escenario.add(new PruebaMultitexturasGLSL(),"8");
//		escenario.add(new PruebaSala(),"9");
//		escenario.add(new PruebaCaleidoscopio(),"10");
//		escenario.add(new PruebaTunelMosaico(),"11");
//		escenario.add(new PruebaTunelCilindrico(),"12");
//		escenario.add(new PruebaShaderGLSL(),"13");
		
		
		/* Navegadores*/
		JPanel botones = new JPanel(new GridLayout(1,0));
		
		JButton anterior = new JButton("Anterior");
		anterior.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				layout.previous(escenario);
			}
		});
		botones.add(anterior);
		
		JButton siguiente = new JButton("Siguiente");
		siguiente.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				layout.next(escenario);
			}
		});
		botones.add(siguiente);
		
		panel.add(escenario,BorderLayout.CENTER);
		panel.add(botones,BorderLayout.SOUTH);
		
		ventana.setContentPane(panel);
		ventana.setVisible( true );
	}
}