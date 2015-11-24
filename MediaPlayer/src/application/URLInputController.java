package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;

public class URLInputController implements Initializable {

	@FXML
	ComboBox<String> urlCombo;

	private static final ArrayList<String> recentItems = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (recentItems.isEmpty()) {
			recentItems.add("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
			recentItems.add("http://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_10mb.mp4");
		}

		urlCombo.setItems(FXCollections.observableArrayList(recentItems));

		urlCombo.valueProperty().addListener((o, ol, nw) -> System.out.println(nw));
	}

	public void setupDialog(Dialog<String> urlInputDialog) {
		urlInputDialog.getDialogPane().getButtonTypes().add(new ButtonType("Open...", ButtonData.OK_DONE));
		urlInputDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		urlInputDialog.setResultConverter(b -> {
			if (b.getButtonData() == ButtonData.OK_DONE && urlCombo.getValue() != null) {
				if (!recentItems.contains(urlCombo.getValue())) {
					recentItems.add(urlCombo.getValue());
				}

				return urlCombo.getValue();
			}
			return null;
		});

	}

}
