
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Andrii on 19.02.2015.
 */
public class LA {

    int idnCode = 1001, keyWordCode = 400,
            constCode = 501;

    public ArrayList<Line> loadCode(String filepath) throws IOException{
        ArrayList<Line> inputlines = new ArrayList<Line>();
        Scanner sc = new Scanner(new File(filepath));
        int num = 1;
        while(sc.hasNextLine()){
            inputlines.add(new Line(sc.nextLine(),num));
            num++;
        }
        return inputlines;
    }

    public TokenTable Analising(){
        ArrayList<Line> lines = null;
        try {
            lines = loadCode("src/testLA.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer buffer;
        AllovedElements allovedElements = new AllovedElements();
        TokenTable tokenTable = new TokenTable();
        Boolean comm = false;

        int comRow = 0;
        int comLine = 0;
        for(Line line: lines){
            int  i = 0;
            //System.out.println(line.str.length());
            while(i < line.str.length()){
                buffer = new StringBuffer();

                if (allovedElements.GetType(line.str.charAt(i)).equals("scom") && (allovedElements.GetType(line.str.charAt(i + 1)).equals("com")) && i < line.str.length() - 1) {
                    comm = !comm;
                    comLine = line.number;
                    comRow = i;
                    ++i;
                }

                 if ((allovedElements.GetType(line.str.charAt(i)).equals("com")) && (allovedElements.GetType(line.str.charAt(i + 1)).equals("ecom")) && (i < line.str.length() - 1)) {
                    comm = !comm;
                    comLine = line.number;
                    comRow = i;
                    i+=2;
                }

                if (comm){
                    i++;
                }

                else if (i < line.str.length() && allovedElements.GetType(line.str.charAt(i)).equals("lit")){
                    if (allovedElements.GetType(line.str.charAt(i + 1)).equals("lit") || allovedElements.GetType(line.str.charAt(i + 1)).equals("dig")){
                        int j = i;
                        while ((j < line.str.length())&&(allovedElements.GetType(line.str.charAt(j)).equals("lit") || allovedElements.GetType(line.str.charAt(j)).equals("dig"))){
                            buffer.append(line.str.charAt(j));
                            ++j;
                        }
                        if (allovedElements.CheckKeyWord(buffer.toString())){
                            int curCode = keyWordCode;
                            tokenTable.tokenList.add(new MultiCharTocken(line.number, i, keyWordCode = tokenTable.GenTokenCode(buffer.toString(), keyWordCode), "KeyW", buffer.toString()));
                            tokenTable.usedSymbols.put(keyWordCode, buffer.toString());
                            if (curCode > keyWordCode) keyWordCode = curCode;
                    }
                        else{
                            int curCode = idnCode;
                            tokenTable.tokenList.add(new MultiCharTocken(line.number, i, idnCode = tokenTable.GenTokenCode(buffer.toString(),idnCode), "IDN", buffer.toString()));
                            tokenTable.usedSymbols.put(idnCode, buffer.toString());
                            if (curCode > idnCode) idnCode = curCode;
                        }
                        i = --j;

                    }else {
                        int curCode = idnCode;
                        tokenTable.tokenList.add(new SingleCharTocken(line.number, i, idnCode = tokenTable.GenTokenCode(new Character(line.str.charAt(i)).toString(), idnCode), "IDN", line.str.charAt(i)));
                        tokenTable.usedSymbols.put( idnCode, new Character(line.str.charAt(i)).toString());
                        if (curCode > idnCode) idnCode = curCode;
                    }
                }



                else if (i < line.str.length() && allovedElements.GetType(line.str.charAt(i)).equals("dig")){
                    if (i < line.str.length() - 1 && allovedElements.GetType(line.str.charAt(i + 1)).equals("dig")){
                        int j = i;
                        while (i < line.str.length() && j < line.str.length() && allovedElements.GetType(line.str.charAt(j)).equals("dig")){
                            buffer.append(line.str.charAt(j));
                            ++j;
                        }
                        int curCode = constCode;
                        tokenTable.tokenList.add(new MultiCharTocken(line.number, i, constCode = tokenTable.GenTokenCode(buffer.toString(), constCode), "NUM", buffer.toString()));
                        tokenTable.usedSymbols.put( constCode, buffer.toString());
                        if (curCode > constCode) constCode = curCode;
                        i = --j;
                    }else {
                        int curCode = constCode;
                        tokenTable.tokenList.add(new SingleCharTocken(line.number, i, constCode = tokenTable.GenTokenCode(new Character(line.str.charAt(i)).toString(), constCode), "NUM", line.str.charAt(i)));
                        tokenTable.usedSymbols.put(constCode, new Character(line.str.charAt(i)).toString());
                        if (curCode > constCode) constCode = curCode;
                    }
                }



                else if (i < line.str.length() && allovedElements.GetType(line.str.charAt(i)).equals("dm")){
                    tokenTable.tokenList.add(new SingleCharTocken(line.number, i, (int)line.str.charAt(i), "DM", line.str.charAt(i)));
                }

                else if (i < line.str.length() && allovedElements.GetType(line.str.charAt(i)).equals("ws")){
                    ++i;
                    continue;
                }

                else if (i < line.str.length())
                    //tokenTable.tokenList.add(new MultiCharTocken(line.number, i, 0, "unknown symbol", "ERROR"));
                    Errors.Errors.add(new Err( "ERROR", "unknown symbol", line.number, i));
                ++i;
            }
        }

         if (comm)
             //tokenTable.tokenList.add(new MultiCharTocken(comLine, comRow, 0, "incorrect comment", "ERROR"));
               Errors.Errors.add(new Err("ERROR", "incorrect comment", comLine, comRow));

        return tokenTable;
    }




}




class  Line{
    String str;
    int number;

    Line(String str, int number){
        this.str = str;
        this.number = number;
    }
}
