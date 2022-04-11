package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.VerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
    public Optional<VerificationCode> findByCode(String code);

    public Optional<VerificationCode> findByEmail(String email);

    void deleteByCode(String code);
}
