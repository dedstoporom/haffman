package com.company;

import java.io.*;

public class FileManager
{
    String line;
    BufferedReader br;
    BufferedWriter bw;
    public String Reader()
    {
        String TextBox="";
        try {
            br = new BufferedReader(new FileReader("OriginText.txt"));
            while((line=br.readLine())!=null)
            {
                TextBox+=line+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TextBox;
    }
    public void Writer(StringBuilder text) throws IOException {
            File file = new File("CodeText.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(text.toString());
            bw.close();
    }
}
