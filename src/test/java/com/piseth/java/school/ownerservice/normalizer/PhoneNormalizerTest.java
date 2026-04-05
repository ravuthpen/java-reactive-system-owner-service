package com.piseth.java.school.ownerservice.normalizer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhoneNormalizerTest {
    /**
     * 1. Phone null
     * 2. Phone blank
     * 3. Phone has space, ex: " 012314311 "
     * 4. Phone valid, ex: "012314311"
     */
    private final PhoneNormalizer normalizer = new PhoneNormalizer();

    @Test
    void shouldReturnNull_whenPhoneIsNull(){
        String result = normalizer.normalize(null);
        Assertions.assertNull(result);
    }

    @Test
    void shouldReturnNull_whenPhoneIsEmpty(){
        String result = normalizer.normalize("");
        Assertions.assertNull(result);
    }

    @Test
    void shouldTrimWhiteSpace(){
        String result = normalizer.normalize(" 012314311 ");
        Assertions.assertEquals("012314311", result);
    }
    @Test
    void shouldNotChangeInternationalSpace(){
        String result = normalizer.normalize(" 012 314 311 ");
        Assertions.assertEquals("012 314 311", result);
    }

    @Test
    void shouldReturnSameValue_whenAlreadyClean(){
        String result = normalizer.normalize("012314311");
        Assertions.assertEquals("012314311", result);
    }
}
