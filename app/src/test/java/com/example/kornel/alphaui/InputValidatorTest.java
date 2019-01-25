package com.example.kornel.alphaui;

import com.example.kornel.alphaui.utils.ProfileInfoValidator;


import static org.junit.Assert.*;
import org.junit.Test;

public class InputValidatorTest {

    @Test
    public void testPasswordValidator_Haslopoprawne12HASH() {
        String name = "Haslopoprawne12#";
        assertTrue("Correct name", ProfileInfoValidator.isPasswordValid(name));
    }

    @Test
    public void testPasswordValidator_toHASHtez2jestdobrE() {
        String name = "to#tez2jestdobrE";
        assertTrue("Correct name", ProfileInfoValidator.isPasswordValid(name));
    }

    @Test
    public void testPasswordValidator_hasloniepoprawne() {
        String name = "hasloniepoprawne";
        assertTrue("Correct name", ProfileInfoValidator.isPasswordValid(name));
    }

    @Test
    public void testPasswordValidator_1234567AT() {
        String name = "1234567@";
        assertTrue("Incorrect name", ProfileInfoValidator.isPasswordValid(name));
    }

    // @Test
    // public void testEmailValidator_kornelATgmailDOTcom() {
    //     String name = "kornel@gmail.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornelATDOTcom() {
    //     String name = "kornel@.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornel2019ATgmailDOTcom() {
    //     String name = "kornel2019@gmail.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornelATkornelATgmailDOTcom() {
    //     String name = "kornel@kornel@gmail.com";
    //     assertTrue("Incorrect name", ProfileInfoValidator.isEmailValid(name));
    // }

    // @Test
    // public void testEmailValidator_kornelATgmailDOTcom() {
    //     String name = "kornel@gmail.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornelATDOTcom() {
    //     String name = "kornel@.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornel2019ATgmailDOTcom() {
    //     String name = "kornel2019@gmail.com";
    //     assertTrue("Correct name", ProfileInfoValidator.isEmailValid(name));
    // }
    //
    // @Test
    // public void testEmailValidator_kornelATkornelATgmailDOTcom() {
    //     String name = "kornel@kornel@gmail.com";
    //     assertTrue("Incorrect name", ProfileInfoValidator.isEmailValid(name));
    // }

    // @Test
    // public void testNameValidator_Kornel() {
    //     String name = "Kornel";
    //     assertTrue("Correct name", ProfileInfoValidator.isNameValid(name));
    // }
    //
    // @Test
    // public void testNameValidator_Kornelia() {
    //     String name = "Kornelia";
    //     assertTrue("Correct name", ProfileInfoValidator.isNameValid(name));
    // }
    //
    // @Test
    // public void testNameValidator_Kor___nel() {
    //     String name = "Kor   nel";
    //     assertTrue("Correct name", ProfileInfoValidator.isNameValid(name));
    // }
    //
    // @Test
    // public void testNameValidator_Kornel2019() {
    //     String name = "Kornel2019";
    //     assertTrue("Incorrect name", ProfileInfoValidator.isNameValid(name));
    // }

    // @Test
    // public void testNameValidatorIncorrect() {
    //     String name = "kornel23";
    //     assertFalse("Incorrect name", ProfileInfoValidator.isNameValid(name));
    // }
    //
    // @Test
    // public void testNameValidatorCorrect() {
    //     String name = "Çelik";
    //     assertTrue("Correct name", ProfileInfoValidator.isNameValid(name));
    // }
    //
    // @Test
    // public void testEmailValidatorIncorrect() {
    //     String email = "kornel@.com";
    //     assertFalse("Incorrect email", ProfileInfoValidator.isEmailValid(email));
    // }
    //
    // @Test
    // public void testEmailValidatorCorrect() {
    //     String email = "Kornekrzeslak@gmail.com";
    //     assertTrue("Correct email", ProfileInfoValidator.isEmailValid(email));
    // }
    //
    // @Test
    // public void testPasswordValidatorIncorrect() {
    //     String password = "dlugiehaslo#12";
    //     assertFalse("Incorrect password", ProfileInfoValidator.isPasswordValid(password));
    // }
    //
    // @Test
    // public void testPasswordValidatorCorrect() {
    //     String password = "Dlugiehaslo#12";
    //     assertTrue("Correct password", ProfileInfoValidator.isPasswordValid(password));
    // }
}
