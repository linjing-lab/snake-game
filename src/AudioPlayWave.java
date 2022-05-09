import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

class AudioPlayWave extends Thread {
	private File soundFile;
	private AudioInputStream audioInputStream;
	private Clip clip;
	private FloatControl gainControl;

	public AudioPlayWave(File wavFile) {
		audioInputStream = null;
		clip = null;
		gainControl = null;

		soundFile = wavFile;
		if (!soundFile.exists()) {
			soundFile = null;
			System.err.println("Wave file not found");
			return;
		}
	}

	public void run() {
		try {
			if (soundFile != null) {
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				clip.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopAudio() {
		if (clip != null)
			clip.stop();
	}

	public void setVolume(int value) {
		if (clip != null) {
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float maxVolume=gainControl.getMaximum();
			float minVolume=gainControl.getMinimum();
			gainControl.setValue((float)(minVolume+(maxVolume-minVolume)/100.0*value)); 
		}
	}


}
