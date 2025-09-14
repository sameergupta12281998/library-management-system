package notification;

import java.util.logging.Logger;

public class EmailNotifier implements Notifier{
    private static final Logger log = Logger.getLogger(EmailNotifier.class.getName());
    @Override
    public void notify(String recipient, String message) {
        log.info(() -> "[Email to " + recipient + "] " + message);
    }
    
}
