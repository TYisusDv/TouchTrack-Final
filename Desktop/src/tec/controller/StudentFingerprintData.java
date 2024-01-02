package tec.controller;

import com.digitalpersona.onetouch.DPFPTemplate;

class StudentFingerprintData {
    private DPFPTemplate fingerprintTemplate;
    private String studentID;
    private String studentName;
    private String imgURL;
    private String career;
    private String group;
    private String semester;

    public StudentFingerprintData(DPFPTemplate fingerprintTemplate, String studentID, String studentName, String imgURL, String career, String group, String semester) {
        this.fingerprintTemplate = fingerprintTemplate;
        this.studentID = studentID;
        this.studentName = studentName;
        this.imgURL = imgURL;
        this.career = career;
        this.group = group;
        this.semester = semester;
    }

    public DPFPTemplate getFingerprintTemplate() {
        return fingerprintTemplate;
    }
    
    public String getStudentID() {
        return studentID;
    }
        
    public String getStudentName() {
        return studentName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getCareer() {
        return career;
    }

    public String getGroup() {
        return group;
    }

    public String getSemester() {
        return semester;
    }
}