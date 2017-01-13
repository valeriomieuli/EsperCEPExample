package com.mycompany.cep.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mycompany.cep.event.TemperatureEvent;
import com.mycompany.cep.subscriber.StatementSubscriber;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.mycompany.cep.subscriber.MonitorEventSubscriber;

/**
 * This class handles incoming Temperature Events. It processes them through the
 * EPService, to which it has attached the 4 queries.
 */
@Component
@Scope(value = "singleton")
public class TemperatureEventHandler implements InitializingBean {

    /**
     * Esper service
     */
    private EPServiceProvider epService;
    private EPStatement criticalEventStatement;
    private EPStatement warningEventStatement;
    private EPStatement monitorEventStatement;
    private EPStatement excursionEventStatement;

    @Autowired
    @Qualifier("criticalEventSubscriber")
    private StatementSubscriber criticalEventSubscriber;

    @Autowired
    @Qualifier("warningEventSubscriber")
    private StatementSubscriber warningEventSubscriber;

    @Autowired
    @Qualifier("monitorEventSubscriber")
    private StatementSubscriber monitorEventSubscriber;
    
    @Autowired
    @Qualifier("excursionEventSubscriber")
    private StatementSubscriber excursionEventSubscriber;

    /**
     * Configure Esper Statement(s).
     */
    public void initService() {

        System.out.println("Initializing service . . .");
        Configuration config = new Configuration();
        config.addEventTypeAutoName("com.mycompany.cep.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);

        createCriticalTemperatureCheckExpression();
        createWarningTemperatureCheckExpression();
        createTemperatureMonitorExpression();
        createTemperatureExcursionExpression();

    }

    /**
     * EPL to check for a sudden critical rise across 4 events, where the last
     * event is 1.5x greater than the first event. This is checking for a
     * sudden, sustained escalating rise in the temperature
     */
    private void createCriticalTemperatureCheckExpression() {
        
        System.out.println("create Critical Temperature Check Expression");
        criticalEventStatement = epService.getEPAdministrator().createEPL(criticalEventSubscriber.getStatement());
        criticalEventStatement.setSubscriber(criticalEventSubscriber);
        
    }

    /**
     * EPL to check for 2 consecutive Temperature events over the threshold - if
     * matched, will alert listener.
     */
    private void createWarningTemperatureCheckExpression() {
        
        System.out.println("create Warning Temperature Check Expression");
        warningEventStatement = epService.getEPAdministrator().createEPL(warningEventSubscriber.getStatement());
        warningEventStatement.setSubscriber(warningEventSubscriber);
        
    }

    /**
     * EPL to monitor the average temperature every 10 seconds. Will call
     * listener on every event.
     */
    private void createTemperatureMonitorExpression() {
        
        System.out.println("create Timed Average Monitor");
        monitorEventStatement = epService.getEPAdministrator().createEPL(monitorEventSubscriber.getStatement());
        monitorEventStatement.setSubscriber(monitorEventSubscriber);
        
    }
    
    /**
     * EPL to detect dangerous excursion. Will call
     * listener on every event.
     */
    private void createTemperatureExcursionExpression() {
        
        System.out.println("create Excursion Temperature check Monitor");
        excursionEventStatement = epService.getEPAdministrator().createEPL(excursionEventSubscriber.getStatement());
        excursionEventStatement.setSubscriber(excursionEventSubscriber);
        
    }

    /**
     * Handle the incoming TemperatureEvent.
     */
    public void handle(TemperatureEvent event) {

        System.out.println(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }

    
    @Override
    public void afterPropertiesSet() {
        
        System.out.println("System configuration . . .");
        initService();
        
    }
}
