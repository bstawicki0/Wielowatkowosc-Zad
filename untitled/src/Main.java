import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ======================
// MODEL
// ======================
class UzytkownikModel {
    // Symulacja walidacji logowania z opóźnieniem (np. połączenie z bazą)
    public boolean walidujLogowanie(String user, String pass) {
        try {
            Thread.sleep(2500); // symulacja długiej operacji
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "admin".equals(user) && "haslo123".equals(pass);
    }
}

// ======================
// VIEW
// ======================
class GlownyView extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginButton;
    JLabel statusLabel;

    public GlownyView() {
        super("System Logowania");

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        loginButton = new JButton("Zaloguj");
        statusLabel = new JLabel("Wprowadź dane do logowania.");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Login:"), gbc);
        gbc.gridx = 1; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Hasło:"), gbc);
        gbc.gridx = 1; add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(loginButton, gbc);
        gbc.gridy = 3; add(statusLabel, gbc);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
    }
}

// ======================
// CONTROLLER
// ======================
class GlownyController {
    private UzytkownikModel model;
    private GlownyView view;

    public GlownyController(UzytkownikModel model, GlownyView view) {
        this.model = model;
        this.view = view;

        view.loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Wyłączamy przycisk i ustawiamy status przed rozpoczęciem pracy w tle
                view.loginButton.setEnabled(false);
                view.statusLabel.setText("Trwa weryfikacja danych...");

                // SwingWorker do asynchronicznej walidacji
                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() {
                        // Wywołanie walidacji w tle
                        String user = view.userField.getText();
                        String pass = new String(view.passField.getPassword());
                        return model.walidujLogowanie(user, pass);
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean wynik = get(); // pobranie wyniku z doInBackground
                            if (wynik) {
                                view.statusLabel.setText("Logowanie pomyślne!");
                            } else {
                                view.statusLabel.setText("Błędny login lub hasło!");
                            }
                        } catch (Exception ex) {
                            view.statusLabel.setText("Wystąpił błąd: " + ex.getMessage());
                        } finally {
                            view.loginButton.setEnabled(true); // zawsze włączamy przycisk
                        }
                    }
                }.execute();
            }
        });
    }
}

// ======================
// MAIN
// ======================
class SystemLogowaniaMVC {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UzytkownikModel model = new UzytkownikModel();
            GlownyView view = new GlownyView();
            new GlownyController(model, view);
            view.setVisible(true);
        });
    }
}