package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public final class EmailService {

    private EmailService() {}

    public static void send(String to, String subject, String textBody) {
        String host   = getenv("SMTP_HOST", null);
        String port   = getenv("SMTP_PORT", "587");
        String user   = getenv("SMTP_USER", null);
        String pass   = getenv("SMTP_PASS", null);
        String from   = getenv("SMTP_FROM", user);
        boolean tls   = Boolean.parseBoolean(getenv("SMTP_TLS", "true"));
        boolean tlsReq= Boolean.parseBoolean(getenv("SMTP_TLS_REQUIRED", "false"));
        boolean ssl   = Boolean.parseBoolean(getenv("SMTP_SSL", "false"));
        boolean debug = Boolean.parseBoolean(getenv("SMTP_DEBUG", "false"));
        String trust  = getenv("SMTP_TRUST", null); // ej. "smtp.gmail.com" o "*"



        if (host == null || user == null || pass == null || from == null) {
            throw new IllegalStateException("SMTP no configurado (SMTP_HOST/USER/PASS/FROM)");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Timeouts
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");

        // TLS/SSL exclusivos
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
            props.remove("mail.smtp.starttls.enable");
            props.remove("mail.smtp.starttls.required");
        } else if (tls) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", String.valueOf(tlsReq));
            props.remove("mail.smtp.ssl.enable");
        } else {
            props.remove("mail.smtp.starttls.enable");
            props.remove("mail.smtp.starttls.required");
            props.remove("mail.smtp.ssl.enable");
        }

        // Confianza para certificados (útil en dev)
        if (trust != null && !trust.isEmpty()) {
            props.put("mail.smtp.ssl.trust", trust); // "smtp.gmail.com" o "*"
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
        session.setDebug(debug);

        if (debug) {
            System.out.println("[EmailService] Conectando a " + host + ":" + port +
                    " | tls=" + tls + " tlsRequired=" + tlsReq + " ssl=" + ssl);
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject(subject);
            message.setText(textBody);
            Transport.send(message);
            if (debug) System.out.println("[EmailService] Correo enviado a " + to);
        } catch (AuthenticationFailedException e) {
            System.out.println("[EmailService] ERROR Auth: " + e.getMessage());
            throw new RuntimeException("SMTP auth falló: " + e.getMessage(), e);
        } catch (SendFailedException e) {
            System.out.println("[EmailService] ERROR SendFailed: " + e.getMessage());
            throw new RuntimeException("Dirección inválida o rechazo del servidor: " + e.getMessage(), e);
        } catch (MessagingException e) {
            System.out.println("[EmailService] ERROR Messaging: " + e.getMessage());
            throw new RuntimeException("Error enviando correo: " + e.getMessage(), e);
        }
    }

    private static String getenv(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    private static String maskSecret(String s) {
        if (s == null || s.isEmpty()) return "(null/empty)";
        int keep = Math.min(2, s.length());
        StringBuilder sb = new StringBuilder();
        sb.append(s, 0, keep);
        for (int i = keep; i < s.length(); i++) sb.append('*');
        return sb.toString();
    }

    private static String maskUser(String s) {
        if (s == null || s.isEmpty()) return "(null/empty)";
        int at = s.indexOf('@');
        if (at <= 1) return maskSecret(s);
        return s.charAt(0) + "*****" + s.substring(at);
    }

    private static String nv(String v) { return v == null ? "(null)" : v; }
}