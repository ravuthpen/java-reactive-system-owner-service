package com.piseth.java.school.ownerservice.normalizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class EmailNormalizerTest {
    //Role of Java : crate function can't user underscore(_), but in Unit test can use it
    /**
     * Test On
     * 1. Email null
     * 2. Email black
     * 3. Email Valid ex: "test@gmail.com"
     * 4. Email has space ex: " test@gmail.com"
     * 5. Email capital ex: "TEST@gmail.com"
     */

    private final EmailNormalizer normalizer = new EmailNormalizer();

    //1. Email Null
    @Test
    void shouldReturnNull_whenEmailIsNull() {
        /**
         * //given
         * String email = null;
         * //when
         * String emailAfterNormalized = normalizer.normalize(email);
         * //then
         * assertEquals(null, emailAfterNormalized);
         */
        String email = normalizer.normalize(null);
        assertNull(normalizer.normalize(email));
    }
    //2. Email has space
    @Test
    void shouldTrimWhitespace(){
        String result = normalizer.normalize(" test@gmail.com ");
        assertEquals("test@gmail.com", result);
    }
    //3. Email Lowercase and whitespace
    @Test
    void shouldTrimAndLowercase(){
        String result = normalizer.normalize(" TEST@gmail.com ");
        assertEquals("test@gmail.com", result);
    }

    @Test
    void shouldReturnNull_whenEmailIsBlank(){
        String email = normalizer.normalize("");
        assertNull(normalizer.normalize(email));
    }

    @Test
    void shouldReturnSameValue_whenAlreadyClean(){
        String result = normalizer.normalize("test@gmail.com");
        assertEquals("test@gmail.com", result);
    }

}
