import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

public class app extends Component implements ActionListener {
    private JButton btnWybierzPlik;
    private JPanel glownyPanel;
    private JPasswordField passwordField1;
    private JButton infoButton;
    private JButton szyfrujButton;
    private JButton odszyfrujButton;
    private JTextField fieldSciezka;
    private JTextField fieldSciezka2;

    static private final String newline = "\n";
    JButton openButton, saveButton;
    JTextArea log;

    private JFileChooser fc;

    private char[][] tablica_szyfrujaca;
    String tekstJawny;
    String tekstOdszyfrowany;
    String tekstZaszyfrowany;
    String kluczSzyfrujacy;
    int liczbaKolumn;
    int liczbaWierszy = 4;
    int rozmiarBloku;
    int przesuniecie;
    private boolean wybranoPlik;
    private boolean wpisanoKlucz;
    private String CalyPlik = "";

    public app() {
        btnWybierzPlik.addActionListener(this);
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String informacje = "Działanie aplikacji:\n\n";
                informacje += "1. Wpisz conajmniej 9-cio znakowy klucz szyfrujący\n";
                informacje += "2. Wybierz plik tekstowy do zaszyfrowania\n";
                informacje += "3. Użyj przycisku \"Szyfruj\", żeby zaszyfrować wybrany plik (Uwaga! Kasuje plik szyfrowany!)\n";
                informacje += "4. Po zaszyfrowaniu wpisany klucz szyfrujący znika! Żeby odszyfrować plik:\n";
                informacje += "    wpisz klucz, wybierz plik, użyj przycisku \"Odszyfruj\"\n";

                JOptionPane.showMessageDialog(null, informacje);
            }
        });

        //nacisniecie przycisku "Szyfruj"
        szyfrujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!wybranoPlik) {
                    JOptionPane.showMessageDialog(null, "Wybierz plik do zaszyfrowania! ");
                    return;
                }
                if (!wpisanoKlucz) {
                    JOptionPane.showMessageDialog(null, "Wpisz klucz szyfrujący! ");
                    return;
                }

                File file_out = new File(fieldSciezka2.getText());
                File file_in = new File(fieldSciezka.getText());
                try {
                    tekstOdszyfrowany =  szyfruj(tekstJawny, kluczSzyfrujacy, liczbaKolumn, liczbaWierszy);

                    file_out.createNewFile();
                    FileWriter bw = new FileWriter(file_out);
                    bw.write(tekstOdszyfrowany);
                    bw.flush();
                    bw.close();
                    file_in.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                wybranoPlik = false;
                wpisanoKlucz = false;
                fieldSciezka.setText("");
                fieldSciezka2.setText("");
                passwordField1.setText("");
                JOptionPane.showMessageDialog(null, "Plik zaszyfrowany!");
            }
        });

        //nacisniecie przycisku "Odszyfruj"
        odszyfrujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!wybranoPlik) {
                    JOptionPane.showMessageDialog(null, "Wybierz plik do odszyfrowania! ");
                    return;
                }
                if (!wpisanoKlucz) {
                    JOptionPane.showMessageDialog(null, "Wpisz klucz do odszyfrowania! ");
                    return;
                }

                File file_in = new File(fieldSciezka.getText());
                String nowyPlik[] = fieldSciezka.getText().split("-x");
//                if (nowyPlik.length > 1)
//                    fieldSciezka2.setText(nowyPlik[0] + nowyPlik[1]);
//                else
//                    fieldSciezka2.setText(nowyPlik[0]);
                File file_out = new File(fieldSciezka2.getText());

                try {
                    tekstZaszyfrowany = CalyPlik;
                    tekstOdszyfrowany =  odszyfruj(tekstZaszyfrowany, kluczSzyfrujacy, liczbaKolumn, liczbaWierszy);

                    file_out.createNewFile();
                    FileWriter bw = new FileWriter(file_out);
                    bw.write(tekstOdszyfrowany);
                    bw.flush();
                    bw.close();
//                    file_in.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                wybranoPlik = false;
                wpisanoKlucz = false;
                fieldSciezka.setText("");
                fieldSciezka2.setText("");
                passwordField1.setText("");
                JOptionPane.showMessageDialog(null, "Plik odszyfrowany!");

            }

        });

        glownyPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

            }
        });

        // po wpisaniu hasła - obliczenie rozmiaru bloku i rozszerzenie kluczSzyfrujacy do tego rozmiaru
        passwordField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                super.focusLost(focusEvent);
                if (passwordField1.getText().length() < 9) {
                    JOptionPane.showMessageDialog(null, "Hasło musi mieć conajmniej 8 znaków!");
                    wpisanoKlucz = false;
                }
                else {
                    kluczSzyfrujacy = passwordField1.getText();
//                przesuniecie = (int)(kluczSzyfrujacy.toCharArray()[1]);
                    wpisanoKlucz = true;
                }

//                liczbaKolumn = (int)kluczSzyfrujacy.charAt(0);
//                liczbaKolumn /= 13;
//                if (liczbaKolumn < 4) liczbaKolumn = 4;
                liczbaKolumn = 5;
                rozmiarBloku = liczbaKolumn * liczbaWierszy;

//                przesuniecie = (int)kluczSzyfrujacy.charAt(1);
//                przesuniecie /= 31;
//                if (przesuniecie < 4) przesuniecie = 4;
                przesuniecie = 0;

//                kluczSzyfrujacy = kluczSzyfrujacy.substring(2);

                if (kluczSzyfrujacy.length() < rozmiarBloku) {
                    String tmp = new String(kluczSzyfrujacy);
                    //char znak = 'a';    //znak uzupełniający klucz szyfrujący
                    for (int i = tmp.length(); i < rozmiarBloku; i++) {
                        tmp += kluczSzyfrujacy.charAt(i % kluczSzyfrujacy.length());
                        //tmp += znak;
//                        znak++;
                    }
                    kluczSzyfrujacy = tmp;
                }
            } //public void focusLost(..)
        });


    } // public app()

    //uruchamianie aplikacji
    public static void main(String[] args) {
        JFrame frame = new JFrame("Szyfrator");
        frame.setContentPane(new app().glownyPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true );

    } // public static void main()

    @Override
    //Wczytanie pliku
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnWybierzPlik) {
            int returnVal = fc.showOpenDialog(app.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                wybranoPlik = true;
                CalyPlik = "";
                fieldSciezka.setText(file.getAbsolutePath());

                try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                    for(String line; (line = br.readLine()) != null; ) {
                        CalyPlik += (line + "\n");
                        String nowyPlik[] = file.getAbsolutePath().split("\\.");
                        fieldSciezka2.setText(nowyPlik[0] + "-x." + nowyPlik[1]);
                    }
                    tekstJawny = CalyPlik;
                }
                catch (Exception exc) {}
            } //if
        } //if
    } //public void actionPerformed

    private String szyfruj(String szyfrowany, String klucz, int ileKolumn, int ileWierszy) {
        if (szyfrowany.length() == 0) return "";
        int blokRozmiar = ileKolumn * ileWierszy;
        int ileBlokow;
        char[][] blok = new char[ileKolumn][ileWierszy];
        String zaszyfrowany = new String();

        ileBlokow = szyfrowany.length() / blokRozmiar;

        //uzupełnienie szyfrowanego tekstu do pełnego bloku
        if (szyfrowany.length() % blokRozmiar != 0) {
            ileBlokow++;
            for (int i = szyfrowany.length(); i < ileBlokow * blokRozmiar; i++)
                szyfrowany += ' ';
        }

        String tmp = "";
        // kodowanie przez dodanie klucza szyfrującego
        for (int i = 0; i < ileBlokow; i++)
            for (int j = 0; j < ileKolumn; j++)
                for (int k = 0; k < ileWierszy; k++) {
                    try {
                        int nrZnaku = (i * ileKolumn * ileWierszy) + (j * ileWierszy) + k;
                        //tmp = "i-" +i+",j-" + j + ",k-" + k + ",nrZnaku-" + nrZnaku;
                        int znakTMP = (int) (szyfrowany.charAt(nrZnaku));
                        int znakTMP2 = (int) klucz.charAt(nrZnaku % blokRozmiar);
                        znakTMP = (znakTMP+znakTMP2)%128;
                        char kolejnyZnak = (char) (znakTMP);
                        //tmp += ",kolejnyZnak-" + kolejnyZnak;
                        zaszyfrowany += kolejnyZnak;

                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        JOptionPane.showMessageDialog(null, tmp);
                    }
                }
        String zaszyfrowany2 = new String();

        // permutacja w ramach bloku
        for (int i = 0; i < ileBlokow; i++) {

            for (int j = 0; j < ileKolumn; j++)
                for (int k = 0; k < ileWierszy; k++) {
                    int nrZnaku = (i * ileKolumn * ileWierszy) + (j * ileWierszy) + k;
                    blok[j][k] = zaszyfrowany.charAt(nrZnaku);
                }

            for (int k = 0; k < ileWierszy; k++)
                for (int j = 0; j < ileKolumn; j++) {
                    zaszyfrowany2 += blok[j][k];
                }
        }

        return zaszyfrowany2;
    } //private void szyfruj()

    private String odszyfruj(String zaszyfrowany, String klucz, int ileKolumn, int ileWierszy) {
        if (zaszyfrowany.length() == 0) return "";
        int blokRozmiar = ileKolumn * ileWierszy;
        int ileBlokow;
        char[][] blok = new char[ileKolumn][ileWierszy];
        String odszyfrowany = new String();

        ileBlokow = zaszyfrowany.length() / blokRozmiar;

        // odwrócenie permutacji w ramach bloków
        String zaszyfrowany2 = new String();
        for (int i = 0; i < ileBlokow; i++) {

            for (int k = 0; k < ileKolumn; k++)
                for (int j = 0; j < ileWierszy; j++) {
                    int nrZnaku = (i * rozmiarBloku) + (j * ileKolumn) + k;
                    blok[k][j] = zaszyfrowany.charAt(nrZnaku);
//                    System.out.println(k + " - " + j + " - " + nrZnaku);
                }

            for (int k = 0; k < ileKolumn; k++)
                for (int j = 0; j < ileWierszy; j++) {
                    zaszyfrowany2 += blok[k][j];
                }
        } // for

        //odszyfrowany = zaszyfrowany2;
        zaszyfrowany = zaszyfrowany2;

        String tmp = "";
        // odkodowanie przy użyciu klucza szyfrującego
        for (int i = 0; i < ileBlokow; i++)
            for (int j = 0; j < ileKolumn; j++)
                for (int k = 0; k < ileWierszy; k++) {
                    try {
                        int nrZnaku = (i * blokRozmiar) + (j * ileWierszy) + k;
                        int znakTMP = (int) (zaszyfrowany.charAt(nrZnaku));
                        int znakTMP2 = (int) klucz.charAt(nrZnaku % blokRozmiar);
                        znakTMP = (znakTMP - znakTMP2 + 128)%128;
                        char kolejnyZnak = (char) (znakTMP);
                        //tmp += ",kolejnyZnak-" + kolejnyZnak;
                        odszyfrowany += kolejnyZnak;
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        JOptionPane.showMessageDialog(null, tmp);
                    }
                }

        return odszyfrowany;
    } // private String odszyfruj(..)

} //public class app
