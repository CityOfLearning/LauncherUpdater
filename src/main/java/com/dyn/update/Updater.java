package com.dyn.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Updater extends Application {

	private static int APP_WIDTH = 320;
	private static int APP_HEIGHT = 160;

	private static UpdaterProgressMonitor monitor = new UpdaterProgressMonitor();

	public static void main(String[] args) {
		launch(args);
	}

	public static void launchApp(File f, UpdaterProgressMonitor progress) {
		/*progress.setStatus("Success, Relauching Launcher");
		progress.setMax(1);
		progress.setProgress(1);*/
		List<String> launchCommand = new ArrayList<String>();
		try {
			launchCommand.add("java");
			launchCommand.add("-jar");
			launchCommand.add(f.getCanonicalPath());

			ProcessBuilder pbuild = new ProcessBuilder(launchCommand);
			Thread.sleep(3000);
			
			pbuild.start();
			System.exit(0);

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Launcher Updater");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Updating Launcher");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
		scenetitle.setFill(Color.YELLOWGREEN);
		grid.add(scenetitle, 0, 0, 2, 1);

		Text status = new Text();
		status.setWrappingWidth(270);
		status.prefWidth(270);
		status.textProperty().bind(monitor.getTextProperty());
		status.setFill(Color.FIREBRICK);
		status.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		grid.add(status, 0, 2, 2, 1);

		
		
		ProgressBar bar = new ProgressBar();
		bar.setOpacity(1);
		bar.setPrefWidth(270);
		bar.progressProperty().bind(monitor.getBarProperty());		
		bar.getStylesheets().add("assets/dyn/updater/striped-progress.css");
		monitor.setMax(1);
		monitor.setProgress(1);
		grid.add(bar, 0, 3, 2, 1);

		BackgroundImage myBI = new BackgroundImage(
				new Image("assets/dyn/updater/options_background.png", 300, 300, false, true), BackgroundRepeat.REPEAT,
				BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		// then you set to your node
		grid.setBackground(new Background(myBI));

		Scene scene = new Scene(grid, APP_WIDTH, APP_HEIGHT);

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Updater.updateApp(monitor);
			}
		});
	}

	public static void updateApp(UpdaterProgressMonitor progress) {
		File updatedFile = new File(System.getProperty("user.dir"), "DYN_Minecraft_Launcher.jar");
		
		if (updatedFile.exists()) {
			updatedFile.delete();
		}
		
		for(File f : updatedFile.getParentFile().listFiles()){
			if (f.getName().toLowerCase().contains("dyn") && f.getName().toLowerCase().contains("launcher") && f.getName().contains("jar")) {
				f.delete();
			}
		}
		
		//progress.setStatus("Downloading updates");
		FileUtils.downloadFileWithProgress(
				"https://github.com/Digital-Youth-Network/MinecraftLauncher/releases/download/2.1/DYN-Minecraft-Launcher-2.1.jar",
				updatedFile, progress);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Updater.launchApp(updatedFile, progress);
			}
		});
	}

}
