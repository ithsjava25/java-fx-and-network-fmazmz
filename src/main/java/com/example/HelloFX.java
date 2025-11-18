package com.example;

import com.example.client.ChatNetworkClient;
import com.example.client.NtfyHttpClient;
import com.example.domain.ChatModel;
import com.example.domain.NtfyMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

import static com.example.utils.EnvLoader.loadEnv;

public class HelloFX extends Application {
    private static final Logger log = LoggerFactory.getLogger("MAIN");
    static final ChatModel model = new ChatModel();

    @Override
    public void start(Stage stage) throws Exception {
        Properties env = loadEnv();
        String baseUrl = env.getProperty("NTFY_BASE_URL", "https://ntfy.sh");
        String topic = env.getProperty("NTFY_TOPIC");

        if (topic == null || topic.isBlank()) {
            throw new IllegalStateException("NTFY_TOPIC is not set");
        }

        FXMLLoader loader = new FXMLLoader(HelloFX.class.getResource("hello-view.fxml"));
        Parent root = loader.load();

        HelloController controller = loader.getController();
        controller.setModel(model);

        ChatNetworkClient client = new NtfyHttpClient(model);
        controller.setClient(client, baseUrl, topic);

        client.subscribe(baseUrl, topic);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(HelloFX.class.getResource("styles.css")).toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }

}