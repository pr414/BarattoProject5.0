package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class APNUtils {
	/*
	 * PRECONDIZIONI:
	 * input != null, !input.empty()
	 */
	/**
	 * Metodo che data una stringa in input la codifica utilizzando MD5.
	 * @param input
	 * @return
	 */
	public static String getMd5(String input)
    {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
  
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
  
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
  
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	/**
	 * Converte da stringa a istanza LocalDate
	 * @param date_str
	 * @return
	 */
	public static LocalDate getDateFromString(String date_str){
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			return LocalDate.parse(date_str, formatter);
		}catch(DateTimeParseException exception)
		{
			return null;
		}
	}
	public static final String TIME_FORMAT = "ore:minuti";
	/**
	 * Converte da stringa a istanza LocalTime 
	 * @param time_str
	 * @return
	 */
	public static LocalTime getTimeFromString(String time_str){
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
			return LocalTime.parse(time_str + ":00", formatter);
		}catch(DateTimeParseException exception)
		{
			return null;
		}
	}
}
