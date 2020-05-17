package main;
    
import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author Steve
 */
public class Crypt_DecryptClass {
    
    public byte[] crypto( boolean encrypting, String passphrase, byte[] input) throws Exception {
    
    String algorithm = "PBEWithMD5AndDES";
    byte[] salt = new byte[8];
    int iterations = 20;
    
    // Create a key from the supplied passphrase.
    KeySpec ks = new PBEKeySpec(passphrase.toCharArray());
    SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
    SecretKey key = skf.generateSecret(ks);
    
    // Read the input.
    ByteArrayInputStream instream = new ByteArrayInputStream(input);
    int length = (int)input.length;
    if (!encrypting) instream.read(salt); // if decrypting read the salt value first
    byte[] buffer = new byte[length - (encrypting ? 0 : 8)];
    instream.read(buffer); // read the input data content
    //instream.close();
    
    if (encrypting) {
      // Create the salt from eight bytes of the digest of P || M.
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(passphrase.getBytes());
      md.update(buffer);
      byte[] digest = md.digest();
      System.arraycopy(digest, 0, salt, 0, 8);
    }
   

    // Create the algorithm parameters.
    AlgorithmParameterSpec aps = new PBEParameterSpec(salt, iterations);
    // Encrypt or decrypt the input.
    Cipher cipher = Cipher.getInstance(algorithm);
    int mode = encrypting ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
    cipher.init(mode, key, aps);
    byte[] output = cipher.doFinal(buffer);
    
        // Write the output.
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    if (encrypting) out.write(salt);
    out.write(output);
    byte[] result = out.toByteArray();
    //out.close();

    return result;
  }
} // end class definition
