package com.mycompany.cep;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mycompany.cep.util.RandomTemperatureEventGenerator;

/**
 * Entry point for the Demo. Run this from your IDE, or from the command line
 * using 'mvn exec:java'.
 */
public class StartApplication {

    /**
     * Main method - start the Demo!
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Application start . . .");

        long noOfTemperatureEvents = 1000;

        if (args.length != 1) {
            System.out.println("It will detect the standard number of temperature event: " + noOfTemperatureEvents + ".");
        } else {
            System.out.println("It will detect " + noOfTemperatureEvents + " temperature events.");
            noOfTemperatureEvents = Long.valueOf(args[0]);
        }

        // Load config
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{"application-context.xml"});
        BeanFactory factory = (BeanFactory) appContext;

        // Start Demo
        RandomTemperatureEventGenerator generator = (RandomTemperatureEventGenerator) factory.getBean("eventGenerator");
        generator.startSendingTemperatureReadings(noOfTemperatureEvents);

    }

}
