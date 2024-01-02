package tec.main;

public class Model {
    private String user_id;
    private String student_id;
    private String password;
    
    public void set_user_id(String user_id){
        this.user_id = user_id;
    }
    
    public void set_student_id(String student_id){
        this.student_id = student_id;
    }
    
    public void set_password(String password){
        this.password = password;
    }
     
    public String get_student_id(){
         return student_id;
    }
    
    public String get_password(){
         return password;
    }
}
