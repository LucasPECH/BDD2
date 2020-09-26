package Application;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	
	JLabel labelAdmin = new JLabel("Bienvenue !");
	JButton buttonAddPatient = new JButton("Inscription d'un nouveau patient");
	JButton buttonAddMeeting = new JButton("Ajouter un rendez-vous");
	JButton buttonHistoricMeeting = new JButton("Voir mes rendez-vous anterieurs et futur");
	JButton buttonWeeklyOrDailyMeeting = new JButton("Voir mes rendez-vous pour une semaine / journée spécifique");
	
	public AdminWindow(Connection conn)
	{		
		super("Administrateur");
		
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		setLayout(new GridLayout(5,2,5,5));
			
		buttonHistoricMeeting.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new AdminAllMeetingWindow(conn).setVisible(true);
				}
			}
		);
		
		buttonAddMeeting.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new AddMeetingWindow(conn).setVisible(true);
				}
			}
		);	
		
		buttonAddPatient.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					new AddWindow(conn).setVisible(true);
				}
			}
		);	
		
		buttonWeeklyOrDailyMeeting.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				new AdminWeeklyOrDaylyMeeting(conn).setVisible(true);
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

		
		add(labelAdmin);
		add(buttonAddPatient);
		add(buttonAddMeeting);
		add(buttonHistoricMeeting);
		add(buttonWeeklyOrDailyMeeting);
	}
}