package tec.views;

import java.awt.Color;
import tec.styles.JComboBoxCustom;

public class TouchTrack2 extends javax.swing.JPanel {
    
    public TouchTrack2() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboReaders = new JComboBoxCustom();
        jLabel3 = new javax.swing.JLabel();
        alertPanel = new javax.swing.JPanel();
        alertTitle = new javax.swing.JLabel();
        alertBody = new javax.swing.JLabel();
        noControlLabel = new javax.swing.JLabel();
        noControlTextField = new javax.swing.JTextField();
        noControlLine = new javax.swing.JPanel();
        fullnameLabel = new javax.swing.JLabel();
        fullnameTextField = new javax.swing.JTextField();
        fullnameLine = new javax.swing.JPanel();
        studentPanel = new javax.swing.JPanel();
        imgStudent = new javax.swing.JLabel();
        careerLabel = new javax.swing.JLabel();
        careerTextField = new javax.swing.JTextField();
        careerLine = new javax.swing.JPanel();
        fingerprintPanel = new javax.swing.JPanel();
        fingerprintImg = new javax.swing.JLabel();
        semesterLabel = new javax.swing.JLabel();
        semesterTextField = new javax.swing.JTextField();
        semesterLine = new javax.swing.JPanel();
        groupLabel = new javax.swing.JLabel();
        groupTextField = new javax.swing.JTextField();
        groupLine = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        imgLoad = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tec/images/touchtrack80px.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("MadaniArabic-Medium", 1, 18)); // NOI18N
        jLabel2.setText("Registro de asistencia");

        comboReaders.setEditable(true);
        comboReaders.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        comboReaders.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cargando..." }));

        jLabel3.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Seleccione el sensor");
        jLabel3.setToolTipText("");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        alertPanel.setVisible(false);
        alertPanel.setBackground(new java.awt.Color(255, 77, 99));
        alertPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        alertTitle.setFont(new java.awt.Font("MadaniArabic-Medium", 1, 14)); // NOI18N
        alertTitle.setForeground(new java.awt.Color(255, 255, 255));
        alertTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        alertTitle.setText("¡Ocurrio un error!");
        alertTitle.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        alertPanel.add(alertTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 280, 30));

        alertBody.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 12)); // NOI18N
        alertBody.setForeground(new java.awt.Color(255, 255, 255));
        alertBody.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        alertBody.setText("¡Ocurrio un error!");
        alertBody.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        alertPanel.add(alertBody, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 280, 40));

        noControlLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        noControlLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        noControlLabel.setText("Numero de control");
        noControlLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        noControlTextField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        noControlTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        noControlTextField.setBorder(null);
        noControlTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        noControlTextField.setEnabled(false);
        noControlTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                noControlTextFieldKeyTyped(evt);
            }
        });

        noControlLine.setBackground(new java.awt.Color(102, 102, 102));
        noControlLine.setToolTipText("");
        noControlLine.setPreferredSize(new java.awt.Dimension(300, 2));

        javax.swing.GroupLayout noControlLineLayout = new javax.swing.GroupLayout(noControlLine);
        noControlLine.setLayout(noControlLineLayout);
        noControlLineLayout.setHorizontalGroup(
            noControlLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        noControlLineLayout.setVerticalGroup(
            noControlLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        fullnameLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        fullnameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        fullnameLabel.setText("Nombre completo");
        fullnameLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        fullnameTextField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        fullnameTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        fullnameTextField.setBorder(null);
        fullnameTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        fullnameTextField.setEnabled(false);
        fullnameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fullnameTextFieldKeyTyped(evt);
            }
        });

        fullnameLine.setBackground(new java.awt.Color(102, 102, 102));
        fullnameLine.setToolTipText("");
        fullnameLine.setPreferredSize(new java.awt.Dimension(300, 2));

        javax.swing.GroupLayout fullnameLineLayout = new javax.swing.GroupLayout(fullnameLine);
        fullnameLine.setLayout(fullnameLineLayout);
        fullnameLineLayout.setHorizontalGroup(
            fullnameLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        fullnameLineLayout.setVerticalGroup(
            fullnameLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        studentPanel.setBackground(new java.awt.Color(255, 255, 255));
        studentPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 23, 156), 2, true));

        imgStudent.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout studentPanelLayout = new javax.swing.GroupLayout(studentPanel);
        studentPanel.setLayout(studentPanelLayout);
        studentPanelLayout.setHorizontalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imgStudent, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );
        studentPanelLayout.setVerticalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imgStudent, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addContainerGap())
        );

        careerLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        careerLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        careerLabel.setText("Carrera");
        careerLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        careerTextField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        careerTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        careerTextField.setBorder(null);
        careerTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        careerTextField.setEnabled(false);
        careerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                careerTextFieldKeyTyped(evt);
            }
        });

        careerLine.setBackground(new java.awt.Color(102, 102, 102));
        careerLine.setToolTipText("");
        careerLine.setPreferredSize(new java.awt.Dimension(300, 2));

        javax.swing.GroupLayout careerLineLayout = new javax.swing.GroupLayout(careerLine);
        careerLine.setLayout(careerLineLayout);
        careerLineLayout.setHorizontalGroup(
            careerLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        careerLineLayout.setVerticalGroup(
            careerLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        fingerprintPanel.setBackground(new java.awt.Color(255, 255, 255));
        fingerprintPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 23, 156), 2, true));
        fingerprintPanel.setPreferredSize(new java.awt.Dimension(150, 200));

        fingerprintImg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout fingerprintPanelLayout = new javax.swing.GroupLayout(fingerprintPanel);
        fingerprintPanel.setLayout(fingerprintPanelLayout);
        fingerprintPanelLayout.setHorizontalGroup(
            fingerprintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fingerprintPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fingerprintImg, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );
        fingerprintPanelLayout.setVerticalGroup(
            fingerprintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fingerprintPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fingerprintImg, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addContainerGap())
        );

        semesterLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        semesterLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        semesterLabel.setText("Semestre");
        semesterLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        semesterTextField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        semesterTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        semesterTextField.setBorder(null);
        semesterTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        semesterTextField.setEnabled(false);
        semesterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                semesterTextFieldKeyTyped(evt);
            }
        });

        semesterLine.setBackground(new java.awt.Color(102, 102, 102));
        semesterLine.setToolTipText("");
        semesterLine.setPreferredSize(new java.awt.Dimension(300, 2));

        javax.swing.GroupLayout semesterLineLayout = new javax.swing.GroupLayout(semesterLine);
        semesterLine.setLayout(semesterLineLayout);
        semesterLineLayout.setHorizontalGroup(
            semesterLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        semesterLineLayout.setVerticalGroup(
            semesterLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        groupLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 16)); // NOI18N
        groupLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        groupLabel.setText("Grupo");
        groupLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        groupTextField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        groupTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        groupTextField.setBorder(null);
        groupTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        groupTextField.setEnabled(false);
        groupTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                groupTextFieldKeyTyped(evt);
            }
        });

        groupLine.setBackground(new java.awt.Color(102, 102, 102));
        groupLine.setToolTipText("");
        groupLine.setPreferredSize(new java.awt.Dimension(300, 2));

        javax.swing.GroupLayout groupLineLayout = new javax.swing.GroupLayout(groupLine);
        groupLine.setLayout(groupLineLayout);
        groupLineLayout.setHorizontalGroup(
            groupLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        groupLineLayout.setVerticalGroup(
            groupLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("<html><p>Desarrollado por los alumnos del plantel Tecnológico Arandas de Ingeniería en sistemas computacionales de séptimo semestre. © 2023 Tecnológico Arandas.</p></html>");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        statusLabel.setFont(new java.awt.Font("MadaniArabic-Medium", 0, 24)); // NOI18N
        statusLabel.setText("En espera...");
        statusLabel.setToolTipText("");
        statusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        imgLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tec/images/preloader.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(imgLoad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imgLoad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(alertPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 101, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(studentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(noControlLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(fullnameTextField)
                                    .addComponent(fullnameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                    .addComponent(noControlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                    .addComponent(fullnameLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(careerTextField)
                                    .addComponent(careerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                    .addComponent(careerLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(noControlTextField)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(groupTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(groupLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(semesterTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(semesterLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(semesterLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(groupLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(fingerprintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboReaders, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(101, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1184, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(alertPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addComponent(comboReaders, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(fingerprintPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(studentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(noControlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(noControlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(noControlLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(fullnameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(fullnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(fullnameLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(careerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(careerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(careerLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(semesterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(semesterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(semesterLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(groupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(groupTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(groupLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void noControlTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noControlTextFieldKeyTyped

    }//GEN-LAST:event_noControlTextFieldKeyTyped

    private void careerTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_careerTextFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_careerTextFieldKeyTyped

    private void fullnameTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fullnameTextFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_fullnameTextFieldKeyTyped

    private void semesterTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_semesterTextFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_semesterTextFieldKeyTyped

    private void groupTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_groupTextFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_groupTextFieldKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel alertBody;
    public javax.swing.JPanel alertPanel;
    public javax.swing.JLabel alertTitle;
    public javax.swing.JLabel careerLabel;
    public javax.swing.JPanel careerLine;
    public javax.swing.JTextField careerTextField;
    public javax.swing.JComboBox<String> comboReaders;
    public javax.swing.JLabel fingerprintImg;
    public javax.swing.JPanel fingerprintPanel;
    public javax.swing.JLabel fullnameLabel;
    public javax.swing.JPanel fullnameLine;
    public javax.swing.JTextField fullnameTextField;
    public javax.swing.JLabel groupLabel;
    public javax.swing.JPanel groupLine;
    public javax.swing.JTextField groupTextField;
    public javax.swing.JLabel imgLoad;
    public javax.swing.JLabel imgStudent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JLabel noControlLabel;
    public javax.swing.JPanel noControlLine;
    public javax.swing.JTextField noControlTextField;
    public javax.swing.JLabel semesterLabel;
    public javax.swing.JPanel semesterLine;
    public javax.swing.JTextField semesterTextField;
    public javax.swing.JLabel statusLabel;
    private javax.swing.JPanel studentPanel;
    // End of variables declaration//GEN-END:variables
}