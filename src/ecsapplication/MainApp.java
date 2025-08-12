package ecsapplication;

import java.awt.EventQueue;
import java.sql.*;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainApp extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnExportTransaction;
	private JPanel panel;
	private JComboBox comboEmployees;
	private JButton btnCheckReminder;
	private JButton btnCheckoutEquipment;
	private JButton btnReturnEquipment;
	private JButton btnOrderEquipment;
	private JButton btnCancelOrder;
	private JTabbedPane tabbedPane;
	private JSplitPane splitPane;
	private JTable tblEmployee;
	private JTextArea txtReminder;
	private JTable tblOrders;
	private JScrollPane scrollPane_1;
	private JTable tblEquipment;

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
		
		// Change background color
		contentPane.setBackground(new Color(0, 153, 153));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		// Export Transaction button
		btnExportTransaction = new JButton("Export Transactions");
		// Action listener for export transaction button
		btnExportTransaction.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setDialogTitle("Save Transactions CSV");
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
		                System.out.println("Transactions exported to: " + filePath);
		                JOptionPane.showMessageDialog(null, "Successfully exported Transactions to:\n" + filePath);
		            } catch (IOException ex) {
		                ex.printStackTrace();
		                System.out.println("Failed to export Transactions.");
		                JOptionPane.showMessageDialog(null, "Failed to export Transactions.\nPlease try again.");
		            }
		        }
		    }
		});
		bottomPanel.add(btnExportTransaction);

		// Export Orders button
		JButton btnExportOrders = new JButton("Export Orders");
		btnExportOrders.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setDialogTitle("Save Orders CSV");
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
		                CSVExporter.exportToCSV(tblOrders, filePath);
		                System.out.println("Orders exported to: " + filePath);
		                JOptionPane.showMessageDialog(null, "Successfully exported Orders to:\n" + filePath);
		            } catch (IOException ex) {
		                ex.printStackTrace();
		                System.out.println("Failed to export Orders.");
		                JOptionPane.showMessageDialog(null, "Failed to export Orders.\nPlease try again.");
		            }
		        }
		    }
		});
		bottomPanel.add(btnExportOrders);


		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		
		btnCheckReminder = new JButton("Check Reminder");
		
		// Action listener for CheckReminder button
		btnCheckReminder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				if(selectedEmployee == null) {
					JOptionPane.showMessageDialog(MainApp.this, "Please select an Employee from the drop menu");
					return;
				}
				
				try(Connection conn = DBConnect.getInstance().getConnection()){
					
					// 1. Retrieve all BORROWED transactions for employees
					List<Transaction> borrowedTransaction = TransactionDAO.getBorrowedTransactionsByEmployee(selectedEmployee.getEmpID(), conn);
					
					if(borrowedTransaction.isEmpty()) {
						txtReminder.setText("No borrowed equipment for " + selectedEmployee.getEmpName());
						return;
					}
					
					StringBuilder remindersText = new StringBuilder();
					
					// 2. Iterate through all borrowed transactions
					for(Transaction t : borrowedTransaction) {
						
						// 3. Make a reminder object and register it to Transaction
						Reminder reminder = new Reminder();
						t.registerObserver(reminder);
						
						// 4. Notify Observer -> generateReminder automatically + save to DB
						t.notifyObservers();
						
						// 5. Retrieve reminder msg from object and add it to the display
						remindersText.append(reminder.getReminderMSG()).append("\n");
					}
					
					// 6. Display all reminder in the txtReminder text area
					txtReminder.setText(remindersText.toString());
					
				}catch (SQLException ex) {
					ex.printStackTrace();
					txtReminder.setText("Error loading reminders.");
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
				    JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before checking out equipment.");
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
		
		// order button event listener
		btnOrderEquipment = new JButton("Order");
		btnOrderEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				if (selectedEmployee == null) {
					JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before ordering equipment.");
					return;
				}
				openOrderDialog(selectedEmployee);
			}
		});
		panel.add(btnOrderEquipment);
		
		
		// cancel order button
		btnCancelOrder = new JButton("Cancel");
		btnCancelOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int selectedRow = tblOrders.getSelectedRow();
			    if (selectedRow >= 0) {
			        // Take orderID from the appropriate column
			        int orderID = (int) tblOrders.getValueAt(selectedRow, 0);

			        Employee currentEmployee = (Employee) comboEmployees.getSelectedItem();

			        if (currentEmployee != null) {
			            String resultMSG = currentEmployee.cancelOrder(orderID);
			            JOptionPane.showMessageDialog(MainApp.this, resultMSG);

			            // Refresh table so that the change is visible
			            refreshOrdersTable();
			            refreshEquipmentTable();
			        } else {
			            JOptionPane.showMessageDialog(MainApp.this, "Please select an employee first.");
			        }
			    } else {
			        JOptionPane.showMessageDialog(MainApp.this, "Please select an order to cancel.");
			    }

			}
		});
		panel.add(btnCancelOrder);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				int selectedIndex = tabbedPane.getSelectedIndex();
		        String selectedTitle = tabbedPane.getTitleAt(selectedIndex);
		        if (selectedTitle.equals("Orders")) {
		            fillOrdersTable();
		        }

			}
		});
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		// ========== TRANSACTION PANEL ========== //
		JPanel transactionPanel = new JPanel();
		tabbedPane.addTab("Transactions", transactionPanel);
		transactionPanel.setLayout(new BorderLayout());

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		// --- Employee Table ---
		tblEmployee = new JTable();
		JScrollPane scrollTblEmployee = new JScrollPane(tblEmployee);
		splitPane.setTopComponent(scrollTblEmployee);

		// --- JTextArea Reminder ---
		txtReminder = new JTextArea();
		txtReminder.setEditable(false);
		
		txtReminder.setFont(txtReminder.getFont().deriveFont(Font.ITALIC));
		txtReminder.setForeground(Color.gray);
		
		txtReminder.setBorder(BorderFactory.createTitledBorder("Reminders"));
		
		JScrollPane scrollReminder = new JScrollPane(txtReminder);
		splitPane.setBottomComponent(scrollReminder);

		// --- Set Proportions ---
		splitPane.setDividerLocation(300);
		splitPane.setResizeWeight(0.7);

		transactionPanel.add(splitPane, BorderLayout.CENTER);

		// ========== ORDER PANEL ========== //
		JPanel orderPanel = new JPanel();
		tabbedPane.addTab("Orders", orderPanel);
		orderPanel.setLayout(new BorderLayout());

		scrollPane_1 = new JScrollPane();
		orderPanel.add(scrollPane_1, BorderLayout.CENTER);

		tblOrders = new JTable();
		scrollPane_1.setViewportView(tblOrders);

		// ========== EQUIPMENT PANEL ========== //
		JPanel equipmentPanel = new JPanel();
		equipmentPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("Equipment", equipmentPanel);

		tblEquipment = new JTable();
		JScrollPane scrollPaneEquipment = new JScrollPane(tblEquipment);
		equipmentPanel.add(scrollPaneEquipment, BorderLayout.CENTER);
		
		FillTable();

	}
	
	// order dialog is invoked after employee clicks the "Order" button
	private void openOrderDialog(Employee employee) {
	    JDialog dialog = new JDialog(this, "Order Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(600, 400);
	    dialog.setLocationRelativeTo(null);

	    DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Skill Required"}, 0);
	    JTable table = new JTable(tableModel);

	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	        List<Equipment> equipment = EquipmentDAO.getOrderableEquipmentBySkill(conn, employee.getSkillClassification());
	        for (Equipment eq : equipment) {
	            tableModel.addRow(new Object[]{eq.getEquipmentID(), eq.getEquipmentName(), eq.getRequiredSkill()});
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(dialog, "Failed to load equipment.", "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    JButton btnConfirm = new JButton("Confirm Order");
	    btnConfirm.addActionListener(e -> {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow >= 0) {
	            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
	            try (Connection conn = DBConnect.getInstance().getConnection()) {
	                Equipment equipment = EquipmentDAO.getEquipmentByID(conn, equipmentId);
	                String result = employee.orderEquipment(equipment);
	                JOptionPane.showMessageDialog(dialog, result);
	                dialog.dispose();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        } else {
	            JOptionPane.showMessageDialog(dialog, "Please select equipment first.");
	        }
	    });

	    dialog.getContentPane().setLayout(new BorderLayout());
	    dialog.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
	    dialog.getContentPane().add(btnConfirm, BorderLayout.SOUTH);
	    dialog.setVisible(true);
	}

	
	// Employee return dialog box invoked after return button is pressed
	private void openReturnDialog(Employee employee) {
	    JDialog dialog = new JDialog(MainApp.this, "Return Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(500, 350);
	    dialog.setLocationRelativeTo(this);
	    
	    // Table columns
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
	        
	        int transactionID = (int) table.getValueAt(selectedRow, 0);
	        
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
	        Transaction returnedTxn = employee.returnEquipment(transactionID, condition);
	        
	        if (returnedTxn == null) {
	            JOptionPane.showMessageDialog(dialog, "Return failed. Equipment not found or already returned.");
	            return;
	        }
	        
	        // troubleshoot 
	        System.out.println("=== Transaction Info Before DB Update ===");
	        System.out.println("Transaction ID: " + returnedTxn.getTransactionID());
	        System.out.println("Transaction Status: " + returnedTxn.getTransactionStatus());
	        System.out.println("Return Date: " + returnedTxn.getReturnDate());
	        System.out.println("Equipment ID: " + returnedTxn.getEquipment().getEquipmentID());
	        System.out.println("Equipment Status: " + returnedTxn.getEquipment().getStatus());
	        System.out.println("Equipment Condition: " + returnedTxn.getEquipment().getEquipmentCondition());

	        
	        Connection conn = null;
	        try {
	            conn = DBConnect.getInstance().getConnection();
	            conn.setAutoCommit(false);
	            
	            

	            TransactionDAO.updateTransactionReturn(conn, returnedTxn);
	            EquipmentDAO.updateEquipment(conn, returnedTxn.getEquipment());

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
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	List<Equipment> equipmentList = EquipmentDAO.getAvailableEquipmentBySkill(conn, employee.getSkillClassification());
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
	        	try (Connection conn = DBConnect.getInstance().getConnection()){
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
	
	
	
	// Load employees into ComboBox
	private void loadEmployeesIntoComboBox() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	        java.util.List<Employee> employees = EmployeeDAO.getAllEmployees(conn);
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

	// Displays employee transactions under 'Transactions' panel 
	public void FillTable() {
		try (Connection conn = DBConnect.getInstance().getConnection()) {
	        List<Transaction> transactions = TransactionDAO.getAllTransactions(conn);
	        
	       
	        String[] columnNames = {
	            "Transaction ID", "Employee Name", "Employee Skill",
	            "Equipment Name", "Required Skill", "Equipment Condition",
	            "Order Date", "Borrow Date", "Expected Return Date",
	            "Transaction Status"
	        };
	        
	        // make a two dimensional data object for JTable from list of transactions
	        Object[][] data = new Object[transactions.size()][columnNames.length];
	        
	        for (int i = 0; i < transactions.size(); i++) {
	            Transaction t = transactions.get(i);
	            data[i][0] = t.getTransactionID();
	            data[i][1] = t.getEmployee().getEmpName();
	            data[i][2] = t.getEmployee().getSkillClassification().name();
	            data[i][3] = t.getEquipment().getEquipmentName();
	            data[i][4] = t.getEquipment().getRequiredSkill().name();
	            data[i][5] = t.getEquipment().getEquipmentCondition().name();
	            data[i][6] = t.getOrder() != null ? t.getOrder().getOrderDate() : null;
	            data[i][7] = t.getBorrowDate();
	            data[i][8] = t.getExpectedReturnDate();
	            data[i][9] = t.getTransactionStatus().name();
	        }
	        
	        // Set new model to JTable
	        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	            private static final long serialVersionUID = 1L;
	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        tblEmployee.setModel(model);

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Failed to load transactions from DB");
	    }
	}
	
	// Displays employee orders under 'Orders' panel
	public void fillOrdersTable() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	        List<Order> orders = OrderDAO.getAllOrders(conn);

	        String[] columnNames = {"Order ID", "Employee Name", "Equipment Name", "Order Date", "Status"};
	        Object[][] data = new Object[orders.size()][columnNames.length];

	        for (int i = 0; i < orders.size(); i++) {
	            Order o = orders.get(i);
	            data[i][0] = o.getOrderID();
	            data[i][1] = o.getEmployee().getEmpName();
	            data[i][2] = o.getEquipment().getEquipmentName();
	            data[i][3] = o.getOrderDate();
	            data[i][4] = o.getOrderStatus().name();
	        }

	        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        tblOrders.setModel(model);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	private void refreshOrdersTable() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	        List<Order> orders = OrderDAO.getAllOrders(conn);

	        String[] columnNames = { "Order ID", "Employee", "Equipment", "Order Date", "Status", "Pickup Date" };
	        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

	        for (Order order : orders) {
	            Object[] row = {
	                order.getOrderID(),
	                order.getEmployee().getEmpName(),
	                order.getEquipment().getEquipmentName(),
	                order.getOrderDate(),
	                order.getOrderStatus(),
	                order.getPickUpDate()
	            };
	            model.addRow(row);
	        }

	        tblOrders.setModel(model);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Failed to refresh orders.");
	    }
	}
	
	 private void refreshEquipmentTable() {
	        try (Connection conn = DBConnect.getInstance().getConnection()) {
	            List<Equipment> equipmentList = EquipmentDAO.getAllEquipment(conn);

	            String[] columnNames = { "Equipment ID", "Name", "Status", "Required Skill" };
	            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

	            for (Equipment eq : equipmentList) {
	                Object[] row = {
	                    eq.getEquipmentID(),
	                    eq.getEquipmentName(),
	                    eq.getStatus(),
	                    eq.getRequiredSkill()
	                };
	                model.addRow(row);
	            }
	            tblEquipment.setModel(model);

	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Failed to load equipment.");
	        }
	    }
}
