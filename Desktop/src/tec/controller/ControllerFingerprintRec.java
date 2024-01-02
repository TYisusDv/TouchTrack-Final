package tec.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import tec.styles.Styles;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import tec.views.FingerprintRec;
import tec.views.Panel;
import tec.views.PreLoader;

public class ControllerFingerprintRec {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private FingerprintRec viewFingerprintRec;
    private String student_id;
    private Connect connect = new Connect();
    
    public ControllerFingerprintRec(FingerprintRec view){
        this.viewFingerprintRec = view; 
        
        this.viewFingerprintRec.noControlTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int charCount = viewFingerprintRec.noControlTextField.getText().length();
                if(charCount > 0){
                    viewFingerprintRec.noControlLine.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.noControlLabel.setForeground(Styles.colorPrimary);
                } else {
                    viewFingerprintRec.noControlLine.setBackground(Styles.colorDisabled);
                    viewFingerprintRec.noControlLabel.setForeground(Color.BLACK);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        this.viewFingerprintRec.btnSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                var btn = viewFingerprintRec.btnSearch.isEnabled();
                if(btn){
                    viewFingerprintRec.btnSearch.setBackground(Styles.colorSecondary);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnSearch.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                var btn = viewFingerprintRec.btnSearch.isEnabled();
                if(btn){
                    viewFingerprintRec.imgStudent.setIcon(null);
                    viewFingerprintRec.btnSearch.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.fullnameTextField.setText("");
                    viewFingerprintRec.careerTextField.setText("");
                    viewFingerprintRec.groupTextField.setText("");
                    viewFingerprintRec.semesterTextField.setText("");                    
                    
                    setView("homeFingerprintRec");
                    formFingerprintRec();
                }
            }
        });
        
        this.viewFingerprintRec.btnSearchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                var btn = viewFingerprintRec.btnSearch.isEnabled();
                if(btn){
                    viewFingerprintRec.btnSearch.setBackground(Styles.colorSecondary);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnSearch.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                var btn = viewFingerprintRec.btnSearch.isEnabled();
                if(btn){
                    viewFingerprintRec.fullnameTextField.setText("");
                    viewFingerprintRec.btnSearch.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.careerTextField.setText("");
                    viewFingerprintRec.groupTextField.setText("");
                    viewFingerprintRec.semesterTextField.setText("");
                    viewFingerprintRec.imgStudent.setIcon(null);
                    
                    setView("homeFingerprintRec");
                    formFingerprintRec();
                }
            }
        });
        
        this.viewFingerprintRec.noControlTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                     var btn = viewFingerprintRec.btnSearch.isEnabled();
                    if(btn){
                        viewFingerprintRec.fullnameTextField.setText("");
                        viewFingerprintRec.btnSearch.setBackground(Styles.colorPrimary);
                        viewFingerprintRec.careerTextField.setText("");
                        viewFingerprintRec.groupTextField.setText("");
                        viewFingerprintRec.semesterTextField.setText("");
                        viewFingerprintRec.imgStudent.setIcon(null);

                        setView("homeFingerprintRec");
                        formFingerprintRec();
                    }
                }
            }
        });
        
        this.viewFingerprintRec.noControlTextField.setFocusable(true);
        this.viewFingerprintRec.noControlTextField.requestFocus();
        
        this.viewFingerprintRec.btnNewFinger.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(student_id == null){
                    alertPanel(Styles.colorDanger, "!Ocurrio un error!", "Selecciona algun alumno!");
                } else {
                    viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
                    setView("newFingerprintRec");
                }
            }
        });
        
        this.viewFingerprintRec.btnNewFingerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(student_id == null){
                    alertPanel(Styles.colorDanger, "!Ocurrio un error!", "Selecciona algun alumno!");
                } else {
                    viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
                    setView("newFingerprintRec");
                }
            }
        });
        
        this.viewFingerprintRec.btnNewFinger.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(student_id == null){
                    alertPanel(Styles.colorDanger, "!Ocurrio un error!", "Selecciona algun alumno!");
                } else {
                    viewFingerprintRec.btnNewFinger.setBackground(Styles.colorPrimary);
                    setView("newFingerprintRec");
                }
            }
        });
        
        this.viewFingerprintRec.btnVerify.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewFingerprintRec.btnVerify.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnVerify.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(student_id == null){
                    alertPanel(Styles.colorDanger, "!Ocurrio un error!", "Selecciona algun alumno!");
                } else {
                    viewFingerprintRec.btnVerify.setBackground(Styles.colorPrimary);
                    setView("verifyFingerprintRec");
                }
            }
        });
        
        this.viewFingerprintRec.btnVerifyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewFingerprintRec.btnVerify.setBackground(Styles.colorSecondary);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewFingerprintRec.btnVerify.setBackground(Styles.colorPrimary);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(student_id == null){
                    alertPanel(Styles.colorDanger, "!Ocurrio un error!", "Selecciona algun alumno!");
                } else {
                    viewFingerprintRec.btnVerify.setBackground(Styles.colorPrimary);
                    setView("verifyFingerprintRec");
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
    
    public void alertPanel(Color color, String title, String msg){
        this.viewFingerprintRec.alertPanel.setBackground(color);       
        this.viewFingerprintRec.alertTitle.setText(title);
        this.viewFingerprintRec.alertBody.setText(msg);
        this.viewFingerprintRec.alertPanel.setVisible(true);
        timerAlert.start();
    }
    
    Timer timerAlert = new Timer(3000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            viewFingerprintRec.alertPanel.setVisible(false);
            timerAlert.stop();
        }
    });
    
    private void formFingerprintRecDisabled(){
        this.viewFingerprintRec.btnSearchLabel.setIcon(null);
        this.viewFingerprintRec.btnSearchLabel.setText("Buscar");
        this.viewFingerprintRec.btnSearch.setEnabled(true);
    }
    
    private void formFingerprintRec(){       
        this.viewFingerprintRec.btnSearch.setEnabled(false);
        this.viewFingerprintRec.btnSearchLabel.setIcon(Styles.imgLoaderWhite);
        this.viewFingerprintRec.btnSearchLabel.setText("");
        
        timerSearch.start();
    }
    
    Timer timerSearch = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                URL url = new URL(connect.get_ip() + "/api/desktop/data/student");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JsonObject jsonRequest = new JsonObject();                    
                 
                jsonRequest.addProperty("action", "get_student");
                jsonRequest.addProperty("id", viewFingerprintRec.noControlTextField.getText());
                
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
                        alertPanel(Styles.colorDanger, "¡Ocurrio un error!", msg);
                        formFingerprintRecDisabled();
                        student_id = null;
                        connection.disconnect();
                        timerSearch.stop();
                        return;
                    }
                    
                    alertPanel(Styles.colorPrimary, "¡Exito!", msg);
                    
                    JsonObject data = responseObject.get("data").getAsJsonObject();
                    JsonObject career = data.get("career").getAsJsonObject();
                    JsonObject group = data.get("group").getAsJsonObject();
                    JsonObject semester = data.get("semester").getAsJsonObject();                    
                    
                    String v_student_id = data.get("_id").getAsString();
                    String fullname = data.get("fullname").getAsString();
                    String imgStudentURL = data.get("img").getAsString();
                    String careerName = career.get("name").getAsString();
                    String groupName = group.get("name").getAsString();
                    String semesterName = semester.get("name").getAsString();
                     
                                        
                    viewFingerprintRec.fullnameTextField.setText(fullname.toUpperCase());
                    viewFingerprintRec.fullnameLine.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.fullnameLabel.setForeground(Styles.colorPrimary);
                    
                    student_id = v_student_id;
                    notifyObservers("{'action': 'set_student_id', 'student_id': '" + v_student_id + "'}");
                       
                    URL urlImage = new URL(connect.get_ip() + "/" + imgStudentURL);
                    
                    ImageIcon imageStudent = new ImageIcon(urlImage);
                    Image imageIcon = imageStudent.getImage();
                    Image iconSized = imageIcon.getScaledInstance(130, 180, Image.SCALE_SMOOTH);   
                    ImageIcon imageFin = new ImageIcon(iconSized);

                    viewFingerprintRec.imgStudent.setIcon(imageFin);
            
                    viewFingerprintRec.careerTextField.setText(careerName.toUpperCase());
                    viewFingerprintRec.careerLine.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.careerLabel.setForeground(Styles.colorPrimary);
                    
                    viewFingerprintRec.groupTextField.setText(groupName.toUpperCase());
                    viewFingerprintRec.groupLine.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.groupLabel.setForeground(Styles.colorPrimary);
                    
                    viewFingerprintRec.semesterTextField.setText(semesterName.toUpperCase());
                    viewFingerprintRec.semesterLine.setBackground(Styles.colorPrimary);
                    viewFingerprintRec.semesterLabel.setForeground(Styles.colorPrimary);
                    
                    timerSearch.stop();
                    formFingerprintRecDisabled();
                    connection.disconnect();
                    return;
                } else {
                    student_id = null;
                    alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "La solicitud no se completo. Intentelo de nuevo.");
                    formFingerprintRecDisabled();
                    connection.disconnect();
                    timerSearch.stop();
                    return;
                }                
            } catch (IOException error) {
                alertPanel(Styles.colorDanger, "¡Ocurrio un error!", "No se pudo conectar al servidor.");
            } catch (Exception error) {
                alertPanel(Styles.colorDanger, "¡Error fatal!", "Contacte con algun administrador.");
            }
            
            student_id = null;
            formFingerprintRecDisabled();
            timerSearch.stop();
        }
    });
    
    public void setView(String option){      
        notifyObservers("{'action': 'setViewFingerprintRec', 'option': '" + option + "'}");
    }   
    
}
