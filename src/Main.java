import com.taozeyu.taolan.analysis.LexicalAnalysis;
import com.taozeyu.taolan.analysis.LexicalAnalysisException;
import com.taozeyu.taolan.analysis.SyntacticAnalysis;
import com.taozeyu.taolan.analysis.SyntacticAnalysisException;
import com.taozeyu.taolan.analysis.node.ChunkNode;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Main {
    public static void main(String[] args){
        String code = "3+5+(\"String\"-5)+" +
						"String.";
        System.out.println(code+"\n\n");
        Reader r = new StringReader(code);
        LexicalAnalysis la = new LexicalAnalysis(r);
        try {
            la.read();
            SyntacticAnalysis sa = new SyntacticAnalysis(la);
            ChunkNode cn = sa.analyze();
            cn.print(2, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LexicalAnalysisException e) {
            e.printStackTrace();
        } catch (SyntacticAnalysisException e) {
            e.printStackTrace();
        }

    }
}
