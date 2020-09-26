package Application;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class PatientAllMeetingWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	
	public PatientAllMeetingWindow(Connection conn, int patient_id)
	{
		super("Historique des rendez-vous");
		
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		setLayout(new GridLayout(1,1));
		
		getAllMeeting(conn, patient_id);
	
	}
	
	public void getAllMeeting(Connection conn, int patient_id)
	{
		
		DefaultTableModel model = new DefaultTableModel();
		
		model.addColumn("DATE");
		model.addColumn("DEBUT");
        model.addColumn("FIN");
        model.addColumn("PRIX");
        model.addColumn("REGLEMENT");
        model.addColumn("DATE REGLEMENT");
		
		
		try 
		{
			PreparedStatement stmt1 = conn.prepareStatement("SELECT DATEDEBUT_CRENEAU, DATEFIN_CRENEAU, PRIX_CONSULTATION, TYPEREGLEMENT_CONSULTATION, DATEREGLEMENT_CONSULTATION FROM CONSULTATION CON JOIN CRENEAU CRE ON CRE.CRENEAUXID_CRENEAU = CON.CRENEAUXID_CRENEAU WHERE PATIENTID_PATIENT = ?");
			stmt1.setInt(1, patient_id);
		
			ResultSet rset1 = stmt1.executeQuery();
			
	        while(rset1.next())
	        {
	            model.addRow(new Object[]{rset1.getDate("DATEDEBUT_CRENEAU"), rset1.getTime("DATEDEBUT_CRENEAU"), rset1.getTime("DATEFIN_CRENEAU"),rset1.getInt("PRIX_CONSULTATION"), rset1.getString("TYPEREGLEMENT_CONSULTATION"),rset1.getDate("DATEREGLEMENT_CONSULTATION")});
	        }
	        
			JTable table = new JTable(model);
			
			add(new JScrollPane(table));
		} 
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
