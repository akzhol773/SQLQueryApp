import java.sql.*;

// Main class for running football league database queries
public class QueryApp {
    // Database connection details
    private static final String url = "jdbc:mysql://localhost:3306/footballleague";
    private static final String user = "root";
    private static final String password = "pass1234!";

    public static void main(String[] args) {

        // Initialize input utility for handling user inputs
        InputFunctions myInput = new InputFunctions();

        try {
            // Load the JDBC driver for establishing database connection
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(url, user, password);

            // Main menu option selection
            int option;
            do{
                // Display the main menu options for the user
                System.out.println("================================================================");
                System.out.println("|                          QUERY MENU                           |");
                System.out.println("================================================================");
                System.out.println("| Select option:                                                |");
                System.out.println("| 1. Find the field position of a particular football player    |");
                System.out.println("| 2. List all players with a particular name                    |");
                System.out.println("| 3. Search for matches on a specific date                      |");
                System.out.println("| 4. Leaderboard                                                |");
                System.out.println("| 5. List of the most common injuries during the league games   |");
                System.out.println("| 6. Number of players organized by their skill level             |");
                System.out.println("| 7. Exit                                                       |");
                System.out.println("================================================================");
                option = myInput.askUserForInt("Please enter number corresponding to your choice", 1, 7);


                // Process user's selection from the main menu
                switch (option){

                    case 1:
                        // Find and display the field position of a specific football player
                        String name = myInput.askUserForText("Please enter a player name: ");
                        findPosition(connection, name);
                        break;
                    case 2:
                        // List all players with a specific name or surname
                        String playerName = myInput.askUserForText("Please enter a player name or surname: ");
                        createListOfPlayers(connection, playerName);
                        break;
                    case 3:
                        // Search and display matches on a specific date
                        System.out.println("================================================================");
                        System.out.println("| Select match date:                                            |");
                        System.out.println("| 1. 17th January 2023                                          |");
                        System.out.println("| 2. 23rd February 2023                                         |");
                        System.out.println("| 3. 16th March 2023                                            |");
                        System.out.println("| 4. 15th April 2023                                            |");
                        System.out.println("| 5. 27th May 2023                                              |");
                        System.out.println("================================================================");
                        int dateChoice = myInput.askUserForInt("Please choose date: ", 1, 5);

                        String dateString = null;
                        switch(dateChoice) {
                            case 1: dateString = "'2023-01-17';"; break;
                            case 2: dateString = "'2023-02-23';"; break;
                            case 3: dateString = "'2023-03-16';"; break;
                            case 4: dateString = "'2023-04-15';"; break;
                            case 5: dateString = "'2023-05-27';"; break;
                        }
                        if (!dateString.isEmpty()) {
                            findMatch(connection, dateString);
                        } else {
                            System.out.println("Invalid date choice.");
                        }
                        break;

                    case 4:
                        // Display the leaderboard
                        creatLeaderboard(connection);
                        break;
                    case 5:
                        // List the most common injuries during the league games
                        findCommonInjuries(connection);
                        break;
                    case 6:
                        // Sort and display number of players organized by their skill level
                        System.out.println("================================================================");
                        System.out.println("| Select list order:                                            |");
                        System.out.println("| 1. Sort the team names in ASCENDING order                     |");
                        System.out.println("| 2. Sort the team names in DESCENDING order                    |");
                        System.out.println("| 3. Any order                                                  |");
                        System.out.println("================================================================");
                        int userchoise = myInput.askUserForInt("Please choose list order: ", 1, 3);

                        String orderChoice = null;
                        switch(userchoise) {
                            case 1: orderChoice = "ORDER BY Team.TeamName ASC;"; break;
                            case 2: orderChoice = "ORDER BY Team.TeamName DESC;"; break;
                            case 3: orderChoice = ";"; break;
                        }
                        if (!orderChoice.isEmpty()) {
                            sortBySkill(connection, orderChoice);
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        break;
                    default:
                        // Option for exiting the application
                }
            }while (option != 7);

            // Thanking the user upon exiting the application
            System.out.println("Thank you for choosing our service");


        } catch (ClassNotFoundException e) {
            // Handle the case where the JDBC driver is not found
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            // Handle SQL connection failures
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }


    /**
     * Searches for and displays the position of a football player based on the provided name.
     * @param connection The established database connection.
     * @param name The name of the player to search for.
     */
    private static void findPosition(Connection connection, String name) {
        // SQL query to retrieve player details based on the provided name.
        // It joins the Player and Team tables to get both player and team information.
        String sql = "SELECT p.FirstName, p.LastName, p.Position, t.TeamName FROM Player p JOIN Team t ON p.TeamID = t.TeamID WHERE p.FirstName LIKE ? OR p.LastName LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set the parameters for the prepared statement to handle both first and last names.
            pstmt.setString(1, name + "%");
            pstmt.setString(2, name + "%");

            // Execute the query and store the result in a ResultSet.
            ResultSet rs = pstmt.executeQuery();

            // Check if the ResultSet contains any rows (players found).
            if (!rs.next()) {
                System.out.println("No players found for this name.");
                return;
            } else {
                // Initialize an index for numbering the output.
                int index = 1;

                // Loop through the ResultSet to process and print each player's details.
                do {
                    // Construct and print the player's details including their position and team.
                    System.out.println(index + ". Player Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                    System.out.println("Position: " + rs.getString("Position"));
                    System.out.println("Team: " + rs.getString("TeamName"));


                    index++; // Increment the index for the next player.
                } while (rs.next());// Continue until all players have been processed.
            }
        } catch (SQLException e) {
            // Catch and handle SQL exceptions.
            System.out.println("Query error: " + e.getMessage());
        }
    }


    /**
     * Generates a list of players from the database whose first or last name matches the provided input.
     * @param connection The established database connection.
     * @param name The name to search for in the player records.
     */
    private static void createListOfPlayers(Connection connection, String name) {
        // SQL query to retrieve player details where the first or last name matches the search criteria.
        String sql = "SELECT FirstName, LastName FROM Player WHERE FirstName LIKE ? OR LastName LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Setting the parameters for the prepared statement to search for players
            // whose names start with the provided input.
            pstmt.setString(1, name + "%");
            pstmt.setString(2, name + "%");

            // Execute the query and store the results in a ResultSet.
            ResultSet rs = pstmt.executeQuery();

            // Check if the ResultSet is empty (no players found).
            if (!rs.next()) {
                System.out.println("No players found for this name.");
                return;
            }

            // Print the header for the list of players.
            System.out.println("List of players:");

            // Initialize an index for numbering the output.
            int index = 1;

            // Loop through the ResultSet to process and print each player's name.
            do {
                // Construct and print the player's full name.
                System.out.println(index + ". " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                index++; // Increment the index for the next player.
            } while (rs.next()); // Continue until all matching players have been processed.
        } catch (SQLException e) {
            // Catch and handle SQL exceptions.
            System.out.println("Query error: " + e.getMessage());
        }
    }


    /**
     * Searches for and displays football matches on a specific date from the database.
     * @param connection The established database connection.
     * @param dateInput The date for which to find matches (expected in 'YYYY-MM-DD' format).
     */
    private static void findMatch(Connection connection, String dateInput) {
        // SQL query to retrieve matches on a specific date, joining the Game table with two instances
        // of the Team table to get the names of the host and guest teams.
        String sql = "SELECT Game.GameId, HT.TeamName as HostTeam, GT.TeamName as GuestTeam, " +
                "Game.HostScore, Game.Date, Game.GuestScore " +
                "FROM Game " +
                "JOIN Team AS HT ON Game.HostTeamID = HT.TeamID " +
                "JOIN Team AS GT ON Game.GuestTeamID = GT.TeamID " +
                "WHERE Game.Date = " + dateInput;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Execute the query and store the result in a ResultSet.
            ResultSet rs = pstmt.executeQuery();

            // Check if any matches were found for the given date.
            if (!rs.next()) {
                System.out.println("No games found for this date.");
                return;
            }

            // Print the header, indicating the date of the matches.
                System.out.println("LIST OF GAMES ON " + rs.getString("Date") + ": ");
            int index = 1;

            // Loop through the ResultSet to process and print each match's details.
            do {
                // Print the details of each game, including teams and scores.
                System.out.println(index + ". " + rs.getString("HostTeam") + " VS " + rs.getString("GuestTeam") +
                        ", Score: " + rs.getInt("HostScore") + "-" + rs.getInt("GuestScore"));
                index++; // Increment the index for the next game.
            } while (rs.next());// Continue until all data have been processed.
        } catch (SQLException e) {
            // Catch and handle any SQL exceptions.
            System.out.println("Query error: " + e.getMessage());
        }
    }


    /**
     * Creates and displays a leaderboard based on team performance in football matches.
     * @param connection The established database connection.
     */
    private static void creatLeaderboard(Connection connection){
        // SQL query to calculate points, goals for, goals against, and goal difference for each team
        // The query uses conditional aggregation to calculate these metrics
        String sql = "SELECT " +
                "    Team.TeamName, " +
                "    SUM(CASE WHEN Game.HostTeamID = Team.TeamID AND Game.HostScore > Game.GuestScore THEN 3 " +
                "             WHEN Game.GuestTeamID = Team.TeamID AND Game.GuestScore > Game.HostScore THEN 3 " +
                "             WHEN Game.HostScore = Game.GuestScore THEN 1 " +
                "             ELSE 0 " +
                "        END) AS Points, " +
                "    SUM(CASE WHEN Game.HostTeamID = Team.TeamID THEN Game.HostScore " +
                "             WHEN Game.GuestTeamID = Team.TeamID THEN Game.GuestScore " +
                "             ELSE 0 " +
                "        END) AS GoalsFor, " +
                "    SUM(CASE WHEN Game.HostTeamID = Team.TeamID THEN Game.GuestScore " +
                "             WHEN Game.GuestTeamID = Team.TeamID THEN Game.HostScore " +
                "             ELSE 0 " +
                "        END) AS GoalsAgainst, " +
                "    (SUM(CASE WHEN Game.HostTeamID = Team.TeamID THEN Game.HostScore " +
                "              WHEN Game.GuestTeamID = Team.TeamID THEN Game.GuestScore " +
                "              ELSE 0 " +
                "          END) - " +
                "     SUM(CASE WHEN Game.HostTeamID = Team.TeamID THEN Game.GuestScore " +
                "              WHEN Game.GuestTeamID = Team.TeamID THEN Game.HostScore " +
                "              ELSE 0 " +
                "          END)) AS GoalDifference " +
                "FROM " +
                "    footballleague.Team Team " +
                "LEFT JOIN " +
                "    footballleague.Game Game ON Team.TeamID = Game.HostTeamID OR Team.TeamID = Game.GuestTeamID " +
                "GROUP BY " +
                "    Team.TeamID " +
                "ORDER BY " +
                "    Points DESC, " +
                "    GoalDifference DESC;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            // Check if the ResultSet contains any data
            if (!rs.next()) {
                System.out.println("Data not found.");
                return;
            }

            // Formatting and printing the leaderboard header
            int index = 1;
            int maxTeamNameLength = 25;
            int pointsColumnLength = 6;
            int otherColumnsLength = 10;
            int totalLength = maxTeamNameLength + pointsColumnLength + otherColumnsLength * 3 + 9; // Calculate total length for formatting
            String separatorLine = String.format("%" + totalLength + "s", "").replace(' ', '_');

            System.out.println("LEADERBOARD");
            System.out.println(String.format("%-" + maxTeamNameLength + "s | %5s | %9s | %9s | %9s", "Teams", "Points", "Goals For", "Goals Agnst", "Goal Diff"));
            System.out.println(separatorLine);

            // Loop through the ResultSet and print each team's details
            do {
                String teamName = rs.getString("TeamName");
                int points = rs.getInt("Points");
                int goalsFor = rs.getInt("GoalsFor");
                int goalsAgainst = rs.getInt("GoalsAgainst");
                int goalDifference = rs.getInt("GoalDifference");
                System.out.println(String.format("%-" + maxTeamNameLength + "s | %5d | %9d | %9d | %9d", index + ". " + teamName, points, goalsFor, goalsAgainst, goalDifference));
                index++;
            } while (rs.next());// Continue until all data have been processed.
            System.out.println(separatorLine); // Print the closing separator line
        } catch (SQLException e) {
            // Handle any SQL exceptions that occur during query execution
            System.out.println("Query error: " + e.getMessage());
        }
    }



    /**
     * Retrieves and displays a list of common injuries and their occurrences from the InjuryRecord table.
     * @param connection The established database connection.
     */
    private static void findCommonInjuries(Connection connection) {
        // SQL query to count the number of occurrences for each type of injury
        // and sort them in descending order of occurrence.
        String sql = "SELECT InjuryType, COUNT(*) AS NumberOfOccurrences " +
                "FROM InjuryRecord " +
                "GROUP BY InjuryType " +
                "ORDER BY NumberOfOccurrences DESC;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Execute the query and store the result in a ResultSet.
            ResultSet rs = pstmt.executeQuery();

            // Check if the ResultSet contains any data (i.e., if any injuries were found).
            if (!rs.next()) {
                System.out.println("No injuries found.");
                return;
            }

            // Print the header for the injuries table.
            int index = 1;
            int maxInjuryTypeLength = 25;
            System.out.println("INJURY TABLE");
            System.out.println(String.format("%-" + maxInjuryTypeLength + "s | %s", "Injury Type", "Occurrence"));

            // Loop through the ResultSet to process and print each injury type and its occurrence count.
            do {
                String injuryType = rs.getString("InjuryType");
                int numberOfOccurrences = rs.getInt("NumberOfOccurrences");
                System.out.println(String.format("%-" + maxInjuryTypeLength + "s | %d", index + ". " + injuryType, numberOfOccurrences));
                index++; // Increment the index for the next entry.
            } while (rs.next());// Continue until all data have been processed.
        } catch (SQLException e) {
            // Handle any SQL exceptions that occur during query execution.
            System.out.println("Query error: " + e.getMessage());
        }
    }


    /**
     * Retrieves and displays the number of players sorted by skill levels for each team.
     * @param connection The established database connection.
     * @param userChoice A string to define the order of sorting (ASC, DESC, or any).
     */
    private static void sortBySkill(Connection connection, String userChoice) {
        // SQL query to count players by skill level (Low, Medium, High) for each team.
        // The userChoice string at the end of the query determines the sorting order.
        String sql = "SELECT " +
                "    Team.TeamName, " +
                "    COUNT(CASE WHEN Player.SkillLevel = 'Low' THEN 1 END) AS LowSkillCount, " +
                "    COUNT(CASE WHEN Player.SkillLevel = 'Medium' THEN 1 END) AS MediumSkillCount, " +
                "    COUNT(CASE WHEN Player.SkillLevel = 'High' THEN 1 END) AS HighSkillCount " +
                "FROM Player " +
                "JOIN Team ON Player.TeamID = Team.TeamID " +
                "GROUP BY Team.TeamName " + userChoice;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Execute the query and store the results in a ResultSet.
            ResultSet rs = pstmt.executeQuery();

            // Check if the ResultSet contains any data.
            if (!rs.next()) {
                System.out.println("Data not found.");
                return;
            }

            // Print the header for the skill level table.
            int index = 1;
            int maxTeamNameLength = 25;
            System.out.println("NUMBER OF PLAYERS SORTED BY SKILL LEVELS");
            System.out.println(String.format("%-" + maxTeamNameLength + "s | %5s | %9s | %9s", "Teams", "Low", "Medium", "High"));

            // Loop through the ResultSet to process and print each team's skill level counts.
            do {
                String teamName = rs.getString("TeamName");
                int lowSkillCount = rs.getInt("LowSkillCount");
                int mediumSkillCount = rs.getInt("MediumSkillCount");
                int highSkillCount = rs.getInt("HighSkillCount");
                System.out.println(String.format("%-" + maxTeamNameLength + "s | %5d | %9d | %9d", index + ". " + teamName, lowSkillCount, mediumSkillCount, highSkillCount));
                index++; // Increment the index for the next team.
            } while (rs.next());// Continue until all data have been processed.

        } catch (SQLException e) {
            // Handle any SQL exceptions that occur during query execution.
            System.out.println("Query error: " + e.getMessage());
        }
    }

}