package no.hiof.geire.coursesapp;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.hiof.geire.coursesapp.dataAccess.GelfLogger;

public class InputValidation {

    //GREYLOG
    private GelfLogger gl = new GelfLogger();

    public boolean isInputEmpty(String string) {
        if (string.equals("")) {
            gl.gelfLogger("Input empty");
            return true;
        }
        return false;
    }

    public boolean personTypeValueIsValid(int personTypeValue) {
        if (personTypeValue != 0 && personTypeValue != 1) {

            gl.gelfLogger("False personTypeValue");
            return false;
        }
        return true;
    }

    public boolean nameContainsValidCharacters(String name) {

        String regex = "^[a-øA-Ø]+(([',. -][a-øA-Ø ])?[a-øA-Ø]*)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (!matcher.matches() || isInputEmpty(name)) {
            gl.gelfLogger("Name contains invalid characters");
            return false;
        }

        return true;
    }

    public boolean checkPasswordStrength(String password) {

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches() || isInputEmpty(password)) {
            return false;
        }

        return true;
    }

    public boolean emailIsValid (String email){

        String regex = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            gl.gelfLogger("Email contains invalid characters");
            return false;
        }

        return true;
    }

    public boolean passwordMatches (String password, String confirmPassword){

        if (!password.equals(confirmPassword) || !isInputEmpty(password)) {
            gl.gelfLogger("Password and confirmed password doesn't match");
            return false;
        }

        return true;
    }

    public boolean studentYearIsValid(int year){

        int now = Calendar.getInstance().get(Calendar.YEAR);

        int minYear = now - 10;
        int maxYear = now + 1;

        if(year < minYear || year > maxYear){
            gl.gelfLogger("student year is invalid");
            return false;
        }

        return true;
    }

}

