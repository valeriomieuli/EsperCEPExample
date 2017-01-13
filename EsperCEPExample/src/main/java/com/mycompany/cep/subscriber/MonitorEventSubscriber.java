package com.mycompany.cep.subscriber;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class MonitorEventSubscriber extends EventSubscriber implements StatementSubscriber {

    private static final String MONITOR_LOG_NAME = "MonitorLog.txt";

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select avg(temperature) as avg_val from TemperatureEvent.win:time_batch(5 sec)";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, Double> eventMap) {

        // average temp over 10 secs
        Double avg = (Double) eventMap.get("avg_val");

        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------");
        sb.append("\n- [MONITOR] Average Temp = " + avg);
        sb.append("\n---------------------------------");

        System.out.println(sb.toString());

        appendOnLog(avg + "," + new Date() + ";", MONITOR_LOG_NAME);

    }

}
