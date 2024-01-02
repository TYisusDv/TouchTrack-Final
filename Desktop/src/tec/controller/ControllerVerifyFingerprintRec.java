package tec.controller;

import tec.styles.Styles;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import tec.views.NewFingerprintRec;
import tec.views.Panel;
import tec.views.VerifyFingerprintRec;

public class ControllerVerifyFingerprintRec {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private VerifyFingerprintRec viewVerifyFingerprintRec;
    
    public ControllerVerifyFingerprintRec(VerifyFingerprintRec view){
        this.viewVerifyFingerprintRec = view; 
        
        this.viewVerifyFingerprintRec.btnFingerCapture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                 if(viewVerifyFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){
                    viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                }else{
                    viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                }    
            }            
        });
        
        this.viewVerifyFingerprintRec.btnFingerCaptureLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                 if(viewVerifyFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){
                    viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                }else{
                    viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
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
