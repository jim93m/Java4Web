import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    static int a=5;

    public static void main(String[] args) throws DataBaseNotFound,SQLeX {

        System.out.println(" ------------------------------------");
        System.out.println("|       First Project - Team 3       |");
        System.out.println(" ------------------------------------");

        System.out.println();

        startingMenu();

    }

    private static void startingMenu() throws DataBaseNotFound, SQLeX {
        System.out.println("---- Select functionality to perform:");
        System.out.println("* 1) Vehicle Insurance Status");
        System.out.println("* 2) Forecoming Expiries");
        System.out.println("* 3) Calculate Fines");
        Scanner scanner = new Scanner(System.in);

        String choice = scanner.nextLine();
        System.out.println("_____________________________________");

        if(choice.equals("1")){
            firstChoiceSelected();
        }
        else if(choice.equals("2")){
            secondChoiceSelected();
        }
        else if (choice.equals("3")){
            thirdChoiceSelected();

        }
        else{
            System.out.println("You must select one of the four choices (1,2,3)");
            startingMenu();
        }

    }

    private static void firstChoiceSelected() throws SQLeX, DataBaseNotFound {

        Scanner scanner = new Scanner(System.in);

        System.out.println("---Please provide the vehicle's plate numbers:");
        String plateNumbers = scanner.nextLine();

        String regex="^[a-zA-Z]{3}-\\d{4}$";

        if(plateNumbers.matches(regex)){

            Jdbc jdbc=new Jdbc();
            Vehicle targetVehicle=jdbc.selectVehicleByPlate(plateNumbers);
            jdbc.closeDBConnection();

            if(isVehicleInsured(targetVehicle)){
                System.out.println("--- The insurance of the vehicle with plate number "+plateNumbers+" is expired");
            }
            else{
                System.out.println("--- The insurance of the vehicle with plate number "+plateNumbers+" is valid");
            }
        }
        else{
            System.out.println("---The given plate does not follow the correct format");
            firstChoiceSelected();
        }


    }


    private static void secondChoiceSelected(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Insert an expiring date in the following format dd/MM/yyyy:");
        String dateString = scanner.nextLine();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date=new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            System.out.println("---The expiring date that was given, does not follow the correct format");
            secondChoiceSelected();
        }

        Jdbc jdbc= null;
        try {
            jdbc = new Jdbc();
        } catch (DataBaseNotFound dataBaseNotFound) {
            dataBaseNotFound.printStackTrace();
        }
        ArrayList<Vehicle> vehicleList= null;
        try {
            vehicleList = jdbc.getlistOfAllVehicles();
        } catch (SQLeX throwables) {
            throwables.printStackTrace();
        }
        try {
            jdbc.closeDBConnection();
        } catch (SQLeX throwables) {
            throwables.printStackTrace();
        }


        for(int i=0;i<vehicleList.size();i++){
            if(vehicleList.get(i).getExpiration_date()!=null){
                if (vehicleList.get(i).getExpiration_date().after(date)) {
                    System.out.println("+++++++++++++++++++");
                    vehicleList.remove(i);
                }
            }
        }

        resultSorting(vehicleList);


        System.out.println("_____________________________________");
        System.out.println("---Enter Export Type:");
        System.out.println("* 1) File");
        System.out.println("* 2) Console");

        String choice = scanner.nextLine();


        if(choice.equals("1")){
            CsvFileCreator.createCSVfile(vehicleList);
        }
        else if(choice.equals("2")){
            System.out.println("--- The list of plate number that their insurances are about to expire until "+ date);
            System.out.println();
            System.out.println(" Vehicle's ID | Plate Number | Owner ID | Insurance Expiration Date");

            for(int i=0; i<vehicleList.size(); i++){
                System.out.print(vehicleList.get(i).getId()+"                ");
                System.out.print(vehicleList.get(i).getPlate()+"     ");
                System.out.print(vehicleList.get(i).getOwner_id()+"     ");
                System.out.print(vehicleList.get(i).getExpiration_date()+"     ");
                System.out.println();
            }

        }
        else{
            System.out.println("You must select one of the three choices (1,2,3)");
            secondChoiceSelected();
        }

    }

    private static void resultSorting(ArrayList<Vehicle> aboutToExpireList){

        Scanner scanner = new Scanner(System.in);
        System.out.println("---Would you like to have the results sorted?");
        System.out.println("--- y : yes   |   n: no");
        String toBeSorted = scanner.nextLine();

        if(toBeSorted.equals("y")){
            // Sort the result list
            Collections.sort(aboutToExpireList);
        }
        else if (toBeSorted.equals("n")){
            // No sorting algorithm will be applied
        }
        else{
            System.out.println("Please choose one of the two valid options: y / n");
            resultSorting(aboutToExpireList);
        }

    }


    private static void thirdChoiceSelected() throws DataBaseNotFound {

        System.out.println("---Please provide the fine cost of an uninsured vehicle (cents):");
        Scanner scanner = new Scanner(System.in);
        int fine = Integer.valueOf(scanner.nextLine());

        System.out.println("---Please provide the owner's id in order to calculate the total fine cost:");
        int ownerId = Integer.valueOf(scanner.nextLine());

        Jdbc jdbc = new Jdbc();

        ArrayList<Vehicle> vehicles = null;
        try {
            vehicles = jdbc.getVehiclesByOwnerId(ownerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int sum = 0;
        for (Vehicle v:vehicles) {

            if(!isVehicleInsured(v))
                sum+=fine;
        }

        System.out.println("The total fine cost that owner with id " + String.valueOf(ownerId) + " is: " + String.valueOf(sum));


        try {
            jdbc.closeDBConnection();
        } catch (SQLeX throwables) {
            throwables.printStackTrace();
        }


        System.out.println();
        String motorbikeFine = scanner.nextLine();



    }

    private static boolean isVehicleInsured(Vehicle v, int daysOffset){

        if(v.getExpiration_date()==null)
            return false;
        else{
            Calendar dateForComparison = Calendar.getInstance();
            if(daysOffset!=0)
                dateForComparison.add(Calendar.DATE, daysOffset);

            Calendar insuranceExpDate = Calendar.getInstance();
            insuranceExpDate.setTime(v.getExpiration_date());

            return v.getExpiration_date()!=null && !Util.isAfterDate(insuranceExpDate,dateForComparison);
        }
    }

    private static boolean isVehicleInsured(Vehicle v){
        return isVehicleInsured(v, 0);
    }

}
