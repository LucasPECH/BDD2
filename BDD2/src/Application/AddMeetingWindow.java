package Application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMeetingWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	
	private JLabel startTimestampLabel = new JLabel("Start date (DD-MM-YYYY HH:mm:ss) :");
	private JLabel endTimestampLabel = new JLabel("End date (DD-MM-YYYY HH:mm:ss) :");
	private JLabel patientLabel = new JLabel("Enter the name of the patient :");
	private JTextField startTimestampField = new JTextField(20);
	private JTextField endTimestampField = new JTextField(20);
	private JButton confirmationButton = new JButton("Add Meeting");
	private JComboBox<JComboItem> patientSelection = new JComboBox<JComboItem>();
	
	public AddMeetingWindow (Connection conn) {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new GridLayout(4,2,5,5));
		
		confirmationButton.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addMeetingDB(conn, startTimestampField.getText(), endTimestampField.getText());
				}
			}
		);
		
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT PATIENTID_PATIENT, PRENOM_PATIENT, NOM_PATIENT FROM PATIENT");
			ResultSet result = statement.executeQuery();
			
	        while (result.next()) {
	        	patientSelection.addItem(
        			new JComboItem(result.getString("NOM_PATIENT").toUpperCase() + ' ' + result.getString("PRENOM_PATIENT"), result.getInt("PATIENTID_PATIENT"))
    			);
        	}
	        
	        result.close();
			statement.close();
		} catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		
		add(startTimestampLabel);
		add(startTimestampField);
		add(endTimestampLabel);
		add(endTimestampField);
		add(patientLabel);
		add(patientSelection);
		add(new JLabel(""));
		add(confirmationButton);
	}
	
	public void addMeetingDB (Connection conn, String startTimestamp, String endTimestamp) {
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT CRENEAUXID_CRENEAU FROM CRENEAU WHERE DATEDEBUT_CRENEAU='" + startTimestamp + "' AND DATEFIN_CRENEAU='" + endTimestamp + "'");
			ResultSet result = statement.executeQuery();
			long slotId;
			if (result.isBeforeFirst()) {
				result.next();
				slotId = result.getLong("CRENEAUXID_CRENEAU");
			} else {
				SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat newFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				java.util.Date startTime = newFormatter.parse(startTimestamp);
				java.util.Date endTime = newFormatter.parse(endTimestamp);
				System.out.println(startTime.getHours());
				if (startTime.getHours() < 8 || startTime.getHours() > 19 || endTime.getHours() < 8 || endTime.getHours() > 19) {
					JOptionPane.showMessageDialog(null, "La psy ne peut travailler qu'entre 8H00 et 20H00");
					return;
				}
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(startTime);
			    if(cal.get(Calendar.DAY_OF_WEEK) == 1) {
					JOptionPane.showMessageDialog(null, "La psy ne travaille pas le dimanche");
					return;
			    }

				long totalWorkTime = (endTime.getTime() - startTime.getTime()) / 1000;
				// Start and end time now represent 
				startTime.setHours(0);
				startTime.setMinutes(0);
				startTime.setSeconds(0);
				endTime.setHours(23);
				endTime.setMinutes(59);
				endTime.setSeconds(59);
				statement = conn.prepareStatement("SELECT DATEDEBUT_CRENEAU, DATEFIN_CRENEAU FROM CRENEAU WHERE DATEDEBUT_CRENEAU>'" + newFormatter.format(startTime) + "' AND DATEDEBUT_CRENEAU<'" + newFormatter.format(endTime) + "'");
				result = statement.executeQuery();
				
				while (result.next()) {
					java.util.Date endSlotTime = oldFormatter.parse(result.getString("DATEDEBUT_CRENEAU"));
					java.util.Date startSlotTime = oldFormatter.parse(result.getString("DATEDEBUT_CRENEAU"));
					totalWorkTime += (endSlotTime.getTime() - startSlotTime.getTime()) / 1000;
				}
				if (totalWorkTime > 36000) {
					JOptionPane.showMessageDialog(null, "La psy ne peut pas travailler plus de 10H par jour");
					return;
				}
				
				statement = conn.prepareStatement("INSERT INTO Creneau (DATEDEBUT_CRENEAU, DATEFIN_CRENEAU) VALUES('" + startTimestamp + "','" + endTimestamp + "')", new String[] { "CRENEAUXID_CRENEAU" });
				statement.executeUpdate();
				result = statement.getGeneratedKeys();
				if (result.next()) {
					slotId = result.getLong(1);
				} else {
					result.close();
					statement.close();
					JOptionPane.showMessageDialog(null, "Incorrect syntax");
					return;
				}
			}
			
			statement = conn.prepareStatement("SELECT COUNT(*) FROM CONSULTATION WHERE CRENEAUXID_CRENEAU=" + slotId);
			result = statement.executeQuery();
			result.next();
			if (result.getInt(1) > 2) {
				JOptionPane.showMessageDialog(null, "Impossible d'avoir plus de trois patient sur une consultation");
				return;
			}
			
			statement = conn.prepareStatement(
				"INSERT INTO CONSULTATION (CRENEAUXID_CRENEAU, PATIENTID_PATIENT) VALUES(" + slotId + ", " + ((JComboItem)patientSelection.getSelectedItem()).getValue() + ")"
			);
			result = statement.executeQuery();
			
			if (!result.next()) {
				JOptionPane.showMessageDialog(null, "Incorrect syntax");
			} else {
				result.close();
				statement.close();
				dispose();
				new AddMeetingWindow(conn).setVisible(true);
			}
		} catch (SQLException | ParseException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
