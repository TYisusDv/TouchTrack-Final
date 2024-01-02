package tec.controller;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tec.views.Login;
import tec.styles.Styles;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.Timer;
public class ControllerLogin {
    ArrayList<ModelObserver> observers = new ArrayList<>();
    
    private Login viewLogin;    
    private int xOffset;
    private int yOffset;
     private Connect connect = new Connect();
    
    public ControllerLogin(Login view){
        this.viewLogin = view;       
        
        this.viewLogin.decorativePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                xOffset = me.getX();
                yOffset = me.getY();
            } 
        });
        
        this.viewLogin.decorativePanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent me) {
                int x = me.getXOnScreen() - xOffset;
                int y = me.getYOnScreen() - yOffset;
                viewLogin.setLocation(x, y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
        });
        
        this.viewLogin.btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                viewLogin.btnClose.setIcon(Styles.imgIconCloseRed);
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                viewLogin.btnClose.setIcon(Styles.imgIconClose);
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                System.exit(0);
            }
        });
        
        this.viewLogin.btnLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorSecondary);
                } 
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                } 
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                    formLogin();
                }
            }
        });
        
        this.viewLogin.btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorSecondary);
                } 
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                } 
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                var btn = viewLogin.btnLogin.isEnabled();
                if(btn){
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                    formLogin();
                }
            }
        });   
        
        this.viewLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                    formLogin();
                }
            }
        });
        
        this.viewLogin.setFocusable(true);
        this.viewLogin.requestFocus();
        
        this.viewLogin.passwordTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                    formLogin();
                }
            }
        });
        
        this.viewLogin.passwordTextField.setFocusable(true);
        this.viewLogin.passwordTextField.requestFocus();
        
        this.viewLogin.userTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    viewLogin.btnLogin.setBackground(Styles.colorPrimary);
                    formLogin();
                }
            }
        });
        
        this.viewLogin.userTextField.setFocusable(true);
        this.viewLogin.userTextField.requestFocus();
        
        this.viewLogin.userTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int charCount = viewLogin.userTextField.getText().length();
                if(charCount > 0){
                    viewLogin.userLine.setBackground(Styles.colorPrimary);
                    viewLogin.userLabel.setForeground(Styles.colorPrimary);
                } else {
                    viewLogin.userLine.setBackground(Styles.colorDisabled);
                    viewLogin.userLabel.setForeground(Color.BLACK);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        this.viewLogin.passwordTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int charCount = viewLogin.passwordTextField.getText().length();
                if(charCount > 0){
                    viewLogin.passwordLine.setBackground(Styles.colorPrimary);
                    viewLogin.passwordLabel.setForeground(Styles.colorPrimary);
                } else {
                    viewLogin.passwordLine.setBackground(Styles.colorDisabled);
                    viewLogin.passwordLabel.setForeground(Color.BLACK);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
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
    
    private void alertLogin(Color color, String title, String msg){
        this.viewLogin.alertPanel.setBackground(color);       
        this.viewLogin.alertTitle.setText(title);
        this.viewLogin.alertBody.setText(msg);
        this.viewLogin.alertPanel.setVisible(true);
    }
    
    private void formLogin(){
        this.viewLogin.btnLogin.setEnabled(false);
        this.viewLogin.btnLoginLabel.setIcon(Styles.imgLoaderWhite);
        this.viewLogin.btnLoginLabel.setText("");
        
        timerLogin.start();
    }
    
    private void formLoginDisabled(){       
        this.viewLogin.btnLoginLabel.setIcon(null);
        this.viewLogin.btnLoginLabel.setText("Entrar");
        this.viewLogin.btnLogin.setEnabled(true);
    }
    
    private Timer timerLogin = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                URL url = new URL(connect.get_ip() + "/api/desktop/data/auth");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JsonObject jsonRequest = new JsonObject();
                
                char[] passwordChars = viewLogin.passwordTextField.getPassword();
                String password = new String(passwordChars);                
                for (int i = 0; i < passwordChars.length; i++) {
                    passwordChars[i] = '\0';
                }      
                
                jsonRequest.addProperty("username", viewLogin.userTextField.getText());
                jsonRequest.addProperty("password", password);
                
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
                    JsonObject responseObject =  new JsonParser().parse(jsonResponse).getAsJsonObject();
                    Boolean success = responseObject.get("success").getAsBoolean();
                    String msg = responseObject.get("msg").getAsString();
                    
                    if(!success){                        
                        alertLogin(Styles.colorDanger, "¡Ocurrio un error!", msg);
                        formLoginDisabled();
                        connection.disconnect();
                        timerLogin.stop();
                        return;
                    }
                            
                    alertLogin(Styles.colorPrimary, "¡Exito!", msg);                    
                    timerOpenPanel.start();                    
                    timerLogin.stop();
                    connection.disconnect();
                    return;
                } else {
                    alertLogin(Styles.colorDanger, "¡Ocurrio un error!", "La solicitud no se completo. Intentelo de nuevo.");
                    formLoginDisabled();
                    connection.disconnect();
                    timerLogin.stop();
                    return;
                }                
            } catch (IOException error) {
                alertLogin(Styles.colorDanger, "¡Ocurrio un error!", "No se pudo conectar al servidor.");
            } catch (Exception error) {
                alertLogin(Styles.colorDanger, "¡Error fatal!", "Contacte con algun administrador.");
            }
            
            formLoginDisabled();
            timerLogin.stop();
        }
    });
    
    private Timer timerOpenPanel = new Timer(2000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String user = viewLogin.userTextField.getText();
            char[] passwordChars = viewLogin.passwordTextField.getPassword();
            String password = new String(passwordChars);                
            for (int i = 0; i < passwordChars.length; i++) {
                passwordChars[i] = '\0';
            }    
            notifyObservers("{'action': 'openPanel'}");
            notifyObservers("{'action': 'saveUser', 'user': '" + user + "', 'password': '" + password + "'}");
            viewLogin.setVisible(false);
            timerOpenPanel.stop();
        }
    });
}
