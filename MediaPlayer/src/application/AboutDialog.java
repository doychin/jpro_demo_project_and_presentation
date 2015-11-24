package application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void showDialog() {
		SwingUtilities.invokeLater(() -> {
			try {
				AboutDialog dialog = new AboutDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JFXPanel panel = new JFXPanel();
			initStage(panel);
			contentPanel.add(panel);
		}
		{
			JLabel lblJavafxMediaPlayer = new JLabel("JavaFX Media Player Demo Application");
			lblJavafxMediaPlayer.setFont(new Font("Tahoma", Font.PLAIN, 18));
			lblJavafxMediaPlayer.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblJavafxMediaPlayer, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	private void initStage(JFXPanel panel) {
		Platform.runLater(() -> {
			ImageView view = new ImageView(new Image(getClass().getResourceAsStream("JavaFx.png")));
			view.setPreserveRatio(true);
			view.setFitHeight(150);
			BorderPane root = new BorderPane(view);
			Scene scene = new Scene(root);
			panel.setScene(scene);
		});
	}

}
