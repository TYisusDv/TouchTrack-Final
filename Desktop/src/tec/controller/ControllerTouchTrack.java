package tec.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import tec.styles.Styles;
import java.util.ArrayList;
import javax.swing.Timer;
import tec.views.TouchTrack;
import tec.views.VerifyFingerprintRec;

public class ControllerTouchTrack {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private TouchTrack viewTouchTrack;
    
    public ControllerTouchTrack(TouchTrack view){
        this.viewTouchTrack = view; 
        
        
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
        this.viewTouchTrack.alertPanel.setBackground(color);       
        this.viewTouchTrack.alertTitle.setText(title);
        this.viewTouchTrack.alertBody.setText(msg);
        this.viewTouchTrack.alertPanel.setVisible(true);
        timerAlert.start();
    }
    
    Timer timerAlert = new Timer(3000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            viewTouchTrack.alertPanel.setVisible(false);
            timerAlert.stop();
        }
    });
}
