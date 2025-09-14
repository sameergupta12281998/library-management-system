package notification;

import java.util.logging.Logger;

public class SMSNotifier implements Notifier {

    private static final Logger log = Logger.getLogger(SMSNotifier.class.getName());
    @Override
    public void notify(String recipient, String message) {
        log.info(() -> "[SMS to " + recipient + "] " + message);
    }
    
}
