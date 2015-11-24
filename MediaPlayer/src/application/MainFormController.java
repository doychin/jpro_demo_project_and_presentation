package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainFormController implements Initializable {

	@FXML
	ToggleButton btnPlay;

	@FXML
	ToggleButton btnPause;

	@FXML
	Button btnStop;

	@FXML
	MediaView mediaView;

	Stage stage;

	@FXML
	Slider playSlider;

	@FXML
	Slider volumeSlider;

	@FXML
	ToggleGroup player;

	@FXML
	Label lblVolume;

	@FXML
	ProgressIndicator progress;

	@FXML
	MenuItem playMenu;

	@FXML
	MenuItem stopMenu;

	@FXML
	MenuItem pauseMenu;

	public void closeApplication() {
		if (mediaView.getMediaPlayer() != null) {
			mediaView.getMediaPlayer().stop();
		}

		System.exit(0);
	}

	public void about() {

	}

	public void play() {
		mediaView.getMediaPlayer().play();
	}

	public void pause() {
		if (mediaView.getMediaPlayer().getStatus() == Status.PLAYING) {
			mediaView.getMediaPlayer().pause();
		} else {
			player.selectToggle(null);
		}
	}

	public void stop() {
		mediaView.getMediaPlayer().stop();
	}

	public void switchScreenSize(MouseEvent me) {
		if (me.getClickCount() == 2) {
			stage.setFullScreen(!stage.isFullScreen());
		}
	}

	private boolean updateFromPlayer = false;

	Timer volumeVisibilityTimer = new Timer();

	public void setupNewMediaPlayer(ObservableValue<? extends MediaPlayer> obs, MediaPlayer oldValue,
			MediaPlayer newPlayer) {
		if (newPlayer == null) {
			return;
		}

		// Connect volume slider with volume property in player
		newPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100.0));

		// make positioning in stream possible
		newPlayer.currentTimeProperty().addListener((ob) -> {
			updateFromPlayer = true;
			playSlider.setValue(newPlayer.currentTimeProperty().getValue().toSeconds());
			updateFromPlayer = false;
		});

		playSlider.valueProperty().addListener((ob) -> {
			if (updateFromPlayer)
				return;
			newPlayer.seek(Duration.seconds(playSlider.getValue()));
		});

		// Show progress indicator in case player is STALLED
		progress.visibleProperty().bind(Bindings.equal(Status.STALLED, newPlayer.statusProperty()));

		newPlayer.statusProperty().addListener(ob -> {
			switch (newPlayer.getStatus()) {
			case PAUSED:
				player.selectToggle(btnPause);
				break;
			case PLAYING:
				player.selectToggle(btnPlay);
				break;
			default:
				player.selectToggle(null);
				break;
			}
		});
	}

	BooleanProperty validPlayer = new SimpleBooleanProperty(false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mediaView.mediaPlayerProperty().addListener(this::setupNewMediaPlayer);

		makeButtonBindinigs();

		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();

		// mediaView.fitWidthProperty().bind(mediaView.getParent().d);

		width.bind(Bindings.selectDouble(mediaView.parentProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.parentProperty(), "height"));

		makeVolumeBindings();

		recentMenu.disableProperty().bind(Bindings.isEmpty(recentMenu.getItems()));
	}

	private void makeVolumeBindings() {
		volumeSlider.setValue(50);

		lblVolume.textProperty().bind(Bindings.concat("Volume: ", volumeSlider.valueProperty().asString("%2.1f"), "%"));
		lblVolume.textProperty().addListener((ob) -> {
			lblVolume.setVisible(true);
			volumeVisibilityTimer.cancel();
			volumeVisibilityTimer = new Timer();
			volumeVisibilityTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Platform.runLater(() -> {
						lblVolume.setVisible(false);
					});
				}
			}, 1000);
		});
	}

	private void makeButtonBindinigs() {
		BooleanBinding validMediaBinding = Bindings.createBooleanBinding(() -> {
			if (mediaView.getMediaPlayer() == null) {
				return false;
			}

			validPlayer.bind(Bindings.createBooleanBinding(() -> {
				Status playerStatus = mediaView.getMediaPlayer().getStatus();
				return !(playerStatus == Status.DISPOSED || playerStatus == Status.HALTED
						|| playerStatus == Status.UNKNOWN);
			} , mediaView.getMediaPlayer().statusProperty()));

			return true;
		} , mediaView.mediaPlayerProperty());

		BooleanBinding bb = Bindings.not(validMediaBinding.and(validPlayer));

		btnPlay.disableProperty().bind(bb);
		btnPause.disableProperty().bind(bb);
		btnStop.disableProperty().bind(bb);

		playMenu.disableProperty().bind(bb);
		stopMenu.disableProperty().bind(bb);
		pauseMenu.disableProperty().bind(bb);
	}

	public void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("All media files", Arrays.asList("*.mpeg", "*.mp4", "*.mp3")),
				new ExtensionFilter("MPEG video file", Arrays.asList("*.mpeg", "*.mp4")),
				new ExtensionFilter("MP3 Audio file", Arrays.asList("*.mp3")));
		fileChooser.setTitle("Open Media file");

		File mediaFile = fileChooser.showOpenDialog(stage);

		if (mediaFile == null) {
			return;
		}

		String fileName = mediaFile.toURI().toString();
		openFileInMediaView(fileName);
	}

	@FXML
	private Menu recentMenu;

	HashSet<String> lastFileNames = new HashSet<>();

	/**
	 *
	 * @param fileName
	 */
	private void openFileInMediaView(String fileName) {

		if (mediaView.getMediaPlayer() != null && mediaView.getMediaPlayer().getStatus() == Status.PLAYING) {
			mediaView.getMediaPlayer().stop();
		}

		updateRecentOpenMenu(fileName);

		Media media = new Media(fileName);
		media.setOnError(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Unable to open requested media!");
			alert.setContentText(media.getError().getMessage());

			alert.showAndWait();
		});
		MediaPlayer player = new MediaPlayer(media);
		mediaView.setMediaPlayer(player);

		player.setOnReady(() -> {
			playSlider.setMax(player.getStopTime().toSeconds());
		});
	}

	/**
	 *
	 * @param fileName
	 */
	private void updateRecentOpenMenu(String fileName) {
		if (lastFileNames.contains(fileName)) {
			for (int i = 0; i < recentMenu.getItems().size(); i++) {
				MenuItem item = recentMenu.getItems().get(i);
				if (item.getText().equals(fileName)) {
					recentMenu.getItems().remove(item);
					break;
				}
			}
		}
		MenuItem newItem = new MenuItem();
		newItem.setText(fileName);

		lastFileNames.add(fileName);

		newItem.setOnAction(e -> {
			openFileInMediaView(newItem.getText());
		});

		recentMenu.getItems().add(0, newItem);
		if (recentMenu.getItems().size() > 5) {
			MenuItem item = recentMenu.getItems().get(recentMenu.getItems().size() - 1);
			lastFileNames.remove(item.getText());
			recentMenu.getItems().remove(item);
		}

		for (int i = 0; i < recentMenu.getItems().size(); i++) {
			MenuItem item = recentMenu.getItems().get(i);
			item.setAccelerator(new KeyCodeCombination(KeyCode.getKeyCode("" + (i + 1)), KeyCombination.SHORTCUT_ANY));
		}
	}

	/**
	 *
	 */
	public void openUrl() {
		Dialog<String> urlInputDialog = new Dialog<String>();

		Pane pane;
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("UrlInputDialog.fxml"));
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		URLInputController controller = loader.getController();
		urlInputDialog.getDialogPane().setContent(pane);

		controller.setupDialog(urlInputDialog);

		Optional<String> urlResult = urlInputDialog.showAndWait();
		if (!urlResult.isPresent()) {
			return;
		}

		openFileInMediaView(urlResult.get());
	}
}
