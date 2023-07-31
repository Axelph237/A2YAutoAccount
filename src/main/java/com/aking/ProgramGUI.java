package com.aking;

import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class ProgramGUI extends JFrame implements ActionListener, ItemListener {

    public ProgramGUI(String title, OptionsHandler d, AccountHandler a )
    {
        super(title);

        optionsData = d;
        accountsData = a;

        JTabbedPane GUIPane = new JTabbedPane();

        JPanel createTabPane = new JPanel();
        JPanel accountTabPane = new JPanel();
        accountTabPane.setLayout( new GridLayout(2, 1));

        this.setPreferredSize(new Dimension(350, 350));
        //this.setLayout( new GridLayout(6, 0) );

        initInfoPane();
        initAccountPane();
        initEmailPane();
        initPasswordPane();
        initTypePane();
        initButtonPane();

        createTabPane.add(infoPane);
        createTabPane.add(accountPrePane);
        createTabPane.add(emailPane);
        createTabPane.add(passwordPane);
        createTabPane.add(accountTypePane);
        createTabPane.add(buttonsPane);


        initViewAccountPane();
        initNotesPane();

        accountTabPane.add(viewPane);
        accountTabPane.add(notesPane);

        GUIPane.addTab("Create", null, createTabPane, "Create Accounts");
        GUIPane.addTab("View", null, accountTabPane, "View Created Accounts");

        this.add(GUIPane);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initializes the Info pane displaying the current
     * iteration and date code
     */
    private void initInfoPane()
    {
        infoPane = new JPanel();
        iterationCount = new JLabel( "Next Iteration: " + String.format("%02d", optionsData.getNextIteration() ) );
        // Adds a mouse listener such that the iteration count can be increased by clicking the label
        iterationCount.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                optionsData.iterateCount();
                iterationCount.setText( "Next Iteration: " + String.format("%02d", optionsData.getNextIteration() ) );
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        infoPane.add(iterationCount);
        infoPane.add( new JLabel( "    Date Code: " + optionsData.getDateCode() ) );
    }

    /**
     * Initializes the Account pane, requesting
     * input from the user for the account prefix
     */
    private void initAccountPane()
    {
        accountPrePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountPrePane.add( new JLabel("Username Prefix") );
        accountPrefix = new JTextField( optionsData.getAccountPrefix() );
        accountPrefix.setPreferredSize( new Dimension( 125, 25 ) );
        accountPrePane.add( accountPrefix );
    }

    /**
     * Initializes the Email pane, requesting
     * input from the user for the email
     */
    private void initEmailPane()
    {
        emailPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPane.add( new JLabel("Email") );
        email = new JTextField( optionsData.getEmailPrefix() + "@" + optionsData.getEmailSuffix() );
        email.setPreferredSize( new Dimension( 250, 25 ) );
        emailPane.add( email );
    }

    /**
     * Initializes the Password pane, requesting
     * input from the user for the password
     */
    private void initPasswordPane()
    {
        passwordPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPane.add( new JLabel("Password") );
        password = new JPasswordField( optionsData.getPassword() );
        password.setPreferredSize( new Dimension( 125, 25 ) );
        passwordPane.add( password );
    }

    /**
     * Initializes the Account Type pane, creating a
     * dropdown for the user to select from
     */
    private void initTypePane()
    {
        accountTypePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountTypePane.add( new JLabel( "Account Type" ) );
        // Creates a dropdown menu
        accountType = new Choice();
        accountType.add("Individual");
        accountType.add("Family");
        accountType.add("Advocate");
        accountType.add("Mentor");
        accountType.add("Organization");
        // Adds the dropdown to the pane
        accountTypePane.add( accountType );
    }

    /**
     * Initializes the Buttons pane, allowing the user
     * to save their settings or create a new account
     */
    private void initButtonPane()
    {
        buttonsPane = new JPanel();
        createButton = new Button("Save & Create Account");
        createButton.addActionListener(this);
        saveButton = new Button("Save");
        saveButton.addActionListener(this);
        buttonsPane.add(createButton);
        buttonsPane.add(saveButton);
    }

    /**
     * Initializes the View Account pane, creating a
     * dropdown for the user to select from
     */
    private void initViewAccountPane()
    {
        viewPane = new JPanel();
        viewPane.setLayout( new FlowLayout(FlowLayout.CENTER));
        viewPane.add( new JLabel( "Accounts:" ) );
        // Creates a dropdown menu
        viewAccounts = new JComboBox();
        viewAccounts.addItemListener(this);

        viewPassword = new JLabel("Password: ");


        // Populate dropdown
        for ( Object o : accountsData.getAccounts() )
        {
            JSONObject object = (JSONObject)o;
            viewAccounts.addItem( new MenuItem( object.get("username") + "  (" + object.get("accountType") + ")", object ));
        }


        viewPane.add( viewAccounts );
        viewPane.add( viewPassword );
    }

    /**
     * Initializes the Notes pane, creating a
     * text box for the user to write notes in,
     * and a button for them to save the notes
     */
    private void initNotesPane()
    {
        notesPane = new JPanel();
        notesPane.setLayout( new BoxLayout(notesPane, BoxLayout.Y_AXIS));
        notesPane.setAlignmentX(0.5f);

        Object[] accounts = accountsData.getAccounts();
        if (accounts.length > 0)
            notes = new JTextArea( ((JSONObject)accounts[0]).get("notes").toString(), 10, 10 );
        else
            notes = new JTextArea( 10, 10);

        saveNotesButton = new Button("Save Notes");
        saveNotesButton.addActionListener(this);

        notesPane.add( new JLabel("Notes: ") );
        notesPane.add(notes);
        JPanel savePanel = new JPanel( new FlowLayout(FlowLayout.LEFT));
        savePanel.add(saveNotesButton);
        notesPane.add( savePanel );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();

        if ( !email.getText().contains("@") )
        {
            //new LineBorder(color,width);
            email.setBorder(new LineBorder(Color.red,2));
            return;
        }

        if ( password.getText().compareTo("") == 0 )
        {
            //new LineBorder(color,width);
            password.setBorder(new LineBorder(Color.red,2));
            return;
        }

        if (source.equals(createButton))
        {
            AccountCreator ac = new AccountCreator();

            saveData();

            // Adds the account to the accountData object
            // Then adds it to the view accounts menu
            String username = optionsData.getAccountPrefix() + optionsData.getDateCode() + String.format("%02d", optionsData.getNextIteration() );
            JSONObject account = accountsData.addAccount( username, optionsData.getPassword(), accountType.getSelectedItem() );
            accountsData.saveToFile();
            viewAccounts.addItem( new MenuItem( account.get("username") + "  (" + account.get("accountType") + ")", account));

            ac.createAccount(optionsData, getPlanID( accountType.getSelectedItem() ) );

            saveData();

            iterationCount.setText( "Next Iteration: " + String.format("%02d", optionsData.getNextIteration() ) );
        }
        else if (source.equals(saveButton))
        {
            saveData();
            if (saveLabel == null)
            {
                buttonsPane.add( saveLabel = new JLabel("Saved!") );
                buttonsPane.updateUI();
            }

        }
        else if (source.equals(saveNotesButton))
        {
            accountsData.setNotes( viewAccounts.getSelectedIndex(), notes.getText() );
            accountsData.saveToFile();
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        MenuItem item = (MenuItem)e.getItem();

        viewPassword.setText( "Password: " + item.data.get("password") );

        if( notes != null)
            notes.setText( (String)item.data.get("notes") );
    }

    /**
     * Takes the String input and determines the corresponding plan ID number.
     * @param selectedType the String to compare.
     * @return the plan ID, -1 if none was found.
     */
    private int getPlanID( String selectedType )
    {
        int planID = -1;

        switch ( selectedType ) {
            case "Individual":
                planID = 25;
                break;
            case "Family":
                planID = 27;
                break;
            case "Advocate":
                planID = 28;
                break;
            case "Mentor":
                planID = 29;
                break;
            case "Organization":
                planID = 30;
                break;
            default:
                break;
        }

        return planID;
    }

    /**
     * Saves the data from the input boxes to the JSON Object,
     * and then saves the data from the JSON Object to a file.
     */
    private void saveData()
    {
        optionsData.setAccountPrefix( accountPrefix.getText() );
        optionsData.setEmail( email.getText() );
        optionsData.setPassword( password.getText() );
        optionsData.saveToFile();
    }

    // Button for saving & creating an account
    private Button createButton;
    // Button for simply saving data
    private Button saveButton;
    // Button for saving the notes data of an account
    private Button saveNotesButton;
    // Dropdown for selecting the account type
    private Choice accountType;
    // Dropdown for selecting the account to view
    private JComboBox viewAccounts;
    // Input field for the account's prefix
    private JTextField accountPrefix;
    // Input field for the user's email
    private JTextField email;
    // Input field for the account's notes
    private JTextArea notes;
    // Input field for the account's password
    private JPasswordField password;
    // Output pane for displaying iteration and date code
    private JPanel infoPane;
    // Pane for the account prefix
    private JPanel accountPrePane;
    // Pane for the email
    private JPanel emailPane;
    // Pane for the account type
    private JPanel accountTypePane;
    // Pane for the buttons
    private JPanel buttonsPane;
    // Pane for the password
    private JPanel passwordPane;
    // Pane for the viewing accounts
    private JPanel viewPane;
    // Pane for viewing the notes of an account
    private JPanel notesPane;
    // Label for responding to when the user clicks the "Save" button
    private JLabel saveLabel = null;
    // Label for displaying the current iteration count
    private JLabel iterationCount;
    // Label for viewing selected account password
    private JLabel viewPassword;
    // The DataHandler to manage data pulled to and from files and/or
    // its internal JSON Object
    final private OptionsHandler optionsData;
    final private AccountHandler accountsData;
}
