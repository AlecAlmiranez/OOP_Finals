/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mp3player;

import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Alec
 */
public class Sounds extends AudioPlayer {
    public Sounds(String filepath) {
        super(filepath);
    }

    @Override
    public void playFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            Player player = new Player(fileInputStream);
            player.play();
        } catch (IOException | JavaLayerException ex) {
            Logger.getLogger(Sounds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

