package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
			Pane root = loader.load();

			MainFormController controller = (MainFormController) loader.getController();
			controller.stage = primaryStage;

			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setOnCloseRequest((e) -> {
				System.exit(0);
			});

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
