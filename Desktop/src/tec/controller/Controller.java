package tec.controller;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.DPFPCapturePriority;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorListener;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.readers.DPFPReaderDescription;
import com.digitalpersona.onetouch.readers.DPFPReadersCollection;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import tec.main.Model;
import tec.styles.ComboBoxItem;
import tec.styles.Styles;
import tec.views.FingerprintRec;
import tec.views.Home;
import tec.views.HomeFingerprintRec;
import tec.views.Login;
import tec.views.NewFingerprintRec;
import tec.views.Panel;
import tec.views.PreLoader;
import tec.views.TouchTrack;
import tec.views.VerifyFingerprintRec;
import tec.controller.Connect;
import tec.views.TouchTrack2;

public class Controller implements ModelObserver{
    //Vars
    private String optionView;
    private JPanel optionJPanel;
    private ArrayList<String> availableReaders = new ArrayList<>();
    private Timer sensorCheckTimer;
    private DPFPCapture capture;
    private static final DPFPVerification verification = DPFPGlobal.getVerificationFactory().createVerification();
    private final String TouchTrack = "";
    
    //Controllers
    private final ControllerLogin controllerLogin;
    private final ControllerPanel controllerPanel;
    private final ControllerFingerprintRec controllerFingerprintRec;
    private final ControllerTouchTrack controllerTouchTrack;
    private final ControllerTouchTrack2 controllerTouchTrack2;
    private final ControllerNewFingerprintRec controllerNewFingerprintRec;
    private final ControllerVerifyFingerprintRec controllerVerifyFingerprintRec;
    
    //Views
    private final Login viewLogin;
    private final Panel viewPanel;
    private Home viewHome;
    private FingerprintRec viewFingerprintRec;
    private TouchTrack viewTouchTrack;
    private TouchTrack2 viewTouchTrack2;
    private HomeFingerprintRec viewHomeFingerprintRec;
    private NewFingerprintRec viewNewFingerprintRec;
    private VerifyFingerprintRec viewVerifyFingerprintRec;
    private Connect connect = new Connect();
    
    //Model
    private final Model model;
    
    private Map<String, StudentFingerprintData> fingerprintCache = new HashMap<>();
    
    public Controller() {
        model = new Model();
        viewLogin = new Login();
        viewPanel = new Panel();
        viewHome = new Home();
        viewFingerprintRec = new FingerprintRec();
        viewTouchTrack = new TouchTrack();
        viewTouchTrack2 = new TouchTrack2();
        viewHomeFingerprintRec = new HomeFingerprintRec();
        viewNewFingerprintRec = new NewFingerprintRec();
        viewVerifyFingerprintRec = new VerifyFingerprintRec();        
        
        controllerLogin = new ControllerLogin(viewLogin);
        controllerLogin.addObserver(this);
        controllerPanel = new ControllerPanel(viewPanel, model);
        controllerPanel.addObserver(this);
        controllerFingerprintRec = new ControllerFingerprintRec(viewFingerprintRec);
        controllerFingerprintRec.addObserver(this);
        controllerTouchTrack = new ControllerTouchTrack(viewTouchTrack);
        controllerTouchTrack.addObserver(this);
        controllerTouchTrack2 = new ControllerTouchTrack2(viewTouchTrack2);
        controllerTouchTrack2.addObserver(this);
        controllerNewFingerprintRec = new ControllerNewFingerprintRec(viewNewFingerprintRec);
        controllerNewFingerprintRec.addObserver(this);
        controllerVerifyFingerprintRec = new ControllerVerifyFingerprintRec(viewVerifyFingerprintRec);
        controllerVerifyFingerprintRec.addObserver(this);
    }
    
    public void start() {
        this.viewLogin.dispose();
        this.viewLogin.setTitle("TouchTrack - V.2.0.0");
        this.viewLogin.setUndecorated(true);
        this.viewLogin.setResizable(false);
        this.viewLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.viewLogin.setSize(1000, 600);
        this.viewLogin.setLocationRelativeTo(null);
        
        Image imageLogo = Styles.imgIconTouchTrack.getImage();
        Image iconSized = imageLogo.getScaledInstance(32, 32, Image.SCALE_SMOOTH);   
        ImageIcon iconFin = new ImageIcon(iconSized);

        this.viewLogin.setIconImage(iconFin.getImage());
        this.viewLogin.setVisible(true);
        
        this.viewNewFingerprintRec.btnFingerCapture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try{
                    ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewNewFingerprintRec.comboReaders.getSelectedItem();
                    if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                        String selectedValue = selectedComboBoxItem.getValue();
                        
                        if(viewNewFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){                    
                            viewNewFingerprintRec.btnFingerCaptureLabel.setText("Detener captura");
                            viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                            viewNewFingerprintRec.fingerprintImg1.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg2.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg3.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg4.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                            getTemplate(viewNewFingerprintRec.statusLabel, selectedValue);
                        } else{
                            viewNewFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                            viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                            setStatusMsg(viewNewFingerprintRec.statusLabel, "Se detuvo la captura de huella.");
                            viewNewFingerprintRec.fingerprintImg1.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg2.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg3.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg4.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                        }  
                    } else {
                        setStatusMsg(viewNewFingerprintRec.statusLabel,"Ningún sensor seleccionado.");
                    }
                } catch(Exception e){
                    setStatusMsg(viewNewFingerprintRec.statusLabel,"Ningún sensor seleccionado.");
                }    
            }
        });
        
        this.viewNewFingerprintRec.btnFingerCaptureLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try{
                    ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewNewFingerprintRec.comboReaders.getSelectedItem();
                    if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                        String selectedValue = selectedComboBoxItem.getValue();
                        
                        if(viewNewFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){                    
                            viewNewFingerprintRec.btnFingerCaptureLabel.setText("Detener captura");
                            viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                            viewNewFingerprintRec.fingerprintImg1.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg2.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg3.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg4.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                            
                            getTemplate(viewNewFingerprintRec.statusLabel, selectedValue);
                        } else{
                            viewNewFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                            viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                            setStatusMsg(viewNewFingerprintRec.statusLabel, "Se detuvo la captura de huella.");
                            viewNewFingerprintRec.fingerprintImg1.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg2.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg3.setIcon(null);
                            viewNewFingerprintRec.fingerprintImg4.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                        }  
                    } else {
                        setStatusMsg(viewNewFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                    }
                } catch(Exception e){
                    setStatusMsg(viewNewFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                }    
            }
        });
        
        this.viewVerifyFingerprintRec.btnFingerCapture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try{
                    ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewVerifyFingerprintRec.comboReaders.getSelectedItem();
                    if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                        String selectedValue = selectedComboBoxItem.getValue();
                        
                        if(viewVerifyFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){                    
                            viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Detener captura");
                            viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                            viewVerifyFingerprintRec.fingerprintImg.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                            verifyFingerprint( selectedValue);
                        } else{
                            viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                            viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Se detuvo la captura de huella.");
                            viewVerifyFingerprintRec.fingerprintImg.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                        }  
                    } else {
                        setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                    }
                } catch(Exception e){
                    setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                }    
            }
        });
        
        this.viewVerifyFingerprintRec.btnFingerCaptureLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try{
                    ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewVerifyFingerprintRec.comboReaders.getSelectedItem();
                    if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                        String selectedValue = selectedComboBoxItem.getValue();
                        
                        if(viewVerifyFingerprintRec.btnFingerCaptureLabel.getText().equals("Capturar")){                    
                            viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Detener captura");
                            viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorDanger);
                            viewVerifyFingerprintRec.fingerprintImg.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                            
                            verifyFingerprint(selectedValue);
                        } else{
                            viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                            viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Se detuvo la captura de huella.");
                            viewVerifyFingerprintRec.fingerprintImg.setIcon(null);
                            if (capture != null && capture.isStarted()){
                                capture.stopCapture();
                            }
                        }  
                    } else {
                        setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                    }
                } catch(Exception e){
                    setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Ningún sensor seleccionado.");
                }    
            }
        });
    } 
    
    public void setView(JPanel panel, String option){
        PreLoader view = new PreLoader();
        
        optionJPanel = panel;
        optionView = option;
        
        view.setLocation(0, 0);
        view.setSize(panel.getWidth(), panel.getHeight());

        panel.removeAll();
        panel.add(view, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
            
        timerPreloader.start();
    }
    
    private Timer timerPreloader = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sensorCheckTimer != null && sensorCheckTimer.isRunning()) {
                sensorCheckTimer.stop();
            }
            
            if (capture != null && capture.isStarted()){
                capture.stopCapture();
            }            
            
            viewNewFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
            viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
            setStatusMsg(viewNewFingerprintRec.statusLabel, "En espera...");
            viewNewFingerprintRec.fingerprintImg1.setIcon(null);
            viewNewFingerprintRec.fingerprintImg2.setIcon(null);
            viewNewFingerprintRec.fingerprintImg3.setIcon(null);
            viewNewFingerprintRec.fingerprintImg4.setIcon(null);
            
            viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
            viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "En espera...");
            viewVerifyFingerprintRec.fingerprintImg.setIcon(null);
            viewVerifyFingerprintRec.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorPrimary, 2));
            viewVerifyFingerprintRec.fingerprintLabel.setText("Sin resultados.");
            viewVerifyFingerprintRec.dateLabel.setText("Sin resultados.");
            viewVerifyFingerprintRec.verificationLabel.setText("Sin resultados.");
            
            
            JPanel view = new JPanel();
            switch (optionView) {
                case "home" -> view = viewHome;
                case "fingerprintRec" -> {
                    view = viewFingerprintRec;             
                }
                case "touchTrack" -> {
                    view = viewTouchTrack;
                    setReaders(viewTouchTrack.comboReaders);          
                    sensorCheckTimer = new Timer(3000, (ActionEvent me) -> {
                        checkAvailableReaders(viewTouchTrack.comboReaders);
                    });                      
                    sensorCheckTimer.start();
                }
                case "touchTrack2" -> {
                    view = viewTouchTrack2;
                    setReaders(viewTouchTrack2.comboReaders);          
                    sensorCheckTimer = new Timer(3000, (ActionEvent me) -> {
                        checkAvailableReaders(viewTouchTrack2.comboReaders);
                    });                      
                    sensorCheckTimer.start();
                }
                case "homeFingerprintRec" -> view = viewHomeFingerprintRec;
                case "newFingerprintRec" -> {
                    view = viewNewFingerprintRec;
                    setReaders(viewNewFingerprintRec.comboReaders);          
                    sensorCheckTimer = new Timer(3000, (ActionEvent me) -> {
                        checkAvailableReaders(viewNewFingerprintRec.comboReaders);
                    });                      
                    sensorCheckTimer.start();                    
                }
                case "verifyFingerprintRec" -> {
                    view = viewVerifyFingerprintRec;
                    setReaders(viewVerifyFingerprintRec.comboReaders);          
                    sensorCheckTimer = new Timer(3000, (ActionEvent me) -> {
                        checkAvailableReaders(viewVerifyFingerprintRec.comboReaders);
                    });                      
                    sensorCheckTimer.start();                
                }
                default -> {
                }
            }

            view.setLocation(0, 0);
            view.setSize(optionJPanel.getWidth(), optionJPanel.getHeight());

            optionJPanel.removeAll();
            optionJPanel.add(view, BorderLayout.CENTER);
            optionJPanel.revalidate();
            optionJPanel.repaint();
            timerPreloader.stop();
            if(optionView.equals("fingerprintRec")){
                setView(viewFingerprintRec.contentPanel, "homeFingerprintRec");
            } else if(optionView.equals("touchTrack")){                
                ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewTouchTrack.comboReaders.getSelectedItem();
                if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                    String selectedValue = selectedComboBoxItem.getValue();

                    verifyFingerprintStudents(selectedValue);
                } else {
                    setStatusMsg(viewTouchTrack.statusLabel, "Ningún sensor seleccionado.");
                }                 
            } else if(optionView.equals("touchTrack2")){                
                ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewTouchTrack2.comboReaders.getSelectedItem();
                if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                    String selectedValue = selectedComboBoxItem.getValue();

                    verifyFingerprintStudents2(selectedValue);
                } else {
                    setStatusMsg(viewTouchTrack2.statusLabel, "Ningún sensor seleccionado.");
                }                 
            }
        }
    });
    
    private Timer timerSensorStudent = new Timer(2400, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewTouchTrack.noControlTextField.setText("");
            viewTouchTrack.fullnameTextField.setText("");
            viewTouchTrack.careerTextField.setText("");
            viewTouchTrack.groupTextField.setText("");
            viewTouchTrack.semesterTextField.setText("");
            viewTouchTrack.imgStudent.setIcon(null);
            viewTouchTrack.fingerprintImg.setIcon(null);
            viewTouchTrack.imgLoad.setIcon(Styles.imgLoader);
            viewTouchTrack.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorPrimary, 2));
            viewTouchTrack.statusLabel.setForeground(Color.BLACK);
            
            ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewTouchTrack.comboReaders.getSelectedItem();
            if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                String selectedValue = selectedComboBoxItem.getValue();

                verifyFingerprintStudents(selectedValue);
            } else {
                setStatusMsg(viewTouchTrack.statusLabel, "Ningún sensor seleccionado.");
            }
            timerSensorStudent.stop();
        }
    });
    
    private Timer timerSensorStudent2 = new Timer(2400, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewTouchTrack2.noControlTextField.setText("");
            viewTouchTrack2.fullnameTextField.setText("");
            viewTouchTrack2.careerTextField.setText("");
            viewTouchTrack2.groupTextField.setText("");
            viewTouchTrack2.semesterTextField.setText("");
            viewTouchTrack2.imgStudent.setIcon(null);
            viewTouchTrack2.fingerprintImg.setIcon(null);
            viewTouchTrack2.imgLoad.setIcon(Styles.imgLoader);
            viewTouchTrack2.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorPrimary, 2));
            viewTouchTrack2.statusLabel.setForeground(Color.BLACK);
            
            ComboBoxItem selectedComboBoxItem = (ComboBoxItem) viewTouchTrack2.comboReaders.getSelectedItem();
            if (selectedComboBoxItem != null && selectedComboBoxItem.getValue() != null) {            
                String selectedValue = selectedComboBoxItem.getValue();

                verifyFingerprintStudents2(selectedValue);
            } else {
                setStatusMsg(viewTouchTrack2.statusLabel, "Ningún sensor seleccionado.");
            }
            timerSensorStudent2.stop();
        }
    });
    
    private void setReaders(JComboBox comboBox){
        DPFPReadersCollection readers = DPFPGlobal.getReadersFactory().getReaders();
        comboBox.removeAllItems();
        DefaultComboBoxModel<ComboBoxItem> comboBoxModel = new DefaultComboBoxModel<>();
        if (readers == null || readers.isEmpty()) {
            comboBoxModel.addElement(new ComboBoxItem(null, "No se encontró ningún sensor."));
        } else {
            for (DPFPReaderDescription readerDescription : readers) {
                String serialNumber = readerDescription.getSerialNumber();           
                comboBoxModel.addElement(new ComboBoxItem(serialNumber, readerDescription.getProductName()));
            }
        }
        comboBox.setModel(comboBoxModel);
    }
    
    private void checkAvailableReaders(JComboBox comboBox) {
        DPFPReadersCollection readers = DPFPGlobal.getReadersFactory().getReaders();
        ArrayList<String> currentReaders = new ArrayList<>();

        if (readers != null && !readers.isEmpty()) {
            for (DPFPReaderDescription readerDescription : readers) {
                String serialNumber = readerDescription.getSerialNumber();           
                currentReaders.add(serialNumber);
            }
        }

        boolean sensorsChanged = !currentReaders.equals(availableReaders);        
        availableReaders = currentReaders;

        if (sensorsChanged) {
            setReaders(comboBox);
        }
    }
    
    public void setStatusMsg(JLabel label, String msg) {
        SwingUtilities.invokeLater(() -> label.setText(msg));
    }
    
    public DPFPSample verifyFingerprint(String activeReader) {
        setStatusMsg(viewVerifyFingerprintRec.statusLabel, "Realizando verificacion de huellas dactilares...");

        SwingWorker<DPFPSample, Void> worker = new SwingWorker<DPFPSample, Void>() {
            @Override
            protected DPFPSample doInBackground() throws Exception {
                DPFPSample sample = null;
                try {
                    sample = getSample(viewVerifyFingerprintRec.statusLabel, activeReader, "Coloque su dedo...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return sample;
            }

            @Override
            protected void done() {
                try {
                    Styles.playSoundFromPackage("Scan.wav");
                    DPFPSample sampleFinaly = get();
                    if (sampleFinaly != null) {                       
                        viewVerifyFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                        viewVerifyFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                        displayFingerprintImage(viewVerifyFingerprintRec.fingerprintImg, sampleFinaly);
                        
                        String jsonResponse = fingerprints_student();
                        if (jsonResponse == null){
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "La solicitud no se completo. Intentelo de nuevo.");
                            return;
                        }
                        
                        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                        Boolean success = responseObject.get("success").getAsBoolean();
                        String msg = responseObject.get("msg").getAsString();                        
                        
                        if (!success){
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, msg);
                            return;
                        }
                        
                        boolean verification = false;
                        String fingerprint_id = "";
                        String fingerprint_date = "";
                        JsonArray fingerprints = responseObject.get("data").getAsJsonArray();
                        for (int i = 0; i < fingerprints.size(); i++) {
                            JsonObject fingerprintObject = fingerprints.get(i).getAsJsonObject();
                            
                            fingerprint_id = fingerprintObject.get("_id").getAsString();
                            String fingerprint = fingerprintObject.get("fingerprint").getAsString();
                            fingerprint_date = fingerprintObject.get("regdate").getAsString();
                            
                            byte[] templateBytes = Encrypt.decrypt(fingerprint, TouchTrack);
                            DPFPTemplate storedTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBytes);

                            verification = verifyFingerprintFinaly(sampleFinaly, storedTemplate);
                            if(verification){
                               break; 
                            } 
                        }
                        
                        if(verification){
                            Styles.playSoundFromPackage("Success.wav");
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "La huella es valida!");                            
                            setStatusMsg(viewVerifyFingerprintRec.fingerprintLabel, fingerprint_id);
                            setStatusMsg(viewVerifyFingerprintRec.dateLabel, fingerprint_date);
                            setStatusMsg(viewVerifyFingerprintRec.verificationLabel, "Huella valida.");
                            viewVerifyFingerprintRec.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                        } else {
                            Styles.playSoundFromPackage("Incorrect.wav");
                            setStatusMsg(viewVerifyFingerprintRec.statusLabel, "La huella no es valida!");
                            setStatusMsg(viewVerifyFingerprintRec.fingerprintLabel, "Sin resultados.");
                            setStatusMsg(viewVerifyFingerprintRec.dateLabel, "Sin resultados.");
                            setStatusMsg(viewVerifyFingerprintRec.verificationLabel, "Sin resultados.");                            
                            viewVerifyFingerprintRec.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorDanger, 2));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
        return null;
    }
    
    public DPFPSample verifyFingerprintStudents(String activeReader) {
        setStatusMsg(viewTouchTrack.statusLabel, "Realizando verificacion de huellas dactilares...");

        SwingWorker<DPFPSample, Void> worker = new SwingWorker<DPFPSample, Void>() {
            @Override
            protected DPFPSample doInBackground() throws Exception {
                DPFPSample sample = null;
                try {
                    sample = getSample(viewTouchTrack.statusLabel, activeReader, "Coloque su dedo...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return sample;
            }

            @Override
            protected void done() {
                try {
                    Styles.playSoundFromPackage("Scan.wav");
                    DPFPSample sampleFinaly = get();
                    if (sampleFinaly != null) {                       
                        displayFingerprintImage(viewTouchTrack.fingerprintImg, sampleFinaly);
                        
                        String jsonResponse = fingerprints_students();
                        if (jsonResponse == null){
                            setStatusMsg(viewTouchTrack.statusLabel, "La solicitud no se completo. Intentelo de nuevo.");
                            timerSensorStudent.start();
                            return;
                        }
                        
                        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                        Boolean success = responseObject.get("success").getAsBoolean();
                        String msg = responseObject.get("msg").getAsString();                        
                        
                        if (!success){
                            setStatusMsg(viewTouchTrack.statusLabel, msg);
                            timerSensorStudent.start();
                            return;
                        }
                        
                        boolean verification = false;
                        String finger_id = "";
                        String student_id = "";
                        String student_name = "";
                        String student_career = "";
                        String student_group = "";
                        String student_semester = "";
                        String imgStudentURL = "";
                        JsonObject studentObject = null;
                        
                        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                        List<Future<String>> verificationResults = new ArrayList<>();
                        
                        JsonArray fingerprints = responseObject.get("data").getAsJsonArray();
                        setStatusMsg(viewTouchTrack.statusLabel, "Buscando coincidencias...");
                        for (int i = 0; i < fingerprints.size(); i++) {
                            JsonObject fingerprintObject = fingerprints.get(i).getAsJsonObject();
                            studentObject = fingerprintObject.get("student").getAsJsonObject();
                            finger_id = fingerprintObject.get("_id").getAsString();                            
                            student_id = studentObject.get("_id").getAsString();

                            if (fingerprintCache.containsKey(finger_id)) {
                                final String f_finger_id = finger_id;
                                Future<String> result = executor.submit(() -> {
                                    StudentFingerprintData cachedData = fingerprintCache.get(f_finger_id);
                                    boolean verfify = verifyFingerprintFinaly(sampleFinaly, cachedData.getFingerprintTemplate());
                                    if(verfify){
                                        return f_finger_id;
                                    }
                                    return null;
                                });
                                verificationResults.add(result);
                            } else {
                                final String f_finger_id = finger_id;
                                final String f_student_id = studentObject.get("_id").getAsString();
                                final String f_student_name = studentObject.get("fullname").getAsString();
                                final String f_imgStudentURL = studentObject.get("img").getAsString();
                                JsonObject careerObject = studentObject.get("career").getAsJsonObject();
                                final String f_student_career = careerObject.get("name").getAsString(); 

                                JsonObject groupObject = studentObject.get("group").getAsJsonObject();
                                final String f_student_group = groupObject.get("name").getAsString(); 

                                JsonObject semesterObject = studentObject.get("semester").getAsJsonObject();
                                final String f_student_semester = semesterObject.get("name").getAsString(); 
                                
                                Future<String> result = executor.submit(() -> {                                   
                                    String fingerprint = fingerprintObject.get("fingerprint").getAsString();
                                    byte[] templateBytes = Encrypt.decrypt(fingerprint, TouchTrack);
                                    DPFPTemplate storedTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBytes);

                                    StudentFingerprintData data = new StudentFingerprintData(storedTemplate, f_student_id, f_student_name, f_imgStudentURL, f_student_career, f_student_group, f_student_semester);
                                    fingerprintCache.put(f_finger_id, data);

                                    boolean verfify = verifyFingerprintFinaly(sampleFinaly, storedTemplate);
                                    if(verfify){
                                        return f_finger_id;
                                    }
                                    return null;
                                });
                                verificationResults.add(result);
                            }
                        }
                        
                        for (Future<String> result : verificationResults) {
                            if (result.get() != null) {
                                StudentFingerprintData cachedData = fingerprintCache.get(result.get());
                                student_id = cachedData.getStudentID();
                                student_name = cachedData.getStudentName();
                                imgStudentURL = cachedData.getImgURL();
                                student_career = cachedData.getCareer();
                                student_group = cachedData.getGroup();
                                student_semester = cachedData.getSemester(); 
                                verification = true;
                                break;
                            }
                        }
                        executor.shutdown();
                        
                        if(verification){                            
                            saveAttendance(student_id, student_name, student_career, student_group, student_semester, imgStudentURL);
                        } else {
                            Styles.playSoundFromPackage("Incorrect.wav");
                            setStatusMsg(viewTouchTrack.statusLabel, "La huella no es valida!");
                            viewTouchTrack.statusLabel.setForeground(Styles.colorDanger);
                            viewTouchTrack.imgLoad.setIcon(Styles.imginCorrect);
                            viewTouchTrack.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorDanger, 2));
                            viewTouchTrack.noControlTextField.setText("");
                            viewTouchTrack.fullnameTextField.setText("");
                            viewTouchTrack.careerTextField.setText("");
                            viewTouchTrack.groupTextField.setText("");
                            viewTouchTrack.semesterTextField.setText("");
                            viewTouchTrack.imgStudent.setIcon(null);
                        }
                        
                        timerSensorStudent.start();
                        return;
                    }
                    setStatusMsg(viewTouchTrack.statusLabel, "La huella no se pudo leer correctamente!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                timerSensorStudent.start();
            }
        };

        worker.execute();
        return null;
    }
    
    public DPFPSample verifyFingerprintStudents2(String activeReader) {
        setStatusMsg(viewTouchTrack2.statusLabel, "Realizando verificacion de huellas dactilares...");

        SwingWorker<DPFPSample, Void> worker = new SwingWorker<DPFPSample, Void>() {
            @Override
            protected DPFPSample doInBackground() throws Exception {
                DPFPSample sample = null;
                try {
                    sample = getSample(viewTouchTrack2.statusLabel, activeReader, "Coloque su dedo...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return sample;
            }

            @Override
            protected void done() {
                try {
                    Styles.playSoundFromPackage("Scan.wav");
                    DPFPSample sampleFinaly = get();
                    if (sampleFinaly != null) {                       
                        displayFingerprintImage(viewTouchTrack2.fingerprintImg, sampleFinaly);
                        
                        String jsonResponse = fingerprints_students();
                        if (jsonResponse == null){
                            setStatusMsg(viewTouchTrack2.statusLabel, "La solicitud no se completo. Intentelo de nuevo.");
                            timerSensorStudent2.start();
                            return;
                        }
                        
                        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                        Boolean success = responseObject.get("success").getAsBoolean();
                        String msg = responseObject.get("msg").getAsString();                        
                        
                        if (!success){
                            setStatusMsg(viewTouchTrack2.statusLabel, msg);
                            timerSensorStudent2.start();
                            return;
                        }
                        
                        boolean verification = false;
                        String finger_id = "";
                        String student_id = "";
                        String student_name = "";
                        String student_career = "";
                        String student_group = "";
                        String student_semester = "";
                        String imgStudentURL = "";
                        JsonObject studentObject = null;
                        
                        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                        List<Future<String>> verificationResults = new ArrayList<>();
                        
                        JsonArray fingerprints = responseObject.get("data").getAsJsonArray();
                        setStatusMsg(viewTouchTrack2.statusLabel, "Buscando coincidencias...");
                        for (int i = 0; i < fingerprints.size(); i++) {
                            JsonObject fingerprintObject = fingerprints.get(i).getAsJsonObject();
                            studentObject = fingerprintObject.get("student").getAsJsonObject();
                            finger_id = fingerprintObject.get("_id").getAsString();                            
                            student_id = studentObject.get("_id").getAsString();

                            if (fingerprintCache.containsKey(finger_id)) {
                                final String f_finger_id = finger_id;
                                Future<String> result = executor.submit(() -> {
                                    StudentFingerprintData cachedData = fingerprintCache.get(f_finger_id);
                                    boolean verfify = verifyFingerprintFinaly(sampleFinaly, cachedData.getFingerprintTemplate());
                                    if(verfify){
                                        return f_finger_id;
                                    }
                                    return null;
                                });
                                verificationResults.add(result);
                            } else {
                                final String f_finger_id = finger_id;
                                final String f_student_id = studentObject.get("_id").getAsString();
                                final String f_student_name = studentObject.get("fullname").getAsString();
                                final String f_imgStudentURL = studentObject.get("img").getAsString();
                                JsonObject careerObject = studentObject.get("career").getAsJsonObject();
                                final String f_student_career = careerObject.get("name").getAsString(); 

                                JsonObject groupObject = studentObject.get("group").getAsJsonObject();
                                final String f_student_group = groupObject.get("name").getAsString(); 

                                JsonObject semesterObject = studentObject.get("semester").getAsJsonObject();
                                final String f_student_semester = semesterObject.get("name").getAsString(); 
                                
                                Future<String> result = executor.submit(() -> {                                   
                                    String fingerprint = fingerprintObject.get("fingerprint").getAsString();
                                    byte[] templateBytes = Encrypt.decrypt(fingerprint, TouchTrack);
                                    DPFPTemplate storedTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBytes);

                                    StudentFingerprintData data = new StudentFingerprintData(storedTemplate, f_student_id, f_student_name, f_imgStudentURL, f_student_career, f_student_group, f_student_semester);
                                    fingerprintCache.put(f_finger_id, data);

                                    boolean verfify = verifyFingerprintFinaly(sampleFinaly, storedTemplate);
                                    if(verfify){
                                        return f_finger_id;
                                    }
                                    return null;
                                });
                                verificationResults.add(result);
                            }
                        }
                        
                        for (Future<String> result : verificationResults) {
                            if (result.get() != null) {                                
                                StudentFingerprintData cachedData = fingerprintCache.get(result.get());
                                student_id = cachedData.getStudentID();
                                student_name = cachedData.getStudentName();
                                imgStudentURL = cachedData.getImgURL();
                                student_career = cachedData.getCareer();
                                student_group = cachedData.getGroup();
                                student_semester = cachedData.getSemester(); 
                                verification = true;
                                break;
                            }
                        }

                        executor.shutdown();
                        
                        if(verification){                            
                            saveAttendance2(student_id, student_name, student_career, student_group, student_semester, imgStudentURL);
                        } else {
                            Styles.playSoundFromPackage("Incorrect.wav");
                            setStatusMsg(viewTouchTrack2.statusLabel, "La huella no es valida!");
                            viewTouchTrack2.imgLoad.setIcon(Styles.imginCorrect);
                            viewTouchTrack2.statusLabel.setForeground(Styles.colorDanger);
                            viewTouchTrack2.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorDanger, 2));
                            viewTouchTrack2.noControlTextField.setText("");
                            viewTouchTrack2.fullnameTextField.setText("");
                            viewTouchTrack2.careerTextField.setText("");
                            viewTouchTrack2.groupTextField.setText("");
                            viewTouchTrack2.semesterTextField.setText("");
                            viewTouchTrack2.imgStudent.setIcon(null);
                        }
                       
                        
                        timerSensorStudent2.start();
                        return;
                    }
                    setStatusMsg(viewTouchTrack2.statusLabel, "La huella no se pudo leer correctamente!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                timerSensorStudent2.start();
            }
        };

        worker.execute();
        return null;
    }
    
    public void loadFingerprintsCache(){
        String jsonResponse = fingerprints_students();
        if (jsonResponse == null){
            return;
        }

        JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
        Boolean success = responseObject.get("success").getAsBoolean();

        if (!success){
            return;
        }

        String finger_id = "";
        JsonObject studentObject = null;

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> verificationResults = new ArrayList<>();

        JsonArray fingerprints = responseObject.get("data").getAsJsonArray();
        for (int i = 0; i < fingerprints.size(); i++) {
            JsonObject fingerprintObject = fingerprints.get(i).getAsJsonObject();
            studentObject = fingerprintObject.get("student").getAsJsonObject();
            finger_id = fingerprintObject.get("_id").getAsString();                            

            if (!fingerprintCache.containsKey(finger_id)) {
                final String f_finger_id = finger_id;
                final String f_student_id = studentObject.get("_id").getAsString();
                final String f_student_name = studentObject.get("fullname").getAsString();
                final String f_imgStudentURL = studentObject.get("img").getAsString();
                JsonObject careerObject = studentObject.get("career").getAsJsonObject();
                final String f_student_career = careerObject.get("name").getAsString(); 

                JsonObject groupObject = studentObject.get("group").getAsJsonObject();
                final String f_student_group = groupObject.get("name").getAsString(); 

                JsonObject semesterObject = studentObject.get("semester").getAsJsonObject();
                final String f_student_semester = semesterObject.get("name").getAsString(); 

                Future<String> result = executor.submit(() -> {                                   
                    String fingerprint = fingerprintObject.get("fingerprint").getAsString();
                    byte[] templateBytes = Encrypt.decrypt(fingerprint, TouchTrack);
                    DPFPTemplate storedTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBytes);
                    
                    StudentFingerprintData data = new StudentFingerprintData(storedTemplate, f_student_id, f_student_name, f_imgStudentURL, f_student_career, f_student_group, f_student_semester);
                    fingerprintCache.put(f_finger_id, data);
                    return null;
                });
                verificationResults.add(result);
            }
        }
        
        executor.shutdown();
    }
    
    public static boolean verifyFingerprintFinaly(DPFPSample sample, DPFPTemplate storedTemplate) {
        DPFPFeatureSet features = Extraction.extract(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        if (features != null) {
            DPFPVerificationResult result = verification.verify(features, storedTemplate);
            return result.isVerified();
        } else {
            return false;
        }
    }
    
    public DPFPTemplate getTemplate(JLabel label, String activeReader) {
        setStatusMsg(label, "Realizando registro de huellas dactilares...");

        SwingWorker<DPFPTemplate, Void> worker = new SwingWorker<DPFPTemplate, Void>() {
            @Override
            protected DPFPTemplate doInBackground() throws Exception {
                DPFPTemplate template = null;
                try {
                    DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
                    DPFPEnrollment enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();

                    int enrollmentsLeft = 4;

                    while (enrollmentsLeft > 0) {
                        String prompt = String.format("Escaneando tu dedo (%d restante)...\n", enrollmentsLeft);
                        DPFPSample sample = getSample(label, activeReader, prompt);
                        if (sample == null) {
                            continue;
                        }
                        
                        Styles.playSoundFromPackage("Scan.wav");
                        DPFPFeatureSet featureSet;
                        try {
                            featureSet = featureExtractor.createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                        } catch (DPFPImageQualityException e) {
                            setStatusMsg(label, "Mala calidad de imagen... Inténtalo de nuevo.");
                            continue;
                        }

                        enrollment.addFeatures(featureSet);

                        switch (enrollmentsLeft) {
                            case 4 -> displayFingerprintImage(viewNewFingerprintRec.fingerprintImg1, sample);
                            case 3 -> displayFingerprintImage(viewNewFingerprintRec.fingerprintImg2, sample);
                            case 2 -> displayFingerprintImage(viewNewFingerprintRec.fingerprintImg3, sample);
                            case 1 -> displayFingerprintImage(viewNewFingerprintRec.fingerprintImg4, sample);
                            default -> {
                            }
                        }

                        enrollmentsLeft--;
                    }

                    template = enrollment.getTemplate();                    
                } catch (DPFPImageQualityException e) {
                    setStatusMsg(label, "No se pudo registrar el dedo!");
                }

                return template;
            }

            @Override
            protected void done() {
                try {
                    DPFPTemplate templateFinaly = get();
                    if (templateFinaly != null) {
                        setStatusMsg(label, "El registro de huella fue exitoso!");
                        viewNewFingerprintRec.btnFingerCaptureLabel.setText("Capturar");
                        viewNewFingerprintRec.btnFingerCapture.setBackground(Styles.colorPrimary);
                        
                        byte[] fingerPrint = templateFinaly.serialize();
                        String encrypt = Encrypt.encrypt(fingerPrint, TouchTrack);
                        saveFingerprint(encrypt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
        return null;
    }
    
    public DPFPSample getSample(JLabel label, String activeReader, String prompt) throws InterruptedException { 
        final LinkedBlockingQueue<DPFPSample> samples = new LinkedBlockingQueue<DPFPSample>();
        capture = DPFPGlobal.getCaptureFactory().createCapture();
        
        capture.setReaderSerialNumber(activeReader);        
        capture.setPriority(DPFPCapturePriority.CAPTURE_PRIORITY_LOW);
        
        capture.addDataListener((DPFPDataEvent e) -> {
            if (e != null && e.getSample() != null) {
                try {
                    samples.put(e.getSample());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } 
        }); 
        
        capture.addReaderStatusListener(new DPFPReaderStatusAdapter(){ 
            int lastStatus = DPFPReaderStatusEvent.READER_CONNECTED;
            @Override
            public void readerConnected(DPFPReaderStatusEvent e) {
                    if (lastStatus != e.getReaderStatus())
                        setStatusMsg(label, "Lector conectado, continue...");
                    lastStatus = e.getReaderStatus();
            } 
            
            @Override
            public void readerDisconnected(DPFPReaderStatusEvent e) {
                    if (lastStatus != e.getReaderStatus())
                        setStatusMsg(label, "El lector fue desconectado!");
                    lastStatus = e.getReaderStatus();
            } 
        });        
        
        try { 
            capture.startCapture();
            setStatusMsg(label, prompt);
            return samples.take();
        } catch (RuntimeException e) {
            setStatusMsg(label, "<html><p>No se pudo iniciar la captura. Compruebe que el lector no esté siendo utilizado por otra aplicación.</p></html>");
            throw e;
        } finally { 
            capture.stopCapture();
        } 
    } 
    
    private void displayFingerprintImage(JLabel label, DPFPSample sample) {   
        Image image = DPFPGlobal.getSampleConversionFactory().createImage(sample);
        label.setIcon(new ImageIcon(image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT)));
    }
    
    private void saveFingerprint(String fingerprint){
        try{
            URL url = new URL(connect.get_ip() + "/api/desktop/data/student");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonRequest = new JsonObject();                    
            
            jsonRequest.addProperty("action", "save_fingerprint");
            jsonRequest.addProperty("student_id", model.get_student_id());
            jsonRequest.addProperty("fingerprint", fingerprint);

            String postData = jsonRequest.toString();

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                Boolean success = responseObject.get("success").getAsBoolean();
                String msg = responseObject.get("msg").getAsString();

                if(!success){                        
                    controllerFingerprintRec.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", msg);
                    connection.disconnect();
                    return;
                }

                controllerFingerprintRec.alertPanel(Styles.colorPrimary, "¡Exito!", msg);
                connection.disconnect();
            } else {
                controllerFingerprintRec.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "La solicitud no se completo. Intentelo de nuevo.");
                connection.disconnect();
            }                
        } catch (IOException error) {
            controllerFingerprintRec.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "No se pudo conectar al servidor.");
        } catch (Exception error) {
            controllerFingerprintRec.alertPanel(Styles.colorDanger, "¡Error fatal!", "Contacte con algun administrador.");
        }
    }
    
    private void saveAttendance(String student_id, String student_name, String student_career, String student_group, String student_semester, String imgStudentURL){
        try{
            URL url = new URL(connect.get_ip() + "/api/desktop/data/student");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonRequest = new JsonObject();                    
            
            jsonRequest.addProperty("action", "save_attendance");
            jsonRequest.addProperty("student_id", student_id);

            String postData = jsonRequest.toString();

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                Boolean success = responseObject.get("success").getAsBoolean();
                String msg = responseObject.get("msg").getAsString();

                if(!success){                        
                    controllerTouchTrack.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", msg);
                    setStatusMsg(viewTouchTrack.statusLabel, msg);
                    viewTouchTrack.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorDanger, 2));
                    viewTouchTrack.noControlTextField.setText("");
                    viewTouchTrack.fullnameTextField.setText("");
                    viewTouchTrack.careerTextField.setText("");
                    viewTouchTrack.groupTextField.setText("");
                    viewTouchTrack.semesterTextField.setText("");
                    viewTouchTrack.imgStudent.setIcon(null);
                    connection.disconnect();
                    return;
                }

                controllerTouchTrack.alertPanel(Styles.colorPrimary, "¡Exito!", msg);
                Styles.playSoundFromPackage("Success.wav");
                setStatusMsg(viewTouchTrack.statusLabel, "La huella es valida!");
                viewTouchTrack.statusLabel.setForeground(Color.GREEN);
                viewTouchTrack.imgLoad.setIcon(Styles.imgCorrect);
                viewTouchTrack.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                viewTouchTrack.noControlTextField.setText(student_id);
                viewTouchTrack.fullnameTextField.setText(student_name.toUpperCase());
                viewTouchTrack.careerTextField.setText(student_career.toUpperCase());
                viewTouchTrack.groupTextField.setText(student_group.toUpperCase());
                viewTouchTrack.semesterTextField.setText(student_semester.toUpperCase());
                URL urlImage = new URL(connect.get_ip() + "/" + imgStudentURL);

                ImageIcon imageStudent = new ImageIcon(urlImage);
                Image imageIcon = imageStudent.getImage();
                Image iconSized = imageIcon.getScaledInstance(130, 180, Image.SCALE_SMOOTH);   
                ImageIcon imageFin = new ImageIcon(iconSized);

                viewTouchTrack.imgStudent.setIcon(imageFin);
                connection.disconnect();
            } else {
                controllerTouchTrack.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "La solicitud no se completo. Intentelo de nuevo.");
                connection.disconnect();
            }                
        } catch (IOException error) {
            controllerTouchTrack.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "No se pudo conectar al servidor.");
        } catch (Exception error) {
            controllerTouchTrack.alertPanel(Styles.colorDanger, "¡Error fatal!", "Contacte con algun administrador.");
        }
    }
    
    private void saveAttendance2(String student_id, String student_name, String student_career, String student_group, String student_semester, String imgStudentURL){
        try{
            URL url = new URL(connect.get_ip() + "/api/desktop/data/student");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonRequest = new JsonObject();                    
            
            jsonRequest.addProperty("action", "save_attendance_individual");
            jsonRequest.addProperty("student_id", student_id);

            String postData = jsonRequest.toString();

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                Boolean success = responseObject.get("success").getAsBoolean();
                String msg = responseObject.get("msg").getAsString();

                if(!success){                        
                    controllerTouchTrack2.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", msg);
                    setStatusMsg(viewTouchTrack2.statusLabel, msg);
                    viewTouchTrack2.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Styles.colorDanger, 2));
                    viewTouchTrack2.noControlTextField.setText("");
                    viewTouchTrack2.fullnameTextField.setText("");
                    viewTouchTrack2.careerTextField.setText("");
                    viewTouchTrack2.groupTextField.setText("");
                    viewTouchTrack2.semesterTextField.setText("");
                    viewTouchTrack2.imgStudent.setIcon(null);
                    connection.disconnect();
                    return;
                }
                
                controllerTouchTrack2.alertPanel(Styles.colorPrimary, "¡Exito!", msg);
                Styles.playSoundFromPackage("Success.wav");
                setStatusMsg(viewTouchTrack2.statusLabel, "La huella es valida!");
                viewTouchTrack2.statusLabel.setForeground(Color.GREEN);
                viewTouchTrack2.imgLoad.setIcon(Styles.imgCorrect);
                viewTouchTrack2.fingerprintPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                viewTouchTrack2.noControlTextField.setText(student_id);
                viewTouchTrack2.fullnameTextField.setText(student_name.toUpperCase());
                viewTouchTrack2.careerTextField.setText(student_career.toUpperCase());
                viewTouchTrack2.groupTextField.setText(student_group.toUpperCase());
                viewTouchTrack2.semesterTextField.setText(student_semester.toUpperCase());
                URL urlImage = new URL(connect.get_ip() + "/" + imgStudentURL);

                ImageIcon imageStudent = new ImageIcon(urlImage);
                Image imageIcon = imageStudent.getImage();
                Image iconSized = imageIcon.getScaledInstance(130, 180, Image.SCALE_SMOOTH);   
                ImageIcon imageFin = new ImageIcon(iconSized);

                viewTouchTrack2.imgStudent.setIcon(imageFin);
                connection.disconnect();
            } else {
                controllerTouchTrack2.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "La solicitud no se completo. Intentelo de nuevo.");
                connection.disconnect();
            }                
        } catch (IOException error) {
            controllerTouchTrack2.alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "No se pudo conectar al servidor.");
        } catch (Exception error) {
            controllerTouchTrack2.alertPanel(Styles.colorDanger, "¡Error fatal!", "Contacte con algun administrador.");
        }
    }
    
    private String fingerprints_student(){
        try{
            URL url = new URL(connect.get_ip() + "/api/desktop/data/student");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonRequest = new JsonObject();                    
            
            jsonRequest.addProperty("action", "get_fingerprints");
            jsonRequest.addProperty("student_id", model.get_student_id());

            String postData = jsonRequest.toString();

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                connection.disconnect();
                return jsonResponse;
            } else {
                connection.disconnect();
                return null;
            }                
        } catch (IOException error) {
            return null;
        } catch (Exception error) {
            return null;
        }
    }   
    
    private String fingerprints_students(){
        try{
            URL url = new URL(connect.get_ip() + "/api/desktop/data/students");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonRequest = new JsonObject();                    
            
            jsonRequest.addProperty("action", "get_fingerprints");

            String postData = jsonRequest.toString();

            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                connection.disconnect();
                return jsonResponse;
            } else {
                connection.disconnect();
                return null;
            }                
        } catch (IOException error) {
            return null;
        } catch (Exception error) {
            return null;
        }
    }   
    
    @Override
    public void update(String jsonResponse) {
        JsonObject responseObject =  new JsonParser().parse(jsonResponse).getAsJsonObject();
        String action = responseObject.get("action").getAsString();
        switch (action) {
            case "openPanel" -> {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                int screenWidth = (int) screenSize.getWidth();
                int screenHeight = (int) screenSize.getHeight();
                this.viewPanel.dispose();
                this.viewPanel.setTitle("TouchTrack - V.2.0.0");
                this.viewPanel.setUndecorated(true);
                this.viewPanel.setResizable(false);
                this.viewPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.viewPanel.setSize(screenWidth - 400, screenHeight - 200);
                this.viewPanel.setLocationRelativeTo(null);
                Image imageLogo = Styles.imgIconTouchTrack.getImage();
                Image iconSized = imageLogo.getScaledInstance(32, 32, Image.SCALE_SMOOTH);   
                ImageIcon iconFin = new ImageIcon(iconSized);

                this.viewPanel.setIconImage(iconFin.getImage());
                this. viewPanel.setExtendedState(JFrame.MAXIMIZED_BOTH);
                this.viewPanel.setVisible(true);
                loadFingerprintsCache();
                setView(viewPanel.contentPanel, "touchTrack2");
            }
            case "setView" -> {
                String option = responseObject.get("option").getAsString();
                setView(viewPanel.contentPanel, option);
            }
            case "setViewFingerprintRec" -> {
                String option = responseObject.get("option").getAsString();
                setView(viewFingerprintRec.contentPanel, option);
            }
            case "set_student_id" -> {
                String student_id = responseObject.get("student_id").getAsString();
                model.set_student_id(student_id);
            }
            case "saveUser" -> {
                String user = responseObject.get("user").getAsString();
                String password = responseObject.get("password").getAsString();
                model.set_password(password);
            }
            default -> {
            }
        }
    }
}
