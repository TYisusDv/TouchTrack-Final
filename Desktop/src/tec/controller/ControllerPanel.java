package tec.controller;

import tec.styles.Styles;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import tec.main.Model;
import tec.views.Panel;

public class ControllerPanel {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private Panel viewPanel;    
    private int xOffset;
    private int yOffset;
    private boolean fullsized = true;
    private String option = "touchTrack2";
    private Model model;
    
    public ControllerPanel(Panel view, Model model){
        this.viewPanel = view;
        this.model = model;
        
        this.viewPanel.barPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if(!fullsized){
                    xOffset = me.getX();
                    yOffset = me.getY();
                } 
            } 
        });

        this.viewPanel.barPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent me) {
                if(!fullsized){
                    int x = me.getXOnScreen() - xOffset;
                    int y = me.getYOnScreen() - yOffset;
                    viewPanel.setLocation(x, y);
                } 
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
        });        
        
        this.viewPanel.btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btnClose.setIcon(Styles.imgIconCloseRed);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btnClose.setIcon(Styles.imgIconCloseWhite);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                System.exit(0);
            }
        });
        
        this.viewPanel.btnFullSize.addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(fullsized){                    
                    viewPanel.setExtendedState(JFrame.NORMAL);
                    viewPanel.btnFullSize.setIcon(Styles.imgIconMax);
                    fullsized = false;
                    setView(option);
                } else {                    
                    viewPanel.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    viewPanel.btnFullSize.setIcon(Styles.imgIconMin);
                    fullsized = true;
                    setView(option);
                }                
            }
        });
        
        this.viewPanel.btn1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn1.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn1.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {                
                option = "fingerprintRec";
                setView("fingerprintRec");
            }
        });
        
        this.viewPanel.btn1Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn1.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn1.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                JPanel panel = new JPanel(); 
                JLabel label = new JLabel("Contraseña:"); 
                JPasswordField pass = new JPasswordField(10); 
                panel.add(label); 
                panel.add(pass);

                String[] options = new String[]{"OK"};
                int panel_option = JOptionPane.showOptionDialog(null, panel, "Verificación", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if(panel_option == 0){
                    char[] password = pass.getPassword();
                    String mpass = new String(password);
                    if(model.get_password().equals(mpass)){
                        option = "fingerprintRec";
                        setView("fingerprintRec");
                    } else {
                        JOptionPane.showMessageDialog(null, "Contraseña incorrecta.", "Verificación", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
            }
        });
        
        this.viewPanel.btn3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn3.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn3.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "touchTrack";
                setView("touchTrack");
            }
        });
        
        this.viewPanel.btn3Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn3.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn3.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "touchTrack";
                setView("touchTrack");
            }
        });
        
        this.viewPanel.btn4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn4.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn4.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "home";
                setView("home");
            }
        });
        
        this.viewPanel.btn4Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn4.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn4.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "home";
                setView("home");
            }
        });
        
        this.viewPanel.btn5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn5.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn5.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "touchTrack2";
                setView("touchTrack2");
            }
        });
        
        this.viewPanel.btn5Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewPanel.btn5.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewPanel.btn5.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                option = "touchTrack2";
                setView("touchTrack2");
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
    
    public void setView(String option){      
        notifyObservers("{'action': 'setView', 'option': '" + option + "'}");
    }   
}
