package ecsapplication;

import java.awt.EventQueue;

import java.sql.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;

public class DBConnect extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblEmployee;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBConnect frame = new DBConnect();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DBConnect() {
		setTitle("Equipment Checkout System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		//tblEmployee = new JTable();
		FillTable();
		contentPane.add(tblEmployee);

	}
	
	public void FillTable() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
			String username = "root";
			String password = "devry123";
			
			//Connect to database
			Connection conn = DriverManager.getConnection(url, username, password);
			
			//SQL string
			String strSQL = "SELECT \r\n"
					+ "  t.transactionID,\r\n"
					+ "  e.empName AS employeeName,\r\n"
					+ "  e.skillClassification AS EmployeeSkill,\r\n"
					+ "  eq.equipmentName,\r\n"
					+ "  eq.requiredSkill AS RequiredSkill,\r\n"
					+ "  eq.equipmentCondition,\r\n"
					+ "  eq.equipStatus,\r\n"
					+ "  o.orderDate,\r\n"
					+ "  t.borrowDate,\r\n"
					+ "  t.expectedReturnDate,\r\n"
					+ "  t.transactionStatus\r\n"
					+ "FROM transaction t\r\n"
					+ "JOIN employee e ON t.empID = e.empID\r\n"
					+ "JOIN equipment eq ON t.equipmentID = eq.equipmentID\r\n"
					+ "JOIN `order` o ON t.orderID = o.orderID;\r\n"
					+ "";
			
			//statement
			Statement s = conn.createStatement();
			
			//Result set
			ResultSet rs = s.executeQuery(strSQL);
			
			//Jtable(vector, vector)
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			
			Vector<String> columnNames = new Vector<String>();
			for(int column=1; column <=columnCount; column++) {
				columnNames.add(metadata.getColumnLabel(column));
			}
			
			//data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for(int columnIndex=1; columnIndex<=columnCount; columnIndex++) {
					vector.add(rs.getObject(columnIndex));
				}
				data.add(vector);
			}
			
			tblEmployee = new JTable(data, columnNames);
			
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.print("Could not connect to the database");
			}
	}
}
