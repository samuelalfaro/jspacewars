package org.sam.tips;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PruebaJList{
	
	static public void main(String args[]){
		
		JFrame frame = new JFrame("Prueba JList");

		frame.setSize(300,300);
		frame.setDefaultCloseOperation(3);
		
		ObjetoPrueba objetos[] = new ObjetoPrueba[5];
		objetos[0] = new ObjetoPrueba(1,"Uno",15.4,false);
		objetos[1] = new ObjetoPrueba(2,"Dos",25.3,true);
		objetos[2] = new ObjetoPrueba(3,"Tres",28.8,false);
		objetos[3] = new ObjetoPrueba(4,"Cuatro",36.7,true);
		objetos[4] = new ObjetoPrueba(5,"Cinco",5.7,false);
		
		final JComboBox combo = new javax.swing.JComboBox(objetos);
		combo.setSelectedIndex(0);
		
		JScrollPane scroll=new JScrollPane(combo);
		
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ObjetoPrueba seleccion = (ObjetoPrueba)combo.getItemAt(combo.getSelectedIndex());
					seleccion.abrirVentanaPropiedades();
				}
			}
		});
		
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(scroll);
		frame.setVisible(true);
	}
}
