# Equipment Checkout System (ECS)

This document provides instructions for installing, configuring, executing, and testing the Equipment Checkout System (ECS). It is intended for technical and end-user reference.

## Project Description
The **Equipment Checkout System (ECS)** is a Java-based desktop application developed to manage the lifecycle of equipment within GB Manufacturing. It enables authorized employees to efficiently borrow, return, order, and track equipment through a centralized system.

**Core features include**:

- **Checkout Equipment**: Allows employees to check out available equipment from warehouse inventory.

- **Return Equipment**: Provides employees the ability to return previously borrowed items. 

- **Order Equipment**: Enables maintenance employees to request required equipment for tasks.

- **Receive Reminders**: Notifies employees of upcoming or overdue equipment returns.

- **View Record**: Grants employees’ access to their transaction history and equipment usage records.

## Testing Support

The ECS project supports testing of all core features using JUnit 5 and an embedded H2 database, allowing each feature to be verified directly.

- JUnit 5 standalone JAR (`junit-platform-console-standalone-1.9.3.jar`) is included in the `/lib` folder of the repository, so no additional JUnit installation is required.
  
- Embedded H2 database (`h2-2.3.232.jar`) is included in the `/lib` folder for testing purposes.
  
- All JUnit annotations (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`) and assertions (`assertEquals`, `assertNotNull`, etc.) are fully supported.

## Running Tests

Tests can be run directly in the Eclipse IDE with the following steps:

- Right-click the test case (e.g., `ReminderTest.java`)

  <img width="319" height="216" alt="image" src="https://github.com/user-attachments/assets/31e252a1-176d-4bac-8487-bd52a115a450" />
   
- Click **Run As** JUnit Test

  <img width="588" height="23" alt="image" src="https://github.com/user-attachments/assets/8ff55f6e-d27c-4154-83ec-78d8fde3e9dd" />

- Results of the JUnit tests can be directly verified through the Eclipse IDE:

  <img width="622" height="133" alt="image" src="https://github.com/user-attachments/assets/dbd4fe4f-4e69-4419-8df4-53326acb76d4" />

## Included JUnit Test Code

- **CSVExporterTest.java** – test code for **TC-EXP-001-A** (tests supported feature: `exporting` employee transaction records)
  
- **ReminderTest.java** – test code for **TC-REM-001-A** (tests core feature: `Receive Reminders`)
  
- **ReturnEquipmentTest.java** - test code for **TC-RET-001-A** (tests core feature: `Return Equipment`)
  
- **TestCheckoutEquipment.java** - test code for **TC-CHK-002-B** (tests core feature: `Checkout Equipment`)
  
- **TestNoAvailableCheckoutEquipment.java** – test code for **TC-CHK-001-A** (tests core feature: `Checkout Equipment`)
  
- **TestOrderEquipment.java** - test code for **TC-ORD-001-A** (tests core feature: `Order Equipment`)
  
- **ViewRecordTest.java** - test code for **TC-VR-001-A** (tests core feature: `View Record`)


## System Requirements

- Java JDK 8 or higher - [Download JDK](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)

- Eclipse IDE for Java - [Download Eclipse IDE for Java](https://eclipseide.org/)

- MySQL Workbench - [Download MySQL Workbench](https://dev.mysql.com/downloads/workbench/)

**Libraries included in the `/lib` folder of the repository (no separate download required):**

- `mysql-connector-j-9.4.0.jar` – MySQL connectivity
  
- `h2-2.3.232.jar` – embedded H2 database for testing
  
- `junit-platform-console-standalone-1.9.3.jar` – full JUnit 5 library for running all tests


## Obtaining the ECS Project

>**Click Here**: [Link to ECS Project in GitHub](https://github.com/rayk210/ECSRepo_Team6.git)

 1. Download ZIP file from GitHub

    <img width="900" height="500" alt="image" src="https://github.com/user-attachments/assets/5393af01-c383-4d01-ac83-db22fffcc82d" />

 2. Extract contents a local directory.
    
 3. The extracted folder will contain:

    - Java source code in the `src/ecsapplication` folder

    - SQL dump and structure in `sql/ceis400courseproject_2.sql`

    - MySQL Connector JAR, H2 JAR, and JUnit standalone JAR in `lib/`

    - JUnit testing code for test cases in `test/ecsapplication/test`

    - Eclipse `.settings` configuration folder

 >**Note**: Ensure the ECS repository has been downloaded from GitHub and extracted so that the **ceis400courseproject_2.sql** dump file is available.
    
## Database Setup

To configure the database:

1. **Start** MySQL Workbench and ensure the server is running.

   - Press the **plus** icon

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/ed169261-b57a-4dc7-8537-a1918a93c10b" />

2. **Create** a new connection with the following settings:

   - Connection Name: ceis400courseproject
   - Connection Method: Standard(TCP/IP)
   - Hostname: localhost
   - Port: 3306
   - Username: root
   - Password: devry123

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/633a6028-83df-43ed-a737-27fc6bbf8e70" />

3. **Click** on Project:
   
     <img width="549" height="335" alt="image" src="https://github.com/user-attachments/assets/24869211-4fb6-4bea-8cd7-13202ebb84a0" />

4. **Click** the Open SQL Script (folder icon) button:

     <img width="81" height="92" alt="image" src="https://github.com/user-attachments/assets/949cd578-7d8a-47e9-ab91-9fd968882508" />

5. **Select** the .SQL dump file **ceis400courseproject_2.sql** from the unzipped GitHub project:

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/07534bb2-baff-4745-b810-1886472b3ffa" />
     
     **Note:** The SQL dump already contains the `CREATE DATABASE` and `USE` commands, so manual creation is not required

6. **Click** the `Execute` (lightning bolt) button to run the script:

   <img width="942" height="226" alt="image" src="https://github.com/user-attachments/assets/06a7afc3-84a6-4f02-8958-1160379d819d" />

7. **Click** the `Refresh Schemas` (circular arrows) icon in the Navigator panel (left-hand side) to reload schemas:

   <img width="430" height="235" alt="image" src="https://github.com/user-attachments/assets/edb2f5fe-802c-4361-9e5f-154c89296a59" />

   **Note:** The circular arrows are located to the right of `SCHEMAS`.

8. **Right-click** the **ceis400courseproject** and select **Set as Default Schema**

   <img width="516" height="111" alt="image" src="https://github.com/user-attachments/assets/f06da57f-cada-45a4-ac1d-e8e420e5c433" />

2. **Verify Database Tables**

   Run this code in a new query tab:

   USE ceis400courseproject;
   SHOW TABLES;

   SELECT * FROM employee;
   
   SELECT * FROM equipment LIMIT 5;
   
   SELECT * FROM `order` LIMIT 5;
   
   SELECT * FROM transaction LIMIT 5;
   
   SELECT * FROM reminder LIMIT 5;

   >**Note**: **order** must be enclosed in backquotes **(``)**

   <img width="627" height="461" alt="image" src="https://github.com/user-attachments/assets/3a009040-d57a-4f88-b06b-e09adea5bb95" />

## Running the ECS System

### 1. Open Project in Eclipse IDE

  - Launch Eclipse IDE

  - Navigate to **File** --> **Import** --> **Existing Projects into Workspace**

  <img width="550" height="500" alt="image" src="https://github.com/user-attachments/assets/3246561b-8e4e-4d21-ae99-f7e5b87654e1" />

  - Select the ECS project folder that was extracted before

  <img width="550" height="700" alt="image" src="https://github.com/user-attachments/assets/9bd2f33d-b16f-4981-b6dc-e254cd43c555" />
  
  - Verify that the following JAR files are present in the `lib` directory:

    - `h2-2.3.232.jar`: embedded H2 database used for testing
   
    - `mysql-connector-j-9.4.0.jar`: MySQL connectivity
   
    - `junit-platform-console-standalone-1.9.3.jar`: full JUnit 5 library for running all tests
   
      <img width="390" height="240" alt="image" src="https://github.com/user-attachments/assets/cb8e5699-84d6-423b-8cec-b34507eef3f4" />

### 2. Configure Database Connection

  - Open the **DBConnect.java** file

  - Ensure that the JDBC url, username, and password match your MySQL setup done in the **Setup Database** part above:
  
    private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
    
    private static final String username = "yourusername";
    
    private static final String password = "yourpassword";
    
  >**Note**: You must use your own MySQL username and password

### 3. Use Sample Data

  -The SQL dump already contains data related to employees

  -Functionality can be immediately tested by choosing an Employee from the ComboBox on the UI

  <img width="507" height="178" alt="image" src="https://github.com/user-attachments/assets/8820b532-7d44-40fe-a7c3-eab6088c89bb" />


### 4. Build and Run

  -Right-click on **MainApp.java** and **Run As --> Java Application**

  <img width="600" height="800" alt="image" src="https://github.com/user-attachments/assets/51b52d54-adc6-4804-b330-3399188a332a" />

  
## Use the ECS System's Features

### Using the Receive Reminder Feature
> **Note**: Reminders can only be viewed from the **Transactions panel**

  1. **Run** the application
  
  2. Drag the boundary of the text area upwards from the **Transactions** panel:

      <img width="346" height="377" alt="image" src="https://github.com/user-attachments/assets/6af39587-3224-4b8a-ab70-dde9b699fa14" />

  3. Ensure the word **Reminders** is visible in the text area:

      <img width="346" height="385" alt="image" src="https://github.com/user-attachments/assets/e7b9f997-4fea-47a1-9f4b-fa5086917d48" />

  4. Select an **Employee** from the ComboBox:

      <img width="349" height="388" alt="image" src="https://github.com/user-attachments/assets/2deeded0-55b8-438b-b50c-ea882a1f365f" />

  5. Click the **Check Reminder** button and view the notifications:

      <img width="502" height="391" alt="image" src="https://github.com/user-attachments/assets/8918288b-eefd-4485-b5c3-cd089ac8e8c0" />

  6. Repeat **steps 4** and **5** to view every Employee reminder

      >**Note**: The **Check Reminder** button must be pressed each time a new Employee is selected to display their reminders

      >**Tips**: If no reminder appears, ensure the SQL dump has been imported correctly

### Using the View Record Feature

  1. **Run** the ECS application

  2. Navigate to the **View Record** panel on the UI:

     <img width="882" height="392" alt="image" src="https://github.com/user-attachments/assets/3e5da102-f219-47e3-9854-c4e7335c7048" />

     >**Note**: The **View Record** panel is active

  3. Select an **Employee** from the ComboBox:

     <img width="340" height="386" alt="image" src="https://github.com/user-attachments/assets/293ab075-7aa5-475f-bfe1-425409f71aba" />

  4. The JTable will automatically be filled with an Employees records:

     <img width="885" height="385" alt="image" src="https://github.com/user-attachments/assets/c7cf1470-769d-4c22-85bb-9e882b907c7d" />

  5. Repeat **step 3** to view each Employee record

  6. Click **Export to CSV** for individual records:

     <img width="387" height="290" alt="image" src="https://github.com/user-attachments/assets/a7e88bf8-dbf9-497f-8128-c3c15a364615" />

     >**Note**: Exporting Employee records using the **Export to CSV** button must be done while on the **View Record** panel as this provides a **personalized transaction history**
     >while the **Export Transactions** and **Export Orders** buttons give a **global view** of all employee transactions and orders

### Using the Order Feature

  1. **Run** the ECS application

  2. Navigate to the **View Record** panel on the UI:

     <img width="450" height="299" alt="image" src="https://github.com/user-attachments/assets/9d55b469-b74f-4d28-8605-5fe01c8959cc" />

  3. Click the **Order** button on the upper part of the UI:

     <img width="975" height="99" alt="image" src="https://github.com/user-attachments/assets/8ea44b8a-d25d-4f7e-ad92-384c48eb1ba0" />

     >**Note**: If the order table is empty after clicking the **Order** button, please **Return** at least one piece of equipment first by clicking the **Return Equipment** button.

     <img width="681" height="455" alt="image" src="https://github.com/user-attachments/assets/8d39fff0-4e56-41fa-b025-2e64ccef3fe4" />

     >**Note**: No equipment available to order

     <img width="577" height="406" alt="image" src="https://github.com/user-attachments/assets/18d36dae-deec-4b9a-9476-27b2b0e4e977" />

     >**Note**: Return a piece of equipment

     <img width="686" height="460" alt="image" src="https://github.com/user-attachments/assets/27464795-57ff-484e-83ee-efbf8132b89e" />

     >**Note**: Click the **Order** button after returning and the equipment will appear as an item that is orderable

  4. Select the equipment to order and press **Confirm Order**:

     <img width="624" height="417" alt="image" src="https://github.com/user-attachments/assets/209c8c6e-80ea-482e-bbe6-705a19bb9198" />

  5. Navigate to the **Orders** panel on the UI

     <img width="534" height="144" alt="image" src="https://github.com/user-attachments/assets/ce94afd5-ebed-497b-be08-68c96c483d83" />

  6. Search for the equipment ordered in **Step 4**:

     <img width="984" height="304" alt="image" src="https://github.com/user-attachments/assets/066ac922-6dfe-48b5-bf32-f20fc8bdb64a" />

  7. Click the **Cancel** button on the top of the UI

     <img width="975" height="51" alt="image" src="https://github.com/user-attachments/assets/c9c95aba-0d69-4314-9d67-7b3622abbffa" />

     >**Note:** To **Cancel** an order for equipment, the **Orders** panel must be active
     >
     >Be sure to highlight the equipment that you would like to cancel first, as shown in Step 6

     <img width="925" height="399" alt="image" src="https://github.com/user-attachments/assets/283b6c56-a776-4127-9438-e3b4bf590746" />

  8. **Observer** the change in state from **Confirmed** to **Cancelled**

     <img width="975" height="241" alt="image" src="https://github.com/user-attachments/assets/3fa2c548-78c1-4cb5-95c3-dc22267a00c2" />
