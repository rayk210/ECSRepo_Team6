/**
 * MainApp.java
 * Entry point for the ECS system.
 * The ECS system is a Java-based desktop application that's designed to manage the life-cycle
 * of equipment in GB manufacturing.
 * It supports the following features:
 *   -Checkout Equipment
 *   -Return Equipment
 *   -Order Equipment
 *   -Receive Reminders
 *   -View Records
 *   
 * The program adheres to the following design patterns:
 * 1. Singleton: DBConnect class
 *   -Ensures only one instance of DBConnect exist throughout the application.
 *   -Provides a global access point to the database connection.
 *   -Helps manage resources efficiently and prevents multiple connection.
 *   
 * 2. Observer: Reminder class (Observer) and Transaction class (Subject)
 *   -Reminder objects observe changes in Transaction objects.
 *   -When a Transaction is updated (e.g., Borrowed or Returned), all registered
 *    Reminders are notified.
 *  
 * This class launches the GUI that's built using the Java Swing toolkit.
 * The Swing components include:
 *   JFrame: main application window
 *   JPanel: used for organizing sections of the UI
 *   JTable: display data related to employees, equipment, transactions, orders and reminders
 *   JDialog: modal dialog that appear during actions performed
 *   JTextArea: display reminders
 *   JScrollPane: gives the ability to scroll with tables
 */

// ===============================================
// Import statements for Java core libraries,
// Swing components, database connectivity,
// and custom enumerations used in ECS application
// ===============================================
package ecsapplication;

import java.awt.EventQueue;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

// MainApp is the main application window
// It extends JFrame, meaning this class inherits
// all behaviors of a standard Swing window frame
public class MainApp extends JFrame {

	// ==============================
	// UI Swing Components and Fields
	// ==============================
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnExportTransaction;
	private JPanel panel;
	private JComboBox<Employee> comboEmployees;
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
	private JTable tblViewRecord;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		// Schedule this application to run on the Event Dispatch Thread (EDT)
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					// Create an instance of the MainApp window
					MainApp frame = new MainApp();
					
					// Set the application window as visible
					frame.setVisible(true);
				} catch (Exception e) {
					
					// Print any errors that occur during startup
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainApp() {
		
		// Set title of the main application window
		setTitle("Equipment Checkout System");
		
		// Ensure application closes completely when window is closed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set window position (x=100, y=100) and size (width=900, height=400)
		setBounds(100, 100, 900, 400);
		
		// Create main panel to hold all components
		contentPane = new JPanel();
		
		// Add padding of 5 pixels on all sides inside the content pane
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// Change background color of main panel
		contentPane.setBackground(new Color(0, 153, 153));
		
		// Set this content pane as the main container for the frame
		setContentPane(contentPane);
		
		// Use Borderlayout as layout manager (allows North, South, East, West, and Center positioning)
		contentPane.setLayout(new BorderLayout(0, 0));
		
		// Create panel to be placed at the bottom
		JPanel bottomPanel = new JPanel();
		
		// Use FlowLayout for bottomPanel with center alignment and 10 pixel gaps
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		// Create a button for exporting transactions
		btnExportTransaction = new JButton("Export Transactions");
		
		// Add action listener to handle export transaction button click events
		btnExportTransaction.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	// Open JFileChooser dialog to select save location
		        JFileChooser fileChooser = new JFileChooser();
		        
		        // Set title of the file chooser dialog
		        fileChooser.setDialogTitle("Save Transactions CSV");
		        
		        // Restrict file type to CSV only
		        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		        fileChooser.setFileFilter(filter);

		        // Show save dialog and capture user selection
		        int userSelection = fileChooser.showSaveDialog(null);
		        
		        // If user clicked 'Save'
		        if (userSelection == JFileChooser.APPROVE_OPTION) {
		        	
		        	// Get file selected by user
		            File fileToSave = fileChooser.getSelectedFile();
		            
		            // Extract file path
		            String filePath = fileToSave.getAbsolutePath();
		            
		            // Append ".csv" if user did not include extension
		            if (!filePath.toLowerCase().endsWith(".csv")) {
		                filePath += ".csv";
		            }
		            try {
		            	
		            	// Export JTable data to CSV file
		                CSVExporter.exportToCSV(tblEmployee, filePath);
		                
		                // Show success message dialog to user
		                JOptionPane.showMessageDialog(null, "Successfully exported Transactions to:\n" + filePath);
		            } catch (IOException ex) {
		            	
		            	// Print error stack trace for debugging
		                ex.printStackTrace();
		                
		                // Print failure message to console
		                System.out.println("Failed to export Transactions.");
		                
		                // Show failure dialog to user
		                JOptionPane.showMessageDialog(null, "Failed to export Transactions.\nPlease try again.");
		            }
		        }
		    }
		});
		
		// Add export button to the bottom panel
		bottomPanel.add(btnExportTransaction);
		
		// ============================
		// EXPORT ORDERS BUTTON SECTION
		// ============================
		
		// Create a new button to export orders
		JButton btnExportOrders = new JButton("Export Orders");
		
		// Add action listener to handle button click
		btnExportOrders.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	// Open a JFileChooser dialog to select save location
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setDialogTitle("Save Orders CSV");  // Set title of CSV dialog
		        
		        // Set filter to allow only CSV files
		        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		        fileChooser.setFileFilter(filter);

		        // Check if user approved the file selection
		        int userSelection = fileChooser.showSaveDialog(null);
		        if (userSelection == JFileChooser.APPROVE_OPTION) {
		            File fileToSave = fileChooser.getSelectedFile();
		            String filePath = fileToSave.getAbsolutePath();  // Retrieve absolute path
		            
		            // Ensure the file name ends with ".csv"
		            if (!filePath.toLowerCase().endsWith(".csv")) {
		                filePath += ".csv";
		            }
		            try {
		            	// Export the orders table to the selected CSV file
		                CSVExporter.exportToCSV(tblOrders, filePath);
		                
		                // Notify the user of a successful export
		                JOptionPane.showMessageDialog(null, "Successfully exported Orders to:\n" + filePath);
		            } catch (IOException ex) {
		                ex.printStackTrace();
		                System.out.println("Failed to export Orders.");
		                
		                // Notify the user if export fails
		                JOptionPane.showMessageDialog(null, "Failed to export Orders.\nPlease try again.");
		            }
		        }
		    }
		});
		
		// Add the export orders button  to the bottom panel
		bottomPanel.add(btnExportOrders);

		// Add the bottom panel (with export buttons) to the SOUTH region of the content pane
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		// Create a new panel for the top section (e.g., Check Reminder, Check Out Equipment)
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		// =============================
		// CHECK REMINDER BUTTON SECTION
		// =============================
		
		// Create the "Check Reminder" button
		btnCheckReminder = new JButton("Check Reminder");
		
		// Add action listener to handle clicks on "Check Reminder"
		btnCheckReminder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Get employee selected in the combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				// Validation selection
				if(selectedEmployee == null) {
					JOptionPane.showMessageDialog(MainApp.this, "Please select an Employee from the drop menu");
					return;
				}
				
				try(Connection conn = DBConnect.getInstance().getConnection()){
					
					// 1. Retrieve all BORROWED transactions for the selected employee
					List<Transaction> borrowedTransaction = TransactionDAO.getBorrowedTransactionsByEmployee(selectedEmployee.getEmpID(), conn);
					
					// 2. If no borrowed transaction exists, show message
					if(borrowedTransaction.isEmpty()) {
						txtReminder.setText("No borrowed equipment for " + selectedEmployee.getEmpName());
						return;
					}
					
					// StringBuilder to hold all reminder messages generated before being displayed them in the text area
					StringBuilder remindersText = new StringBuilder();
					
					// 3. Iterate through all borrowed transactions
					for(Transaction t : borrowedTransaction) {
						
						// 3a. Create a reminder object and register it as observer
						Reminder reminder = new Reminder();
						t.registerObserver(reminder);
						
						// 3b. Notify Observer to generate reminder
						t.notifyObservers();
						
						// 3c. Append the reminder message to the string builder
						remindersText.append(reminder.getReminderMSG()).append("\n");
					}
					
					// 4. Display all reminder in the txtReminder text area
					txtReminder.setText(remindersText.toString());
					
				}catch (SQLException ex) {
					ex.printStackTrace();
					txtReminder.setText("Error loading reminders.");
				}
			}
		});
		
		// Set layout for the top panel
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Add the "Check Reminder" button to the top panel
		panel.add(btnCheckReminder);
		
		// ============================
		// EMPLOYEE SELECTION COMBO BOX
		// ============================
		
		// Create a combo box to select employees
		comboEmployees = new JComboBox<>();
		comboEmployees.setPreferredSize(new java.awt.Dimension(120, 25));
		
		// Add the combo box to the top panel
		panel.add(comboEmployees);
		
		// Load all employees into the combo box
		loadEmployeesIntoComboBox();
		comboEmployees.setSelectedIndex(-1);  // No selection by default
		
		// ==================================
		// CHECK OUT EQUIPMENT BUTTON SECTION
		// ==================================
		
		// Create "Check Out Equipment" button
		btnCheckoutEquipment = new JButton("Check Out Equipment");
		
		// Add action listener to handle clicks on the checkout button
		btnCheckoutEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Get the employee selected in the combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				// If an employee is selected, open checkout dialog
				if (selectedEmployee != null) {
				    openCheckoutDialog(selectedEmployee);
				} else {
					
					// Show warning if no employee is selected
				    JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before checking out equipment.");
				}
			}
		});
		
		// Add the button to the top panel
		panel.add(btnCheckoutEquipment);
		
		// ===============================
		// RETURN EQUIPMENT BUTTON SECTION
		// ===============================
		
		// Create the "Return Equipment" button
		btnReturnEquipment = new JButton("Return Equipment");
		
		// Add action listener to handle clicks on the return button
		btnReturnEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Get the employee selected in the combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				// Show warning if no employee is selected
				if (selectedEmployee == null) {
				    JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before returning equipment.");
				    return;
				}
				
				// Open the return dialog for the selected employee
				openReturnDialog(selectedEmployee);
			}
		});
		
		// Add the button to the top panel
		panel.add(btnReturnEquipment);
		
		// ====================
		// ORDER BUTTON SECTION
		// ====================
		
		// Create the "Order" button
		btnOrderEquipment = new JButton("Order");
		
		// Add action listener to handle clicks on the order button
		btnOrderEquipment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Get employee selected in the combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				// Show warning if no employee is selected
				if (selectedEmployee == null) {
					JOptionPane.showMessageDialog(MainApp.this, "Please select an employee before ordering equipment.");
					return;
				}
				
				// Open the order dialog for the selected employee
				openOrderDialog(selectedEmployee);
			}
		});
		
		// Add the order button to the top panel
		panel.add(btnOrderEquipment);
		
		// ===========================
		// CANCEL ORDER BUTTON SECTION
		// ===========================
		
		// Create the "Cancel" button
		btnCancelOrder = new JButton("Cancel");
		
		// Add action listener to handle clicks on the cancel button
		btnCancelOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Get the selected row in the order table
				int selectedRow = tblOrders.getSelectedRow();
				
			    if (selectedRow >= 0) {
			    	
			        // Retrieve the orderID from the first column of the selected row
			        int orderID = (int) tblOrders.getValueAt(selectedRow, 0);

			        // Get the currently selected employee from the combo box
			        Employee currentEmployee = (Employee) comboEmployees.getSelectedItem();

			        if (currentEmployee != null) {
			        	
			        	// Call the cancelOrder() method of Employee and display the result
			            String resultMSG = currentEmployee.cancelOrder(orderID);
			            JOptionPane.showMessageDialog(MainApp.this, resultMSG);

			            // Refresh the orders table to reflect changes
			            refreshOrdersTable();
			   
			        } else {
			        	
			        	// Warn if no employee is selected
			            JOptionPane.showMessageDialog(MainApp.this, "Please select an employee first.");
			        }
			    } else {
			    	
			    	// Warn if no order is selected
			        JOptionPane.showMessageDialog(MainApp.this, "Please select an order to cancel.");
			    }
			}
		});
		
		// Add the cancel button to the top panel
		panel.add(btnCancelOrder);
		
		// ==========================
		// ORDER TAB LISTENER SECTION
		// ==========================
		
		// Create a tabbed pane with tabs positioned at the top
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		// Add a listener to handle tab selection changes
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				// Get the index of the selected tab
				int selectedIndex = tabbedPane.getSelectedIndex();
				
				// Get the title of the selected tab
		        String selectedTitle = tabbedPane.getTitleAt(selectedIndex);
		        
		        // If the "Orders" tab is selected, refresh the orders table
		        if (selectedTitle.equals("Orders")) {
		            fillOrdersTable();
		        }
			}
		});
		
		// Add the tabbed pane to the main content pane at the center
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		// ==== TRANSACTION PANEL SECTION ==== //
		// Show employee transactions (top)
		// and reminders (bottom)
		// ================================== //
		JPanel transactionPanel = new JPanel();
		tabbedPane.addTab("Transactions", transactionPanel);
		transactionPanel.setLayout(new BorderLayout());

		// Split pane to separate transaction table (top) and reminders (bottom)
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		// --- Employee Table ---
		tblEmployee = new JTable();      // Table for employee transactions
		JScrollPane scrollTblEmployee = new JScrollPane(tblEmployee);
		splitPane.setTopComponent(scrollTblEmployee);

		// --- JTextArea Reminder ---
		txtReminder = new JTextArea();  // Area to display reminders
		txtReminder.setEditable(false);
		txtReminder.setFont(txtReminder.getFont().deriveFont(Font.ITALIC));   // Set font
		txtReminder.setForeground(Color.gray);
		txtReminder.setBorder(BorderFactory.createTitledBorder("Reminders")); // Create title
		
		JScrollPane scrollReminder = new JScrollPane(txtReminder);
		splitPane.setBottomComponent(scrollReminder);

		// --- Set Proportions ---
		splitPane.setDividerLocation(300);  // Initial divider position
		splitPane.setResizeWeight(0.7);     // Top components gets 70% of space

		// Add split pane to transaction panel
		transactionPanel.add(splitPane, BorderLayout.CENTER);

		// ===== ORDER PANEL SECTION ===== //
		// Displays all orders in a table
		// =============================== //
		JPanel orderPanel = new JPanel();
		tabbedPane.addTab("Orders", orderPanel);
		orderPanel.setLayout(new BorderLayout());

		// Scroll pane to hold the orders table
		scrollPane_1 = new JScrollPane();
		orderPanel.add(scrollPane_1, BorderLayout.CENTER);

		// Table for displaying orders
		tblOrders = new JTable();
		scrollPane_1.setViewportView(tblOrders);  // Attach table to scroll pane

		// ================ VIEW RECORD PANEL =============== //
		// Displays individual transaction records in a table
		// ================================================== //
		JPanel viewRecordPanel = new JPanel();
		viewRecordPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("View Record", viewRecordPanel);

		// Table for displaying individual employee transaction records
		tblViewRecord = new JTable();
		JScrollPane scrollPaneViewRecord = new JScrollPane(tblViewRecord);
		viewRecordPanel.add(scrollPaneViewRecord, BorderLayout.CENTER);  // Attach table to scroll panel
		
		// =========== EXPORT CSV BUTTON FOR VIEW PANEL SECTION ========= //
		// Allows exporting the selected employee's records to a CSV file
		// ============================================================== //
		JButton btnExportCSV = new JButton("Export to CSV");
		
		// Action listener to handle button clicks
		btnExportCSV.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Get the selected employee from combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				
				// Validate selection
				if (selectedEmployee == null) {
					JOptionPane.showMessageDialog(null, "Please select an employee first.");
					return;
				}

				// Open JFileChooser to select save file
				JFileChooser fileChooser = new JFileChooser();
				
				// Set personalized dialog title with employee name
				// If no employee name is select or the name is null, fallback to "Employee"
				String empName = (selectedEmployee != null && selectedEmployee.getEmpName() != null) ? selectedEmployee.getEmpName() : "Employee";
				
				// Filter for CSV files
				fileChooser.setDialogTitle("Save " + empName + "'s Records CSV");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
				fileChooser.setFileFilter(filter);

				// Show save dialog and wait for the users action (Approve or Cancel)
				int userSelection = fileChooser.showSaveDialog(null);
				if (userSelection == JFileChooser.APPROVE_OPTION) {  // User clicked "Save"
					
					// Get selected file
					File fileToSave = fileChooser.getSelectedFile();
					
					// Get full path of the file
					String filePath = fileToSave.getAbsolutePath();

					// Ensure file has .csv extension
					// If user did not type the extension, append it automatically
					if (!filePath.toLowerCase().endsWith(".csv")) {
						filePath += ".csv";
					}

					try {
						// Use the CSVExporter utility class to write the JTable data to a CSV file
						CSVExporter.exportToCSV(tblViewRecord, filePath);
						
						// Notify the user that export was successful
						JOptionPane.showMessageDialog(null, 
								"Successfully exported View Record to:\n" + filePath);
					} catch (IOException ex) {
						
						// Print the stack trace for debugging
						ex.printStackTrace();
						
						// Notify the user that export failed
						JOptionPane.showMessageDialog(null, 
								"Failed to export View Record.\nError: " + ex.getMessage());
					}
				}
			}

		});
		
		// =================================
		// PANEL SETUP FOR EXPORT CSV BUTTON
		// =================================
		
		// Create a panel to hold the export button, aligned to the right
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(btnExportCSV);
		viewRecordPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		// =========================================
		// REFRESH VIEW RECORD ON EMPLOYEE SELECTION
		// =========================================
		
		// Add action listener to the employee combo box
		// When a different employee is selected, the View Record table updates automatically
		comboEmployees.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Get the selected employee from the combo box
				Employee selectedEmployee = (Employee) comboEmployees.getSelectedItem();
				if(selectedEmployee != null) {
					
					// Refresh the table with the selected employee's transaction records
					refreshViewRecordTable(selectedEmployee);
				}
			}
		});

		// Initial population of tables
		FillTable();
	}
	
	// =========== POPULATE VIEW RECORD TABLE ======== //
	// Retrieves all transactions for a given employee
	// and displays them in the JTable
	// =============================================== //
	private void refreshViewRecordTable(Employee emp) {
		
		// Get all transactions for the employee using the Employee's viewRecord() method
		List<Transaction> transactions = emp.viewRecord();
		
		// If no transaction exists, notify the user
		if(transactions.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No records are available for this employee.");
		}
		
		// Define the column headers for the JTable
		String[] columnNames = { "Transaction ID", "Equipment Name", "Borrow Date",
								  "Expected Return Date", "Return Date", "Status", "Late" };
		
		// Create a new table model with the column names and no rows initially
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		
		// Get the current date to check for overdue transactions
		LocalDate today = LocalDate.now();
		
		// Iterate through each transaction to populate table rows
		for(Transaction t : transactions) {
			
			// Determine if the transaction is late
			String lateInfo = "No";
			if(t.getTransactionStatus() == TransactionStatus.Borrowed && t.getExpectedReturnDate() != null) {
				long daysLate = ChronoUnit.DAYS.between(t.getExpectedReturnDate(), today);
				if(daysLate > 0 ) {
					lateInfo = daysLate + " days";  // Record how many days late
				}
			}
			
			// Create an Object array representing a row in the table
			Object[] row = {
				t.getTransactionID(),                                 // Transaction ID
				t.getEquipment().getEquipmentName(),                  // Equipment Name
				t.getBorrowDate() != null ? t.getBorrowDate().toString() : "",  // Borrow Date
				t.getExpectedReturnDate() != null ? t.getExpectedReturnDate().toString() : "",  // Expected Return Date
				t.getReturnDate() != null ? t.getReturnDate().toString() : "",  // Return Date
				t.getTransactionStatus(),                             // Transaction Status
				lateInfo                                              // Late info
			};
			
			// Add the row to the table model
			model.addRow(row);
		}
		
		// Update the JTable to display the new model
		tblViewRecord.setModel(model);
	}

	// ================== ORDER DIALOG METHOD ================== //
	// This method opens a modal dialog that allows an employee
	// to order equipment that is compatible with their skill
	// classification. The dialog displays a table of available
	// equipment, lets the employee select one, and confirms the
	// order. Once confirmed, the database and Orders panel is
	// refreshed for real-time updates.
	// ========================================================= //
	
	// Order dialog is invoked after the employee clicks the "Order" button
	private void openOrderDialog(Employee employee) {
		
		// Create a modal dialog with the employees name in the title
	    JDialog dialog = new JDialog(this, "Order Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(600, 400);
	    dialog.setLocationRelativeTo(null);

	    // Table model for displaying equipment data (ID, Name, Skill Required)
	    DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Skill Required"}, 0);
	    JTable table = new JTable(tableModel);

	    // Establish a database connection
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	
	    	// Retrieve a list of orderable equipment matching the employee's skill
	        List<Equipment> equipment = EquipmentDAO.getOrderableEquipmentBySkill(conn, employee.getSkillClassification());
	        
	        // Add each equipment as a row in the table model
	        for (Equipment eq : equipment) {
	            tableModel.addRow(new Object[]{
	            		eq.getEquipmentID(),     // Unique identifier of the equipment
	            		eq.getEquipmentName(),   // Name of the equipment
	            		eq.getRequiredSkill()    // Skill required to use the equipment
	            });
	        }
	    } catch (SQLException ex) {
	    	
	    	// Log technical error
	        ex.printStackTrace();
	        
	        // Inform user of failure
	        JOptionPane.showMessageDialog(dialog, "Failed to load equipment.", "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    // Confirm button to process order
	    JButton btnConfirm = new JButton("Confirm Order");
	    btnConfirm.addActionListener(e -> {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow >= 0) {
	        	
	        	// Get the selected equipment ID from the table
	            int equipmentId = (int) tableModel.getValueAt(selectedRow, 0);
	            try (Connection conn = DBConnect.getInstance().getConnection()) {
	            	
	            	// Retrieve the equipment object by ID
	                Equipment equipment = EquipmentDAO.getEquipmentByID(conn, equipmentId);
	                
	                // Call the orderEquipment method from the Employee class to place an order
	                String result = employee.orderEquipment(equipment);
	                JOptionPane.showMessageDialog(dialog, result);
	                
	                // Refresh the Orders panel to show updated data
	                refreshOrdersTable();
	                
	                // Close the dialog after a successful order
	                dialog.dispose();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        } else {
	        	
	        	// Alert if no equipment is selected
	            JOptionPane.showMessageDialog(dialog, "Please select equipment first.");
	        }
	    });

	    // Layout configuration for dialog: table in center, confirm button at button
	    dialog.getContentPane().setLayout(new BorderLayout());
	    dialog.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
	    dialog.getContentPane().add(btnConfirm, BorderLayout.SOUTH);
	    
	    // Make dialog visible
	    dialog.setVisible(true);
	}

	// ========== EMPLOYEE RETURN DIALOG ========== //
	// Employee return dialog box invoked after 
	// return button is pressed. This dialog allows
	// employees to to view all borrowed equipment
	// and return them by selecting the equipment
	// condition.
	// ============================================ //
	private void openReturnDialog(Employee employee) {
		
		// Create a modal dialog window to for returning equipment
		// The title dynamically includes the employees name
	    JDialog dialog = new JDialog(MainApp.this, "Return Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(500, 350);            // Set width and height
	    dialog.setLocationRelativeTo(this);  // Center the dialog relative to the main application
	    
	    // Define the table columns headers
	    String[] columns = {"Transaction ID", "Equipment ID", "Equipment Name", "Borrow Date", "Expected Return Date"};
	    
	    // Get only the employee's transactions that are currently Borrowed
	    List<Transaction> borrowedTxns = employee.getEmpTransaction().stream()
	        .filter(t -> t.getTransactionStatus() == TransactionStatus.Borrowed)
	        .toList();
	    
	    // Convert the list of borrowed transactions into a 2D array for the JTable
	    Object[][] data = new Object[borrowedTxns.size()][columns.length];
	    for (int i = 0; i < borrowedTxns.size(); i++) {
	        Transaction t = borrowedTxns.get(i);
	        data[i][0] = t.getTransactionID();                   // Transaction ID
	        data[i][1] = t.getEquipment().getEquipmentID();      // Equipment ID
	        data[i][2] = t.getEquipment().getEquipmentName();    // Equipment Name
	        data[i][3] = t.getBorrowDate();                      // Borrow Date
	        data[i][4] = t.getExpectedReturnDate();              // Expected Return Date
	    }
	    
	    // Create the table and enable single row selection
	    JTable table = new JTable(data, columns);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    // Add table inside a scroll panel so the data is scrollable
	    JScrollPane scrollPane = new JScrollPane(table);
	    
	    // Create a confirm button for returning equipment
	    JButton btnConfirmReturn = new JButton("Confirm Return");
	    btnConfirmReturn.addActionListener(e -> {
	    	
	    	// Check if the user has selected a row
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(dialog, "Please select equipment to return.");
	            return;
	        }
	        
	        // Extract the transaction ID from the selected row
	        int transactionID = (int) table.getValueAt(selectedRow, 0);
	        
	        // Ask employees to specify the condition of the returned equipment
	        EquipmentCondition condition = (EquipmentCondition) JOptionPane.showInputDialog(
	            dialog,
	            "Select condition of the equipment:",
	            "Equipment Condition",
	            JOptionPane.QUESTION_MESSAGE,
	            null,
	            EquipmentCondition.values(),
	            EquipmentCondition.values()[0]  // Default selection value
	        );
	        
	        // If no condition is selected, cancel the return
	        if (condition == null) {
	            JOptionPane.showMessageDialog(dialog, "Return cancelled: No condition selected.");
	            return;
	        }
	        
	        // Call returnEquipment() method in Employee class
	        // This method updates the Transaction object and Equipment object
	        // both in memory and persists the change to the database.
	        Transaction returnedTxn = employee.returnEquipment(transactionID, condition);
	        
	        // If return fails, notify the user
	        if (returnedTxn == null) {
	            JOptionPane.showMessageDialog(dialog, "Return failed. Equipment not found or already returned.");
	            return;
	        }
	        
	        // Perform database operations inside a try-with-resources block
	        try (Connection conn = DBConnect.getInstance().getConnection()) {
	            conn.setAutoCommit(false);  // Begin transaction

	            // Update the transaction record in the database (e.g., set status = Returned)
	            TransactionDAO.updateTransactionReturn(conn, returnedTxn);
	            
	            // Update the equipment status (e.g., Loaned) based on condition
	            EquipmentDAO.updateEquipment(conn, returnedTxn.getEquipment());

	            // Commit both updates to the database
	            conn.commit();

	            // Inform the user and close the dialog
	            JOptionPane.showMessageDialog(dialog, "Equipment returned successfully.");
	            dialog.dispose();

	            // Update the Transaction panel
	            FillTable();
	            
	            // Update the View Record panel
	            refreshViewRecordTable(employee);

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            
	            // Show error message to user
	            JOptionPane.showMessageDialog(dialog, "Return failed: " + ex.getMessage());
	        }
	    });
	    
	    // Add components into a panel (table in center, button at bottom)
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(scrollPane, BorderLayout.CENTER);
	    panel.add(btnConfirmReturn, BorderLayout.SOUTH);
	    
	    // Set the panel as dialog content and display it
	    dialog.setContentPane(panel);
	    dialog.setVisible(true);
	}
	
	// ================ EMPLOYEE CHECKOUT DIALOG =============== //
	// Employee checkout dialog invoke after the checkout button
	// is pressed. This dialog allows an employee to view all 
	// available equipment for their skill classification and
	// check out one piece of equipment.
	// ========================================================= //
	private void openCheckoutDialog(Employee employee) {
		
	    // Create a modal dialog window for equipment checkout
		// The title dynamically includes the employee's name
	    JDialog dialog = new JDialog(this, "Check Out Equipment for " + employee.getEmpName(), true);
	    dialog.setSize(400, 300);            // Set width and height
	    dialog.setLocationRelativeTo(this);  // Center dialog relative to the main application
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);  // Dispose on close

	    // Define the table columns for available equipment
	    String[] columnNames = {"ID", "Name", "Condition", "Status", "Required Skill"};
	    
	    // Initialize a 2D array for JTable data
	    Object[][] data = new Object[0][];
	    
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	
	    	// Retrieve all available equipment matching the employee's skill
	    	List<Equipment> equipmentList = EquipmentDAO.getAvailableEquipmentBySkill(conn, employee.getSkillClassification());
	    	
	    	// Re-initialize the 2D array to the correct size based on the number of retrieved equipment
	    	// Rows = number of equipment, Columns = 5 (ID, Name, Condition, Status, Required Skill)
	    	data = new Object[equipmentList.size()][5];
	    	
	    	// If no equipment is available for an employees skill, show message and exit
	    	if(equipmentList.isEmpty()) {
	    		JOptionPane.showMessageDialog(null, 
	    				"No available equipment for: " + employee.getSkillClassification(),
	    				"No equipment found",
	    				JOptionPane.INFORMATION_MESSAGE);
	    		return;
	    	}
	    	// Populate data array with equipment attributes for JTable
	    	for(int i=0; i < equipmentList.size(); i++) {
	    		Equipment eq = equipmentList.get(i);
	    		data[i][0] = eq.getEquipmentID();                 // Equipment ID
	    		data[i][1] = eq.getEquipmentName();               // Equipment Name
	    		data[i][2] = eq.getEquipmentCondition().name();   // Equipment Condition (enum as string)
	    		data[i][3] = eq.getStatus().name();               // Equipment Status (enum as string)
	    		data[i][4] = eq.getRequiredSkill().name();        // Required Skill (enum as string)
	    	}
	    	
	    } catch (Exception e){
	    	e.printStackTrace();
	    	
	    	// Display a failure message to the user
	    	JOptionPane.showMessageDialog(dialog, "Failed to load equipment data");
	    }
	    
	    // Create table with retrieved equipment data
	    JTable table = new JTable(data, columnNames);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Single selection only
	    JScrollPane scrollPane = new JScrollPane(table);   // Make table scrollable

	    // Button to confirm checkout of selected equipment
	    JButton btnConfirm = new JButton("Confirm Checkout");
	    btnConfirm.addActionListener(e -> {
	    	
	    	// Check if row is selected
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(dialog, "Please select an equipment to check out.");
	            return;
	        }
	        // Retrieve selected equipment attributes from the table
	        // Each column corresponds to a specific attribute of the equipment
	        int equipmentID = (int) table.getValueAt(selectedRow, 0);              // Equipment ID
	        String equipmentName = (String) table.getValueAt(selectedRow, 1);      // Equipment Name
	        String equipmentCondition = (String) table.getValueAt(selectedRow, 2); // Equipment Condition (enum as string)
	        String equipmentStatus = (String) table.getValueAt(selectedRow, 3);    // Equipment Status (enum as string)
	        String requiredSkill = (String) table.getValueAt(selectedRow, 4);      // Required Skill (enum as string)
	        
	        // Create Equipment object based on selected row
	        // Convert string representations of enums back to their respective enum constants
	        Equipment selectedEquipment = new Equipment(
	                equipmentID,
	                equipmentName,
	                EquipmentCondition.valueOf(equipmentCondition),  // Convert string to enum
	                EquipmentStatus.valueOf(equipmentStatus),        // Convert string to enum
	                SkillClassification.valueOf(requiredSkill)       // Convert string to enum
	            );
	        
	        // Safety check for null employee or equipment
	        if (employee == null || selectedEquipment == null) {
	            JOptionPane.showMessageDialog(dialog, "Please select both an employee and equipment.");
	            return;
	        }
	        
	        // Ask for confirmation from employee before proceeding
	        int confirm = JOptionPane.showConfirmDialog(dialog,
    		        "Are you sure you want to check out:\n" + equipmentName + " (ID: " + equipmentID + ")",
    		        "Confirm Checkout", JOptionPane.YES_NO_OPTION);
	        
	        if(confirm == JOptionPane.YES_OPTION) {
	        	
	        	// Call Employee's checkOut() method to create a new transaction in memory
	        	Transaction newTxn = employee.checkOut(selectedEquipment);
	        	
	        	// Establish a database connection
	        	try (Connection conn = DBConnect.getInstance().getConnection()){
	        		conn.setAutoCommit(false);  // Begin transaction
	        		
	        		// Update equipment status to loaned in the database
	        		String updateSQL = "UPDATE equipment SET equipStatus = 'Loaned' WHERE equipmentID = ?";
	        		try (PreparedStatement stmtUpdate = conn.prepareStatement(updateSQL)){
	        			stmtUpdate.setInt(1, equipmentID);
	        			stmtUpdate.executeUpdate();  // executes statement of DML
	        		}
	        		
	        		// Insert new transaction into transaction table 
	        		// Records empID, equipmentID, borrow and expected dates, and transaction status
	        		String insertSQL = "INSERT INTO transaction " +
	        			    "(empID, equipmentID, borrowDate, expectedReturnDate, transactionStatus, checkoutCondition) " +
	        			    "VALUES (?, ?, ?, ?, ?, ?)";

	        		// Prepare a SQL statement to insert a new transaction into the database
	        		// Statement.RETURN_GENERATED_KEYS allows retrieval of the auto-generated transaction ID
	        		try (PreparedStatement stmtInsert = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)){
	        			
	        			// Set parameters for the prepared statement
	        			stmtInsert.setInt(1, newTxn.getEmployee().getEmpID());          // Employee ID
	        			stmtInsert.setInt(2, newTxn.getEquipment().getEquipmentID());   // Equipment ID
	        			stmtInsert.setDate(3, newTxn.getBorrowDate() != null ? java.sql.Date.valueOf(newTxn.getBorrowDate()) : null);  // Borrow Date
	        			stmtInsert.setDate(4, newTxn.getExpectedReturnDate() != null ? java.sql.Date.valueOf(newTxn.getExpectedReturnDate()) : null);  // Expected Return Date
	        			stmtInsert.setString(5, newTxn.getTransactionStatus().name());  // Transaction Status
	        			stmtInsert.setString(6, newTxn.getCheckoutCondition().name());  // Checkout Condition
	        			
	        			// Execute the insert statement
	        			stmtInsert.executeUpdate();
	        			
	        			// Retrieve auto-generated transaction ID from database
	        			try(ResultSet rs = stmtInsert.getGeneratedKeys()){
	        				
	        				// Check if the ResultSet contains a generated key
	        				if(rs.next()) {
	        					
	        					// Retrieve the auto-generated transaction ID from the first column
	        					int generatedID = rs.getInt(1);
	        					
	        					// Update in-memory Transaction object with the generated transaction ID
	        					newTxn.setTransactionID(generatedID);
	        				}
	        			}
	        		}
	        		conn.commit();  // Commit all changes to database
	        		
	        		// Notify user of a successful checkout
	        		JOptionPane.showMessageDialog(dialog, "Successful checkout for: " + equipmentName);
	        		dialog.dispose();
	        		
	        		// Refresh UI tables to reflect new transaction
	        		FillTable();        // Transaction panel
		            refreshViewRecordTable(employee);  // View Record panel
	        		
	        	}catch (Exception ex) {
	        		
	        		// Print stack trace to the console for debugging
	        		ex.printStackTrace();
	        		
	        		// Show a general error message to the user
	        		JOptionPane.showMessageDialog(dialog, "Checkout failed. Please try again.");
	        		
	        		// Show a detailed error message for additional information
	        		JOptionPane.showMessageDialog(dialog, "SQL Error: " + ex.getMessage());
	        	}
	        }
	        
	        // Show detailed attributes of selected equipment for confirmation
	        JOptionPane.showMessageDialog(dialog,
	            "Selected Equipment:\nID: " + equipmentID + "\nName: " + equipmentName + "\n" + "Equipment Condition: "
	            		+ equipmentCondition +"\n" + "Equipment Status: " + equipmentStatus + "\n" + "Required Skill: "
	            		+ requiredSkill);

	        // Close dialog after confirmation
	        dialog.dispose();
	    });

	    // Set up the layout of the dialog using BorderLayout
	    JPanel panel = new JPanel(new BorderLayout());
	    
	    // Add the scrollable table to the center of the panel
	    panel.add(scrollPane, BorderLayout.CENTER);
	    
	    // Add the Confirm Checkout button to the bottom (south) of the panel
	    panel.add(btnConfirm, BorderLayout.SOUTH);

	    // Set panel as the content of the dialog
	    dialog.setContentPane(panel);
	    
	    // Make the dialog visible to the user
	    dialog.setVisible(true);
	}
	
	// ============ COMBO BOX FOR EMPLOYEES ============== //
	// Load employees from the database into the JComboBox
	// =================================================== //
	private void loadEmployeesIntoComboBox() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	
	    	// Retrieve a list of all employees using the EmployeeDAO
	        List<Employee> employees = EmployeeDAO.getAllEmployees(conn);
	        
	        // Clear existing items from the combo box
	        comboEmployees.removeAllItems();
	        
	        // Iterate through each employee and add them to the combo box
	        for (Employee emp : employees) {
	        	
	        	// Add employee to the JComboBox
	            comboEmployees.addItem(emp);
	        }
	    } catch (Exception e) {
	    	
	    	// Print stack trace if an exception is thrown during database access
	        e.printStackTrace();
	    }
	}

	// ==================== TRANSACTIONS PANEL DATA ==================== //
	// This method populates the 'Employee' JTable in the Transactions
	// panel of the UI. It retrieves all transactions from the database,
	// formats the data, and loads it into a read-only JTable, which is
	// displayed to the user.
	// ================================================================= //
	public void FillTable() {
		try (Connection conn = DBConnect.getInstance().getConnection()) {
			
			// Retrieve all transactions from the database
	        List<Transaction> transactions = TransactionDAO.getAllTransactions(conn);
	        
	        // Define column names for the JTable
	        String[] columnNames = {
	        	    "Transaction ID", "Employee Name", "Employee Skill",
	        	    "Equipment Name", "Required Skill",
	        	    "Checkout Condition", "Return Condition",
	        	    "Borrow Date", "Expected Return Date",
	        	    "Transaction Status"
	        	};
	        
	        // Create a 2D Object array to hold table data
	        // Rows = number of transactions, Columns = number of columns defined above
	        Object[][] data = new Object[transactions.size()][columnNames.length];
	        
	        // Populate the data array with transaction details
	        for (int i = 0; i < transactions.size(); i++) {
	            Transaction t = transactions.get(i);
	            data[i][0] = t.getTransactionID();          // Column 0: Transaction ID
	            data[i][1] = t.getEmployee().getEmpName();  // Column 1: Employee Name
	            data[i][2] = t.getEmployee().getSkillClassification().name();  // Column 2: Employee Skill Classification
	            data[i][3] = t.getEquipment().getEquipmentName();  // Column 3: Equipment Name
	            data[i][4] = t.getEquipment().getRequiredSkill().name();  // Column 4: Required Skill for Equipment
	            
	            // If t.getCheckoutCondition() is not null, display the enum condition (e.g., Good, Damaged)
	            // If null, display "N/A"
	            data[i][5] = t.getCheckoutCondition() != null
	            		? t.getCheckoutCondition().name() : "N/A";  // Column 5: Checkout Condition for Equipment
	            
	            // If t.getReturnCondition() is not null, display the enum condition
	            // If null, display "N/A"
	            data[i][6] = t.getReturnCondition() != null
	            		? t.getReturnCondition().name() : "N/A";   // Column 6: Return Condition for Equipment
	            data[i][7] = t.getBorrowDate();               // Column 7: Borrow Date
	            data[i][8] = t.getExpectedReturnDate();       // Column 8: Expected Return Date
	            data[i][9] = t.getTransactionStatus().name(); // Column 9: Transaction Status (e.g., Returned, Borrowed)
	        }
	        
	        // Create a new DefaultTableModel with the populate data
	        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	            private static final long serialVersionUID = 1L;
	            @Override
	            // Make the table read-only
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        // Set the model into the JTable for rendering in the UI
	        tblEmployee.setModel(model);

	    } catch (Exception e) {
	    	// Handle any error that occur during DB access or table population
	        e.printStackTrace();
	        System.out.println("Failed to load transactions from DB");
	    }
	}
	
	// ==================== ORDERS PANEL DATA ===================== //
	// This method populates the 'Orders' JTable in the Orders
	// panel of the UI. It retrieves all employee equipment orders 
	// from the database and displays them in a read-only JTable.
	// ============================================================ //
	public void fillOrdersTable() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	
	    	// Retrieve all orders from the database via OrderDAO
	        List<Order> orders = OrderDAO.getAllOrders(conn);

	        // Define column names for the JTable
	        String[] columnNames = {
	        		"Order ID", 
	        		"Employee Name", 
	        		"Equipment Name", 
	        		"Order Date", 
	        		"Status"
	        };
	        
	        // Create a 2D Object array to hold the JTable data
	        // Row = number of orders, Columns = number of column names as defined above
	        Object[][] data = new Object[orders.size()][columnNames.length];

	        // Fill the array with data from every order
	        for (int i = 0; i < orders.size(); i++) {
	            Order o = orders.get(i);
	            data[i][0] = o.getOrderID();                // Column 0: Order ID
	            data[i][1] = o.getEmployee().getEmpName();  // Column 1: Employee Name
	            data[i][2] = o.getEquipment().getEquipmentName();  // Column 2: Equipment Name
	            data[i][3] = o.getOrderDate();              // Column 3: Order date
	            data[i][4] = o.getOrderStatus().name();     // Column 4: Order Status (e.g., Confirmed, Cancelled)
	        }

	        // Create a DefaultTableModel from the populated data
	        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	        	private static final long serialVersionUID = 1L;
	            @Override
	            // Make the table read-only 
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };

	        // Set the new table model to the Orders JTable in the Orders panel
	        tblOrders.setModel(model);

	    } catch (SQLException e) {
	    	// Display connection or query errors
	        e.printStackTrace();
	    }
	}
	
	// =================== REFRESH ORDERS TABLE =================== //
    // This method refreshes the 'Orders' JTable in the Orders
	// panel of the UI. It retrieves the latest list of orders 
	// from the database and re-populates the table model, ensuring
	// that the displayed data is always up-to-date.
	// ============================================================ //
	private void refreshOrdersTable() {
	    try (Connection conn = DBConnect.getInstance().getConnection()) {
	    	
	    	// Retrieve the latest order data from the database
	        List<Order> orders = OrderDAO.getAllOrders(conn);

	        // Define column names for the Order table
	        String[] columnNames = { 
	        		"Order ID", 
	        		"Employee", 
	        		"Equipment", 
	        		"Order Date", 
	        		"Status", 
	        		"Pickup Date" 
	        };
	        
	        // Create a new DefaultTableModel with the above column names and without initial rows
	        // (The parameter 0 means start with 0 rows, later they are manually added via the addRow() method)
	        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

	        // Fill the table with each order
	        for (Order order : orders) {
	            Object[] row = {
	                order.getOrderID(),               // Column 0: Order ID
	                order.getEmployee().getEmpName(), // Column 1: Employee Name
	                order.getEquipment().getEquipmentName(),  // Column 2: Equipment Name
	                order.getOrderDate(),             // Column 3: Order date
	                order.getOrderStatus(),           // Column 4: Order Status
	                order.getPickUpDate()             // Column 5: Pick Up Date
	            };
	            
	            // Add rows to the table model
	            model.addRow(row);
	        }

	        // Set the new model to the Orders JTable in the Orders panel
	        tblOrders.setModel(model);
	    } catch (SQLException e) {
	    	// Handle any errors that occur during DB connection or queries
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Failed to refresh orders.");
	    }
	}
}
