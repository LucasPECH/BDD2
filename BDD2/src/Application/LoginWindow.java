package Application;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int WINDOW_WIDTH = 300;
	private final int WINDOW_HEIGHT = 150;
	
	private JLabel labelEmail = new JLabel("Email : ");
	private JLabel labelPassword = new JLabel("Mot de passe : ");
	private JTextField textEmail = new JTextField(20);
	private JPasswordField textPassword = new JPasswordField(20);
	private JButton buttonLogin = new JButton("Login");
	
	public LoginWindow(Connection conn)
	{
		super("Login");
		
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		setLayout(new GridLayout(3,2,5,5));
		
		buttonLogin.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Connection(conn);
			}
		});
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				if(JOptionPane.showConfirmDialog(null,  "Confirmer la fermeture ?", "Fermeture", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
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
		
		add(labelEmail);
		add(textEmail);
		add(labelPassword);
		add(textPassword);
		add(new JLabel(""));
		add(buttonLogin);
	}
	
	public void Connection(Connection conn)
	{
		try 
		{		
			if(!textEmail.getText().equals("admin"))
			{	
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PATIENT WHERE email_Patient = ? AND mot_de_passe_Patient = ?");
				stmt.setString(1, textEmail.getText());
				stmt.setString(2, new String(textPassword.getPassword()));
				
				ResultSet rset = stmt.executeQuery();
			
				if(!rset.next())
				{
					JOptionPane.showMessageDialog(null, "Mauvais mail / mot de passe");
				}
				else
				{
					new PatientWindow(conn, rset.getInt("PATIENTID_PATIENT")).setVisible(true);
					dispose();
					rset.close();
					stmt.close();
				}
			}
			else
			{
				String password = new String(textPassword.getPassword());
				if(password.equals("admin"))
				{
					dispose();
					new AdminWindow(conn).setVisible(true);
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Wrong username/password");
				}
			}
		}
		catch (SQLException e1) 
		{
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
	}
	
	public static void main(String[] args) throws SQLException 
	{	
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe","admin","admin");
			
			new LoginWindow(conn).setVisible(true);
		}  
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		catch(ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
