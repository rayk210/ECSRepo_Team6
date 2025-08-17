# Equipment Checkout System (ECS)

## Project Description
The **ECS system** is a Java-based desktop application that's designed to manage the lifecycle of equipment in GB manufacturing.

It supports the following features:

-**Checkout Equipment**: Facilitates the check-out of available equipment from warehouse inventory by authorized employees.

-**Return Equipment**: Enables employees to return previously borrowed equipment through the system. 

-**Order Equipment**: Enables maintenance employees to order equipment required for their tasks

-**Receive Reminders**: Describes a user-driven process for receiving reminders about upcoming or overdue equipment return dates.

-**View Record**: Enables employees to view their personal transaction history related to equipment usage

## System Requirements
-Java JDK 8 or higher

[Download JDK](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)

-Eclipse IDE for Java

[Download Eclipse IDE for Java](https://eclipseide.org/)

-MySQL Workbench

[Download MySQL Workbench](https://dev.mysql.com/downloads/workbench/)

-This project already contains **mysql-connector-j-9.4.0.jar** within the lib folder, so it does not need to be downloaded separately

## Obtaining the ECS Project

>**Click Here**: [Link to ECS Project in GitHub](https://github.com/rayk210/ECSRepo_Team6.git)

 1. Download ZIP file from GitHub

    <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/7d401a10-d0d0-45ff-9f9a-e16eeb27fc41" />


 2. Extract contents to local folder
    
 3. The extracted folder will contain the following:

    -Java source code in the **src/ecsapplication** folder

    -SQL dump and structure in **sql/ceis400courseproject_2.sql**

    -JAR file in **lib/mysql-connector-j-9.4.0.jar**

    -.settings folder
    
## Setup Database

>**Note**: Ensure the ECS repository has been downloaded from GitHub and extracted so that the **ceis400courseproject_2.sql** dump file is available.

1. **Start MySQL Workbench**
   
   -Ensure MySQL server is running.

   -Press the **plus** icon

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/ed169261-b57a-4dc7-8537-a1918a93c10b" />

   -Setup New Connection

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/633a6028-83df-43ed-a737-27fc6bbf8e70" />

     >**Note**: Enter the following:
     >
     >**Connection Name:** ceis400courseproject400
     >
     >**Connection Method:** Standard(TCP/IP)
     >**Hostname:** localhost
     >
     >**Port:** 3306
     >
     >**Username:** root
     >
     >**Password:** devry123
  
   -Click on Project:
   
     <img width="549" height="335" alt="image" src="https://github.com/user-attachments/assets/24869211-4fb6-4bea-8cd7-13202ebb84a0" />

   -Click on the folder SQL button:

     <img width="81" height="92" alt="image" src="https://github.com/user-attachments/assets/949cd578-7d8a-47e9-ab91-9fd968882508" />

   -Select the .SQL dump file **ceis400courseproject_2.sql** in the unzipped file from GitHub:

     <img width="750" height="450" alt="image" src="https://github.com/user-attachments/assets/07534bb2-baff-4745-b810-1886472b3ffa" />

   -Add this piece of code to the top at line 1:
   
     CREATE DATABASE IF NOT EXISTS ceis400courseproject;
   
     USE ceis400courseproject;

   -Select lightning bolt icon to execute query:

     <img width="77" height="69" alt="image" src="https://github.com/user-attachments/assets/0f6b701b-3ee1-4dd4-8009-95bbc3c35148" />

   -Click on schemas and the rotating errors to reload.

     <img width="400" height="300" alt="image" src="https://github.com/user-attachments/assets/3dd84dd0-8dad-4821-b1b0-4ac479105292" />

   -Right click and set **ceis400courseproject** as **Default Schema**

     <img width="450" height="300" alt="image" src="https://github.com/user-attachments/assets/1e9f3449-419a-47e2-b013-fdda45e7c9ae" />

2. **Verify Database Tables**

   Run this code in a new query tab:

   USE ceis400courseproject;
   SHOW TABLES;

   SELECT * FROM employee;
   
   SELECT * FROM equipment LIMIT 5;
   
   SELECT * FROM `order` LIMIT 5;
   
   SELECT * FROM transaction LIMIT 5;
   
   SELECT * FROM reminder LIMIT 5;

   >**Note**: **order** must be enclosed in backquotes **``**

   <img width="627" height="461" alt="image" src="https://github.com/user-attachments/assets/3a009040-d57a-4f88-b06b-e09adea5bb95" />

## Running the ECS System

### 1. Open Project in Eclipse IDE

  -Launch Eclipse IDE

  -Navigate to **File** --> **Import** --> **Existing Projects into Workspace**

  <img width="550" height="500" alt="image" src="https://github.com/user-attachments/assets/3246561b-8e4e-4d21-ae99-f7e5b87654e1" />

  -Select the ECS project folder that was extracted before

  <img width="550" height="700" alt="image" src="https://github.com/user-attachments/assets/9bd2f33d-b16f-4981-b6dc-e254cd43c555" />
  
  -Ensure **lib** folder contains **mysql-connector-j-9.4.0.jar**

  <img width="404" height="160" alt="image" src="https://github.com/user-attachments/assets/480baffc-0dc1-4842-bfc3-93b55660cd8d" />


### 2. Configure Database Connection

  -Open the **DBConnect.java** file

  -Ensure that the JDBC url, username, and password match your MySQL setup done in the **Setup Database** part above:
  
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
