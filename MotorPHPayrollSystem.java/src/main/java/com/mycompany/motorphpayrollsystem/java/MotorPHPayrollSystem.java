/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

// ================= PACKAGE DECLARATION =================
// This tells Java which folder (package) this class belongs to.
// NetBeans automatically creates this when you create a new project.
package com.mycompany.motorphpayrollsystem.java;

/*
 * ==============================================================
 *  MotorPH Basic Payroll System
 *  Course: Computer Programming 1
 *
 *  Description:
 *  This program simulates a basic payroll system for MotorPH.
 *  It reads employee information and attendance records from CSV files,
 *  computes total hours worked from June to December,
 *  calculates gross salary, applies government deductions,
 *  and displays the final net salary.
 *
 *  IMPORTANT RULES FOLLOWED:
 *  - No OOP concepts used (only methods and arrays).
 *  - Only one Java file is used.
 *  - No rounding of values.
 *  - All data is read from CSV files using BufferedReader.
 *  - Working hours counted only between 8:00 AM to 5:00 PM.
 *  - Deductions computed after combining 1st and 2nd cutoffs.
 * ==============================================================
 */
import java.io.BufferedReader;  // Used to read CSV files
import java.io.FileReader;      // Used to open file
import java.io.IOException;     // Used to handle file errors
import java.util.Scanner;       // Used to get user input

public class MotorPHPayrollSystem {

    // ===================== CONSTANTS =====================
    // Maximum number of employees that can be stored
    static final int MAX_EMP = 100;

    // Maximum attendance records that can be stored
    static final int MAX_ATT = 2000;

    // ===================== EMPLOYEE ARRAYS =====================
    // Array to store employee numbers
    static String[] empNumber = new String[MAX_EMP];

    // Array to store full employee names
    static String[] empName = new String[MAX_EMP];

    // Array to store birthdays
    static String[] birthday = new String[MAX_EMP];

    // Array to store hourly rates
    static double[] hourlyRate = new double[MAX_EMP];

    // Counter to track how many employees are loaded
    static int empCount = 0;

    // ===================== ATTENDANCE ARRAYS =====================
    // Array to store employee numbers in attendance
    static String[] attEmpNumber = new String[MAX_ATT];

    // Array to store attendance dates
    static String[] attDate = new String[MAX_ATT];

    // Array to store login time
    static String[] timeIn = new String[MAX_ATT];

    // Array to store logout time
    static String[] timeOut = new String[MAX_ATT];

    // Counter to track number of attendance records
    static int attCount = 0;

    // ===================== MAIN METHOD =====================
    public static void main(String[] args) {

        // Scanner object used to read user input
        Scanner scanner = new Scanner(System.in);

        // Load employee data from CSV file
        loadEmployees();

        // Load attendance data from CSV file
        loadAttendance();

        // Ask user for login credentials
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Validate username and password
        if (!((username.equals("employee") || username.equals("payroll_staff"))
                && password.equals("12345"))) {

            // If invalid credentials, terminate program
            System.out.println("Incorrect username and/or password.");
            return;
        }

        // If username is employee, show employee menu
        if (username.equals("employee")) {
            employeeMenu(scanner);
        } // If username is payroll staff, show payroll menu
        else {
            payrollMenu(scanner);
        }

        // Close scanner to prevent memory leak
        scanner.close();
    }

    // ===================== LOAD EMPLOYEE DATA =====================
    public static void loadEmployees() {

        try {
            // Open employees.csv file
            BufferedReader br = new BufferedReader(new FileReader("employees.csv"));

            String line;

            // Skip header row
            br.readLine();

            // Read file line by line
            while ((line = br.readLine()) != null) {

                // Split CSV columns by comma
                String[] data = line.split(",");

                // Store employee number
                empNumber[empCount] = data[1];

                // Combine first and last name
                empName[empCount] = data[2] + " " + data[3];

                // Store birthday
                birthday[empCount] = data[4];

                // Store hourly rate (converted to double)
                hourlyRate[empCount] = Double.parseDouble(data[19]);

                // Increase employee counter
                empCount++;
            }

            // Close file after reading
            br.close();

        } catch (IOException e) {

            // Display error if file cannot be read
            System.out.println("Error loading employees file.");
        }
    }

    // ===================== LOAD ATTENDANCE DATA =====================
    public static void loadAttendance() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("attendance.csv"));
            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                attEmpNumber[attCount] = data[1];
                attDate[attCount] = data[4];
                timeIn[attCount] = data[5];
                timeOut[attCount] = data[6];

                attCount++;
            }

            br.close();

        } catch (IOException e) {

            System.out.println("Error loading attendance file.");
        }
    }

    // ===================== EMPLOYEE ROLE =====================
    public static void employeeMenu(Scanner scanner) {

        System.out.print("Enter employee number: ");
        String number = scanner.nextLine();

        int index = findEmployee(number);

        if (index == -1) {
            System.out.println("Employee number does not exist.");
            return;
        }

        System.out.println("Employee Number: " + empNumber[index]);
        System.out.println("Employee Name: " + empName[index]);
        System.out.println("Birthday: " + birthday[index]);
    }

    // ===================== PAYROLL ROLE =====================
    public static void payrollMenu(Scanner scanner) {

        System.out.println("1. Process One Employee");
        System.out.println("2. Process All Employees");
        System.out.println("3. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {

            System.out.print("Enter employee number: ");
            String number = scanner.nextLine();
            int index = findEmployee(number);

            if (index == -1) {
                System.out.println("Employee number does not exist.");
            } else {
                processPayroll(index);
            }
        } else if (choice == 2) {

            for (int i = 0; i < empCount; i++) {
                processPayroll(i);
            }
        }
    }

    // ===================== PAYROLL COMPUTATION =====================
    public static void processPayroll(int index) {

        double cutoff1 = computeHours(empNumber[index], 1, 15);
        double cutoff2 = computeHours(empNumber[index], 16, 31);

        double gross1 = cutoff1 * hourlyRate[index];
        double gross2 = cutoff2 * hourlyRate[index];

        double totalGross = gross1 + gross2;

        double sss = totalGross * 0.045;
        double philhealth = totalGross * 0.02;
        double pagibig = 100;
        double tax = totalGross * 0.10;

        double totalDeductions = sss + philhealth + pagibig + tax;
        double netSalary = totalGross - totalDeductions;

        System.out.println("----------------------------------");
        System.out.println("Employee #: " + empNumber[index]);
        System.out.println("Name: " + empName[index]);
        System.out.println("Total Gross Salary: " + totalGross);
        System.out.println("Total Deductions: " + totalDeductions);
        System.out.println("Net Salary: " + netSalary);
    }

    // ===================== HOURS COMPUTATION =====================
    public static double computeHours(String number, int startDay, int endDay) {

        double total = 0;

        for (int i = 0; i < attCount; i++) {

            if (attEmpNumber[i].equals(number)) {

                int day = Integer.parseInt(attDate[i].split("-")[2]);

                if (day >= startDay && day <= endDay) {

                    double in = convertToDecimal(timeIn[i]);
                    double out = convertToDecimal(timeOut[i]);

                    if (in < 8.0) {
                        in = 8.0;
                    }
                    if (out > 17.0) {
                        out = 17.0;
                    }

                    double worked = out - in - 1.0;

                    if (worked < 0) {
                        worked = 0;
                    }

                    total += worked;
                }
            }
        }

        return total;
    }

    // ===================== TIME CONVERSION =====================
    public static double convertToDecimal(String time) {

        String[] parts = time.split(":");

        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        return hour + (minute / 60.0);
    }

    // ===================== FIND EMPLOYEE =====================
    public static int findEmployee(String number) {

        for (int i = 0; i < empCount; i++) {

            if (empNumber[i].equals(number)) {
                return i;
            }
        }

        return -1;
    }
}
