package com.company;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader
{
    BufferedReader br;

    public String Reader()
    {
        String TextBox="";
        try {
            br = new BufferedReader(new FileReader("OriginText.txt"));
            String line;
            while((line=br.readLine())!=null)
            {
                TextBox+=line+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TextBox;
    }
}
