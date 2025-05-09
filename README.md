# Shticell Project: Real-Time Collaborative Spreadsheet Application

## Overview
Shticell is a custom-built program designed to emulate the functionality of electronic spreadsheet software like Microsoft Excel and Google Sheets. The project aims to provide hands-on experience in developing and managing a large codebase, involving dozens of classes and requiring sophisticated design principles.

## Key Features

- **Real-Time Collaboration**  
  Seamless updates across multiple users with HTTP and JSON-based communication.

- **Integrated Chat**  
  Chat while editing, making teamwork intuitive and productive.

- **Dynamic Updates and Interactive Changes**  
  - **Real-Time Updates:** Automatically recalculates formulas and ensures accurate results.  
  - **Dynamic Change Feature:** Adjust cell values using a slider and see real-time updates in the spreadsheet and linked charts.

- **Data Visualization**  
  Create dynamic charts (e.g., bar and line charts) directly from spreadsheet data for better analysis.

- **Role-Based Permissions**  
  - **Owner:** Full control to edit and manage who can view or edit the sheet.  
  - **Writer:** Can edit the sheet but can’t manage permissions.  
  - **Reader:** Can only look at the sheet without making changes.  
  If you need edit permission, you can send a request to the spreadsheet owner. Owners can quickly approve or deny these requests.

- **Responsive UI**  
  Built with JavaFX, offering three themes and smooth navigation for large datasets.

## Architecture

- **Client-Server Model**  
  Built for efficiency, with the server managing permissions, updates, and data synchronization, while thread synchronization ensures multiple users can collaborate seamlessly in real time.

  - **Server:** Handles core operations and enforces access control.  
  - **Client:** Features an intuitive UI for seamless interaction with spreadsheets.

## Technologies Used

- **Backend:** Java, Apache Tomcat, Gson, JAXB.  
- **Frontend:** Java, JavaFX, FXML, OkHttp, Gson.  
- **Communication:** HTTP and JSON.

## Getting Started

### Navigate to the Latest Release:

- **Release ZIP**: Contains both the client-side and server-side applications, ready for deployment. There are **three versions**, each corresponding to a specific milestone of the project.

  - **Version 1 - Console UI Implementation**:  
    The first milestone introduces the console-based version of the application.
    
  - **Version 2 - JavaFX Graphics and Engine Enhancement**:  
    The second version enhances the previous implementation by introducing a graphical user interface (GUI) using JavaFX.

  - **Version 3 - Client-Server Final Version**:  
    The final version of the project integrates a full client-server architecture.

## Set Up the Environment

- Install **Java JDK 21** and **Apache Tomcat** for server deployment.
- The client application includes **JavaFX**, which is bundled with the project.

## Run the Application

1. Deploy the server on Tomcat.
2. Run the client using the provided scripts.
