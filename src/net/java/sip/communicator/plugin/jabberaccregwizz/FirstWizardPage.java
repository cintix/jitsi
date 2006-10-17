/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.jabberaccregwizz;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>FirstWizardPage</tt> is the page, where user could enter the uin
 * and the password of the account.
 *
 * @author Yana Stamcheva
 * @author Damian Minkov
 */
public class FirstWizardPage extends JPanel
    implements WizardPage, DocumentListener {

    public static final String FIRST_PAGE_IDENTIFIER = "FirstPageIdentifier";

    private static final String GOOGLE_USER_SUFFIX = "gmail.com";
    private static final String GOOGLE_CONNECT_SRV = "talk.google.com";

    private JPanel uinPassPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel labelsPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JPanel valuesPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JLabel uinLabel = new JLabel(Resources.getString("uin"));

    private JLabel passLabel = new JLabel(Resources.getString("password"));

    private JTextField uinField = new JTextField();

    private JPasswordField passField = new JPasswordField();

    private JCheckBox rememberPassBox = new JCheckBox(
            Resources.getString("rememberPassword"));

    private JPanel advancedOpPanel = new JPanel(new BorderLayout(10, 10));

    private JPanel labelsAdvOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JPanel valuesAdvOpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    private JCheckBox sendKeepAliveBox = new JCheckBox(
            Resources.getString("sendKeepAlive"));

    private JCheckBox enableAdvOpButton = new JCheckBox(
        Resources.getString("ovverideServerOps"), false);

    private JLabel serverLabel = new JLabel(Resources.getString("server"));
    private JTextField serverField = new JTextField();

    private JLabel portLabel = new JLabel(Resources.getString("port"));
    private JTextField portField = new JTextField("5222");

    private JPanel mainPanel = new JPanel();

    private JabberAccountRegistration registration;

    private WizardContainer wizardContainer;

    /**
     * Creates an instance of <tt>FirstWizardPage</tt>.
     * @param registration the <tt>JabberAccountRegistration</tt>, where
     * all data through the wizard are stored
     * @param wizardContainer the wizardContainer, where this page will
     * be added
     */
    public FirstWizardPage(JabberAccountRegistration registration,
            WizardContainer wizardContainer) {

        super(new BorderLayout());

        this.wizardContainer = wizardContainer;

        this.registration = registration;

        this.setPreferredSize(new Dimension(300, 150));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        this.init();

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Initializes all panels, buttons, etc.
     */
    private void init() {
        this.uinField.getDocument().addDocumentListener(this);
        this.rememberPassBox.setSelected(true);

        labelsPanel.add(uinLabel);
        labelsPanel.add(passLabel);

        valuesPanel.add(uinField);
        valuesPanel.add(passField);

        uinPassPanel.add(labelsPanel, BorderLayout.WEST);
        uinPassPanel.add(valuesPanel, BorderLayout.CENTER);
        uinPassPanel.add(rememberPassBox, BorderLayout.SOUTH);

        uinPassPanel.setBorder(BorderFactory
                .createTitledBorder(Resources.getString("uinAndPassword")));

        mainPanel.add(uinPassPanel);

        serverField.setEditable(false);
        portField.setEditable(false);

        enableAdvOpButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
            // Perform action
            JCheckBox cb = (JCheckBox)evt.getSource();

            serverField.setEditable(cb.isSelected());
            portField.setEditable(cb.isSelected());
        }});

        portField.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e){}
            public void insertUpdate(DocumentEvent e)
            {
                setNextButtonAccordingToPort();
            }

            public void removeUpdate(DocumentEvent e)
            {
                setNextButtonAccordingToPort();
            }
        });

        labelsAdvOpPanel.add(serverLabel);
        labelsAdvOpPanel.add(portLabel);

        valuesAdvOpPanel.add(serverField);
        valuesAdvOpPanel.add(portField);

        JPanel checkBoxesPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        checkBoxesPanel.add(sendKeepAliveBox);
        checkBoxesPanel.add(enableAdvOpButton);

        advancedOpPanel.add(checkBoxesPanel, BorderLayout.NORTH);
        advancedOpPanel.add(labelsAdvOpPanel, BorderLayout.WEST);
        advancedOpPanel.add(valuesAdvOpPanel, BorderLayout.CENTER);

        advancedOpPanel.setBorder(BorderFactory
                .createTitledBorder(Resources.getString("advancedOptions")));

        mainPanel.add(advancedOpPanel);

        this.add(mainPanel, BorderLayout.NORTH);
    }

    /**
     * Implements the <code>WizardPage.getIdentifier</code> to return
     * this page identifier.
     */
    public Object getIdentifier() {
        return FIRST_PAGE_IDENTIFIER;
    }

    /**
     * Implements the <code>WizardPage.getNextPageIdentifier</code> to return
     * the next page identifier - the summary page.
     */
    public Object getNextPageIdentifier() {
        return WizardPage.SUMMARY_PAGE_IDENTIFIER;
    }

    /**
     * Implements the <code>WizardPage.getBackPageIdentifier</code> to return
     * the next back identifier - the default page.
     */
    public Object getBackPageIdentifier() {
        return WizardPage.DEFAULT_PAGE_IDENTIFIER;
    }

    /**
     * Implements the <code>WizardPage.getWizardForm</code> to return
     * this panel.
     */
    public Object getWizardForm() {
        return this;
    }

    /**
     * Before this page is displayed enables or disables the "Next" wizard
     * button according to whether the UIN field is empty.
     */
    public void pageShowing() {
        this.setNextButtonAccordingToUIN();
    }

    /**
     * Saves the user input when the "Next" wizard buttons is clicked.
     */
    public void pageNext() {
        registration.setUin(uinField.getText());
        registration.setPassword(new String(passField.getPassword()));
        registration.setRememberPassword(rememberPassBox.isSelected());

        registration.setServerAddress(serverField.getText());
        registration.setSendKeepAlive(sendKeepAliveBox.isSelected());
        try
        {
            registration.setPort(Integer.parseInt(portField.getText()));
        }
        catch (NumberFormatException ex)
        {}
    }

    /**
     * Enables or disables the "Next" wizard button according to whether the
     * UIN field is empty.
     */
    private void setNextButtonAccordingToUIN() {
        if (uinField.getText() == null || uinField.getText().equals("")) {
            wizardContainer.setNextFinishButtonEnabled(false);
        }
        else {
            wizardContainer.setNextFinishButtonEnabled(true);
        }
    }

    /**
     * Handles the <tt>DocumentEvent</tt> triggered when user types in the
     * UIN field. Enables or disables the "Next" wizard button according to
     * whether the UIN field is empty.
     */
    public void insertUpdate(DocumentEvent e) {
        this.setNextButtonAccordingToUIN();
        this.setServerFieldAccordingToUIN();
    }

    /**
     * Handles the <tt>DocumentEvent</tt> triggered when user deletes letters
     * from the UIN field. Enables or disables the "Next" wizard button
     * according to whether the UIN field is empty.
     */
    public void removeUpdate(DocumentEvent e) {
        this.setNextButtonAccordingToUIN();
        this.setServerFieldAccordingToUIN();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public void pageHiding() {
    }

    public void pageShown() {
    }

    public void pageBack() {
    }

    /**
     * Fills the UIN and Password fields in this panel with the data comming
     * from the given protocolProvider.
     * @param protocolProvider The <tt>ProtocolProviderService</tt> to load the
     * data from.
     */
    public void loadAccount(ProtocolProviderService protocolProvider) {
        AccountID accountID = protocolProvider.getAccountID();
        String password = (String)accountID.getAccountProperties()
            .get(ProtocolProviderFactory.PASSWORD);

        this.uinField.setText(accountID.getUserID());

        if(password != null) {
            this.passField.setText(password);
            this.rememberPassBox.setSelected(true);
        }
    }

    /**
     * Parse the server part from the jabber id and set it to server
     * as default value. If Advanced option is enabled Do nothing.
     */
    private void setServerFieldAccordingToUIN()
    {
        if(!enableAdvOpButton.isSelected())
        {
            String uin = uinField.getText();
            int delimIndex = uin.indexOf("@");
            if (delimIndex != -1)
            {
                String newServerAddr = uin.substring(delimIndex + 1);
                if(newServerAddr.equals(GOOGLE_USER_SUFFIX))
                    serverField.setText(GOOGLE_CONNECT_SRV);
                else
                    serverField.setText(newServerAddr);
            }
        }
    }

    /**
     * Disables Next Button if Port field value is incorrect
     */
    private void setNextButtonAccordingToPort()
    {
        try
        {
            String portValue = portField.getText();
            new Integer(portField.getText());
            wizardContainer.setNextFinishButtonEnabled(true);
        }
        catch (NumberFormatException ex)
        {
             wizardContainer.setNextFinishButtonEnabled(false);
        }
    }
}
