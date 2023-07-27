package org.aking;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AccountHandler {


    public AccountHandler( OptionsHandler options )
    {
        file = new File(path);
        fileData = new JSONArray();

        try
        {
            // If a new file has been created or if this is the
            // first account iteration, reset and initialize the file
            if (file.createNewFile() || options.getNextIteration() == 1)
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
     * Adds an account to the file data JSON Array
     * @param username the account's username
     * @param password the account's password
     * @param accountType the account's account type
     * @return false if any of the parameters were null
     */
    public JSONObject addAccount( String username, String password, String accountType )
    {
        if ( username == null || password == null || accountType == null )
            return null;

        JSONObject accountData = new JSONObject();

        accountData.put("username", username);
        accountData.put("password", password);
        accountData.put("accountType", accountType);

        fileData.add( accountData );

        return accountData;
    }

    /**
     * Removes an account from the specified index of the array (unused)
     * @param index the index to remove the account from
     * @return false if the index was out of bounds
     */
    public boolean removeAccount( int index )
    {
        if ( index > fileData.size() - 1 || index < 0 )
            return false;

        fileData.remove( index );

        return true;
    }

    /**
     * Gets a JSONArray of all the accounts stored
     * @return an Object array with all the stored accounts
     */
    public Object[] getAccounts()
    {
        return fileData.toArray();
    }

    /**
     * Initializes the data file with baseline values
     */
    private void initializeFile()
    {
        if (fileData == null)
            fileData = new JSONArray();

        saveToFile();
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
     * Retrieves the JSONObject from the data file.
     * @return a JSONObject, null if unable to retrieve the data.
     */
    private JSONArray getJSONData()
    {
        try (FileReader in = new FileReader(path))
        {
            return (JSONArray) parser.parse(in);
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    // The path to the data.json file
    // by default is implicitly in the .exe 's folder
    private String path = "accounts.json";
    // The JSON Parser to use for parsing
    private JSONParser parser = new JSONParser();
    // The File object obtained from the specified path
    private File file;
    // The JSON Object to use for data transfer
    private JSONArray fileData;
}
