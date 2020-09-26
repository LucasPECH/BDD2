package Application;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PatientWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	
	JLabel labelPatient = new JLabel("Bienvenue !");
	JButton buttonPatient = new JButton("Voir mes rdv antérieurs et futurs");
	
	public PatientWindow(Connection conn, int patient_id)
	{	
		super("Patient");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		setLocationRelativeTo(null);
		
		setResizable(false);
		
		setLayout(new GridLayout(2,2,5,5));
			
		buttonPatient.addActionListener(new ActionListener()
		{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new PatientAllMeetingWindow(conn, patient_id).setVisible(true);		
				}
		});
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				if(JOptionPane.showConfirmDialog(null, "Confirmer la fermeture ?", "Fermeture", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					try 
					{
						conn.close();
					} 
					catch (SQLException e) 
					{
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
					System.exit(0);
				}
			}
		});
		
		add(labelPatient);
		add(buttonPatient);
	}
}