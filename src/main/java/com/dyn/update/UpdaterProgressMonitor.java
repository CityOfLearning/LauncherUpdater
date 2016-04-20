package com.dyn.update;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UpdaterProgressMonitor implements IProgressMonitor {

	private int _progress;
	private DoubleProperty progressProperty;
	private StringProperty titleProperty;
	private int max_length;

	public UpdaterProgressMonitor() {
		this.progressProperty = new SimpleDoubleProperty(0);
		this.titleProperty = new SimpleStringProperty("");
		this._progress = 0;
		this.max_length = 1;
	}

	public DoubleProperty getBarProperty() {
		return this.progressProperty;
	}

	public int getProgress() {
		return this._progress;
	}

	public final double getProgressPercent() {
		return Math.max(Math.min((double) this._progress / (double) this.max_length, 1), 0);
	}

	public String getStatus() {
		return this.titleProperty.getValue();
	}

	public StringProperty getTextProperty() {
		return this.titleProperty;
	}

	@Override
	public final void incrementProgress(int amount) {
		this._progress = Math.min(this.max_length, this._progress + amount);
		// progressProperty.setValue(getProgressPercent());
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				UpdaterProgressMonitor.this.progressProperty.setValue(UpdaterProgressMonitor.this.getProgressPercent());
			}
		});
	}

	@Override
	public void setMax(int len) {
		this.max_length = len;
	}

	@Override
	public final void setProgress(int progress) {
		this._progress = Math.min(this.max_length, progress);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				UpdaterProgressMonitor.this.progressProperty.setValue(UpdaterProgressMonitor.this.getProgressPercent());
			}
		});

	}

	@Override
	public void setStatus(String status) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				UpdaterProgressMonitor.this.titleProperty.setValue(status);
			}
		});
	}
}
