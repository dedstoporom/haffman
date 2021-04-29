package com.company;
import sun.security.util.BitArray;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
public class Main
{
    public static void main(String[] args) throws IOException {
        FileManager fileManager=new FileManager();
        String text=fileManager.Reader();
        File file = new File("CodeText.huf");
        TreeMap<Character,Integer> freq=freq(text);
        System.out.println(text);
        System.out.println(freq);
      ArrayList<CodeTree> codeTreeArrayList=new ArrayList<>();
      for(Character c:freq.keySet())//перебор всех с в freq
      {
          codeTreeArrayList.add(new CodeTree(c,freq.get(c)));
      }
      CodeTree tree=huffman(codeTreeArrayList);
       //TreeMap с символом и его кодом
      TreeMap<Character,String> CodeMap=new TreeMap<>();
      for(Character c:freq.keySet())
      {
          CodeMap.put(c, tree.CodeComb(c,""));
      }
      System.out.println(CodeMap);
      ////Для вывода веса
        StringBuilder encoded=new StringBuilder();
        for(int i=0;i<text.length();i++)
        {
            encoded.append(CodeMap.get(text.charAt(i)));
        }
//        System.out.println(encoded);
        System.out.println("Размер исходной строки: " + text.getBytes().length * 8 + " бит");
        System.out.println("Размер сжатой строки: " + encoded.length() + " бит");
        System.out.println("Расшифровка:"+decoder(encoded.toString(),tree));
        fileManager.Writer(encoded);
        saveToFile(file, freq, encoded.toString());
    }

    //Нахождение частот каждого символа
    private static TreeMap<Character,Integer> freq(String text)
    {
        TreeMap<Character,Integer> freqmap=new TreeMap<>();
        Character c;
        Integer cout;
        for(int i=0;i<text.length();i++)
        {
            c=text.charAt(i);
            cout=freqmap.get(c);
            if(cout!=null)
            { cout+=1;}
            else {cout=1;}
            freqmap.put(c,cout);
        }
        return freqmap;
    }
    //сортировка по Хаффману.Объединение элементов
    private static CodeTree huffman(ArrayList<CodeTree> codeTreeArrayList)
    {
        while (codeTreeArrayList.size() > 1)//удаляет,совмещает и добавляет
        {
            Collections.sort(codeTreeArrayList);
            CodeTree left=codeTreeArrayList.remove(codeTreeArrayList.size()-1);
            CodeTree right=codeTreeArrayList.remove(codeTreeArrayList.size()-1);
            CodeTree parent=new CodeTree(null, right.weight+ left.weight,left,right);
            codeTreeArrayList.add(parent);
        }
        return codeTreeArrayList.get(0);
    }
    private static String decoder(String encoded,CodeTree tree)//Расшифровка
    {
        StringBuilder decoder=new StringBuilder();
        CodeTree codeTree=tree;
        for(int i=0;i<encoded.length();i++)
        {
            codeTree=encoded.charAt(i)=='0'? codeTree.left:codeTree.right;
            if(codeTree.content!=null)
            {
                decoder.append(codeTree.content);
                codeTree=tree;
            }
        }
      return decoder.toString();
    }
    private static class CodeTree implements Comparable<CodeTree>
    {
        int weight;
        Character content;
        CodeTree left;
        CodeTree right;
        public CodeTree(Character content,int weight)
        {
            this.weight = weight;
            this.content = content;
        }
        public CodeTree(Character content,int weight,CodeTree left,CodeTree right) {
            this.weight = weight;
            this.content = content;
            this.left = left;
            this.right = right;
        }
        @Override
        public int compareTo(CodeTree o)
        {
            return o.weight-weight;
        }
        public String CodeComb(Character ch, String parentPath)
        {
            if (content==ch)
            {
                return parentPath;
            }
            else
                {
                    if(left!=null)
                    {
                      String path=left.CodeComb(ch,parentPath+0);
                      if(path!=null)
                      {
                          return path;
                      }
                    }
                    if(right!=null)
                    {
                        String path=right.CodeComb(ch,parentPath+1);
                        if(path!=null)
                        {
                            return path;
                        }
                    }
                }
          return null;
        }

    }
    ////////////////////////////////////////////////////////////////////////////
    public static class BitArray {
        int size;
        byte[] bytes;

        private byte[] masks = new byte[] {0b00000001, 0b00000010, 0b00000100, 0b00001000,
                0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000};

        public BitArray(int size) {
            this.size = size;
            int sizeInBytes = size / 8;
            if (size % 8 > 0) {
                sizeInBytes = sizeInBytes + 1;
            }
            bytes = new byte[sizeInBytes];
        }

        public BitArray(int size, byte[] bytes) {
            this.size = size;
            this.bytes = bytes;
        }

        public int get(int index) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            return (bytes[byteIndex] & masks[bitIndex]) != 0 ? 1 : 0;
        }

        public void set(int index, int value) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if (value != 0) {
                bytes[byteIndex] = (byte) (bytes[byteIndex] | masks[bitIndex]);
            } else {
                bytes[byteIndex] = (byte) (bytes[byteIndex] & ~masks[bitIndex]);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(get(i) > 0 ? '1' : '0');
            }
            return sb.toString();
        }

        public int getSize() {
            return size;
        }

        public int getSizeInBytes() {
            return bytes.length;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    // сохранение таблицы частот и сжатой информации в файл
    private static void saveToFile(File output, Map<Character, Integer> freq, String bits)
    {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(output));
            os.writeInt(freq.size());
            for (Character character: freq.keySet()) {
                os.writeChar(character);
                os.writeInt(freq.get(character));
            }
            int compressedSizeBits = bits.length();
            BitArray bitArray = new BitArray(compressedSizeBits);
            for (int i = 0; i < bits.length(); i++) {
                bitArray.set(i, bits.charAt(i) != '0' ? 1 : 0);
            }

            os.writeInt(compressedSizeBits);
            os.write(bitArray.bytes, 0, bitArray.getSizeInBytes());
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

