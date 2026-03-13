// ================= PACKAGE DECLARATION =================
// This tells Java which folder (package) this class belongs to.
// NetBeans automatically creates this when you create a new project.
package com.mycompany.motorphpayrollsystem.java;

/*
 * ============================================================================
 * MOTORPH BASIC PAYROLL SYSTEM
 * * DESCRIPTION:
 * This program manages employee information and attendance records for MotorPH
 * It calculates monthly salaries from June to December across two cutoffs.
 * It applies government deductions at the end of the 2nd cutoff.
 * ============================================================================
 */

//==============IMPORT STATEMENTS================
//This is needed for reading CSV files and interacting with the user.
import java.io.BufferedReader; // Reads text from files line by line.
import java.io.FileReader; // Opens a file
import java.io.IOException; // Handles error
import java.util.Scanner; // Reads user input

// ================= MAIN CLASS =================
public class MotorPHPayrollSystem { 

    // Global Constants for Array Capacities
    static final int MAX_EMP = 100; // Maximum number of employees supported
    static final int MAX_ATT = 8000; // Maximum number of attendance records

    // Parallel Arrays to store Employee Data
    static String[] empID = new String[MAX_EMP]; // Employee ID
    static String[] empName = new String[MAX_EMP]; // Employee full names
    static String[] empBday = new String[MAX_EMP]; // Employee birthdays
    static double[] empRate = new double[MAX_EMP]; // Employee hourly rates
    static int empTotal = 0;                       // Current total employee count

    // Parallel Arrays for Attendance Logs
    static String[] attID = new String[MAX_ATT]; // Employee numbers for attendance records
    static String[] attDate = new String[MAX_ATT]; // Date of attendance
    static String[] attIn = new String[MAX_ATT]; // Clock-in time
    static String[] attOut = new String[MAX_ATT]; // Clock-out time
    static int attTotal = 0;                      // Current total attendance records
    

    // ================= MAIN METHOD =================
    public static void main(String[] args) {
        // Initialize data by reading CSV files
        loadEmployees("employees.csv"); // Loads employee data from CSV
        loadAttendance("attendance.csv"); // Loads attendance data from CSV

        Scanner input = new Scanner(System.in); // Scanner object to read console input

        // 1. LOGIN SEQUENCE
        System.out.println("------------------------------------------");
        System.out.println("   Welcome to MotorPH Payroll System      ");
        System.out.println("------------------------------------------");
        System.out.print("Username: ");
        String user = input.nextLine(); // Reads the username input
        System.out.print("Password: ");
        String pass = input.nextLine(); // Reads the password input

        // Validate login Credentials
        if (pass.equals("12345") && (user.equals("employee") || user.equals("payroll_staff"))) {
        // This line checks if the entered password and username matches the given values.
            if (user.equals("employee")) {
                employeeRole(input); // This calls the employee menu
            } else {
                payrollRole(input); // This calls the payroll staff menu
            }
        } else {
            // Informs user if login details entered is incorrect.
            System.out.println("Incorrect username and/or password.");
        }
        // This closes the scanner.
        input.close();
    }

    // ==========================================
    // ROLE: EMPLOYEE
    // ==========================================
    // Displays personal info for employees.
    public static void employeeRole(Scanner sc) {

    // This keeps the Employee search running until the user chooses to exit.
    while (true) {

        // Ask the user to enter an employee number.
        // The user can enter 0 if they want to exit the employee view.
        System.out.print("\nEnter employee number (0 to Exit): ");

        // Read the employee number entered by the user.
        String idSearch = sc.nextLine();

        // If the user enters 0, exit the employee role function.
        if (idSearch.equals("0")) {
            System.out.println("Exiting Employee View...");
            break; // Exit the loop and return to the previous menu.
        }

        // Check if the input is empty or invalid.
        if (idSearch.trim().isEmpty()) {
            System.out.println("Enter valid choice number to proceed.");
            continue; // Restart the loop.
        }

        // Search for the employee ID in the system.
        int index = findEmp(idSearch);

        // If the returned index is not -1, the employee exists.
        if (index != -1) {

            // This prints the employee stored details.
            System.out.println("\n--- Employee Details ---");
            System.out.println("Employee Number: " + empID[index]);
            System.out.println("Employee Name:   " + empName[index]);
            System.out.println("Birthday:        " + empBday[index]);

        } 
        else {

            // This prints if the employee ID is not found.
            System.out.println("Employee number does not exist.");
        }
    }
}

    // ==========================================
    // ROLE: PAYROLL STAFF
    // ==========================================
    // Staff can process payroll for one or all employees.
    public static void payrollRole(Scanner sc) {

    // Loop to keep the Payroll Staff Menu running until the user chooses to exit.
    while (true) {

        // Display the main payroll menu.
        System.out.println("\n--- Payroll Staff Menu ---");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("Select choice: ");

        // Validate if the user entered a number.
        if (!sc.hasNextInt()) {
            // If input is not a number, show an error message.
            System.out.println("Enter valid choice number to proceed.");
            sc.nextLine(); // Clear invalid input from the scanner.
            continue; // Return to the start of the loop.
        }

        // Read the user's menu selection.
        int choice = sc.nextInt();
        sc.nextLine(); // Clear the input buffer.

        // If the user selects option 2, exit the payroll menu.
        if (choice == 2) {
            System.out.println("Exiting Payroll Menu...");
            break; // Stops the loop and exits the function.
        }

        // If the user selects option 1, display the payroll processing submenu.
        if (choice == 1) {

            // Submenu loop so the user can process payroll multiple times.
            while (true) {

                // Display submenu options.
                System.out.println("\n[1] One employee");
                System.out.println("[2] All employees");
                System.out.println("[3] Exit");
                System.out.print("Choice: ");

                // Validate submenu input
                if (!sc.hasNextInt()) {
                    System.out.println("Enter valid choice number to proceed.");
                    sc.nextLine(); // Clear invalid input.
                    continue; // Restart submenu.
                }

                // Capture the user's submenu choice.
                int sub = sc.nextInt();
                sc.nextLine(); // Clear buffer.

                // OPTION 1: Process payroll for one specific employee.
                if (sub == 1) {

                    // Ask the user for the employee number.
                    System.out.print("Enter employee number (0 to Exit): ");
                    String id = sc.nextLine();

                    // If user enters 0, return to the submenu.
                    if (id.equals("0")) {
                        System.out.println("Returning to Payroll Menu...");
                        continue;
                    }

                    // Find the employee in the system using the employee ID.
                    int index = findEmp(id);

                    // If the employee exists, process payroll.
                    if (index != -1) {
                        processPayroll(index);
                    } 
                    else {
                        // Display error message if employee ID does not exist.
                        System.out.println("Employee number does not exist.");
                    }
                }

                // OPTION 2: Process payroll for all employees.
                else if (sub == 2) {

                    // Loop through all employees stored in the system.
                    for (int i = 0; i < empTotal; i++) {

                        // Run payroll calculation for each employee.
                        processPayroll(i);
                    }
                }

                // OPTION 3: Exit the payroll processing submenu.
                else if (sub == 3) {
                    break; // Exit the submenu and return to main payroll menu.
                }

                // If the user enters an invalid submenu choice.
                else {
                    System.out.println("Enter valid choice number to proceed.");
                }
            }
        }

        // If the main menu choice is not 1 or 2.
        else {
            System.out.println("Enter valid choice number to proceed.");
        }
    }
}

    // ==========================================
    // PAYROLL PROCESSING
    // ==========================================
    // Handles monthly payroll, hours, gross/net salary & deductions
    public static void processPayroll(int idx) {
        System.out.println("\n****************************************");
        System.out.println("Employee #: " + empID[idx]);
        System.out.println("Name: " + empName[idx]);
        System.out.println("Birthday: " + empBday[idx]);

        // Process months June (6) to December (12)
        for (int m = 6; m <= 12; m++) {
            // 1st Cutoff: Day 1-15
            // m  - Month
            // th1 - Total hours during 1st half.
            // gs1 - Gross Salary for the first cutoff.
            double th1 = getHours(empID[idx], m, 1, 15);
            double gs1 = th1 * empRate[idx];  // Gross salary for the 1st cutoff.

            // 2nd Cutoff: Day 16-31
            // th2 - Total hours during 2nd half.
            // gs2 - Gross Salary for the second cutoff.
            double th2 = getHours(empID[idx], m, 16, 31);
            double gs2 = th2 * empRate[idx]; // Gross salary for the 2nd cutoff.

            // 1st Cutoff Output
            System.out.println("\nCutoff: " + m + "/01 to " + m + "/15");
            System.out.println("Total Hours: " + th1);
            System.out.println("Gross Salary: " + gs1);
            System.out.println("Net Salary: " + gs1);

            // Calculation of Government Deductions (Applied on 2nd Cutoff)
            double totalGross = gs1 + gs2;
            double sss = computeSSS(totalGross); // Computes SSS contribution.
            double phil = computePhilHealth(totalGross); // Computes Philhealth contribution.
            double pagibig = computePagibig(totalGross); // Computes Pagibig contribution.
            double taxable = totalGross - (sss + phil + pagibig); // Computes the taxable amount for computing the withholding tax.
            double tax = computeTax(taxable); // Computes withholding tax.

            double totalDed = sss + phil + pagibig + tax; // Computes total deductions.
            double net2 = gs2 - totalDed; // Computes the net salary after all deductions.

            // 2nd Cutoff Output
            // This prints the final payroll summary of the month.
            System.out.println("\nCutoff: " + m + "/16 to end");
            System.out.println("Total Hours: " + th2);
            System.out.println("Gross Salary: " + gs2);
            System.out.println("SSS: " + sss);
            System.out.println("PhilHealth: " + phil);
            System.out.println("Pag-IBIG: " + pagibig);
            System.out.println("Tax: " + tax);
            System.out.println("Total Deductions: " + totalDed);
            System.out.println("Net Salary: " + net2);
        }
    }

    // ==========================================
    // TIME CALCULATION LOGIC (8 AM - 5 PM)
    // ==========================================
    // Method that calculates total working hours of an employee
    // id = employee ID
    // month = selected month
    // start = start day of cutoff
    // end = end day of cutoff
    public static double getHours(String id, int month, int start, int end) {
        double totalH = 0; // Variable that will store the total computed hours

        // Loop through all attendance records
        for (int i = 0; i < attTotal; i++) {
            // Check if the attendance record belongs to the selected employee
            if (attID[i].equals(id)) {
                // Split date MM/DD/YYYY
                String[] dParts = attDate[i].split("/");
                // Convert month from String to integer
                int m = Integer.parseInt(dParts[0]);
                // Convert day from String to integer
                int d = Integer.parseInt(dParts[1]);
                
                // Check if the record is within the selected month and cutoff dates
                if (m == month && d >= start && d <= end) {
                    // Convert time-in from HH:MM format to decimal
                    double tin = timeToDec(attIn[i]);
                    // Convert time-out from HH:MM format to decimal
                    double tout = timeToDec(attOut[i]);

                    // Rule: Only 8:00 AM to 5:00 PM (17.0)
                    // If employee arrived earlier than 8 AM, set it to 8 AM
                    if (tin < 8.0) {
                        tin = 8.0;
                    }
                    // If employee left later than 5 PM, limit it to 5 PM
                    if (tout > 17.0) {
                        tout = 17.0;
                    }
                    // Compute total work hours for that day
                    double daily = tout - tin;

                    // Subtract 1 hour for lunch if they worked a full shift
                    if (daily > 4.0) {
                        daily = daily - 1.0;
                    }
                    // Only add positive hours to total
                    if (daily > 0) {
                        totalH = totalH + daily;
                    }
                }
            }
        }
        // Return the total computed hours for the employee
        return totalH;
    }
    // Method that converts time from HH:MM format to decimal format
    public static double timeToDec(String t) {
        // Split the time string using ":" delimiter
        String[] p = t.split(":");
        // Convert the hour part to double
        double hours = Double.parseDouble(p[0]);
        // Convert the minutes part to double
        double mins = Double.parseDouble(p[1]);
        // Convert minutes into decimal and add to hours
        return hours + (mins / 60.0);
    }

    // ==========================================
    // FILE HANDLING (BUFFERED READER)
    // ==========================================
    // Method that loads employee data from employees.csv
    public static void loadEmployees(String path) {
        
        // Try-with-resources automatically closes the file after reading
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Read the first line (header) and skip it

            String line;
            while ((line = br.readLine()) != null && empTotal < MAX_EMP) { // Read each line of the file until end or array limit reached
                // Basic split: uses a manual loop to clean quotes instead of advanced regex
                String[] data = line.split(",");
                empID[empTotal] = data[0]; // Store employee ID
                empName[empTotal] = data[2] + " " + data[1]; // Combine last name and first name
                empBday[empTotal] = data[3]; // Store birthday

                // Get hourly rate column and remove quotes and commas
                String rateRaw = data[data.length - 1].replace("\"", "").replace(",", "");
                empRate[empTotal] = Double.parseDouble(rateRaw); // Convert the hourly rate string into double
                empTotal++; // Increase employee counter
            }
        } catch (IOException e) {
            System.out.println("Error reading employees.csv"); // Display error if the file cannot be read
        }
    }
    
    // Method that loads attendance records from attendance.csv
    public static void loadAttendance(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Skip header row

            String line;
            while ((line = br.readLine()) != null && attTotal < MAX_ATT) { // Read each attendance line
                String[] data = line.split(","); // Split CSV data into columns
                attID[attTotal] = data[0]; // Store employee ID
                attDate[attTotal] = data[3]; // Store attendance date
                attIn[attTotal] = data[4]; // Store time-in
                attOut[attTotal] = data[5]; // Store time-out
                attTotal++; // Increase attendance record counter
            }
        } catch (IOException e) {
            // Display error if attendance file cannot be read
            System.out.println("Error reading attendance.csv");
        }
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    // Method to find the index of an employee based on ID
    public static int findEmp(String id) {
        for (int i = 0; i < empTotal; i++) { // Loop through all employee records
            if (empID[i].equals(id)) { // If employee ID matches the input ID
                return i; // Return the index position of the employee
            }
        }
        // Return -1 if employee is not found
        return -1;
    }
    
    //==========SSS CONDITIONS==========
   public static double computeSSS(double salary) {

    // Each condition checks the salary range and returns the corresponding SSS contribution

    if (salary <= 3250) return 135;
    else if (salary <= 3750) return 157.50;
    else if (salary <= 4250) return 180;
    else if (salary <= 4750) return 202.50;
    else if (salary <= 5250) return 225;
    else if (salary <= 5750) return 247.50;
    else if (salary <= 6250) return 270;
    else if (salary <= 6750) return 292.50;
    else if (salary <= 7250) return 315;
    else if (salary <= 7750) return 337.50;
    else if (salary <= 8250) return 360;
    else if (salary <= 8750) return 382.50;
    else if (salary <= 9250) return 405;
    else if (salary <= 9750) return 427.50;
    else if (salary <= 10250) return 450;
    else if (salary <= 10750) return 472.50;
    else if (salary <= 11250) return 495;
    else if (salary <= 11750) return 517.50;
    else if (salary <= 12250) return 540;
    else if (salary <= 12750) return 562.50;
    else if (salary <= 13250) return 585;
    else if (salary <= 13750) return 607.50;
    else if (salary <= 14250) return 630;
    else if (salary <= 14750) return 652.50;
    else if (salary <= 15250) return 675;
    else if (salary <= 15750) return 697.50;
    else if (salary <= 16250) return 720;
    else if (salary <= 16750) return 742.50;
    else if (salary <= 17250) return 765;
    else if (salary <= 17750) return 787.50;
    else if (salary <= 18250) return 810;
    else if (salary <= 18750) return 832.50;
    else if (salary <= 19250) return 855;
    else if (salary <= 19750) return 877.50;
    else if (salary <= 20250) return 900;
    else if (salary <= 20750) return 922.50;
    else if (salary <= 21250) return 945;
    else if (salary <= 21750) return 967.50;
    else if (salary <= 22250) return 990;
    else if (salary <= 22750) return 1012.50;
    else if (salary <= 23250) return 1035;
    else if (salary <= 23750) return 1057.50;
    else if (salary <= 24250) return 1080;
    else if (salary <= 24750) return 1102.50;
    else return 1125; // Maximum contribution
}
    
    //==========PHILHEALTH CONDITIONS==========
    public static double computePhilHealth(double salary) {

    double premium; // Variable to store PhilHealth premium

    // If salary is 10,000 or below
    if (salary <= 10000) {
        premium = 300;
    }

    // If salary is between 10,000.01 and 59,999.99
    else if (salary < 60000) {
        premium = salary * 0.03;
    }

    // If salary is 60,000 and above
    else {
        premium = 1800;
    }

    // Employee pays only 50%
    return premium / 2;
}

    // ==========PAGIBIG CONDITIONS==========
    public static double computePagibig(double salary) {

    double rate; // Contribution rate
    double contribution; // Computed contribution

    // If salary is 1500 or below
    if (salary <= 1500) {
        rate = 0.01; // 1% contribution
    } else {
        rate = 0.02; // 2% contribution
    }

    // Compute contribution based on rate
    contribution = salary * rate;

    // Maximum contribution limit is 100
    if (contribution > 100) {
        contribution = 100;
    }
    // Return final Pag-IBIG deduction
    return contribution;
}

    //==========WITHHOLDING TAX CONDITIONS==========
    public static double computeTax(double taxable) {
        
        // No tax if taxable income is below threshold
        if (taxable <= 20833) { 
            return 0;
        }
        // 20% tax bracket
        if (taxable <= 33333) {
            return (taxable - 20833) * 0.20;
        }
        // 25% tax bracket with base tax
        return (taxable - 33333) * 0.25 + 2500;
    }
}

