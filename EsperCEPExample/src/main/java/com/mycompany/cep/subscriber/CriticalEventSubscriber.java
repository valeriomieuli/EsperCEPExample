package com.mycompany.cep.subscriber;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.mycompany.cep.event.TemperatureEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class CriticalEventSubscriber extends EventSubscriber implements StatementSubscriber {

    private static final String CRITICAL_LOG_NAME = "CriticalLog.txt";

    /**
     * Used as the minimum starting threshold for a critical event.
     */
    private static final String CRITICAL_EVENT_THRESHOLD = "100";

    /**
     * If the last event in a critical sequence is this much greater than the
     * first - issue a critical alert.
     */
    private static final String CRITICAL_EVENT_MULTIPLIER = "1.5";

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        String crtiticalEventExpression = "select * from TemperatureEvent "
                + "match_recognize ( "
                + "       measures A as temp1, B as temp2, C as temp3, D as temp4 "
                + "       pattern (A B C D) "
                + "       define "
                + "               A as A.temperature > " + CRITICAL_EVENT_THRESHOLD + ", "
                + "               B as (A.temperature < B.temperature), "
                + "               C as (B.temperature < C.temperature), "
                + "               D as (C.temperature < D.temperature) and D.temperature > (A.temperature * " + CRITICAL_EVENT_MULTIPLIER + ")" + ")";

        return crtiticalEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, TemperatureEvent> eventMap) {

        // 1st Temperature in the Critical Sequence
        TemperatureEvent temp1 = (TemperatureEvent) eventMap.get("temp1");
        // 2nd Temperature in the Critical Sequence
        TemperatureEvent temp2 = (TemperatureEvent) eventMap.get("temp2");
        // 3rd Temperature in the Critical Sequence
        TemperatureEvent temp3 = (TemperatureEvent) eventMap.get("temp3");
        // 4th Temperature in the Critical Sequence
        TemperatureEvent temp4 = (TemperatureEvent) eventMap.get("temp4");

        StringBuilder sb = new StringBuilder();
        sb.append("***************************************");
        sb.append("\n* [ALERT] : CRITICAL EVENT DETECTED! ");
        sb.append("\n* " + temp1.getTemperature() + " > " + temp2.getTemperature() + " > " + temp3.getTemperature() + " > " + temp4.getTemperature());
        sb.append("\n***************************************");

        System.out.println(sb.toString());

        appendOnLog(temp1.getTemperature() + "," + temp2.getTemperature() + ","
                + temp3.getTemperature() + "," + temp4.getTemperature() + ","
                + temp4.getTimeOfReading() + ";", CRITICAL_LOG_NAME);

    }

}
