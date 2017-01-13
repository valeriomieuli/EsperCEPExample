package com.mycompany.cep.util;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mycompany.cep.event.TemperatureEvent;

import com.mycompany.cep.handler.TemperatureEventHandler;

/**
 * Just a simple class to create a number of Random TemperatureEvents and pass
 * them off to the TemperatureEventHandler.
 */
@Component
public class RandomTemperatureEventGenerator {
    
    @Autowired
    private TemperatureEventHandler temperatureEventHandler;

    /**
     * Creates simple random Temperature events and lets the implementation
     * class handle them.
     */
    public void startSendingTemperatureReadings(final long noOfTemperatureEvents) {

        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
            public void run() {

                System.out.println(getStartingMessage());

                int count = 0;
                while (count < noOfTemperatureEvents) {
                    TemperatureEvent ve = new TemperatureEvent(new Random().nextInt(500), new Date());
                    temperatureEventHandler.handle(ve);
                    count++;
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        System.out.println("Thread Interrupted. " + e);
                    }
                }

            }
        });
    }

    private String getStartingMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n************************************************************");
        sb.append("\n* STARTING - ");
        sb.append("\n* PLEASE WAIT - TEMPERATURES ARE RANDOM SO MAY TAKE");
        sb.append("\n* A WHILE TO SEE WARNING AND CRITICAL EVENTS!");
        sb.append("\n************************************************************\n");
        return sb.toString();
    }
}
