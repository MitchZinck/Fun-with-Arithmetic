package com.mzinck.fwa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

/**
 * Program that does distances
 * 
 * @author Mitchell Zinck <mitch@mzinck.com> <mitchellzinck@yahoo.com>
 */

public class FWA {
    
    private static State        state     = State.START;
    private static Properties   props     = new Properties();
    private static String       name      = "";
    private static Scanner      scan      = new Scanner(System.in);
    private static int          timeCount = 0,
                                hardTime = 0;
    private static OutputStream output;

    public static void main(String[] args) throws IOException {
        output = new FileOutputStream(System.getProperty("user.home") + "arithmetic.cfg");
        props.load(new FileInputStream(System.getProperty("user.home") + "arithmetic.cfg"));
        if(props.getProperty("timeCount") != null) {
            timeCount = Integer.parseInt(props.getProperty("timeCount"));
            hardTime = Integer.parseInt(props.getProperty("hardTime"));
        }
        setState();        
    }
    
    public static void test(String z) {
        z = "k";
    }
    
    public static void setState() {
        state = State.START;
        System.out.println("Welcome to Fun with Arithmetic " + name + "!\nPlease enter the letters:\n"
                + "'N' = New users press this!\n"
                + "'T' = Time Attack Mode\n"
                + "'L' = Learner Mode\n"
                + "'H' = Hard Mode\n"
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
                
            default:                
                log("Wrong input please try again!");
                break;
        }
        if(state == State.START) {
            setState();
        } else {
            try {
                mainLoop();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static void mainLoop() throws InterruptedException {        
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
                break;
            case HARD:
                log("In Hard mode you are given 5 questions that you have to complete as fast as possible.\n"
                  + "Questions will start in 3 seconds.");
                countDown();
                long time = System.currentTimeMillis();
                submitQuestion(getQuery(5, true));
                time = System.currentTimeMillis() - time;
                if(hardTime < time) {
                    log("Congratulations you beat your highscore! 5 hard questions in " + (time / 1000) + " seconds!");
                    hardTime = (int) (time / 1000);
                    setProps();
                } else {
                    log("You completed 5 hard questions in " + (time / 1000) + " seconds!");
                }
                break;
            case LEARNER:
                log("In Learner mode you are given a infinite number of medium questions. Type -1000 to quit.");
                int c = 0;
                while(submitQuestion(getQuery(1, true))){
                    c++;
                }
                log("You completed " + c + " questions!");
                break;
            case TIME:
                log("In Time Attack mode you will try to do as many questions as possible in 30 seconds.");
                countDown();
                long cd = 30, counter = 0;
                while(cd > 0) {
                   submitQuestion(getQuery(1, false));
                   counter++;
                }
                if(timeCount < counter) {
                    log("Congratulations, you beat your highscore by " + (counter - timeCount) + "!");
                    timeCount = (int) (counter / 1000);
                    setProps();
                } else {
                    log("Congratulations, you completed " + counter + " questions in 30 seconds!");
                }
                break;          
        }
        setState();        
    }
    
    public static void setProps() {
        props.setProperty("hardTime", Integer.toString(hardTime));
        props.setProperty("timeCount", Integer.toString(timeCount));
    }
    
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
    
    public static String[] getQuery(int totalQ, boolean hard) {        
        String[] array = new String[totalQ];
        Random rand = new Random();
        String query = "What is ";
        int total = 0;        
        int indexes = rand.nextInt(3) + 2;
        
        int z = hard ? rand.nextInt(50) + 1 : rand.nextInt(20) + 1;
        total += z;
        query += z;
        
        for(int e = 0; e < totalQ; e++) {
            for(int i = 0; i < indexes; i++) {
                if(rand.nextInt(2) == 0) {
                    z = rand.nextInt(50) + 1;
                    query += " - " + z;
                    total -= z;
                } else {
                    z = rand.nextInt(50) + 1;
                    query += " + " + z;
                    total += z;
                }            
            }
            array[e] = query + "?." + total;
        }
        
        return array;
    }
    
    public static boolean submitQuestion(String[] query) {
        for(int i = 0; i < query.length; i++) {
            log("Question " + (i + 1) + ": " + query[i].substring(0, query[i].indexOf(".")));
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
    
    public static void log(String message) {
        System.out.println(message);
    }
    
}

