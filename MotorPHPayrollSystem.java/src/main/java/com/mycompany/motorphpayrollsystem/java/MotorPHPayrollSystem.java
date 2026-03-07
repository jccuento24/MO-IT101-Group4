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
    private static String line;                   // Temporary variable for reading file lines


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
        System.out.print("\nEnter employee number: ");
        String idSearch = sc.nextLine(); // Get employee ID
        int index = findEmp(idSearch); // Find index in parallel arrays

        if (index != -1) { 
            // This displays the employee information such as employee ID, name and birthday.
            System.out.println("\n--- Employee Details ---");
            System.out.println("Employee Number: " + empID[index]);
            System.out.println("Employee Name:   " + empName[index]);
            System.out.println("Birthday:        " + empBday[index]);
        } else {
            // This prints if the employee ID is not found.
            System.out.println("Employee number does not exist.");
        }
    }

    // ==========================================
    // ROLE: PAYROLL STAFF
    // ==========================================
    // Staff can process payroll for one or all employees.
    public static void payrollRole(Scanner sc) {
        System.out.println("\n--- Payroll Staff Menu ---");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("Select choice: ");
        int choice = sc.nextInt(); // Read choices
        sc.nextLine(); // clear buffer
        
        // This checks if the user selected the first option from the main menu.
        if (choice == 1) {
            // Display the submenu options for payroll processing.
            System.out.println("\n[1] One employee");
            System.out.println("[2] All employees");
            System.out.print("Choice: ");
            int sub = sc.nextInt(); // This captures the user's submenu selection.
            sc.nextLine(); // This prevents skipping future inputs

            // 1. This processes payroll for a specific individual.
            if (sub == 1) {
                System.out.print("Enter employee number: ");
                String id = sc.nextLine(); // This gets the ID to support for.
                int index = findEmp(id); // This looks for the ID in the system and get its position
                if (index != -1) {     // If index is not -1, the employee was found.
                    processPayroll(index);  // Runs the payroll.
                } else {
                    // This prints if employee ID doesn't match any records.
                    System.out.println("Employee number does not exist.");
                }
            // 2. This process payroll for every employee in the system.
            } else if (sub == 2) { 
                for (int i = 0; i < empTotal; i++) {
                    processPayroll(i); // Runs payroll logic for the current employee in the loop
                }
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
            // h1 - Total hours during 1st half.
            // g1 - Gross Salary for the first cutoff.
            double h1 = getHours(empID[idx], m, 1, 15);
            double g1 = h1 * empRate[idx];  // Gross salary for the 1st cutoff.

            // 2nd Cutoff: Day 16-31
            // h2 - Total hours during 2nd half.
            // g2 - Gross Salary for the second cutoff.
            double h2 = getHours(empID[idx], m, 16, 31);
            double g2 = h2 * empRate[idx]; // Gross salary for the 2nd cutoff.

            // 1st Cutoff Output
            System.out.println("\nCutoff: " + m + "/01 to " + m + "/15");
            System.out.println("Total Hours: " + h1);
            System.out.println("Gross Salary: " + g1);
            System.out.println("Net Salary: " + g1);

            // Calculation of Government Deductions (Applied on 2nd Cutoff)
            double totalGross = g1 + g2;
            double sss = computeSSS(totalGross); // Computes SSS contribution.
            double phil = computePhilHealth(totalGross); // Computes Philhealth contribution.
            double pagibig = computePagibig(totalGross); // Computes Pagibig contribution.
            double taxable = totalGross - (sss + phil + pagibig); // Computes the taxable amount for computing the withholding tax.
            double tax = computeTax(taxable); // Computes withholding tax.

            double totalDed = sss + phil + pagibig + tax; // Computes total deductions.
            double net2 = g2 - totalDed; // Computes the net salary after all deductions.

            // 2nd Cutoff Output
            // This prints the final payroll summary of the month.
            System.out.println("\nCutoff: " + m + "/16 to " + m + "/end");
            System.out.println("Total Hours: " + h2);
            System.out.println("Gross Salary: " + g2);
            System.out.println("SSS: " + sss + " | PhilHealth: " + phil);
            System.out.println("Pag-IBIG: " + pagibig + " | Tax: " + tax);
            System.out.println("Total Deductions: " + totalDed);
            System.out.println("Net Salary: " + net2);
        }
    }

    // ==========================================
    // TIME CALCULATION LOGIC (8 AM - 5 PM)
    // ==========================================
    public static double getHours(String id, int month, int start, int end) {
        double totalH = 0;
        for (int i = 0; i < attTotal; i++) {
            if (attID[i].equals(id)) {
                // Split date MM/DD/YYYY
                String[] dParts = attDate[i].split("/");
                int m = Integer.parseInt(dParts[0]);
                int d = Integer.parseInt(dParts[1]);

                if (m == month && d >= start && d <= end) {
                    double tin = timeToDec(attIn[i]);
                    double tout = timeToDec(attOut[i]);

                    // Rule: Only 8:00 AM to 5:00 PM (17.0)
                    if (tin < 8.0) {
                        tin = 8.0;
                    }
                    if (tout > 17.0) {
                        tout = 17.0;
                    }

                    double daily = tout - tin;

                    // Subtract 1 hour for lunch if they worked a full shift
                    if (daily > 4.0) {
                        daily = daily - 1.0;
                    }

                    if (daily > 0) {
                        totalH = totalH + daily;
                    }
                }
            }
        }
        return totalH;
    }

    public static double timeToDec(String t) {
        String[] p = t.split(":");
        double hours = Double.parseDouble(p[0]);
        double mins = Double.parseDouble(p[1]);
        return hours + (mins / 60.0);
    }

    // ==========================================
    // FILE HANDLING (BUFFERED READER)
    // ==========================================
    public static void loadEmployees(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null && empTotal < MAX_EMP) {
                // Basic split: uses a manual loop to clean quotes instead of advanced regex
                String[] data = line.split(",");
                empID[empTotal] = data[0];
                empName[empTotal] = data[2] + " " + data[1];
                empBday[empTotal] = data[3];

                // Get the last column (Hourly Rate) and remove quotes/commas
                String rateRaw = data[data.length - 1].replace("\"", "").replace(",", "");
                empRate[empTotal] = Double.parseDouble(rateRaw);
                empTotal++;
            }
        } catch (IOException e) {
            System.out.println("Error reading employees.csv");
        }
    }

    public static void loadAttendance(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            while ((line = br.readLine()) != null && attTotal < MAX_ATT) {
                String[] data = line.split(",");
                attID[attTotal] = data[0];
                attDate[attTotal] = data[3];
                attIn[attTotal] = data[4];
                attOut[attTotal] = data[5];
                attTotal++;
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance.csv");
        }
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    public static int findEmp(String id) {
        for (int i = 0; i < empTotal; i++) {
            if (empID[i].equals(id)) {
                return i;
            }
        }
        return -1;
    }
    
    //==========SSS CONDITIONS==========
   public static double computeSSS(double salary) {

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
    else return 1125;
}
    
    //==========PHILHEALTH CONDITIONS==========
    public static double computePhilHealth(double salary) {

    double premium;

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

    double rate;
    double contribution;

    // Determine contribution rate
    if (salary <= 1500) {
        rate = 0.01; // 1%
    } else {
        rate = 0.02; // 2%
    }

    // Compute contribution
    contribution = salary * rate;

    // Maximum contribution cap
    if (contribution > 100) {
        contribution = 100;
    }

    return contribution;
}

    //==========WITHHOLDING TAX CONDITIONS==========
    public static double computeTax(double taxable) {
        if (taxable <= 20833) {
            return 0;
        }
        if (taxable <= 33333) {
            return (taxable - 20833) * 0.20;
        }
        return (taxable - 33333) * 0.25 + 2500;
    }
}

