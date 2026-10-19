package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

import static java.lang.Class.forName;

public class HospitalMS {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Masih22!";

    static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patients patients = new Patients(connection, scanner);
            Doctors doctors = new Doctors(connection);

            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter an option: ");

                int option = scanner.nextInt();

                switch (option){

                    case 1:
                        patients.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patients.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patients, doctors, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Get well soon! Thank you for using Hospital Management System!");
                        return;
                    default:
                        System.out.println("Enter a valid option!");
                        break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }





    }

    public static void bookAppointment(Patients patients, Doctors doctors, Connection connection, Scanner scanner){
        System.out.print("Enter Patient ID: ");
        int patientID = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorID = scanner.nextInt();
        System.out.print("Enter appointment date (YYY-MM-DD): ");
        String date = scanner.next();

        if(patients.getPatientById(patientID) && doctors.getDoctorById(doctorID)){
            if(checkDoctorAvailability(doctorID, date, connection)){
                String appointmentQuery = "insert into appointments (patient_id, doctor_id, appointment_date) values(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setString(3, date);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment booked successfully!");
                    }else{
                        System.out.println("Failed to book appointment, Please try again later!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();

                }

            } else {
                System.out.println("Failed to book appointment, Please try again with valid Date, Doctor ID and Patient ID!");
            }
        }




    }

    public static boolean checkDoctorAvailability(int doctorid, String date, Connection connection) {
        String query = "select count(*) from appointments where doctor_id = ? and appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorid);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

}
