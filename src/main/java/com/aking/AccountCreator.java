package com.aking;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class AccountCreator {
    private WebDriver driver;
    private OptionsHandler accountData;
    private int planID;

    /**
     * Main public implementation method. Executes necessary steps for Selenium in proper order.
     * @param data the DataHandler containing the data necessary for this to run.
     * @param planID the numeric ID representing the account type to be created.
     */
    public void createAccount(OptionsHandler data, int planID )
    {
        setUp( data, planID );

        try
        {
            run();
        }
        finally
        {
            tearDown();
        }
    }

    private void setUp(OptionsHandler data, int plan ) {
        driver = new ChromeDriver();
        accountData = data;
        planID = plan;
    }

    private void tearDown() {
        driver.quit();
    }

    private void run() {

        // Necessary page URLs
        String signupURL = "https://my.accountable2you.dev/Signup?planId=" + planID;
        String legalURL = "https://my.accountable2you.dev/Signup/Legal?planId=" + planID;
        String paymentURL = "https://my.accountable2you.dev/Signup/Payment?planId=" + planID;

        // Navigates to the Signup URL and maximizes window
        driver.get(signupURL);
        driver.manage().window().maximize();
        
        //// Signup page 
        runSignupPage();
        // Pause for ReCaptcha
        waitForPageLoad(signupURL);
        accountData.iterateCount();

        //// Legal page
        runLegalPage();
        // Pause until payment page loads
        waitForPageLoad(legalURL);


        //// Payment page
        runPaymentPage();
        // Pause until card details are finalized
        waitForPageLoad(paymentURL);

        String username = accountData.getAccountPrefix() + accountData.getDateCode() + String.format("%02d", accountData.getNextIteration());
        System.out.println("A2YAutoAccount: Completed creating account \"" + username + "\"");
    }

    /**
     * Runs script to fill out the Signup page
     */
    private void runSignupPage()
    {
        String account = accountData.getAccountPrefix();
        String accountCode = accountData.getDateCode() + String.format("%02d", accountData.getNextIteration());
        String emailPrefix = accountData.getEmailPrefix();
        String emailSuffix = accountData.getEmailSuffix();
        
        // Accesses the First Name field
        driver.findElement(By.id("FirstName")).click();
        // Types the account name into the First Name field
        driver.findElement(By.id("FirstName")).sendKeys( account );
        // Types the account code into the Last Name field
        driver.findElement(By.id("LastName")).sendKeys( accountCode );
        // Types the edited email into the Email field
        driver.findElement(By.id("Email")).sendKeys(emailPrefix + "+" + accountCode + "@" + emailSuffix);
        // Types the account name and code into the Username field
        driver.findElement(By.id("username")).sendKeys(account + accountCode);
        // Types the password into the Password field
        driver.findElement(By.id("password")).sendKeys(accountData.getPassword());
        // Types the password into the Confirm Password field
        driver.findElement(By.id("ConfirmPassword")).sendKeys(accountData.getPassword());
        // Clicks the Continue button
        driver.findElement(By.cssSelector(".btn")).click();
    }

    /**
     * Runs script to fill out the Legal page
     */
    private void runLegalPage()
    {
        // Wait for page to finish loading
        rest(200);
        // Click TOS checkbox
        driver.findElement(By.id("PersonalUse")).click();
        // Click Continue button
        driver.findElement(By.cssSelector(".btn-success")).click();
    }

    /**
     * Runs script to fill out the Payment page
     */
    private void runPaymentPage()
    {
        // Switches to the Stripe card info iFrame
        driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/form/div[3]/div/div[1]/div/iframe")));
        // Clicks and then types in the card number into the Stripe card info box
        WebElement cardNumber = driver.findElement(By.name("cardnumber"));
        cardNumber.click();
        cardNumber.sendKeys("4242 4242 4242 4242");
        // Types all other card data into the Stripe card info box
        driver.findElement(By.name("exp-date")).sendKeys("04 / 24");
        driver.findElement(By.name("cvc")).sendKeys("242");
        driver.findElement(By.name("postal")).sendKeys("42424");
        // Returns to the default frame of the page and then clicks the "Continue" button
        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/form/div[5]/div[2]/button")).click();
    }

    /**
     * Temporarily rests the Thread, while also voiding any exceptions
     * @param millis time in milliseconds to rest for
     */
    private static void rest( int millis )
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception e) {}
    }

    /**
     * Waits until the next page completes loading
     * running script for next page
     * @param currURL the current URL
     */
    private void waitForPageLoad( String currURL )
    {
        while (driver.getCurrentUrl().compareTo(currURL) == 0)
        {
            rest(100);
        }
    }
}