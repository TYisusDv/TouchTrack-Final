package tec.styles;

import java.awt.Color;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class Styles {
    public static Color colorPrimary = new Color(0x33179C);
    public static Color colorSecondary = new Color(0xA292FF);
    public static Color colorDanger = new Color(0xFF4D63);
    public static Color colorSuccess = new Color(0xFF4D63);
    public static Color colorDisabled = new Color(0x666666);
    
    public static ClassLoader classLoader = Styles.class.getClassLoader();
    public static ImageIcon imgIconTouchTrack = new ImageIcon(classLoader.getResource("tec/images/touchtrackLogo.png"));
    public static ImageIcon imgIconClose = new ImageIcon(classLoader.getResource("tec/images/iconClose.png"));
    public static ImageIcon imgIconCloseRed = new ImageIcon(classLoader.getResource("tec/images/iconCloseRed.png"));
    public static ImageIcon imgIconCloseWhite = new ImageIcon(classLoader.getResource("tec/images/iconCloseWhite.png"));
    public static ImageIcon imgLoaderWhite = new ImageIcon(classLoader.getResource("tec/images/loaderWhite30px.gif"));
    public static ImageIcon imgIconMax = new ImageIcon(classLoader.getResource("tec/images/max.png"));
    public static ImageIcon imgIconMin = new ImageIcon(classLoader.getResource("tec/images/min.png"));
    public static ImageIcon imgLoader = new ImageIcon(classLoader.getResource("tec/images/preloader.gif"));
    public static ImageIcon imgCorrect = new ImageIcon(classLoader.getResource("tec/images/correct.png"));
    public static ImageIcon imginCorrect = new ImageIcon(classLoader.getResource("tec/images/incorrect.png"));

    public static void playSoundFromPackage(String soundFileName) {
        try {
            File soundFile = new File("sounds/" + soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
