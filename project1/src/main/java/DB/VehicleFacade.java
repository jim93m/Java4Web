package DB;

import Entities.Owner;
import Entities.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class VehicleFacade {

    public ArrayList<Vehicle> getListOfAllVehicles(Connection dbConnection) {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        Vehicle veh = null;
        String show = "select vehicle.*, owner.first_name, owner.last_name from vehicle " +
                "inner join owner on vehicle.owner_id=owner.id";
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(show)) {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int owner_id = resultSet.getInt("vehicle.owner_id");
                String owner_last_name = resultSet.getString("owner.last_name");
                String owner_first_name = resultSet.getString("owner.first_name");
                Owner owner = new Owner(owner_id,owner_last_name,owner_first_name);

                int id = resultSet.getInt("vehicle.id");
                String plateNumber = resultSet.getString("vehicle.plate");
                Date insurance_exp_date = resultSet.getDate("vehicle.insurance_exp_date");
                veh = new Vehicle(id, plateNumber, owner, insurance_exp_date);

                vehicles.add(veh);
            }
            return vehicles;

        } catch (SQLException e) {
            System.out.println("--- ! Data from table vehicle could not be fetched.");
            return null;
        }
    }

    public  Vehicle selectVehicleByPlate(Connection dbConnection, String plate) {
        String show = "select id,plate,owner_id,insurance_exp_date from vehicle where plate = ?";
        ResultSet resultSet = null;

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(show)) {
            preparedStatement.setString(1, plate);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                int owner_id = resultSet.getInt("owner_id");
                Owner owner = new OwnerFacade().getOwnerById(dbConnection, owner_id);

                int id = resultSet.getInt("id");
                String plateNumber = resultSet.getString("plate");
                Date insurance_exp_date = resultSet.getDate("insurance_exp_date");
                return new Vehicle(id, plateNumber, owner, insurance_exp_date);
            }

            return null;

        } catch (SQLException e) {
            System.out.println("--- ! Data from table vehicle could not be fetched.");
            return null;
        }

    }

    public ArrayList<Vehicle> getVehiclesByOwnerId(Connection dbConnection, int ownerId) {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        Vehicle veh = null;
        String show = "select id, plate, insurance_exp_date from vehicle where owner_id=?";
        ResultSet resultSet = null;

        Owner owner = new OwnerFacade().getOwnerById(dbConnection,ownerId);
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(show)) {
            preparedStatement.setInt(1, ownerId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String plateNumber = resultSet.getString("plate");
                Date insurance_exp_date = resultSet.getDate("insurance_exp_date");


                veh = new Vehicle(id, plateNumber, owner, insurance_exp_date);

                vehicles.add(veh);
            }
            return vehicles;
        } catch (SQLException e) {
            System.out.println("--- ! Data from table vehicle could not be fetched.");
            return null;
        }
    }
}