/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cep.subscriber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Utente
 */
public class EventSubscriber {
   
    protected FileWriter logFW;
    protected BufferedWriter logBuff;
    
    protected void appendOnLog(String stringToLog, String fileLog){
        try {
            File logFile = new File(fileLog);
            FileWriter logFW = new FileWriter(logFile, true);
            BufferedWriter logBuff = new BufferedWriter(logFW);
            logFW.append(stringToLog);
            logBuff.flush();
            logBuff.close();
        } catch (FileNotFoundException ex) {
            System.err.println("LogFile not found. "+ex);
        } catch (IOException ex) {
            System.err.println("Error wirting LogFile. "+ex);
        }
    }
    
}
