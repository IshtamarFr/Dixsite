package fr.ishtamar.starter.util;

import fr.ishtamar.starter.model.user.UserInfoService;
import fr.ishtamar.starter.model.user.UserInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Value("${spring.mail.username}")
    private String USERNAME;
    @Value("${fr.ishtamar.starter.base-url}")
    private String BASE_URL;

    private final UserInfoService userInfoService;

    final private JavaMailSender emailSender;
    public EmailService(JavaMailSender emailSender, UserInfoServiceImpl userInfoService){
        this.emailSender=emailSender;
        this.userInfoService=userInfoService;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(USERNAME);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info("email sent to {} from {}", to, USERNAME);
    }




    public void sendValidationLink(String to,Long id,String token, String language){
        if (language.equals("fr")) sendValidationLinkFr(to,id,token); else sendValidationLinkEn(to,id,token);
    }

    private void sendValidationLinkFr(String to,Long id,String token){
        sendSimpleMessage(to, "Inscription : Dixsite",
                "Bienvenue sur le Dixsite. Pour valider votre inscription, merci de cliquer sur ce lien : "
                        + BASE_URL+ "/#/validate?id=" + id + "&token=" + token
        );
    }

    private void sendValidationLinkEn(String to,Long id,String token){
        sendSimpleMessage(to, "Inscription: Dixsite",
                "Welcome to Dixsite. To validate your registration, please click on this link: "
                        + BASE_URL+ "/#/validate?id=" + id + "&token=" + token
        );
    }

    public void sendTemporaryPassword(String to,String token, String language){
        if (language.equals("fr")) sendTemporaryPasswordFr(to,token); else sendTemporaryPasswordEn(to,token);
    }

    private void sendTemporaryPasswordFr(String to,String token){
        try {
            userInfoService.getUserByUsername(to); //required to see if this user exists before trying to send en email
            sendSimpleMessage(to, "Mot de passe oublié : Dixsite",
                    "Bonjour, voici un mot de passe temporaire pour vous connecter sur le Dixsite : "
                            + BASE_URL + " : " + token
                            + "\n\nAttention, il n'est valable que pour une seule connexion. Pensez à modifier votre mot de passe."
            );
        }catch(Error e){
            log.info("someone required temp password for {} but they don't exist", to);
        }
    }

    private void sendTemporaryPasswordEn(String to,String token){
        try {
            userInfoService.getUserByUsername(to); //required to see if this user exists before trying to send en email
            sendSimpleMessage(to, "Forgotten password: Dixsite",
                    "Hello, here is a temporary password to log in to Dixsite: "
                            + BASE_URL + " : " + token
                            + "\n\nWarning, this is only valid for one login. Please consider changing your password."
            );
        }catch(Error e){
            log.info("someone required temp password for {} but they don't exist", to);
        }
    }
}
