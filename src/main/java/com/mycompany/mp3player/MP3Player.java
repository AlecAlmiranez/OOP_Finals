/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mp3player;

import java.util.Scanner;

/**
 *
 * @author Alec
 */
// test
public class MP3Player {
    //Main Class
    public static void main(String[] args) {
        int choice;
        Scanner choice1 = new Scanner(System.in);
        AudioPlayer player1 = new Sounds("GoatScream.mp3");
        AudioPlayer player2 = new Sounds("Ride.mp3");
        AudioPlayer player3 = new Sounds("CompleteTheMission.mp3");
        do {
            
        System.out.println("Select Audio File");
        System.out.println("1 - Goat Scream");
        System.out.println("2 - Ride of the Valkyries");
        System.out.println("3 - Complete The Mission");
        System.out.println("4 - Exit");
        choice = choice1.nextInt();
        switch(choice)
        {
            case 1:
                player1.playFile();

                break;
            case 2:
                player2.playFile();

                break;
                
            case 3:
                player3.playFile();
                break;
                
            default:
                break;
        }
        } while (choice != 4);
    }
   }

