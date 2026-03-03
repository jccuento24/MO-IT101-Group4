/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

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
 * * RULES APPLIED:
 * 1. No Object-Oriented Programming (OOP) - strictly methods and arrays.
 * 2. Time Logic: Only counts hours between 08:00 AM and 05:00 PM.
 * 3. Deductions: SSS, PhilHealth, Pag-IBIG, and Tax are computed after 
 * combining the 1st and 2nd cutoff gross salaries.
 * 4. File Handling: Uses BufferedReader for CSV processing.
 * ============================================================================
 */

//==============IMPORT STATEMENTS================
//This is needed for reading CSV files and interacting with the user.
import java.io.BufferedReader; // Reads text from files line by line.
import java.io.FileReader; // Opens a file
import java.io.IOException; // Handles error
import java.util.Scanner; //

public class MotorPHPayrollSystem {

    // Global Constants for Array Capacities
    static final int MAX_EMP = 100;
    static final int MAX_ATT = 8000;

    // Parallel Arrays to store Employee Data
    static String[] empID = new String[MAX_EMP];
    static String[] empName = new String[MAX_EMP];
    static String[] empBday = new String[MAX_EMP];
    static double[] empRate = new double[MAX_EMP];
    static int empTotal = 0;

    // Parallel Arrays for Attendance Logs
    static String[] attID = new String[MAX_ATT];
    static String[] attDate = new String[MAX_ATT];
    static String[] attIn = new String[MAX_ATT];
    static String[] attOut = new String[MAX_ATT];
    static int attTotal = 0;
    private static String line;

    public static void main(String[] args) {
        // Initialize data by reading CSV files
        loadEmployees("employees.csv");
        loadAttendance("attendance.csv");

        Scanner input = new Scanner(System.in);

        // 1. LOGIN SEQUENCE
        System.out.println("------------------------------------------");
        System.out.println("   Welcome to MotorPH Payroll System      ");
        System.out.println("------------------------------------------");
        System.out.print("Username: ");
        String user = input.nextLine();
        System.out.print("Password: ");
        String pass = input.nextLine();

        // Validate Credentials
        if (pass.equals("12345") && (user.equals("employee") || user.equals("payroll_staff"))) {
            if (user.equals("employee")) {
                employeeRole(input);
            } else {
                payrollRole(input);
            }
        } else {
            System.out.println("Incorrect username and/or password.");
        }

        input.close();
    }

    // ==========================================
    // ROLE: EMPLOYEE
    // ==========================================
    public static void employeeRole(Scanner sc) {
        System.out.print("\nEnter employee number: ");
        String idSearch = sc.nextLine();
        int index = findEmp(idSearch);

        if (index != -1) {
            System.out.println("\n--- Employee Details ---");
            System.out.println("Employee Number: " + empID[index]);
            System.out.println("Employee Name:   " + empName[index]);
            System.out.println("Birthday:        " + empBday[index]);
        } else {
            System.out.println("Employee number does not exist.");
        }
    }

    // ==========================================
    // ROLE: PAYROLL STAFF
    // ==========================================
    public static void payrollRole(Scanner sc) {
        System.out.println("\n--- Payroll Staff Menu ---");
        System.out.println("1. Process Payroll");
        System.out.println("2. Exit");
        System.out.print("Select choice: ");
        int choice = sc.nextInt();
        sc.nextLine(); // clear buffer

        if (choice == 1) {
            System.out.println("\n[1] One employee");
            System.out.println("[2] All employees");
            System.out.print("Choice: ");
            int sub = sc.nextInt();
            sc.nextLine();

            if (sub == 1) {
                System.out.print("Enter employee number: ");
                String id = sc.nextLine();
                int index = findEmp(id);
                if (index != -1) {
                    processPayroll(index);
                } else {
                    System.out.println("Employee number does not exist.");
                }
            } else if (sub == 2) {
                for (int i = 0; i < empTotal; i++) {
                    processPayroll(i);
                }
            }
        }
    }

    // ==========================================
    // PAYROLL PROCESSING ALGORITHM
    // ==========================================
    public static void processPayroll(int idx) {
        System.out.println("\n****************************************");
        System.out.println("Employee #: " + empID[idx]);
        System.out.println("Name: " + empName[idx]);
        System.out.println("Birthday: " + empBday[idx]);

        // Process months June (6) to December (12)
        for (int m = 6; m <= 12; m++) {
            // Cutoff 1: Day 1-15
            double h1 = getHours(empID[idx], m, 1, 15);
            double g1 = h1 * empRate[idx];

            // Cutoff 2: Day 16-31
            double h2 = getHours(empID[idx], m, 16, 31);
            double g2 = h2 * empRate[idx];

            // 1st Cutoff Output
            System.out.println("\nCutoff: " + m + "/01 to " + m + "/15");
            System.out.println("Total Hours: " + h1);
            System.out.println("Gross Salary: " + g1);
            System.out.println("Net Salary: " + g1);

            // Calculation of Government Deductions (Applied on 2nd Cutoff)
            double totalGross = g1 + g2;
            double sss = computeSSS(totalGross);
            double phil = totalGross * 0.03; // Simple 3% rule
            double pagibig = 100.0;
            double taxable = totalGross - (sss + phil + pagibig);
            double tax = computeTax(taxable);

            double totalDed = sss + phil + pagibig + tax;
            double net2 = g2 - totalDed;

            // 2nd Cutoff Output
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

    public static double computeSSS(double g) {
        if (g <= 3250) {
            return 135;
        }
        if (g >= 24750) {
            return 1125;
        }
        return g * 0.045;
    }

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

