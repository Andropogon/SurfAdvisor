package livewind.example.andro.liveWind;

public class Contact {
    private String phoneNumber;
    private String emailAddress;
    private String webAddress;

    Contact(){
        phoneNumber="DEFAULT";
        emailAddress="DEFAULT";
        webAddress="DEFAULT";
    }

    Contact(String mPhoneNumber, String mEmailAddress, String mWebAddress){
        phoneNumber=mPhoneNumber;
        emailAddress=mEmailAddress;
        webAddress=mWebAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

}
