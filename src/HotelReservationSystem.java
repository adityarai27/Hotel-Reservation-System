import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    //jdbc:mysql://localhost:3306/?user=root
    private static final String username = "root";
    private static final String password = "Aditya@2701";

    public static void main(String[] args)throws ClassNotFoundException, SQLException{

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM.");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(connection, sc);
                        break;

                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try Again.");
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner sc){
        try {
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter Contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)VALUES('" + guestName + "' , " + roomNumber + ", '" + contactNumber + "')";

            try(Statement statement = connection.createStatement()){
                int  affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation Successfully.");
                }else {
                    System.out.println("Reservation Failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection)throws SQLException{
        String sql = "SELECT reservation_Id, guest_name, room_number, contact_number, reservation_date FROM reservations;";

        try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("CURRENT RESERVATIONS: ");
            System.out.println("+----------------+----------------+----------------+----------------------+-----------------------+");
            System.out.println("| Reservation_id |   Guest_Name   |  Room_Number   |  Contact_No.         |   Reservation_Date    |");
            System.out.println("+----------------+----------------+----------------+----------------------+-----------------------+");


            while (resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+----------------+----------------+----------------------+-----------------------+");
        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc){
        try{
            System.out.print("Enter Reservation Id: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter Guest Name :");
            String guestName = sc.next();
            String sql =  "SELECT room_number FROM reservations " +
                    " WHERE reservation_id = " + reservationId + " " +
                    "AND guest_name= '" + guestName + "'";

            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber  = resultSet.getInt("room_number");
                    System.out.println("Room number " + roomNumber + " for Reservation Id " + reservationId +
                            " and Guest " + guestName + "." );
                }else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner sc){
        try{
            System.out.print("Enter Reservation Id to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the your " + reservationId + " given Id.");
                return;
            }

            System.out.println("Enter new Guest Name: ");
            String newGuestName = sc.nextLine();
            System.out.println("Enter the Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter the Contact Number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows>0){
                    System.out.println("Reservation Updated Successfully!");
                }else {
                    System.out.println("Reservation Update Failed");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc){
        try {
            System.out.println("Enter Reservation Id to delete: ");
            int reservationId = sc.nextInt();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given Id.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println("Reservation Deleted Successfully.");
                }else {
                    System.out.println("Reservation Deletion Failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static  boolean reservationExists(Connection connection, int reservationId){
            try {
                String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

                try(Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)){

                    return resultSet.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("THANKING YOU, SEE YOU SOON.");
    }
}
