package com.mycompany.cep.subscriber;

import java.util.Map;
import org.springframework.stereotype.Component;
import com.mycompany.cep.event.TemperatureEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class ExcursionEventSubscriber extends EventSubscriber implements StatementSubscriber {

    private static final String EXCURSION_LOG_NAME = "ExcursionLog.txt";

    /**
     * Used as the minimum starting threshold for a critical event.
     */
    private static final String HIGH_THRESHOLD = "300";

    /**
     * If the last event in a critical sequence is this much greater than the
     * first - issue a critical alert.
     */
    private static final String LOW_THRESHOLD = "80";

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        String excursionEventExpression = "select t1.temperature, t2.temperature, t3.temperature, t3.timeOfReading "
                + "from pattern ["
                + " every ( t1=TemperatureEvent(temperature > " + HIGH_THRESHOLD +") -> t2=TemperatureEvent(temperature > " + HIGH_THRESHOLD + ") )"
                + " -> t3=TemperatureEvent(temperature < " + LOW_THRESHOLD + ")"
                + "]";

        return excursionEventExpression;
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(int temp1, int temp2, int temp3, Date timestamp) {

        StringBuilder sb = new StringBuilder();
        sb.append("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        sb.append("\n [ALERT] : EXCURSION EVENT DETECTED! ");
        sb.append("\n " + temp1 + " -> " + temp2 + " -> " + temp3);
        sb.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        System.out.println(sb.toString());

        appendOnLog(temp1 + "," + temp2 + ","+ temp3 + "," + timestamp + ";", EXCURSION_LOG_NAME);

    }

}
