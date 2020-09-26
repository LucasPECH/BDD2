package Application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditMeetingWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	
	private JLabel startTimestampLabel = new JLabel("Start date (DD-MM-YYYY HH:mm:ss) :");
	private JLabel endTimestampLabel = new JLabel("End date (DD-MM-YYYY HH:mm:ss) :");
	private JLabel patientLabel = new JLabel("Enter the name of the patient :");
	private JTextField startTimestampField = new JTextField(20);
	private JTextField endTimestampField = new JTextField(20);
	private JButton confirmationButton = new JButton("Edit Meeting");
	private JComboBox<JComboItem> patientSelection = new JComboBox<JComboItem>();
	
	JLabel consultationPriceLabel = new JLabel("Prix: ");
	JLabel paiementMeanLabel = new JLabel("Moyen de paiement: ");
	JLabel postureLabel = new JLabel("Posture: ");
	JLabel keyWordsLabel = new JLabel("Mots clefs: ");
	JLabel classificationLabel = new JLabel("Classification: ");
	JLabel anxietyGradeLabel = new JLabel("Note d'anxiété: ");
	JLabel paiementDateLabel = new JLabel("Date de paiement: ");
	
	JTextField consultationPriceText = new JTextField();
	JTextField paiementMeanText = new JTextField();
	JTextField postureText = new JTextField();
	JTextField keyWordsText = new JTextField();
	JTextField classificationText = new JTextField();
	JTextField anxietyGradeText = new JTextField();
	JTextField paiementDateText = new JTextField();
	
	public EditMeetingWindow (Connection conn, long slotId, long patientId) {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new GridLayout(3,2,35,5));
		
		ResultSet result;
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT CLASSIFICATION_CONSULTATION, PRENOM_PATIENT, NOM_PATIENT, CON.CRENEAUXID_CRENEAU, CON.PATIENTID_PATIENT, DATEDEBUT_CRENEAU, DATEFIN_CRENEAU, POSTURE_CONSULTATION, MOT_CLEF_CONSULTATION, COMPORTEMENT_CONSULTATION, NOTEANXIETE_CONSULTATION, PRIX_CONSULTATION, TYPEREGLEMENT_CONSULTATION, DATEREGLEMENT_CONSULTATION FROM CONSULTATION CON JOIN PATIENT P ON CON.PATIENTID_PATIENT = P.PATIENTID_PATIENT JOIN CRENEAU CRE ON CRE.CRENEAUXID_CRENEAU = CON.CRENEAUXID_CRENEAU WHERE CON.CRENEAUXID_CRENEAU=" + slotId +" AND P.PATIENTID_PATIENT=" + patientId);
			result = statement.executeQuery();
			result.next();
			
			SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat newFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			try {
				java.util.Date oldStartDate = oldFormatter.parse(result.getString("DATEDEBUT_CRENEAU"));
				startTimestampField.setText(newFormatter.format(oldStartDate));
				
				java.util.Date oldEndDate = oldFormatter.parse(result.getString("DATEFIN_CRENEAU"));
				endTimestampField.setText(newFormatter.format(oldEndDate));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			System.out.println(result.getString("DATEDEBUT_CRENEAU"));
			consultationPriceText.setText(Float.toString(result.getFloat("PRIX_CONSULTATION")));
			paiementMeanText.setText(result.getString("TYPEREGLEMENT_CONSULTATION"));
			postureText.setText(result.getString("POSTURE_CONSULTATION"));
			keyWordsText.setText(result.getString("MOT_CLEF_CONSULTATION"));
			classificationText.setText(result.getString("CLASSIFICATION_CONSULTATION"));
			anxietyGradeText.setText(result.getString("NOTEANXIETE_CONSULTATION"));
			paiementDateText.setText(result.getString("DATEREGLEMENT_CONSULTATION"));
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		
		confirmationButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						editMeetingDB(conn, slotId, patientId);
					}
				}
			);
			
			try {
				PreparedStatement statement = conn.prepareStatement("SELECT PATIENTID_PATIENT, PRENOM_PATIENT, NOM_PATIENT FROM PATIENT");
				result = statement.executeQuery();
				
		        while (result.next()) {
		        	patientSelection.addItem(
	        			new JComboItem(result.getString("NOM_PATIENT").toUpperCase() + ' ' + result.getString("PRENOM_PATIENT"), result.getInt("PATIENTID_PATIENT"))
	    			);
	        	}
		        
		        result.close();
				statement.close();
			} catch (Exception e) {
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

			add(consultationPriceLabel);
			add(consultationPriceText);
			add(paiementMeanLabel);
			add(paiementMeanText);
			add(postureLabel);
			add(postureText);
			add(keyWordsLabel);
			add(keyWordsText);
			add(classificationLabel);
			add(classificationText);
			add(anxietyGradeLabel);
			add(anxietyGradeText);
			add(paiementDateLabel);
			add(paiementDateText);
		}
		
		private void editMeetingDB (Connection conn, long slotId, long patientId) {
			try {				
				Long newSlotId = null;
				PreparedStatement statement = conn.prepareStatement("SELECT CRENEAUXID_CRENEAU FROM CRENEAU WHERE DATEDEBUT_CRENEAU='" + startTimestampField.getText() + "' AND DATEFIN_CRENEAU='" + endTimestampField.getText() + "'");
				ResultSet result = statement.executeQuery();
				if (result.isBeforeFirst()) {
					result.next();
					newSlotId = result.getLong("CRENEAUXID_CRENEAU");
				} else {
					SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat newFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					java.util.Date startTime = newFormatter.parse(startTimestampField.getText());
					java.util.Date endTime = newFormatter.parse(endTimestampField.getText());
					if (startTime.getHours() < 8 || startTime.getHours() > 19 || endTime.getHours() < 8 || endTime.getHours() > 19) {
						JOptionPane.showMessageDialog(null, "La psy ne peut travailler qu'entre 8H00 et 20H00");
						return;
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(startTime);
				    if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
						JOptionPane.showMessageDialog(null, "La psy ne pas travailler le dimanche");
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
						JOptionPane.showMessageDialog(null, "La psy ne peut pas travailler plus de 10H apr jour");
						return;
					}
					
					statement = conn.prepareStatement("INSERT INTO Creneau (DATEDEBUT_CRENEAU, DATEFIN_CRENEAU) VALUES('" + startTimestampField.getText() + "','" + endTimestampField.getText() + "')", new String[] { "CRENEAUXID_CRENEAU" });
					statement.executeUpdate();
					result = statement.getGeneratedKeys();
					if (result.next()) {
						newSlotId = result.getLong(1);
					} else {
						result.close();
						statement.close();
						JOptionPane.showMessageDialog(null, "Incorrect syntax in slot fields");
						return;
					}
				}
				
				statement = conn.prepareStatement(
					"UPDATE CONSULTATION SET "
					+ "CRENEAUXID_CRENEAU=" + newSlotId
					+ ", PATIENTID_PATIENT=" + ((JComboItem)patientSelection.getSelectedItem()).getValue()
					+ ", PRIX_CONSULTATION=" + consultationPriceText.getText()
					+ ", TYPEREGLEMENT_CONSULTATION='" + paiementMeanText.getText()
					+ "', POSTURE_CONSULTATION='" + postureText.getText()
					+ "', MOT_CLEF_CONSULTATION='" + keyWordsText.getText()
					+ "', CLASSIFICATION_CONSULTATION='" + classificationText.getText()
					+ "', NOTEANXIETE_CONSULTATION=" + (anxietyGradeText.getText() == "" ? anxietyGradeText.getText() : "NULL")
					+ ", DATEREGLEMENT_CONSULTATION=" + (paiementDateText.getText() == "" ? paiementDateText.getText() : "NULL")
					+ " WHERE CRENEAUXID_CRENEAU=" + slotId
					+ " AND PATIENTID_PATIENT=" + patientId
				);
				int updatedRowCount = statement.executeUpdate();
				
				statement = conn.prepareStatement("DELETE FROM CRENEAU WHERE NOT EXISTS(SELECT NULL FROM CONSULTATION WHERE CONSULTATION.CRENEAUXID_CRENEAU = CRENEAU.CRENEAUXID_CRENEAU)");
				result = statement.executeQuery();
				
				if (updatedRowCount == 0) {
					JOptionPane.showMessageDialog(null, "Incorrect syntax");
				} else {
					result.close();
					statement.close();
					dispose();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
}
