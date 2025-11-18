package com.example;

import com.example.client.ChatNetworkClient;
import com.example.domain.ChatModel;
import com.example.domain.NtfyEventResponse;
import com.example.domain.NtfyMessage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    private ChatNetworkClient client;
    private String baseUrl;
    private String topic;
    private File selectedFile =  null;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<NtfyEventResponse> messagesList;

    @FXML
    private TextField messageInput;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField tagsInput;

    public void setClient(ChatNetworkClient client, String baseUrl, String topic) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.topic = topic;
    }

    public void setModel(ChatModel model) {
        messagesList.setItems(model.getMessages());
        messagesList.setCellFactory(list -> new MessageCell());
    }

    private static String formatTime(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        LocalTime time = LocalTime.ofInstant(instant, ZoneId.systemDefault());
        return time.toString();
    }

    private void showStatus(String text) {
        messageLabel.setText(text);
        Timeline t = new Timeline(new KeyFrame(javafx.util.Duration.seconds(3),
                ev -> messageLabel.setText("")));
        t.setCycleCount(1);
        t.play();
    }


    @FXML
    private void onPickAttachment() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select attachment");
        File file = chooser.showOpenDialog(messageInput.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            messageLabel.setText("Attachment selected: " + file.getName());
        }
    }

    @FXML
    private void onSend() {
        String txt = messageInput.getText();

        if ((txt == null || txt.isBlank()) && selectedFile == null) {
            showStatus("Nothing to send");
            return;
        }

        String title = titleInput.getText();
        if (title != null && title.isBlank()) title = null;

        String tagsRaw = tagsInput.getText();
        List<String> tags = null;

        if (tagsRaw != null && !tagsRaw.isBlank()) {
            tags = java.util.Arrays.stream(tagsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        NtfyMessage msg = new NtfyMessage.Builder()
                .id(UUID.randomUUID().toString())
                .time(System.currentTimeMillis())
                .event("message")
                .topic(topic)
                .message(txt)
                .title(title)
                .tags(tags)
                .attach(null)
                .filename(null)
                .build();

        try {
            client.send(baseUrl, msg, selectedFile);
            showStatus(selectedFile == null ? "Message sent" : "Attachment sent");
        } catch (InterruptedException | IOException e) {
            showStatus("Error sending: " + e.getMessage());
        }

        messageInput.clear();
        titleInput.clear();
        tagsInput.clear();
        selectedFile = null;
    }


    private static final class MessageCell extends ListCell<NtfyEventResponse> {
        @Override
        protected void updateItem(NtfyEventResponse msg, boolean empty) {
            super.updateItem(msg, empty);

            if (empty || msg == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            setText(null);

            VBox container = new VBox();
            container.setSpacing(6);
            container.getStyleClass().add("message-bubble");

            container.setStyle("-fx-alignment: CENTER_LEFT;");
            if (msg.title() != null) {
                Label titleLabel = new Label(msg.title());
                titleLabel.getStyleClass().add("message-title");
                container.getChildren().add(titleLabel);
            }

            if (msg.attachment() != null) {
                NtfyEventResponse.Attachment att = msg.attachment();

                if (att.type() != null && att.type().startsWith("image")) {
                    Image image = new Image(att.url(), 300, 0, true, true);
                    ImageView imageView = new ImageView(image);
                    container.getChildren().add(imageView);
                } else {
                    Label fileLabel = getFileLabel(att);
                    container.getChildren().add(fileLabel);
                }
            }

            if (msg.message() != null && !msg.message().isBlank()) {
                Label messageLabel = new Label(msg.message());
                messageLabel.setWrapText(true);
                messageLabel.getStyleClass().add("message-text");
                container.getChildren().add(messageLabel);
            }

            if (msg.tags() != null && !msg.tags().isEmpty()) {
                Label tagsLabel = new Label(String.join(", ", msg.tags()));
                tagsLabel.getStyleClass().add("message-tags");
                container.getChildren().add(tagsLabel);
            }

            if (msg.time() != null) {
                Label timeLabel = new Label(formatTime(msg.time()));
                timeLabel.getStyleClass().add("message-time");
                container.getChildren().add(timeLabel);
            }

            setGraphic(container);
        }

        // Helper method to allow user to open file
        private static Label getFileLabel(NtfyEventResponse.Attachment att) {
            Label fileLabel = new Label("Open file: " + (att.name() != null ? att.name() : att.url()));
            fileLabel.setStyle("-fx-text-fill: #2c75ff; -fx-underline: true;");
            fileLabel.setOnMouseClicked(ev -> {
                try {
                    String url = att.url();

                    // method that works on linux as Desktop is not always supported and crashes application
                    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        try {
                            new ProcessBuilder("xdg-open", url).start();
                            return;
                        }catch (IOException e) {
                            log.error("Error opening file: {}", url, e);
                        }
                    }

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(new URI(url));
                    }

                } catch (IOException | URISyntaxException ex) {
                    log.error("Failed to open attachment: {}", ex.getMessage());
                    log.error(Arrays.toString(ex.getStackTrace()));
                }
            });
            return fileLabel;
        }
    }
}
