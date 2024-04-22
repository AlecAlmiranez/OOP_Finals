/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mp3player;

/**
 *
 * @author Alec
 */
public abstract class AudioPlayer {
    String filepath;
    
    public AudioPlayer(String filepath) {
        this.filepath = filepath;
    }
    //getter setter methods
        public String getFilePath() {
        return this.filepath;
    }

    public void setFilePath(String filePath) {
        this.filepath = filePath;
    }


    public abstract void playFile();

}

