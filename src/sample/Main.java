package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import java.math.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Main.
 */
public class Main extends Application {

    private final static BigInteger ONE = new BigInteger("1");
    public static final BigInteger INIT_NUMBER = new BigInteger("2");

    List<BigInteger> pq = new ArrayList<>();
    BigInteger p, q, e, d, n;

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        // Create elements
        Text title1 = new Text("Encryption");
        Text pResult = new Text();
        Text qResult = new Text();
        Text eResult = new Text();
        Text mResult = new Text();
        Text time = new Text();

        // Decryption
        Text title2 = new Text("Decryption");
        Text dResult = new Text();
        Text mResult2 = new Text();

        title1.setStyle("-fx-font: 18 arial;");
        title2.setStyle("-fx-font: 18 arial;");

        TextField textField1 = new TextField();
        TextField message = new TextField();

        // Decryption
        TextField nValue = new TextField();
        TextField eValue = new TextField();
        TextField cValue = new TextField();

        textField1.setPromptText("value of n");
        message.setPromptText("value of m");
        nValue.setPromptText("value of n");
        eValue.setPromptText("value of e");
        cValue.setPromptText("value of c. Format: 0,0,0...6");

        Button eButton1 = new Button("Step 1");
        Button eButton2 = new Button("Step 2");
        Button eButton3 = new Button("Step 3");
        Button dButton1 = new Button("Step 1");
        Button dButton2 = new Button("Step 2");

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(15, 0, 15, 0));
        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        separator2.setPadding(new Insets(15, 0, 15, 0));
        Separator separator3 = new Separator(Orientation.HORIZONTAL);
        separator3.setPadding(new Insets(15, 0, 15, 0));

        // Gridpane settings
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(800, 600);

        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        // Encryption - 1
        gridPane.add(title1, 0, 0);
        gridPane.add(textField1, 0, 1);
        gridPane.add(eButton1, 0, 2);
        gridPane.add(pResult, 0, 3);
        gridPane.add(qResult, 0, 4);
        gridPane.add(time, 0, 5);
        gridPane.add(separator, 0, 6);

        // 2
        gridPane.add(eButton2, 0, 7);
        gridPane.add(eResult, 0, 8);
        gridPane.add(separator2, 0, 9);

        // 3
        gridPane.add(message, 0, 10);
        gridPane.add(eButton3, 0, 11);
        gridPane.add(mResult, 0, 12);

        // Decryption - 1
        gridPane.add(title2, 1, 0);
        gridPane.add(nValue, 1, 1);
        gridPane.add(eValue, 1, 2);
        gridPane.add(dButton1, 1, 3);
        gridPane.add(dResult, 1, 4);
        gridPane.add(separator3, 1, 5);

        gridPane.add(cValue, 1, 6);
        gridPane.add(dButton2, 1, 7);
        gridPane.add(mResult2, 1, 8);

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("RSA Encyption & Decryption");
        stage.setScene(scene);


        // Step 1 generate n
        eButton1.setOnAction(action -> {
            //reset p & q
            pq.clear();
            Integer N = Integer.valueOf(textField1.getText());

            // measure time
            long startTime = System.currentTimeMillis();
            pq = calculatePQ(N);
            long stopTime = System.currentTimeMillis();

            p = pq.get(0);
            q = pq.get(1);

            time.setText("Amount of time busy finding p and q: " + (stopTime - startTime) + "ms");
            pResult.setText("p is " + p.toString());
            qResult.setText("q is " + q.toString());
        });

        // Step 2 generate e
        eButton2.setOnAction(action -> {
            generateE(p,q);
            eResult.setText("e is " + e.toString());
        });

        // Step 3 encrypt message
        eButton3.setOnAction(action -> {
            BigInteger[] encryptedMessage = encrypt(message.getText());

            for( int i = 0 ; i < encryptedMessage.length ; i++ )
            {
                if( i != encryptedMessage.length - 1 );
            }

            // Decryption, testing purposes
            String decrypt = decrypt(encryptedMessage);

            mResult.setText("Message after encryption is: \n" + Arrays.toString(encryptedMessage));
            System.out.println(decrypt);
        });

        // Decrypt Step 1, calculate d
        dButton1.setOnAction(action -> {
            //reset p & q
            pq.clear();

            Integer N = Integer.valueOf(nValue.getText());
            Integer E = Integer.valueOf(eValue.getText());
            e = BigInteger.valueOf(E);
            n = BigInteger.valueOf(N);

            pq = calculatePQ(N);
            p = pq.get(0);
            q = pq.get(1);
            generateD(e);

            dResult.setText("d is " + d.toString());
        });

        // Decrypt Step 2, decrypt c
        dButton2.setOnAction(action -> {

            System.out.println(Arrays.toString(cValue.getText().split(",")));

            String[] strings = cValue.getText().split(",");
            BigInteger[] encrypted = new BigInteger[strings.length];
            for (int i = 0; i < strings.length; i++) {
                encrypted[i] = new BigInteger(String.valueOf(strings[i]).replaceAll("\\s+",""));
            }

            String decrypt = decrypt(encrypted);
            mResult2.setText("Message after decryption is: " + decrypt);
        });

        stage.show();
    }

    /**
     * Calculate pq list.
     *
     * @param formInt the form int
     * @return the list
     */
    List<BigInteger> calculatePQ(int formInt) {
        BigInteger N = new BigInteger(String.valueOf(formInt));
        BigInteger P = INIT_NUMBER;

        //For each P
        while(P.compareTo(N.divide(INIT_NUMBER)) <= 0){

            //If n mod p = 0, q = n/p
            if(N.mod(P).equals(BigInteger.ZERO)){
                // q = n/p
                BigInteger Q = N.divide(P);
                pq.add(P);
                pq.add(Q);
            }
            //P = the next prime number
            P = P.nextProbablePrime();
        }
        return pq;
    }

    /**
     * Generate e big integer.
     *
     * @param P
     * @param Q
     * @return the big integer
     */
    void generateE(BigInteger P, BigInteger Q) {
        // Euler totient, phi = (p-1)(q-1)
        BigInteger phi = (P.subtract(ONE)).multiply(Q.subtract(ONE));
        // n = p*q
        n  = P.multiply(Q);

        SecureRandom random = new SecureRandom();

        do e = BigInteger.probablePrime(phi.bitLength(),random);
        while (e.compareTo(ONE) <= 0
                || e.compareTo(phi) >= 0
                || !e.gcd(phi).equals(ONE));

        // for testing
        d = e.modInverse(phi);
    }

    void generateD(BigInteger E) {
        // Euler totient, phi = (p-1)(q-1)
        BigInteger phi = (p.subtract(ONE)).multiply(q.subtract(ONE));

        //private key, d = E^-1 mod phi
        d = E.modInverse(phi);
    }

    /**
     * Encrypt big integer.
     *
     * @param message the message
     * @return the big integer
     */
    BigInteger[] encrypt(String message) {
        int i ;
        byte[] temp = new byte[1];

        // convert string to byte array
        byte[] bytes = message.getBytes();
        BigInteger[] bigInts = new BigInteger[bytes.length] ;

        // add the bytes to the bigint array
        for( i = 0 ; i < bigInts.length ; i++ ) {
            temp[0] = bytes[i] ;
            bigInts[i] = new BigInteger( temp ) ;
        }
        BigInteger[] encrypted = new BigInteger[bigInts.length] ;

        // encrypt every bigInt with modPow(e,n)
        // bigInts[i]^e % n
        for( i = 0 ; i < bigInts.length ; i++ )
            encrypted[i] = bigInts[i].modPow( e, n ) ;

        return( encrypted ) ;
    }

    /**
     * Decrypt big integer.
     *
     * @param encrypted the encrypted
     * @return the big integer
     */
    String decrypt (BigInteger[] encrypted) {
        int i ;
        BigInteger[] decrypted = new BigInteger[encrypted.length] ;

        // fill decrypted array with encrypted int: encrypted[i]^e % n
        for( i = 0 ; i < decrypted.length ; i++ )
            decrypted[i] = encrypted[i].modPow( d, n ) ;

        char[] charArray = new char[decrypted.length] ;

        // Add characters to char[] and return string
        for( i = 0 ; i < charArray.length ; i++ )
            charArray[i] = (char) ( decrypted[i].intValue() ) ;
        return( new String( charArray ) ) ;
    }

    public static void main(String[] args) { launch(args); }

}
