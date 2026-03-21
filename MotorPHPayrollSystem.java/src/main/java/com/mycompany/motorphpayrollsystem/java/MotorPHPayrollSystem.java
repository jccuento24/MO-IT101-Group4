// ================= PACKAGE DECLARATION =================
// This tells Java which folder (package) this class belongs to.
// NetBeans automatically creates this when you create a new project.
package com.mycompany.motorphpayrollsystem.java;

/*
 * ============================================================================
 * MOTORPH BASIC PAYROLL SYSTEM
 * * DESCRIPTION:
 * This program manages employee information and attendance records for MotorPH.
 * It calculates monthly salaries from June to December across two cutoffs.
 * It applies government deductions at the end of the 2nd cutoff.
 *
 * * PURPOSE:
 * This program simulates a payroll system that allows:
 * - Employees to view their personal information
 * - Payroll staff to compute salaries, attendance, and deductions
 *
 * * DESIGN:
 * The system uses parallel arrays to store employee and attendance data.
 * Payroll is calculated based on:
 * - Hours worked within cutoff periods
 * - Government-mandated deductions (SSS, PhilHealth, Pag-IBIG, Tax)
 *
 * * NOTE:
 * All computations follow basic company rules such as:
 * - 8:00 AM – 5:00 PM working hours
 * - 10-minute grace period
 * - 1-hour unpaid lunch break
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
    static final double WORK_START = 8.0; // Official start of the workday in decimal hours (8:00 AM).
    static final double GRACE_END = 8.1667; // End of the grace period in decimal hours (8:10 AM ≈ 8 + 10/60).
                                            // Employees arriving at or before this time are still considered on time.

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

    
    // ================================================================
    // MAIN METHOD
    // ================================================================
    // Entry point of the program where execution begins.
    // This method controls the overall system flow: initialization, user authentication, and role-based navigation.
    // It ensures that all required data is loaded first, then restricts access through login before allowing any operations.
    // This design improves security, prevents errors from missing data, and organizes the program into a clear step-by-step process.
    public static void main(String[] args) {

        initializeSystem(); // Load all required system data (employees and attendance) before the user can interact with the payroll system.

        Scanner input; // Create a Scanner object to allow user input from the keyboard during login and menu navigation.
        input = new Scanner(System.in);

        String[] credentials = login(input); // Call the login method to authenticate the user and capture their username/password.

        if (credentials != null) { // Only proceed if login was successful (null means authentication failed).
            redirectUserRole(credentials[0], input); // Send the user to the correct menu based on their role (employee or payroll staff).
        }

        input.close(); // Close the Scanner to free system resources and avoid memory leaks.
    }
    
    
    // ------------------------------
    // SYSTEM INITIALIZATION FUNCTION
    // ------------------------------
    public static void initializeSystem() {

        // Load employee records from the CSV file so the system has access to employee information for payroll calculations.
        loadEmployees("employees.csv");

        // Load attendance data from the CSV file since payroll computation depends on hours worked.
        loadAttendance("attendance.csv");
    }

    
    // ------------------------------
    // LOG-IN METHOD
    // ------------------------------
    public static String[] login(Scanner input) {

        System.out.println("------------------------------------------"); // Display a separator line to make the login screen visually clear.
        System.out.println("   Welcome to MotorPH Payroll System      "); // Display the system title so users know what application they are accessing.
        System.out.println("------------------------------------------"); // Closing separator for formatting.

        System.out.print("Username: "); // Prompt the user to enter their username for authentication.
        String user = input.nextLine(); // Read the username input from the keyboard.

        System.out.print("Password: "); // Prompt the user to enter their password.
        String pass = input.nextLine(); // Read the password input from the keyboard.

        // Validate login credentials to restrict system access only to authorized users.
        // The system currently supports two roles: employee and payroll_staff.
        if (pass.equals("12345")
                && (user.equals("employee") || user.equals("payroll_staff"))) {

            return new String[]{user, pass}; // Return the credentials if authentication succeeds so the system can determine the user role.
        }

        System.out.println("Incorrect username and/or password."); // Inform the user that the login attempt failed.
        return null; // Return null to indicate authentication failure and prevent access to system functions.
    }

    
    // ------------------------------
    // REDIRECT USER ROLE METHOD
    // ------------------------------
    public static void redirectUserRole(String user, Scanner input) {

        // Direct the user to the appropriate system functionality based on their role.
        if (user.equals("employee")) {
            employeeRole(input); // Employees are redirected to their personal payroll/attendance view.
        } else {
            payrollRole(input); // Payroll staff are redirected to payroll management functions.
        }
    }
    
    

    // ================================================================
    // ROLE: EMPLOYEE
    // ================================================================
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

            } else {

                // This prints if the employee ID is not found.
                System.out.println("Employee number does not exist.");
            }
        }
    }

    
    // ================================================================
    // ROLE: PAYROLL STAFF
    // ================================================================
    // Staff can process payroll for one or all employees.
    public static void payrollRole(Scanner sc) {

        while (true) { // Keep showing the payroll menu until the user chooses to exit

            int choice = showPayrollMenu(sc); // Display the payroll staff menu and capture the user's selection

            if (choice == 2) { // If the user selects option 2, they want to leave the payroll menu
                System.out.println("Exiting Payroll Menu..."); // Inform the user that the payroll menu is closing
                break; // Exit the loop and return to the previous system level
            }

            if (choice == 1) { // If the user selects option 1, they want to start payroll processing
                payrollProcessingMenu(sc); // Redirect to the payroll processing submenu
            }
        }
    }
    
    // ----------------------------
    // SHOW PAYROLL MENU
    // ----------------------------
    public static int showPayrollMenu(Scanner sc) {

        System.out.println("\n--- Payroll Staff Menu ---"); // Display menu header for payroll staff options
        System.out.println("1. Process Payroll"); // Option for processing employee payroll
        System.out.println("2. Exit"); // Option for exiting the payroll staff menu
        System.out.print("Select choice: "); // Prompt the user to select a menu option

        if (!sc.hasNextInt()) { // Check if the user entered a number to prevent input mismatch errors
            System.out.println("Enter valid choice number to proceed."); // Inform the user that only numeric input is allowed
            sc.nextLine(); // Clear the invalid input from the scanner buffer to avoid repeated errors
            return 0; // Return 0 so the calling method treats it as an invalid menu choice
        }

        int choice = sc.nextInt(); // Read the numeric choice entered by the user
        sc.nextLine(); // Consume the leftover newline character to prevent input skipping in later prompts

        return choice; // Return the user's selection so the program can determine the next action
    }
    
    // ----------------------------
    // PAYROLL PROCESSING MENU
    // ----------------------------
    public static void payrollProcessingMenu(Scanner sc) {

        while (true) { // Continuously display the payroll processing menu until the user exits

            int sub = showProcessingMenu(sc); // Display the submenu and capture the user's selection

            switch (sub) { // Use a switch statement to handle multiple menu options clearly

                case 1:
                    processSingleEmployee(sc); // Process payroll for a specific employee selected by the user
                    break; // Stop executing further cases once this action is complete

                case 2:
                    processAllEmployees(); // Process payroll for every employee in the system
                    break; // Prevent fall-through to the next case

                case 3:
                    return; // Exit the payroll processing menu and return to the previous payroll menu

                default:
                    System.out.println("Enter valid choice number to proceed."); // Handle invalid menu choices
            }
        }
    }

    // ----------------------------
    // PRINT PROCESSING MENU
    // ----------------------------
    public static int showProcessingMenu(Scanner sc) {

        System.out.println("\n[1] One employee"); // Option to process payroll for a single employee
        System.out.println("[2] All employees"); // Option to process payroll for all employees in the system
        System.out.println("[3] Exit"); // Option to exit the payroll processing submenu
        System.out.print("Choice: "); // Prompt the user to select an option

        if (!sc.hasNextInt()) { // Validate that the user entered a numeric value
            System.out.println("Enter valid choice number to proceed."); // Notify the user that the input is invalid
            sc.nextLine(); // Clear the invalid input to prevent scanner errors
            return 0; // Return 0 so the calling method knows the input was invalid
        }

        int sub = sc.nextInt(); // Read the submenu choice entered by the user
        sc.nextLine(); // Consume the leftover newline to prevent issues with future input

        return sub; // Return the submenu choice so the program can determine the action to perform
    }

    // ----------------------------
    // PROCESS SINGLE EMPLOYEE
    // ----------------------------
    public static void processSingleEmployee(Scanner sc) {

        System.out.print("Enter employee number (0 to Exit): "); // Ask the payroll staff to enter the employee ID they want to process
        String id = sc.nextLine(); // Read the employee ID entered by the user

        if (id.equals("0")) { // Allow the user to cancel the operation by entering 0
            System.out.println("Returning to Payroll Menu..."); // Inform the user that the operation is cancelled
            return; // Exit this method and go back to the payroll processing menu
        }

        int index = findEmp(id); // Search for the employee in the employee list and return their index position

        if (index != -1) { // If the employee is found (index not equal to -1)
            processPayroll(index); // Process payroll calculations for that specific employee
        } else {
            System.out.println("Employee number does not exist."); // Notify the user if the employee ID was not found in the system
        }
    }

    // ----------------------------
    // PROCESS ALL EMPLOYEES
    // ----------------------------
    public static void processAllEmployees() {

        for (int i = 0; i < empTotal; i++) { // Loop through all stored employees using the total employee count
            processPayroll(i); // Process payroll for each employee one by one using their index
        }
    }
    
    
    // ================================================================
    // CALCULATE GOVERNMENT DEDUCTIONS
    // ================================================================
    // This method computes all mandatory deductions from an employee's gross pay.
    // These include SSS, PhilHealth, Pag-IBIG, and income tax.
    // The goal is to determine how much should be deducted before getting net pay.
    public static double[] calculateDeductions(double totalGross) {

        // Compute SSS contribution based on salary bracket rules
        double sss = computeSSS(totalGross);

        // Compute PhilHealth contribution (usually percentage-based)
        double phil = computePhilHealth(totalGross);

        // Compute Pag-IBIG contribution (fixed or capped amount)
        double pagibig = computePagibig(totalGross);

        // Taxable income is reduced because government contributions are non-taxable
        double taxable = totalGross - (sss + phil + pagibig);

        // Compute income tax based on the remaining taxable income
        double tax = computeTax(taxable);

        // Total deductions represent everything that will be subtracted from gross pay
        double totalDed = sss + phil + pagibig + tax;

        // Return all computed values for use in payroll breakdown and reporting
        return new double[]{sss, phil, pagibig, tax, totalDed};
    }
   
    
    // ================================================================
    // PAYROLL PROCESSING
    // ================================================================
    // This method handles the full payroll computation for a single employee.
    // It processes salaries month-by-month and splits each month into two cutoffs
    // (1–15 and 16–end), which is a common payroll structure.
    public static void processPayroll(int idx) {

        // Display basic employee details before showing payroll breakdown
        printEmployeeInfo(idx);

        // Loop through months June (6) to December (12)
        // Assumes payroll data is only needed for this time period
        for (int m = 6; m <= 12; m++) {

            // FIRST CUTOFF (1st–15th of the month)
            // Get total hours worked during the first half of the month
            double totalHours1 = getHours(empID[idx], m, 1, 15);

            // Gross pay is calculated as hours worked multiplied by hourly rate
            double gross1 = totalHours1 * empRate[idx];

            // SECOND CUTOFF (16th–end of the month)
            // Captures remaining working days of the month
            double totalHours2 = getHours(empID[idx], m, 16, 31);

            // Compute gross pay for second cutoff
            double gross2 = totalHours2 * empRate[idx];

            // Combine both cutoffs to get total monthly gross income
            double totalGross = gross1 + gross2;

            // Deductions are calculated based on total monthly gross,
            // since government contributions and taxes are applied monthly
            double[] deductions = calculateDeductions(totalGross);

            // Print breakdown for first cutoff (usually no deductions applied yet)
            printFirstCutoff(m, totalHours1, gross1);

            // Print second cutoff including all deductions and final net pay
            // Deductions are applied here to reflect the full monthly adjustment
            printSecondCutoff(m, totalHours2, gross2, deductions);
        }
    }
    
    // ----------------------------
    // PRINT EMPLOYEE INFO
    // ----------------------------
    // Displays the basic personal details of the selected employee.
    // This serves as a header before showing payroll records,
    // so the user knows which employee the report belongs to.
    public static void printEmployeeInfo(int idx) {

        System.out.println("\n****************************************");
        System.out.println("Employee #: " + empID[idx]);   // Unique employee identifier
        System.out.println("Name: " + empName[idx]);       // Full name of employee
        System.out.println("Birthday: " + empBday[idx]);   // Used for identity/reference
    }

    // ----------------------------
    // PRINT FIRST CUTOFF
    // ----------------------------
    // Displays payroll details for the first half of the month (1st–15th).
    // No deductions are applied here because government deductions
    // are calculated on a monthly basis and usually reflected in the second cutoff.
    public static void printFirstCutoff(int month, double hours, double gross) {

        System.out.println("\nCutoff: " + month + "/01 to " + month + "/15");

        // Total working hours recorded for the first cutoff period
        System.out.println("Total Hours: " + hours);

        // Gross salary is based purely on hours worked (no deductions yet)
        System.out.println("Gross Salary: " + gross);

        // Net salary is equal to gross since no deductions are applied in this cutoff
        System.out.println("Net Salary: " + gross);
    }

    // ----------------------------
    // PRINT SECOND CUTOFF
    // ----------------------------
    // Displays payroll details for the second half of the month (16th–end).
    // All government deductions are applied here to reflect total monthly obligations.
    // This ensures that contributions and taxes are deducted only once per month.
    public static void printSecondCutoff(int month, double hours, double gross, double[] deductions) {

        // Extract individual deductions from the array for clarity
        double sss = deductions[0];        // Social Security contribution
        double phil = deductions[1];      // PhilHealth contribution
        double pagibig = deductions[2];   // Pag-IBIG contribution
        double tax = deductions[3];       // Income tax based on taxable income
        double totalDed = deductions[4];  // Sum of all deductions

        // Net salary is what the employee actually receives after deductions
        double net = gross - totalDed;

        System.out.println("\nCutoff: " + month + "/16 to end");

        // Total working hours for second cutoff
        System.out.println("Total Hours: " + hours);

        // Gross salary before deductions
        System.out.println("Gross Salary: " + gross);

        // Breakdown of each deduction for transparency
        System.out.println("SSS: " + sss);
        System.out.println("PhilHealth: " + phil);
        System.out.println("Pag-IBIG: " + pagibig);
        System.out.println("Tax: " + tax);

        // Total deductions applied for the entire month
        System.out.println("Total Deductions: " + totalDed);

        // Final take-home pay after all deductions
        System.out.println("Net Salary: " + net);
    }

    // ================================================================
    // TIME CALCULATION LOGIC (8 AM - 5 PM)
    // ================================================================
    // This method computes the total working hours of an employee, within a specific cutoff period and month.
    // It enforces company rules such as work schedule limits, grace periods, and lunch break deductions.
    public static double getHours(String id, int month, int start, int end) {

        double totalH = 0; // Accumulates total valid working hours

        // Loop through all attendance records in the dataset
        for (int i = 0; i < attTotal; i++) {

            // Process only records that belong to the selected employee
            if (attID[i].equals(id)) {

                // Extract month and day from date (format: MM/DD/YYYY)
                String[] dParts = attDate[i].split("/");
                int m = Integer.parseInt(dParts[0]); // Month
                int d = Integer.parseInt(dParts[1]); // Day

                // Include only records within the selected cutoff range
                if (m == month && d >= start && d <= end) {

                    // Convert time-in and time-out into decimal format
                    // Grace period is applied to avoid penalizing minor lateness
                    double timeIn = applyGracePeriod(timeToDec(attIn[i]));
                    double timeOut = timeToDec(attOut[i]);

                    // Enforce official working hours (8:00 AM – 5:00 PM)
                    // This prevents counting overtime or early logins
                    if (timeIn < 8.0) {
                        timeIn = 8.0;
                    }
                    if (timeOut > 17.0) {
                        timeOut = 17.0;
                    }

                    // Compute daily working hours
                    double daily = timeOut - timeIn;

                    // Deduct 1 hour lunch break if employee worked a half-day or more
                    // This reflects standard unpaid break policies
                    if (daily > 4.0) {
                        daily = daily - 1.0;
                    }

                    // Only add valid (positive) working hours
                    // Prevents errors from invalid or incomplete logs
                    if (daily > 0) {
                        totalH += daily;
                    }
                }
            }
        }

        // Return total hours worked for the cutoff period
        return totalH;
    }

    // ----------------------------
    // CONVERSION METHOD
    // ----------------------------
    // Converts time from "HH:MM" format into decimal hours.
    // This allows easier mathematical operations (e.g., 8:30 → 8.5).
    public static double timeToDec(String t) {

        String[] p = t.split(":"); // Separate hours and minutes

        double hours = Double.parseDouble(p[0]);
        double mins = Double.parseDouble(p[1]);

        // Convert minutes into fractional hours
        return hours + (mins / 60.0);
    }

    // ----------------------------
    // GRACE PERIOD
    // ----------------------------
    // Applies a grace period rule for late arrivals.
    // Employees arriving slightly late (within GRACE_END), are still considered on time (set to WORK_START).
    // This avoids penalizing minor delays.
    public static double applyGracePeriod(double tin) {

        // If employee arrives before official start, normalize to start time
        if (tin < WORK_START) {
            return WORK_START;
        }

        // If arrival is within grace period window, still count as on time
        if (tin <= GRACE_END) {
            return WORK_START;
        }

        // Otherwise, use actual time-in (late beyond grace period)
        return tin;
    }
    

    // ================================================================
    // FILE HANDLING (BUFFERED READER)
    // ================================================================
    // Uses BufferedReader to efficiently read large CSV files line-by-line instead of loading the entire file into memory.
    
    // ----------------------------
    // LOAD EMPLOYEE DATA
    // ----------------------------
    // Method that loads employee data from employees.csv
    // This serves as the main source of employee information used in payroll.
    public static void loadEmployees(String path) {

        // Try-with-resources automatically closes the file after reading
        // to prevent memory leaks and ensure proper resource management
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Read the first line (header) and skip it, because it only contains column labels, not actual data.

            String line;
            while ((line = br.readLine()) != null && empTotal < MAX_EMP) { // Read each line of the file until end or array limit reached, prevents array overflow errors.

                // Basic split: uses a manual loop to clean quotes instead of advanced regex, to keep the implementation simple and beginner-friendly.
                String[] data = line.split(",");
                empID[empTotal] = data[0]; // Store employee ID
                empName[empTotal] = data[2] + " " + data[1]; // Combine last name and first name, for easier display in reports.
                empBday[empTotal] = data[3]; // Store birthday (reference info for employee)

                // Get hourly rate column and remove quotes and commas, because CSV formatting may include symbols that prevent numeric conversion.
                String rateRaw = data[data.length - 1].replace("\"", "").replace(",", "");
                empRate[empTotal] = Double.parseDouble(rateRaw); // Convert the hourly rate string into double, to allow mathematical operations.
                empTotal++; // Increase employee counter
            }
        } catch (IOException e) {
            System.out.println("Error reading employees.csv"); // Display error if the file cannot be read, prevents program from crashing unexpectedly.
        }
    }
    // ----------------------------
    // LOAD ATTENDANCE RECORDS
    // ----------------------------
    // Method that loads attendance records from attendance.csv
    // This data is used to compute working hours for payroll.
    public static void loadAttendance(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Skip header row, since it does not contain actual attendance data.

            String line;
            while ((line = br.readLine()) != null && attTotal < MAX_ATT) { // Read each attendance line, ensures we do not exceed array capacity.
                String[] data = line.split(","); // Split CSV data into columns
                attID[attTotal] = data[0]; // Store employee ID, used to match attendance with employee records.
                attDate[attTotal] = data[3]; // Store attendance date, needed for filtering by cutoff and month.
                attIn[attTotal] = data[4]; // Store time-in, used to compute working hours.
                attOut[attTotal] = data[5]; // Store time-out, used to compute working hours.
                attTotal++; // Increase attendance record counter
            }
        } catch (IOException e) {
            // Display error if attendance file cannot be read, ensures user is informed of missing or incorrect file.
            System.out.println("Error reading attendance.csv");
        }
    }

    
    // ================================================================
    // UTILITY METHODS
    // ================================================================
    // These helper methods support searching and payroll computations.
    
    // Method to find the index of an employee based on ID.
    // This allows easy access to employee data stored in parallel arrays.
    public static int findEmp(String id) {
        for (int i = 0; i < empTotal; i++) { // Loop through all employee records
            if (empID[i].equals(id)) { // If employee ID matches the input ID
                return i; // Return the index position of the employee, so related data (name, rate, etc.) can be accessed.
            }
        }
        // Return -1 if employee is not found, acts as a simple error indicator for invalid ID input.
        return -1;
    }

    //==========SSS CONDITIONS==========
    // Computes SSS contribution based on salary brackets.
    // Each range corresponds to a fixed contribution as per policy.
    public static double computeSSS(double salary) {

        // Each condition checks the salary range and returns the corresponding SSS contribution.
        // This follows a bracket system where contribution increases with salary
        if (salary <= 3250) {
            return 135;
        } else if (salary <= 3750) {
            return 157.50;
        } else if (salary <= 4250) {
            return 180;
        } else if (salary <= 4750) {
            return 202.50;
        } else if (salary <= 5250) {
            return 225;
        } else if (salary <= 5750) {
            return 247.50;
        } else if (salary <= 6250) {
            return 270;
        } else if (salary <= 6750) {
            return 292.50;
        } else if (salary <= 7250) {
            return 315;
        } else if (salary <= 7750) {
            return 337.50;
        } else if (salary <= 8250) {
            return 360;
        } else if (salary <= 8750) {
            return 382.50;
        } else if (salary <= 9250) {
            return 405;
        } else if (salary <= 9750) {
            return 427.50;
        } else if (salary <= 10250) {
            return 450;
        } else if (salary <= 10750) {
            return 472.50;
        } else if (salary <= 11250) {
            return 495;
        } else if (salary <= 11750) {
            return 517.50;
        } else if (salary <= 12250) {
            return 540;
        } else if (salary <= 12750) {
            return 562.50;
        } else if (salary <= 13250) {
            return 585;
        } else if (salary <= 13750) {
            return 607.50;
        } else if (salary <= 14250) {
            return 630;
        } else if (salary <= 14750) {
            return 652.50;
        } else if (salary <= 15250) {
            return 675;
        } else if (salary <= 15750) {
            return 697.50;
        } else if (salary <= 16250) {
            return 720;
        } else if (salary <= 16750) {
            return 742.50;
        } else if (salary <= 17250) {
            return 765;
        } else if (salary <= 17750) {
            return 787.50;
        } else if (salary <= 18250) {
            return 810;
        } else if (salary <= 18750) {
            return 832.50;
        } else if (salary <= 19250) {
            return 855;
        } else if (salary <= 19750) {
            return 877.50;
        } else if (salary <= 20250) {
            return 900;
        } else if (salary <= 20750) {
            return 922.50;
        } else if (salary <= 21250) {
            return 945;
        } else if (salary <= 21750) {
            return 967.50;
        } else if (salary <= 22250) {
            return 990;
        } else if (salary <= 22750) {
            return 1012.50;
        } else if (salary <= 23250) {
            return 1035;
        } else if (salary <= 23750) {
            return 1057.50;
        } else if (salary <= 24250) {
            return 1080;
        } else if (salary <= 24750) {
            return 1102.50;
        } else {
            return 1125; // Maximum contribution, ensures contribution does not exceed government cap.
        }
    }

    //==========PHILHEALTH CONDITIONS==========
    // Computes PhilHealth contribution based on salary.
    // Uses percentage with minimum and maximum limits.
    public static double computePhilHealth(double salary) {

        double premium; // Variable to store PhilHealth premium

        // If salary is 10,000 or below, apply minimum contribution to ensure baseline coverage.
        if (salary <= 10000) {
            premium = 300;
        } // If salary is between 10,000.01 and 59,999.99, use percentage-based computation for fairness.
        else if (salary < 60000) {
            premium = salary * 0.03;
        } // If salary is 60,000 and above, apply maximum cap to limit contributions.
        else {
            premium = 1800;
        }

        // Employee pays only 50%, since the employer shoulders the other half.
        return premium / 2;
    }

    // ==========PAGIBIG CONDITIONS==========
    // Computes Pag-IBIG contribution based on salary percentage.
    // Includes a cap to limit maximum deduction.
    public static double computePagibig(double salary) {

        double rate; // Contribution rate
        double contribution; // Computed contribution

        // If salary is 1500 or below, apply lower rate to reduce burden on low-income earners.
        if (salary <= 1500) {
            rate = 0.01; // 1% contribution
        } else {
            rate = 0.02; // 2% contribution
        }

        // Compute contribution based on rate
        contribution = salary * rate;

        // Maximum contribution limit is 100, ensures deduction does not grow indefinitely.
        if (contribution > 100) {
            contribution = 100;
        }
        // Return final Pag-IBIG deduction
        return contribution;
    }

    //==========WITHHOLDING TAX CONDITIONS==========
    // Computes income tax based on progressive tax brackets.
    // Higher income results in higher tax rates.
    public static double computeTax(double taxable) {

        // No tax if taxable income is below threshold, protects low-income earners from taxation.
        if (taxable <= 20833) {
            return 0;
        }
        // 20% tax bracket, applies to middle-income range.
        if (taxable <= 33333) {
            return (taxable - 20833) * 0.20;
        }
        // 25% tax bracket with base tax, applies higher rate to higher income levels.
        return (taxable - 33333) * 0.25 + 2500;
    }
}
