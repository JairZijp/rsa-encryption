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

import java.lang.reflect.Array;
import java.math.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Main.
 */
public class Main extends Application {

    private final static BigInteger one = new BigInteger("1");
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
        Text text2 = new Text("Password");
        title1.setStyle("-fx-font: 18 arial;");

        TextField textField1 = new TextField();
        TextField message = new TextField();
        textField1.setPromptText("value of n");
        message.setPromptText("value of m");

        Button eButton1 = new Button("Step 1");
        Button eButton2 = new Button("Step 2");
        Button eButton3 = new Button("Step 3");
        Button button2 = new Button("Submit");

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPadding(new Insets(15, 0, 15, 0));
        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        separator2.setPadding(new Insets(15, 0, 15, 0));

        // Gridpane settings
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(600, 400);

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

        //Creating a scene object
        Scene scene = new Scene(gridPane);

        //Setting title to the Stage
        stage.setTitle("RSA Encyption & Decryption");
        stage.setScene(scene);


        // Step 1 generate n
        eButton1.setOnAction(e -> {
            //reset p & q
            pq.clear();
            Integer n = Integer.valueOf(textField1.getText());

            // measure time
            long startTime = System.currentTimeMillis();
            pq = calculatePQ(n);
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
            eResult.setText("q is " + e.toString());
        });

        // Step 3 encrypt message
        eButton3.setOnAction(action -> {
            BigInteger[] encryptedMessage = encrypt(message.getText());

            for( int i = 0 ; i < encryptedMessage.length ; i++ )
            {
                if( i != encryptedMessage.length - 1 );
            }

            // Decryption
            String decrypt = decrypt(encryptedMessage);

            mResult.setText("Message after encryption is: \n" + Arrays.toString(encryptedMessage));
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
        BigInteger n = new BigInteger(String.valueOf(formInt));
        BigInteger p = INIT_NUMBER;

        //For each p
        while(p.compareTo(n.divide(INIT_NUMBER)) <= 0){

            //If p is found
            if(n.mod(p).equals(BigInteger.ZERO)){
                // q = n/p
                BigInteger q = n.divide(p);
                pq.add(p);
                pq.add(q);
            }
            //p = the next prime number
            p = p.nextProbablePrime();
        }
        return pq;
    }

    /**
     * Generate e big integer.
     *
     * @param p the p
     * @param q the q
     * @return the big integer
     */
    void generateE(BigInteger p, BigInteger q) {
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
        n  = p.multiply(q);

        SecureRandom rnd = new SecureRandom();

        do e = BigInteger.probablePrime(phi.bitLength(),rnd);
        while (e.compareTo(one) <= 0
                || e.compareTo(phi) >= 0
                || !e.gcd(phi).equals(one));

        // private key
        d = e.modInverse(phi);
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
        byte[] digits = message.getBytes();

        BigInteger[] bigdigits = new BigInteger[digits.length] ;

        for( i = 0 ; i < bigdigits.length ; i++ )
        {
            temp[0] = digits[i] ;
            bigdigits[i] = new BigInteger( temp ) ;
        }

        BigInteger[] encrypted = new BigInteger[bigdigits.length] ;

        for( i = 0 ; i < bigdigits.length ; i++ )
            encrypted[i] = bigdigits[i].modPow( e, n ) ;

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

        for( i = 0 ; i < decrypted.length ; i++ )
            decrypted[i] = encrypted[i].modPow( d, n ) ;

        char[] charArray = new char[decrypted.length] ;

        for( i = 0 ; i < charArray.length ; i++ )
            charArray[i] = (char) ( decrypted[i].intValue() ) ;
        return( new String( charArray ) ) ;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) { launch(args); }

}
