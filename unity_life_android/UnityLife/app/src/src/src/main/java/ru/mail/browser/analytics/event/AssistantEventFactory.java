package src.main.java.ru.mail.browser.analytics.event;

public class AssistantEventFactory {

    public static Event assistantClicked() {
        return Event
                .newEvent("Assistant_Clicked")
                .addParam("Place", "NTP")
                .build();
    }

    public static Event assistantOpened(boolean isAuthed) {
        return Event
                .newEvent("Assistant_Opened")
                .addParam("Method", "Icon")
                .addParam("Authed", isAuthed)
                .build();
    }

    public static Event assistantClosed(boolean isAuthed) {
        return Event
                .newEvent("Assistant_Closed")
                .addParam("Method", "Icon")
                .addParam("Authed", isAuthed)
                .build();
    }

    public static Event assistantButtonViewed(String userId) {
        return Event
                .newEvent("Assistant_Viewed")
                .addParam("Via", "NTP")
                .addParam("Type", "Button")
                .addParam("Opens_Assistant", true)
                .addParam("User_Id", userId)
                .build();
    }
}
