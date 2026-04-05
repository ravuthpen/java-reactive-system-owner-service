package com.piseth.java.school.ownerservice.normalizer;

import com.piseth.java.school.ownerservice.dto.OwnerRegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerRegisterRequestNormalizerTest {

    @Mock
    private EmailNormalizer emailNormalizer;
    @Mock
    private PhoneNormalizer phoneNormalizer;

    @InjectMocks
    private OwnerRegisterRequestNormalizer normalizer;

    @Test
    void shouldReturnNewInstance_andNotMutableInput(){

        //Given; call this function
        String email = " Test@Example.Com ";
        String phone = " 012 345 ";
        OwnerRegisterRequest input = new OwnerRegisterRequest();
        input.setEmail(email);
        input.setPhone(phone);

        //When,
        when(emailNormalizer.normalize(email)).thenReturn("test@example.com");
        when(phoneNormalizer.normalize(phone)).thenReturn("012 345");

        //then, Mock Behavior; output new object
        OwnerRegisterRequest out = normalizer.normalize(input);

        //Assert: new instance no modify(immutability)
        Assertions.assertNotNull(out);
        Assertions.assertNotSame(input, out);


        //Assert: output contains normalizer values
        Assertions.assertEquals("test@example.com", out.getEmail());
        Assertions.assertEquals("012 345", out.getPhone());

        // Assert: input remain unchanged
        Assertions.assertEquals(email, input.getEmail());
        Assertions.assertEquals(phone, input.getPhone());

        // Verify interaction
        verify(emailNormalizer).normalize(email);
        //verify(phoneNormalizer).normalize(phone);
        //verifyNoMoreInteractions(emailNormalizer, phoneNormalizer);
        verifyNoMoreInteractions(phoneNormalizer);

    }
    // Unit Test help developer to write better.
    @Test
    void shouldHandleNulls_fromNormalizers(){
        //Given
        OwnerRegisterRequest input = new OwnerRegisterRequest();
        input.setEmail(null);
        input.setPhone(" ");

        //When
        when(emailNormalizer.normalize(null)).thenReturn(null);
        when(phoneNormalizer.normalize(" ")).thenReturn(null);

        //Act
        OwnerRegisterRequest out = normalizer.normalize(input);

        // Asset
        Assertions.assertNotNull(out);
        Assertions.assertNotSame(input, out);

        Assertions.assertNull(out.getEmail());
        Assertions.assertNull(out.getPhone());

        // Input remains unchanged
        Assertions.assertNull(input.getEmail());
        Assertions.assertEquals(" ", input.getPhone());

        verify(emailNormalizer).normalize(null);
        verify(phoneNormalizer).normalize(" ");
        //verifyNoMoreInteractions(emailNormalizer, phoneNormalizer);


    }
    @Test
    void shouldAllowPartialInput_whenEmailProvided(){

        String email = "TEST@EXAMPLE.COM";
        String phone = "012 345";
        //Given
        OwnerRegisterRequest input = new OwnerRegisterRequest();
        input.setEmail(email);
        input.setPhone(null);

        // when
        when(emailNormalizer.normalize(email)).thenReturn("test@example.com");
        when(phoneNormalizer.normalize(null)).thenReturn(null);

        //Then
        OwnerRegisterRequest out = normalizer.normalize(input);

        //Assert
        Assertions.assertNotSame(input, out);
        Assertions.assertEquals("test@example.com", out.getEmail());
        Assertions.assertNull(out.getPhone());

        //verify
        verify(emailNormalizer).normalize(email);
        verify(phoneNormalizer).normalize(null);
        //verifyNoMoreInteractions(emailNormalizer, phoneNormalizer);

    }

    @Test
    void shouldAllowPartialInput_whenOnlyPhoneProvided(){
        //Arrange
        OwnerRegisterRequest input = new OwnerRegisterRequest();
        input.setEmail(null);
        input.setPhone(" 012345 ");

        when(emailNormalizer.normalize(null)).thenReturn(null);
        when(phoneNormalizer.normalize(" 012345 ")).thenReturn("012345");

        //Act

        OwnerRegisterRequest out = normalizer.normalize(input);
        //Assert
        Assertions.assertNotSame(input, out);
        Assertions.assertNull(out.getEmail());
        Assertions.assertEquals("012345", out.getPhone());

        verify(emailNormalizer).normalize(null);
        verify(phoneNormalizer).normalize(" 012345 ");

    }
}
