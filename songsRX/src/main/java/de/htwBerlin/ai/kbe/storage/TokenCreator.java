/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwBerlin.ai.kbe.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwBerlin.ai.kbe.bean.Contact;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class TokenCreator {

    private static TokenCreator instance = null;
    static List<String> tokenList;
    private static String token = null;

    private TokenCreator() {
        tokenList = new ArrayList<>();
        tokenList.add("testToken");
    }

    public synchronized static TokenCreator getInstance() {
        if (instance == null) {
            instance = new TokenCreator();
        }
        return instance;
    }

    public boolean checkContact(String userId) {
        List<Contact> listOfContacts = new ArrayList<>();
        try {
            listOfContacts = readJSONToContacts(getClass().getClassLoader().getResource("contacts.json").getFile());
        } catch (IOException e) {
            System.out.println("TokenInitError");
        }
        for (Contact contactIterator : listOfContacts) {
            if (contactIterator.getUserId().equals(userId)) {
                createToken();
                return true;
            }
        }
        return false;
    }

    static List<Contact> readJSONToContacts(String fileName) throws FileNotFoundException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            return objectMapper.readValue(is, new TypeReference<List<Contact>>() {
            });
        }
    }

    private String createToken() {
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs(random.nextLong());
        String createdToken = Long.toString(longToken, 16);
        this.token = createdToken;
        tokenList.add("testToken");
	tokenList.add(token);
        return createdToken;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public static void addTokenList(String token) {
        tokenList.add(token);
    }

    public static String getToken() {
        return token;
    }

}
