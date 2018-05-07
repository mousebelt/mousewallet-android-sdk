package module.nrlwallet.com.sdkmodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.common.io.BaseEncoding;
import java.security.SecureRandom;

import module.nrlwallet.com.bip39.MnemonicGenerator;
import module.nrlwallet.com.bip39.MnemonicValidator;
import module.nrlwallet.com.bip39.SeedCalculator;
import module.nrlwallet.com.bip39.Validation.InvalidChecksumException;
import module.nrlwallet.com.bip39.Validation.InvalidWordCountException;
import module.nrlwallet.com.bip39.Validation.UnexpectedWhiteSpaceException;
import module.nrlwallet.com.bip39.Validation.WordNotFoundException;
import module.nrlwallet.com.bip39.Words;
import module.nrlwallet.com.bip39.wordlists.English;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateMnemonic();
    }

    private void generateMnemonic(){
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
        System.out.println("************----------- Mnemonic : " + sb.toString());

        //Validate Mnemonic
        try {
            MnemonicValidator.ofWordList(English.INSTANCE).validate(sb);
        } catch (UnexpectedWhiteSpaceException e){

        } catch (InvalidWordCountException e){

        } catch (InvalidChecksumException e){

        } catch (WordNotFoundException e) {
        }

        //Generate a seed
        byte[] seed = new SeedCalculator().mnemonicToSeed(sb.toString(), "");
        String s_seed = BaseEncoding.base16().lowerCase().encode(seed);

        System.out.println("************----------- SEED : " + s_seed);
    }
}
