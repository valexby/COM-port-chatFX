package comChat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL location = getClass().getResource("chatGui.fxml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent parent = fxmlLoader.load(location.openStream());
        primaryStage.setTitle("Serial Chat");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @Override
    public void stop() {
        ((ChatController) fxmlLoader.getController()).onStop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}