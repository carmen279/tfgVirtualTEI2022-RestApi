package com.tfg.restapi.business;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

public class EmailManager {
    private final String from;
    private final JavaMailSender javaMailSender;

    public EmailManager (JavaMailSender javaMailSender, String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    /**
     * Método que, dada una lista de emails y un link a la encuesta, envía a cada email un correo con el contenido predeterminado y el enlace a la encuesta
     * @param emails
     * @param decodedPollLink
     * @return
     * @throws MessagingException
     */
    public String sendEmail(List<String> emails, String decodedPollLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        helper = new MimeMessageHelper(message,
                true);//true indicates multipart message
        helper.setFrom(from);
        helper.setSubject("Encuesta de valoración de orientación");
        helper.setTo(emails.toArray(new String[0]));
        helper.setText("Buenos días: <br> Para conocer más a los alumnos se pide por favor que se rellene el siguiente formulario: <br>"+decodedPollLink
                +" <br> No llevará más de 10-15 minutos rellenarlo y se pide la mayor sinceridad posible. <br> Un saludo, <br> VirtualTEI", true);//true indicates body is html
        javaMailSender.send(message);

        return "Emails have been distributed correctly";
    }
}
