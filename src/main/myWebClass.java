package main;

import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Steve
 */
public class myWebClass {

    private int responseCode;
    private String baseurl = "https://crainengineering.net/secfolder/secdata.php";
    //private String baseurl = "http://crainengineering.net/secfolder/secdata.php";
    private String ResponseMsg;

    void myWebClass() {

        this.responseCode = 0;
        this.baseurl = "https://crainengineering.net/secfolder/secdata.php";
        //this.baseurl = "http://crainengineering.net/secfolder/secdata.php";
        this.ResponseMsg = "";
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponseMsg() {
        return this.ResponseMsg;
    }

//*******************************************************************************************
//        Read Website Information file    
//*******************************************************************************************
    public byte[] getWebData(String command, String account) throws IOException {

        //System.out.println("Reading from Web...");
        String httpsURL = "";
        switch (command) {
            case "listfiles":
                httpsURL = this.baseurl + "?listfiles";
                break;
            case "read":
                httpsURL = this.baseurl + "?account=" + account + ".dat";
                break;
            default:
                System.out.println("Error reading Web data. Command not recognized");
                break;
        }
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        //HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
        con.setConnectTimeout(5000);
        con.setUseCaches(false);
        InputStream ins = con.getInputStream();
        BufferedInputStream isr = new BufferedInputStream(ins);
        //BufferedReader br = new BufferedReader(isr);
        ByteArrayOutputStream outMemStream = new ByteArrayOutputStream();

        //String line;
        int i;
        while ((i = isr.read()) != -1) {
            outMemStream.write(i);
        }
        outMemStream.flush();
        byte[] result = outMemStream.toByteArray();
        //System.out.println(new String(result, "UTF-8"));
        this.responseCode = con.getResponseCode();
        return result;
    }

//*******************************************************************************************
//        Write Website Information file    
//*******************************************************************************************
    public boolean postWebData(String account, byte[] data) throws IOException {

        //System.out.println("Writing to Web...");

        String attachmentName = "mydata";
        String attachmentFileName = account + ".dat";
        //System.out.println("Attachment file name:" + attachmentFileName); 
        String charset = "UTF-8";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "------------------------" + String.valueOf(System.currentTimeMillis()); // Just generate some unique random value.
        //System.out.println("Boundary text: " + boundary); 

        String URLstring = this.baseurl;
        URL url = new URL(URLstring);
        //System.out.println("URL: " + this.baseurl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        //HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(5000);
        con.setUseCaches(false);
        con.setDoOutput(true);
        con.setRequestMethod("POST");

        //add request header
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        //con.addRequestProperty("Accept", "*/*");
        //con.setRequestProperty("Type", "plain/text");
        try ( // Start the content wrapper
                DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(twoHyphens + boundary + crlf);
            wr.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            wr.writeBytes("Content-Type: application/octet-stream" + crlf);
            wr.writeBytes(crlf);
            // Write the actual data
            //System.out.println("Writing Data");
            byte[] bytedata = data;
            wr.write(bytedata);
            // End the content wrapper
            wr.writeBytes(crlf);
            wr.writeBytes(twoHyphens + boundary + twoHyphens);
            wr.flush();
        }

        // Now read the response
        this.responseCode = con.getResponseCode();
        //System.out.println("POST response code: " + this.responseCode);

        InputStream ins = new BufferedInputStream(con.getInputStream());
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader br = new BufferedReader(isr);

        String line;
        String result = "";
        while ((line = br.readLine()) != null) {
            result += line + "\n";
        }
        this.ResponseMsg = result;
        return (result.startsWith("***Success***"));
    }

//*******************************************************************************************
//        Create New Account
// This routine sends the request as a GET and reads the response as an ordinary string
//*******************************************************************************************
    public boolean createAccount(String acct) throws IOException {
        //System.out.println("Sending new Account Request to Web..."); 

        String httpsURL = this.baseurl + "?newaccount=" + acct;
        //System.out.println("URL sent is: " + httpsURL);
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        //HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
        con.setConnectTimeout(5000);
        con.setUseCaches(false);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        this.responseCode = con.getResponseCode();

        String result, line;
        result = "";
        while ((line = br.readLine()) != null) {
            //System.out.println(line);
            result += line + "\n";
        }

        this.ResponseMsg = result;
        return (result.startsWith("***Success***"));
    }

} // end class definition
