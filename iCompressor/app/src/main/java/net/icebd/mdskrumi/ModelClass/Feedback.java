package net.icebd.mdskrumi.ModelClass;

public class Feedback {
    private String  feedback , email;

    public Feedback() {
    }

    public Feedback(String feedback, String email) {
        this.feedback = feedback;
        this.email = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
