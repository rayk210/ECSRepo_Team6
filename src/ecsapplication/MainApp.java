package ecsapplication;

import java.awt.EventQueue;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

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
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;

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
	private JButton btnCheckoutEquipment;
	private JButton btnReturnEquipment;

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
		
		// load Employee
		loadEmployeesIntoComboBox();
		comboEmployees.setSelectedIndex(-1);
		
		
		// checkout equipment button event handler
		btnCheckoutEquipment = new JButton("Check Out Equipment");
		btnCheckoutEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				if (selectedEmployee != null) {
				    openCheckoutDialog(selectedEmployee);
				} else {
				    txtReminder.setText("Please select an employee before checking out equipment.");
				}
			}
		});
		panel.add(btnCheckoutEquipment);
		
		
		// return equipment button
		btnReturnEquipment = new JButton("Return Equipment");
		btnReturnEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				if (selectedEmployee == null) {
				    JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before returning equipment.");
				    return;
				}
				openReturnDialog(selectedEmployee);
			}
		});
		panel.add(btnReturnEquipment);
		
		//loadLatestReminder(4);

	}
	
	// Employee return dialog box invoke after return button is pressed
	private void openReturnDialog(Employee employee) {
	    JDialog dialog = new JDialog(this, "Return Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(500, 350);
	    dialog.setLocationRelativeTo(this);
	    
	    // Table column
	    String[] columns = {"Transaction ID", "Equipment ID", "Equipment Name", "Borrow Date", "Expected Return Date"};
	    
	    // Find employee transactions with a borrowed status
	    List<Transaction> borrowedTxns = employee.getEmpTransaction().stream()
	        .filter(t -> t.getTransactionStatus() == TransactionStatus.Borrowed)
	        .toList();
	    
	    // Data for table
	    Object[][] data = new Object[borrowedTxns.size()][columns.length];
	    for (int i = 0; i < borrowedTxns.size(); i++) {
	        Transaction t = borrowedTxns.get(i);
	        data[i][0] = t.getTransactionID();
	        data[i][1] = t.getEquipment().getEquipmentID();
	        data[i][2] = t.getEquipment().getEquipmentName();
	        data[i][3] = t.getBorrowDate();
	        data[i][4] = t.getExpectedReturnDate();
	    }
	    
	    JTable table = new JTable(data, columns);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    JScrollPane scrollPane = new JScrollPane(table);
	    
	    // button to confirm the return
	    JButton btnConfirmReturn = new JButton("Confirm Return");
	    btnConfirmReturn.addActionListener(e -> {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(dialog, "Please select equipment to return.");
	            return;
	        }
	        
	        int equipmentID = (int) table.getValueAt(selectedRow, 1);
	        
	        // ask employee to input the condition of the equipment when returning
	        EquipmentCondition condition = (EquipmentCondition) JOptionPane.showInputDialog(
	            dialog,
	            "Select condition of the equipment:",
	            "Equipment Condition",
	            JOptionPane.QUESTION_MESSAGE,
	            null,
	            EquipmentCondition.values(),
	            EquipmentCondition.values()[0]
	        );
	        
	        if (condition == null) {
	            JOptionPane.showMessageDialog(dialog, "Return cancelled: No condition selected.");
	            return;
	        }
	        
	        // call returnEquipment from Employee class
	        Transaction returnedTxn = employee.returnEquipment(equipmentID, condition);
	        if (returnedTxn == null) {
	            JOptionPane.showMessageDialog(dialog, "Return failed. Equipment not found or already returned.");
	            return;
	        }
	        
	        Connection conn = null;
	        try {
	            conn = DBConnect.getConnection();
	            conn.setAutoCommit(false);

	            DBConnect.updateTransactionReturn(conn, returnedTxn);
	            DBConnect.updateEquipment(conn, returnedTxn.getEquipment());

	            conn.commit();

	            JOptionPane.showMessageDialog(dialog, "Equipment returned successfully.");
	            dialog.dispose();

	            FillTable();
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            if (conn != null) {
	                try {
	                    conn.rollback();
	                } catch (SQLException rollbackEx) {
	                    rollbackEx.printStackTrace();
	                }
	            }
	            JOptionPane.showMessageDialog(dialog, "Return failed: " + ex.getMessage());
	        } finally {
	            if (conn != null) {
	                try {
	                    conn.setAutoCommit(true);  // reset autocommit
	                    conn.close();
	                } catch (SQLException closeEx) {
	                    closeEx.printStackTrace();
	                }
	            }
	        }

	    });
	    
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(scrollPane, BorderLayout.CENTER);
	    panel.add(btnConfirmReturn, BorderLayout.SOUTH);
	    
	    dialog.setContentPane(panel);
	    dialog.setVisible(true);
	}
	
	// Employee checkout dialog box invoked after checkout button is pressed with selected employee
	private void openCheckoutDialog(Employee employee) {
	    // JDialog model
	    JDialog dialog = new JDialog(this, "Check Out Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(400, 300);
	    dialog.setLocationRelativeTo(this);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	    // table columns
	    String[] columnNames = {"ID", "Name", "Condition", "Status", "Required Skill"};
	    
	    // retrieve data from mySql
	    // two dimensional array used save data from equipment list
	    // [i] = equipment whereas second [] represents column
	    Object[][] data = new Object[0][];
	    try (Connection conn = DBConnect.getConnection()) {
	    	List<Equipment> equipmentList = DBConnect.getAvailableEquipmentBySkill(conn, employee.getSkillClassification());
	    	data = new Object[equipmentList.size()][5];
	    	
	    	// display message if no equipment is available for an employees skill
	    	if(equipmentList.isEmpty()) {
	    		JOptionPane.showMessageDialog(null, 
	    				"No available equipment for: " + employee.getSkillClassification(),
	    				"No equipment found",
	    				JOptionPane.INFORMATION_MESSAGE);
	    		return;
	    	}
	    	
	    	for(int i=0; i < equipmentList.size(); i++) {
	    		Equipment eq = equipmentList.get(i);
	    		data[i][0] = eq.getEquipmentID();
	    		data[i][1] = eq.getEquipmentName();
	    		data[i][2] = eq.getEquipmentCondition().name();   // name() to return enum constant
	    		data[i][3] = eq.getStatus().name();
	    		data[i][4] = eq.getRequiredSkill().name();
	    	}
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    	JOptionPane.showMessageDialog(dialog, "Failed to load equipment data");
	    }

	    JTable table = new JTable(data, columnNames);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    JScrollPane scrollPane = new JScrollPane(table);

	    // confirm checkout button
	    JButton btnConfirm = new JButton("Confirm Checkout");
	    btnConfirm.addActionListener(e -> {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(dialog, "Please select an equipment to check out.");
	            return;
	        }
	        // display ID and name that is chosen
	        int equipmentID = (int) table.getValueAt(selectedRow, 0);
	        String equipmentName = (String) table.getValueAt(selectedRow, 1);
	        String equipmentCondition = (String) table.getValueAt(selectedRow, 2);
	        String equipmentStatus = (String) table.getValueAt(selectedRow, 3);
	        String requiredSkill = (String) table.getValueAt(selectedRow, 4);
	        
	        Equipment selectedEquipment = new Equipment(
	                equipmentID,
	                equipmentName,
	                EquipmentCondition.valueOf(equipmentCondition),
	                EquipmentStatus.valueOf(equipmentStatus),
	                SkillClassification.valueOf(requiredSkill)
	            );
	        
	        if (employee == null || selectedEquipment == null) {
	            JOptionPane.showMessageDialog(dialog, "Please select both an employee and equipment.");
	            return;
	        }
	        
	        Transaction newTxn = employee.checkOut(selectedEquipment);
	        
	        // Confirmation from employee
	        int confirm = JOptionPane.showConfirmDialog(dialog,
    		        "Are you sure you want to check out:\n" + equipmentName + " (ID: " + equipmentID + ")",
    		        "Confirm Checkout", JOptionPane.YES_NO_OPTION);
	        
	        if(confirm == JOptionPane.YES_OPTION) {
	        	try (Connection conn = DBConnect.getConnection()){
	        		conn.setAutoCommit(false);
	        		
	        		// Update equipment status to loaned after checked out
	        		String updateSQL = "UPDATE equipment SET equipStatus = 'Loaned' WHERE equipmentID = ?";
	        		try (PreparedStatement stmtUpdate = conn.prepareStatement(updateSQL)){
	        			stmtUpdate.setInt(1, equipmentID);
	        			stmtUpdate.executeUpdate();  // executes statement of DML
	        		}
	        		
	        		// Insert new transaction
	        		// records who and when equipment was checked out
	        		String insertSQL = "INSERT INTO transaction (empID, equipmentID, borrowDate, expectedReturnDate, transactionStatus)" +
	        		                   "VALUES (?, ?, ?, ?, ?)";
	        		try (PreparedStatement stmtInsert = conn.prepareStatement(insertSQL)){
	        			stmtInsert.setInt(1, newTxn.getEmployee().getEmpID());
	        			stmtInsert.setInt(2, newTxn.getEquipment().getEquipmentID());
	        			stmtInsert.setDate(3, newTxn.getBorrowDate() != null ? java.sql.Date.valueOf(newTxn.getBorrowDate()) : null);
	        			stmtInsert.setDate(4, newTxn.getExpectedReturnDate() != null ? java.sql.Date.valueOf(newTxn.getExpectedReturnDate()) : null);
	        			stmtInsert.setString(5, newTxn.getTransactionStatus().name());
	        			stmtInsert.executeUpdate();
	        		}
	        		
	        		conn.commit();    // saved changes to database
	        		
	        		JOptionPane.showMessageDialog(dialog, "Successful checkout for: " + equipmentName);
	        		dialog.dispose();
	        		
	        		FillTable();
	        		
	        	}catch (Exception ex) {
	        		ex.printStackTrace();
	        		JOptionPane.showMessageDialog(dialog, "Checkout failed. Please try again.");
	        		JOptionPane.showMessageDialog(dialog, "SQL Error: " + ex.getMessage());
	        	}
	        }
	        
	        // selected attributes are displayed in the MessageDialog
	        JOptionPane.showMessageDialog(dialog,
	            "Selected Equipment:\nID: " + equipmentID + "\nName: " + equipmentName + "\n" + "Equipment Condition: "
	            		+ equipmentCondition +"\n" + "Equipment Status: " + equipmentStatus + "\n" + "Required Skill: "
	            		+ requiredSkill);

	        // Close dialog after confirmation
	        dialog.dispose();
	    });

	    // Adjust layout dialog
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(scrollPane, BorderLayout.CENTER);
	    panel.add(btnConfirm, BorderLayout.SOUTH);

	    dialog.setContentPane(panel);
	    dialog.setVisible(true);
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
	            System.out.println("Employee: " + emp.getEmpName());
	            System.out.println("Transactions:");
	            for (Transaction t : emp.getEmpTransaction()) {
	                System.out.println("TxnID: " + t.getTransactionID() +
	                    ", Equipment: " + t.getEquipment().getEquipmentName() +
	                    ", Status: " + t.getTransactionStatus());
	            }
	            System.out.println("-----");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void FillTable() {
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
				    + "LEFT JOIN `order` o ON t.orderID = o.orderID\r\n"
				    + "LEFT JOIN reminder r ON t.transactionID = r.transactionID;";
			
			
			try (Connection conn = DBConnect.getConnection();
			         Statement s = conn.createStatement();
			         ResultSet rs = s.executeQuery(strSQL)) {

			        ResultSetMetaData metadata = rs.getMetaData();
			        int columnCount = metadata.getColumnCount();

			        Vector<String> columnNames = new Vector<>();
			        for (int column = 1; column <= columnCount; column++) {
			            columnNames.add(metadata.getColumnLabel(column));
			        }

			        Vector<Vector<Object>> data = new Vector<>();
			        while (rs.next()) {
			            Vector<Object> vector = new Vector<>();
			            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			                vector.add(rs.getObject(columnIndex));
			            }
			            data.add(vector);
			        }

			        DefaultTableModel model = new DefaultTableModel(data, columnNames);
			        tblEmployee.setModel(model);

			    } catch (Exception e) {
			        e.printStackTrace();
			        System.out.println("Could not connect to the database");
			    }
	}
}
