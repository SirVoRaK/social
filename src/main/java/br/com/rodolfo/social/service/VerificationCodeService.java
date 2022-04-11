package br.com.rodolfo.social.service;

import br.com.rodolfo.social.model.VerificationCode;
import br.com.rodolfo.social.repository.VerificationCodeRepository;
import br.com.rodolfo.social.utils.Random;
import br.com.rodolfo.social.utils.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class VerificationCodeService {
    @Autowired
    private VerificationCodeRepository repository;

    private final Random random = new Random();
    private final SendEmail sendEmail = new SendEmail();

    public VerificationCode create(String email) throws MessagingException {
        if (this.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Verification code already sent");

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        String randNumbers = this.random.numbers(5);
        while (this.findByCode(randNumbers).isPresent())
            randNumbers = this.random.numbers(5);
        verificationCode.setCode(randNumbers);
        sendEmail.send(email, "Password reset", "Your verification code is: " + verificationCode.getCode());
        return repository.save(verificationCode);
    }

    public void delete(String code) {
        repository.deleteByCode(code);
    }

    public boolean isValid(String code, String email) {
        Optional<VerificationCode> verificationCode = this.findByCode(code);
        if (verificationCode.isEmpty()) return false;
        return verificationCode.get().getEmail().equals(email);
    }

    public Optional<VerificationCode> findByCode(String code) {
        return repository.findByCode(code);
    }

    public Optional<VerificationCode> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
