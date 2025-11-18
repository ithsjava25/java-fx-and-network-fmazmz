package com.example.domain;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ChatModel {
    private final ObservableList<NtfyEventResponse> messages = FXCollections.observableArrayList();


    public void addMessage(NtfyEventResponse msg) {
        runOnFx(() -> messages.add(msg));
    }

    public ObservableList<NtfyEventResponse> getMessages() {
        return messages;
    }

    private static void runOnFx(Runnable task) {
        try {
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else if (!Platform.isImplicitExit()) {
                Platform.runLater(task);
            } else {
                // execute test case immediately
                task.run();
            }
        } catch (IllegalStateException notInitialized) {
            task.run();
        }
    }


}
