package notification;

import java.util.logging.Logger;

public class ConsoleNotifier implements Notifier{

    private static final Logger log = Logger.getLogger(ConsoleNotifier.class.getName());
    @Override
    public void notify(String recipient, String message) {
        log.info(() -> "[Notify -> " + recipient + "] " + message);
    }
    
}
