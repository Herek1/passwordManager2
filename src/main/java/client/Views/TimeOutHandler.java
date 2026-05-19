package client.Views;

import client.Util.JsonExtract;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TimeOutHandler {
    private StageHandler stageHandler;
    public TimeOutHandler(StageHandler stageHandler){
        this.stageHandler = stageHandler;
    }

    public void handleTimeout(String message){
        String timeout = JsonExtract.extract(message, "message");
        openTimeoutView(Integer.parseInt(timeout));
    }

    private void openTimeoutView(int seconds) {

        Label titleLabel = new Label("Too many requests");
        Label timerLabel = new Label();

        //titleLabel.setStyle("-fx-font-size: 20px;");
        //timerLabel.setStyle("-fx-font-size: 16px;");

        VBox root = new VBox(15, titleLabel, timerLabel);

        root.setStyle("""
        -fx-alignment: center;
        -fx-padding: 30;
   """);

        final int[] remaining = {seconds};

        timerLabel.setText(
                "You are timed out for " + remaining[0] + " seconds"
        );

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {

                    remaining[0]--;

                    timerLabel.setText(
                            "You are timed out for " + remaining[0] + " seconds"
                    );

                    if (remaining[0] <= 0) {
                        stageHandler.setDefaultView();
                    }
                })
        );

        timeline.setCycleCount(seconds);
        timeline.play();

        stageHandler.setScene(root, "Timeout");
    }
}
