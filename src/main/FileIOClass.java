package main;

import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Steve
 */
public class FileIOClass {
    
    private List<String> acctIDList = new ArrayList<String>();
    private String latestacctID;
    private String acctComputerID;
    private final String dbversion = "1.0";
    
    FileIOClass() {
        this.acctIDList.add("null");
        this.latestacctID = "null";
        this.acctComputerID = "null";
    }
    
    public List<String> getAcctIDList() {
        return this.acctIDList;
    }
    
    public String getLatestAcctID() {
        return this.latestacctID;
    }

    public boolean readConfigFile(String filename) {        
            try {
                File accf = new File(filename);
                if (accf.exists()) {    // file exists, so read it
                    //System.out.println("File exists");
                    this.acctIDList.clear(); 
                    BufferedReader br = new BufferedReader(new FileReader(filename));
                    String line = new String();
                    line = br.readLine();
                    if (!line.startsWith("database")) { //old database, delete and recreate
                        br.close();
                        accf.delete();
                        configfileInitialize(filename);
                    }
                    while((line = br.readLine()) != null) {
                        String[] words = line.split("\t");
                        switch (words[0])
                        {
                            case "account":   this.acctIDList.add(words[1].trim()); 
                            case "latestID":  this.latestacctID = words[1].trim();
                            case "machineID": this.acctComputerID = words[1].trim(); 
                        }
                    }
                    br.close();
                }
                else {  // file didn't exists, so create and initialize it
                    configfileInitialize(filename);
                    }
            } catch (IOException e) {
                //System.out.println("***Error***" + e);
                return false;
            }
           
        return true;
    }
    
    private void configfileInitialize(String filename) throws IOException {

        FileWriter acctf = new FileWriter(filename);
        acctf.write("database\t" + dbversion + System.lineSeparator());
        for (String str : this.acctIDList) {
            acctf.write("account\t" + str + System.lineSeparator());
        }
        acctf.write("latestID\t" + latestacctID + System.lineSeparator());
        this.acctComputerID = String.valueOf(Instant.now().getEpochSecond());
        acctf.write(this.acctComputerID);
        acctf.close();
    }
    
    public boolean saveAcctData(String filename, String latestid) {

        List<String> cleanlist = new ArrayList<String>();
        
        this.latestacctID = latestid;
        cleanlist = cleanDuplicates();
        try {
            File accf = new File(filename);
            if (accf.exists()) {    // file exists, so write it
                FileWriter acctf = new FileWriter(filename);
                acctf.write("database\t" + dbversion + System.lineSeparator());
                for (String str : cleanlist) {
                    acctf.write("account\t" + str + System.lineSeparator());
                }
                acctf.write("latestID\t" + latestacctID + System.lineSeparator());
                acctf.write("machineID\t" + this.acctComputerID + System.lineSeparator());
                acctf.close();
            }
        } catch (IOException e) {
                //System.out.println("***Error***" + e);
                return false;
            }           
        return true;        
    }
    
    public boolean clearHistory(String filename) {

        try {
            File accf = new File(filename);
            if (accf.exists()) {    // file exists, so write it
                FileWriter acctf = new FileWriter(filename);
                acctf.write("database\t" + dbversion + System.lineSeparator());
                acctf.write("latestID\t" + latestacctID + System.lineSeparator());
                acctf.write("machineID\t" + this.acctComputerID + System.lineSeparator());
                acctf.close();
            }
        } catch (IOException e) {
                //System.out.println("***Error***" + e);
                return false;
            }           
        return true;        
    }
    
    private List<String> cleanDuplicates() {
        List<String> newlist = new ArrayList<String>();
        newlist.add(latestacctID);
        for (String str : this.acctIDList) {
            boolean flg=false;
            for (String tmp: newlist) {
                if (tmp.equals(str)) flg = true;
            }
            if (!flg && !str.equals("null")) {
                newlist.add(str);
            }
        }
        return newlist;
    }


}
