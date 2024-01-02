package tec.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import tec.styles.Styles;
import java.util.ArrayList;
import javax.swing.Timer;
import tec.views.TouchTrack2;
import tec.views.VerifyFingerprintRec;

public class ControllerTouchTrack2 {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private TouchTrack2 viewTouchTrack2;
    
    public ControllerTouchTrack2(TouchTrack2 view){
        this.viewTouchTrack2 = view; 
        
        
    }
    
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String json) {
        for (ModelObserver observer : observers) {
            observer.update(json); 
        }
    }  
    
    public void alertPanel(Color color, String title, String msg){
        this.viewTouchTrack2.alertPanel.setBackground(color);       
        this.viewTouchTrack2.alertTitle.setText(title);
        this.viewTouchTrack2.alertBody.setText(msg);
        this.viewTouchTrack2.alertPanel.setVisible(true);
        timerAlert.start();
    }
    
    Timer timerAlert = new Timer(3000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            viewTouchTrack2.alertPanel.setVisible(false);
            timerAlert.stop();
        }
    });
}
