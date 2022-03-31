package br.com.rodolfo.social.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CryptTest {
    @Test
    public void itShouldEncrypt() {
        String result = Crypt.sha256("message");
        assertThat(result).isEqualTo("q1MKE+RZFJgrefm34/uplM/R8/si9xzqGvvwK0YMbR0=");
    }
}
