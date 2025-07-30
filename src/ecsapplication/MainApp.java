package ecsapplication;

import java.awt.EventQueue;
import java.sql.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;

public class MainApp extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollEmp;
	private JButton btnExportCSV;
	private JTable tblEmployee;
	private JScrollPane scrollReminder;
	private JTextArea txtReminder;
	private JPanel panel;
	private JComboBox comboEmployees;
	private JButton btnCheckReminder;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp frame = new MainApp();
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
	public MainApp() {
		setTitle("Equipment Checkout System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		scrollEmp = new JScrollPane();
		//contentPane.add(scrollPane);
		
		tblEmployee = new JTable();
		scrollEmp.setViewportView(tblEmployee);
		
		
		// Fills Employee JTable
		FillTable();
		
		btnExportCSV = new JButton("Export to CSV");
		
		// Action listener for export button
		btnExportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Navigate file system
				JFileChooser fileChooser = new JFileChooser();
			    fileChooser.setDialogTitle("Save CSV file");
			    
			  
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
			    fileChooser.setFileFilter(filter);
			    
			    int userSelection = fileChooser.showSaveDialog(null);
			    
			    if (userSelection == JFileChooser.APPROVE_OPTION) {
			        File fileToSave = fileChooser.getSelectedFile();
			        String filePath = fileToSave.getAbsolutePath();
			        
			       
			        if (!filePath.toLowerCase().endsWith(".csv")) {
			            filePath += ".csv";
			        }
			        
			        try {
			            CSVExporter.exportToCSV(tblEmployee, filePath);
			            System.out.println("CSV successfully was exported to: " + filePath);
			        } catch (IOException ex) {
			            ex.printStackTrace();
			            System.out.println("Failed to save file CSV.");
			        }
			    } else {
			        System.out.println("Export cancelled by user.");
			    }
			}
		});
		contentPane.add(btnExportCSV, BorderLayout.SOUTH);
		
		scrollReminder = new JScrollPane();
		//contentPane.add(scrollPane_1, BorderLayout.CENTER);
		
		txtReminder = new JTextArea();
		scrollReminder.setViewportView(txtReminder);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(scrollEmp);
		splitPane.setBottomComponent(scrollReminder);
		splitPane.setDividerLocation(250);

		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		btnCheckReminder = new JButton("Check Reminder");
		
		// Action listener for CheckReminder button
		btnCheckReminder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
		        if (selectedEmployee != null) {
		            loadLatestReminder(selectedEmployee.getEmpID());
		        } else {
		            txtReminder.setText("Please select an employee.");
		        }
			}
		});
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(btnCheckReminder);
		
		comboEmployees = new JComboBox();
		comboEmployees.setPreferredSize(new java.awt.Dimension(120, 25));
		panel.add(comboEmployees);
		
		loadEmployeesIntoComboBox();
		comboEmployees.setSelectedIndex(-1);
		
		//loadLatestReminder(4);

	}
	
	// Load reminders in txtReminder TextArea
	private void loadLatestReminder(int empID) {
        try (Connection conn = DBConnect.getConnection()) {
            Reminder reminderMessage = DBConnect.getLatestReminderMessage(conn, empID);
            
            Employee emp = DBConnect.getEmployeeByID(conn, empID); 
            String empName = (emp != null) ? emp.getEmpName() : "Unknown Employee";
            if (reminderMessage != null) {
            	String message = reminderMessage.getReminderMSG();
            	txtReminder.setText("Reminder for:" + empName + ":\n" + message);
            }
            else {
            	//String skill = (emp != null) ? emp.getSkillClassification().toString() : "Unknown";
            	txtReminder.setText("No reminder found for:" + empName);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            txtReminder.setText("Failed to load reminder.");
        }
    }
	
	// Load employees into ComboBox
	private void loadEmployeesIntoComboBox() {
	    try (Connection conn = DBConnect.getConnection()) {
	        java.util.List<Employee> employees = DBConnect.getAllEmployees(conn);
	        comboEmployees.removeAllItems();
	        for (Employee emp : employees) {
	        	// Verify employees loaded
	        	System.out.println("Loaded employee: " + emp);
	            comboEmployees.addItem(emp);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void FillTable() {
		try {
			Connection conn = DBConnect.getConnection();
			//SQL string
			String strSQL = "SELECT \r\n"
				    + "  t.transactionID,\r\n"
				    + "  e.empName AS employeeName,\r\n"
				    + "  e.skillClassification AS employeeSkill,\r\n"
				    + "  eq.equipmentName,\r\n"
				    + "  eq.requiredSkill AS requiredSkill,\r\n"
				    + "  eq.equipmentCondition,\r\n"
				    + "  eq.equipStatus,\r\n"
				    + "  o.orderDate,\r\n"
				    + "  t.borrowDate,\r\n"
				    + "  t.expectedReturnDate,\r\n"
				    + "  t.transactionStatus,\r\n"
				    + "  r.reminderDate,\r\n"
				    + "  r.reminderMSG \r\n"
				    + "FROM transaction t\r\n"
				    + "JOIN employee e ON t.empID = e.empID\r\n"
				    + "JOIN equipment eq ON t.equipmentID = eq.equipmentID\r\n"
				    + "JOIN `order` o ON t.orderID = o.orderID\r\n"
				    + "LEFT JOIN reminder r ON t.transactionID = r.transactionID;";
			
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
			
			DefaultTableModel model = new DefaultTableModel(data, columnNames);
	        tblEmployee.setModel(model);
			
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.print("Could not connect to the database");
			}
	}

}
