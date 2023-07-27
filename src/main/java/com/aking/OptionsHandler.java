package com.aking;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class OptionsHandler {

    public OptionsHandler()
    {
        file = new File(path);
        fileData = new JSONObject();

        try
        {
            if (file.createNewFile())
                initializeFile();
            else
                fileData = getJSONData();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the next account iteration value
     * @return the current iteration incremented by 1, or 1 if it is a new day
     */
    public int getNextIteration()
    {
        int currDateIndex = Integer.valueOf(getDateCode());
        int dateIndex = Integer.valueOf(fileData.get("dateCode").toString());

        if (currDateIndex > dateIndex)
        {
            return 1;
        }
        else
            return Integer.valueOf(fileData.get("iterations").toString()) + 1;
    }

    /**
     * Gets the account prefix from the JSON Object
     * @return the current account prefix
     */
    public String getAccountPrefix()
    {
        return fileData.get("accountPrefix").toString();
    }

    /**
     * Gets the email prefix from the JSON Object
     * @return the current email prefix
     */
    public String getEmailPrefix()
    {
        return fileData.get("emailPrefix").toString();
    }

    /**
     * Gets the email suffix from the JSON Object
     * @return the current email suffix
     */
    public String getEmailSuffix()
    {
        return fileData.get("emailSuffix").toString();
    }

    /**
     * Gets the password from the JSON Object
     * @return the current password
     */
    public String getPassword()
    {
        return fileData.get("password").toString();
    }

    /**
     * Sets the passed String to the JSON Object's account prefix
     * @param accPre the String to set it to
     */
    public void setAccountPrefix( String accPre )
    {
        fileData.put("accountPrefix", accPre);
    }

    /**
     * Sets the passed String to the JSON Object's password
     * @param pass the String to set it to
     */
    public void setPassword( String pass )
    {
        fileData.put("password", pass);
    }

    /**
     * Splits the passed String by "@" and then sets the pieces to the JSON Object's
     * email prefix and email suffix respectively
     * @param email the String to use as the email
     * @return false if the email did not contain an "@"
     */
    public boolean setEmail( String email )
    {
        if (email.contains("@"))
        {
            String[] splitEmail = email.split("@");
            fileData.put("emailPrefix", splitEmail[0]);
            fileData.put("emailSuffix", splitEmail[1]);

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Obtains the date in the format yyMMdd. Example: date is 01/06/2005. Result: 050601
     * @return the date in the given time format.
     */
    public String getDateCode()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Saves the data from the JSON Object to file
     */
    public void saveToFile()
    {
        if (fileData == null)
            return;

        try (FileWriter out = new FileWriter(path))
        {
            out.write(fileData.toJSONString());
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the data file with baseline values
     */
    private void initializeFile()
    {
        if (fileData == null)
            fileData = new JSONObject();

        fileData.put("dateCode", getDateCode());
        fileData.put("iterations", 0);
        fileData.put("accountPrefix", "none");
        fileData.put("emailPrefix", "none");
        fileData.put("emailSuffix", "none.none");
        fileData.put("password", "none");

        saveToFile();
    }

    /**
     * Iterates the current integer count of accounts created today. If new day, sets to 1.
     */
    public int iterateCount()
    {
        int currDateIndex = Integer.valueOf(getDateCode());
        int dateIndex = Integer.valueOf(fileData.get("dateCode").toString());

        if (currDateIndex > dateIndex)
        {
            fileData.put("dateCode", currDateIndex);
            fileData.put("iterations", 1);
            return 1;
        }
        else
        {
            int nextIteration = Integer.valueOf(fileData.get("iterations").toString()) + 1;

            fileData.put("iterations", nextIteration);
            return nextIteration;
        }
    }

    /**
     * Retrieves the JSONObject from the data file.
     * @return a JSONObject, null if unable to retrieve the data.
     */
    private JSONObject getJSONData()
    {
        try (FileReader in = new FileReader(path))
        {
            return (JSONObject) parser.parse(in);
        }
        catch (IOException|ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    // The path to the data.json file
    // by default is implicitly in the .exe 's folder
    private String path = "options.json";
    // The JSON Parser to use for parsing
    private JSONParser parser = new JSONParser();
    // The File object obtained from the specified path
    private File file;
    // The JSON Object to use for data transfer
    private JSONObject fileData;
}
