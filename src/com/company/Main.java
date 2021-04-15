package com.company;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
public class Main
{
    public static void main(String[] args)
    {
      String text="I like it, i'm not gonna crack.";
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
        System.out.println("Размер исходной строки: " + text.getBytes().length * 8 + " бит");
        System.out.println("Размер сжатой строки: " + encoded.length() + " бит");
        System.out.println("Расшифровка:"+decoder(encoded.toString(),tree));
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
    //сортировка по Хаффману.Объединение элементов и создание parent для них
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
}

