package notification;

public class NotifierFactory {
    public enum Channel { CONSOLE, EMAIL, SMS }

    public static Notifier create(Channel channel){
        return switch (channel){
            case CONSOLE -> new ConsoleNotifier();
            case EMAIL -> new EmailNotifier();
            case SMS -> new SMSNotifier();
        };
    }
    
}
