package tec.controller;

import tec.styles.Styles;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import tec.views.NewFingerprintRec;
import tec.views.Panel;

public class ControllerNewFingerprintRec {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private NewFingerprintRec viewNewFingerprintRec;
    
    public ControllerNewFingerprintRec(NewFingerprintRec view){
        this.viewNewFingerprintRec = view; 
        
        this.viewNewFingerprintRec.btnFingerCapture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                 if(viewNewFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){
                    viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                }else{
                    viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                }    
            }            
        });
        
        this.viewNewFingerprintRec.btnFingerCaptureLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                 if(viewNewFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){
                    viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                }else{
                    viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                }    
            }
            
        });
    }
    
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String json) {
        for (ModelObserver observer : observers) {
            observer.update(json); 
        }
    }   
}
