import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.Scanner;
public class InputFunctions {
    /**
     * Ask the user to enter some text and return that text
     * if the user does not enter text, display an error and ask again
     * @param prompt the question or prompt to be displayed
     * @return valid text that the user input
     */
    public String askUserForText(String prompt){

        Scanner myKB = new Scanner(System.in);
        String userInput;

        do{
            System.out.println(prompt);
            System.out.println("Please enter TEXT ONLY");

            userInput = myKB.nextLine();

        }while (!userInput.matches("[a-zA-Z]+"));

        return userInput;
    }

    /**
     * Ask the user to enter an integer value
     * if it is not an integer - display an error and ask again
     * @param prompt the prompt or question to be displayed
     * @return a valid integer
     */
    public int askUserForInt(String prompt){

        Scanner myKB = new Scanner(System.in);
        int userInput=0;
        boolean valid = false; //assume user input is not valid

        do{
            System.out.println(prompt);
            System.out.println("Please enter INTEGER VALUES ONLY");
            try{
                userInput = myKB.nextInt();
                valid = true;

            }catch(Exception e){

                System.out.println("That was not an integer value. Please try again.");
                //don't need to set valid to false because it is already false
            }

        }while(!valid);

        return userInput;
    }

    /**
     * Ask the user to enter an integer with a given minimum allowed
     * if it is not valid then display an error and ask again
     * @param prompt the question or prompt to be displayed
     * @param minimum the minimum value allowed
     * @return a valid int greater than minimum
     */
    public int askUserforInt(String prompt, int minimum){


        Scanner myKB = new Scanner(System.in);
        int userInput=0;
        boolean valid = false; //assume user input is not valid

        do{
            System.out.println(prompt);
            System.out.println("Please enter an integer value bigger than " + minimum); //gives the user a clear guide
            try{
                userInput = myKB.nextInt();
                valid = true;

            }catch(Exception e){

                System.out.println("That was not an integer value. Please try again.");
                //don't need to set valid to false because it is already false
            }

        }while(!valid && (userInput<minimum));

        return userInput;
    }

    /**
     * Ask the user to enter an integer within a given range
     * if it is not valid then display an error and ask again
     * @param prompt the question or prompt to be displayed
     * @param minimum the minimum value allowed
     * @param maximum the maximum value allowed
     * @return a valid int within the allowed range
     */
    public int askUserForInt(String prompt, int minimum, int maximum) {
        Scanner myKB = new Scanner(System.in);
        int userInput = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            System.out.println(prompt);
            System.out.println("Please enter an integer value between " + minimum + " and " + maximum);

            if (myKB.hasNextInt()) {
                userInput = myKB.nextInt();
                if (userInput >= minimum && userInput <= maximum) {
                    isValidInput = true; // Valid input, exit the loop
                } else {
                    System.out.println("Input is not within the allowed range. Please try again.");
                }
            } else {
                System.out.println("That was not an integer value. Please try again.");
                myKB.next(); // Consume the invalid input
            }
        }

        return userInput;
    }










}
