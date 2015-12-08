package com.mzinck.fwa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

/**
 * Program that teaches and helps kids with arithmetic
 * 
 * @author Mitchell Zinck <mitch@mzinck.com> <mitchellzinck@yahoo.com>
 */

public class FWA {
    
    private static State        state     = State.START;
    private static Properties   props     = new Properties();
    private static String       name      = "";
    private static Scanner      scan      = new Scanner(System.in);
    private static int          timeCount = 0,
                                hardTime = Integer.MAX_VALUE / 1001;
    private static File output;
    
    /**
     * Start of our program. Loads the properties files and reads their highscores.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        output = new File(System.getProperty("user.home") + "\\arithmetic.properties");
        try {
            props.load(new FileInputStream(System.getProperty("user.home") + "\\arithmetic.properties"));
        } catch (FileNotFoundException e){
            try {
                output.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
           
        if(props.getProperty("timeCount") != null) {
            timeCount = Integer.parseInt(props.getProperty("timeCount"));
            hardTime = Integer.parseInt(props.getProperty("hardTime"));
            name = props.getProperty("name");
        } else {
            log("What is your name?");
            name = scan.nextLine();
            setProps();
        }
        setState();        
    }
    
    /**
     * Menu picker for our program. Sets the state based on what the user types into the console
     * and then initiates the main loop.
     */
    public static void setState() {
        state = State.START;
        log("Welcome to Fun with Arithmetic " + name + "!\nPlease enter the letters:\n"
                + "'N' = New users press this!\n"
                + "'T' = Time Attack Mode\n"
                + "'L' = Learner Mode\n"
                + "'H' = Hard Mode\n"
                + "'HS' = Check your Highscores\n"
                + "'X' = Exit Program"); 
        switch(scan.nextLine()) {
            case "N":
                state = State.NEW;
                break;                
            case "T":
                state = State.TIME;
                break;
            case "L":
                state = State.LEARNER;
                break;
            case "H":
                state = State.HARD;
                break;
            case "X":
                System.exit(0);
                break;
            case "HS":
                log(name + ", your highscores are:\n"
                        + "Time Attack: " + timeCount + " questions!\n"
                        + "Hard Mode: " + hardTime + " seconds!\n"
                        + "Press any key to go back to the main menu!");
                scan.nextLine();
                break;
               
            default:                
                log("Wrong input please try again!");
                break;
        }
        if(state == State.START) {
            setState();
        } else {
            mainLoop();
        }
    }
    
    /**
     * Switches through the states. Whichever state was picked has a set of instructions.
     */
    public static void mainLoop() {        
        switch(state) {
            case NEW:
                log("=====Fun with Arithmetic=====\n"
                        + "Fun with Arithmetic is a teaching tool that is used to teach kids of\n"
                        + "all ages basic math skills. You will be given asterick formations\n"
                        + "that you will have to compute mathematically in your head. Each asterick is\n"
                        + "equal to one unit. So (*) plus (*) would equal (**) or 2!\n"
                        + "Lets get started with the tutorial! We will give you 3 starter questions\n"
                        + "and then you can jump into the full experience!\n");
                
                submitQuestion(getQuery(4, false));           
                log("You completed the tutorial, press any key to continue!");
                scan.nextLine();
                scan.nextLine();
                break;
            case HARD:
                log("In Hard mode you are given 5 questions that you have to complete as fast as possible.\n"
                  + "Questions will start in 3 seconds.");
                countDown();
                long time = System.currentTimeMillis();
                submitQuestion(getQuery(1, true));
                time = System.currentTimeMillis() - time;
                if(time < hardTime * 1000) {
                    log("Congratulations you beat your highscore! 5 hard questions in " + (time / 1000) + " seconds!");
                    hardTime = (int) (time / 1000);
                    setProps();
                } else {
                    log("You completed 5 hard questions in " + (time / 1000) + " seconds!");
                }
                log("Press any key to continue!");
                scan.nextLine();
                scan.nextLine();
                break;
            case LEARNER:
                log("In Learner mode you are given a infinite number of medium questions. Type -1000 to quit.");
                int c = 0;
                while(submitQuestion(getQuery(1, true))){
                    c++;
                }
                log("You completed " + c + " questions!");
                log("Press any key to continue!");
                scan.nextLine();
                scan.nextLine();
                break;
            case TIME:
                log("In Time Attack mode you will try to do as many questions as possible in 30 seconds.");
                countDown();
                int counter = 0;
                long current = System.currentTimeMillis();
                while(true) {
                   submitQuestion(getQuery(1, false));
                   counter++;
                   if(System.currentTimeMillis() - current > 10000) {
                       break;
                   }                  
                }
                if(timeCount < counter) {
                    log("Congratulations, you beat your highscore by " + (counter - timeCount) + "!");
                    timeCount = counter;
                    setProps();
                } else {
                    log("Congratulations, you completed " + counter + " questions in 30 seconds!");
                }
                log("Press any key to continue!");
                scan.nextLine();
                scan.nextLine();
                break;          
        }
        System.out.println();
        System.out.println();
        setState();        
    }
    
    /**
     * Stores the users highscores as a properties file.
     */
    public static void setProps() {
        props.setProperty("hardTime", Integer.toString(hardTime));
        props.setProperty("timeCount", Integer.toString(timeCount));
        props.setProperty("name", name);
        try {
            FileWriter writer = new FileWriter(output);
            props.store(writer, null);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Method for counting down.
     */
    public static void countDown() {
        try {
            Thread.sleep(1000);
            log("3...");
            Thread.sleep(1000);
            log("2...");
            Thread.sleep(1000);
            log("1...");
            Thread.sleep(1000);
            log("GO!");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Returns a String array of questions for the user.
     * 
     * @param totalQ
     *            Amount of questions needed.
     * @param hard
     *            Determines whether the questions are harder than usual.
     * @return a <code>String[] array</code>
     */
    public static String[] getQuery(int totalQ, boolean hard) {        
        String[] array = new String[totalQ];
        Random rand = new Random();
        String query = "What is ";
        int total = 0;        
        int indexes = rand.nextInt(2) + 2;
        int z;        
        
        for(int e = 0; e < totalQ; e++) {
            if(hard == true) {            
                z = rand.nextInt(4) + 1;
            } else {
                z = rand.nextInt(1) + 1;
            }
            total += z;
            query += new String(new char[z]).replace("\0", "*");
            for(int i = 0; i < indexes; i++) {
                if(rand.nextInt(2) == 0) {
                    z = hard == true ? rand.nextInt(4) + 1 : rand.nextInt(2) + 1;
                    query += " - " + new String(new char[z]).replace("\0", "*");
                    total -= z;
                } else {
                    z = hard == true ? rand.nextInt(4) + 1 : rand.nextInt(2) + 1;
                    query += " + " + new String(new char[z]).replace("\0", "*");
                    total += z;
                }            
            }
            array[e] = query + "?." + total;
            query = "What is ";
            total = 0;
        }
        
        return array;
    }
    
    /**
     * Queries the user with questions.
     * 
     * @param query
     *            The array with questions
     * @return <code>true</code> if user didn't exit.
     */
    public static boolean submitQuestion(String[] query) {
        for(int i = 0; i < query.length; i++) {
            if(query.length > 1) {
                log("Question " + (i + 1) + ": " + query[i].substring(0, query[i].indexOf(".")));
            } else {
                log("Question: " + query[i].substring(0, query[i].indexOf(".")));
            }
            int a;
            while((a = scan.nextInt()) != Integer.parseInt(query[i].split("\\.")[1])) {
                if(a == -1000) {
                    return false;
                }
                log("Wrong answer! Try again: ");
            } 
        }
        return true;
    }
    
    /**
     * Prints the given message.
     * 
     * @param message
     *            The message to be printed.
     */
    public static void log(String message) {
        System.out.println(message);
    }
    
}

