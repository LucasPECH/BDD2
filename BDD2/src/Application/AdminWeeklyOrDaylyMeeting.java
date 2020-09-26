package Application;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class AdminWeeklyOrDaylyMeeting extends JFrame
{	
	private static final long serialVersionUID = 3601234845868338245L;
	private final int WINDOW_WIDTH = 1200;
	private final int WINDOW_HEIGHT = 1000;
	private final JFrame frame = this; // Needed to access the frame inside the action listenener
	
	private JDatePickerImpl datePicker;
	private JButton buttonOneDay = new JButton("Voir les rendez-vous de ce jour");
	private JButton buttonOneWeek = new JButton("Voir les rendez-vous de cette semaine");
	private JPanel resultPanel;
	
	public AdminWeeklyOrDaylyMeeting(Connection conn)
	{	
		super("Liste des rendez-vous");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		setBasicProperties();
		
		buttonOneDay.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
				
				if(selectedDate != null)
				{
					frame.getContentPane().removeAll();
					setBasicProperties();
					resultPanel.setLayout(new GridLayout(1,1));
					
					java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
				
					seeMeetingGivenDay(conn, sqlDate, 0);
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Veuillez entrer une date");
				}
			}
		});
		
		buttonOneWeek.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
				java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
				
				if(selectedDate != null)
				{
					frame.getContentPane().removeAll();
					setBasicProperties();
					resultPanel.setLayout(new GridLayout(7,1));
					
					java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
				
					seeMeetingGivenDay(conn, sqlDate, 0); // We display the chosen day and the 6 following day
					seeMeetingGivenDay(conn, sqlDate, 1);
					seeMeetingGivenDay(conn, sqlDate, 2);
					seeMeetingGivenDay(conn, sqlDate, 3);
					seeMeetingGivenDay(conn, sqlDate, 4);
					seeMeetingGivenDay(conn, sqlDate, 5);
					seeMeetingGivenDay(conn, sqlDate, 6);	
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Veuillez entrer une date");
				}
			}
		});
	}
	
	public void setBasicProperties()
	{	
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		Properties p = new Properties();
		
		p.put("text.today", "today");
		p.put("text.month", "month");
		p.put("text.year", "year");
		
		UtilDateModel model1 = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model1, p);
		datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
			
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		add(datePicker, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		add(buttonOneDay, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1;
		add(buttonOneWeek, c);
		
		resultPanel = new JPanel();
		
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		add(resultPanel,c);
		
	}
	
	public void seeMeetingGivenDay(Connection conn, java.sql.Date sqlDate, int nbDayToAdd)
	{
		
		try 
		{
			DefaultTableModel model = new DefaultTableModel();
		
			model.addColumn("CLASSIFICATION");
			model.addColumn("PRENOM");
			model.addColumn("NOM");
			model.addColumn("DEBUT");
	        model.addColumn("FIN");
	        model.addColumn("POSTURE");
	        model.addColumn("MOT CLEF");
	        model.addColumn("COMPORTEMENT");
	        model.addColumn("ANXIETE");
	        model.addColumn("PRIX");
	        model.addColumn("REGLEMENT");
	        model.addColumn("DATE REGLEMENT");
	        
			PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM CRENEAU WHERE TRUNC(datedebut_creneau) = TO_CHAR(?+?, 'DD/MM/YYYY')");
			stmt1.setDate(1,sqlDate);
			stmt1.setInt(2,nbDayToAdd);
			ResultSet rs1 = stmt1.executeQuery();
					
			while(rs1.next())
			{
				
				PreparedStatement stmt2 = conn.prepareStatement("SELECT CLASSIFICATION_CONSULTATION, PRENOM_PATIENT, NOM_PATIENT, POSTURE_CONSULTATION, MOT_CLEF_CONSULTATION, COMPORTEMENT_CONSULTATION, NOTEANXIETE_CONSULTATION, PRIX_CONSULTATION, TYPEREGLEMENT_CONSULTATION, DATEREGLEMENT_CONSULTATION FROM CONSULTATION C JOIN PATIENT P ON C.PATIENTID_PATIENT = P.PATIENTID_PATIENT WHERE C.CRENEAUXID_CRENEAU = ?");
				stmt2.setInt(1,rs1.getInt(1));
				ResultSet rs2 = stmt2.executeQuery();
				
				if(rs2.next())
				{
					model.addRow(new Object[]{rs2.getString("CLASSIFICATION_CONSULTATION"), rs2.getString("PRENOM_PATIENT"), rs2.getString("NOM_PATIENT"), rs1.getTime("DATEDEBUT_CRENEAU"), rs1.getTime("DATEFIN_CRENEAU"), rs2.getString("POSTURE_CONSULTATION"), rs2.getString("MOT_CLEF_CONSULTATION"), rs2.getString("COMPORTEMENT_CONSULTATION"), rs2.getInt("NOTEANXIETE_CONSULTATION"), rs2.getInt("PRIX_CONSULTATION"), rs2.getString("TYPEREGLEMENT_CONSULTATION"),rs2.getDate("DATEREGLEMENT_CONSULTATION")});
				}
				
				stmt2.close();
				rs2.close();
			}
			
			JTable table = new JTable(model);
			 
			
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(table); // Using TableColumnAdjuster (opensource code) tool we can easily resize column width
			tca.adjustColumns();
			
			JScrollPane scrollPane = new JScrollPane(table);
			
			java.sql.Date sqlRealDate = new java.sql.Date(sqlDate.getTime() + 24*60*60*1000*nbDayToAdd); // We add day to the current date
			
			scrollPane.setBorder(BorderFactory.createTitledBorder (sqlRealDate+""));
			
			resultPanel.add(scrollPane);
			
			invalidate();
			validate();
			repaint();
			
			stmt1.close();
			rs1.close();
		} 
		catch (SQLException e1) 
		{
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
	}
}
