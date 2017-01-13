package com.mycompany.cep.subscriber;

import com.mycompany.cep.event.TemperatureEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class WarningEventSubscriber extends EventSubscriber implements StatementSubscriber {

    private static final String WARNING_LOG_NAME = "WarningLog.txt";

    /**
     * If 2 consecutive temperature events are greater than this - issue a
     * warning
     */
    private static final String WARNING_EVENT_THRESHOLD = "400";

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example using 'Match Recognise' syntax.
        String warningEventExpression = "select * from TemperatureEvent "
                + "match_recognize ( "
                + "       measures A as temp1, B as temp2 "
                + "       pattern (A B) "
                + "       define "
                + "               A as A.temperature > " + WARNING_EVENT_THRESHOLD + ", "
                + "               B as B.temperature > " + WARNING_EVENT_THRESHOLD + ")";

        return warningEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, TemperatureEvent> eventMap) {

        // 1st Temperature in the Warning Sequence
        TemperatureEvent temp1 = (TemperatureEvent) eventMap.get("temp1");
        // 2nd Temperature in the Warning Sequence
        TemperatureEvent temp2 = (TemperatureEvent) eventMap.get("temp2");

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------");
        sb.append("\n- [WARNING] : TEMPERATURE SPIKE DETECTED"
                + "\n" + temp1.getTemperature() + "," + temp2.getTemperature());
        sb.append("\n--------------------------------------------------");

        System.out.println(sb.toString());

        appendOnLog(temp1.getTemperature() + "," + temp2.getTemperature() + "," + temp2.getTimeOfReading() + ";", WARNING_LOG_NAME);

    }

}
