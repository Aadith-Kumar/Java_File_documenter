import java.io.*;

import java.util.*;
import javafx.scene.control.*;
public class Classifier
{
    static class Statement{
        String stmt;
        //int lineNo;
        TextField[] txts;
        
        TextField var;
        TextField methDef;
        TextField ret;
        TextField classDef;
        public Statement(String stmt){
            this.stmt = stmt;
            //this.lineNo = lineNo;
        }
        
        public  Statement(String stmtF,TextField[] txtF,TextField varF,TextField meth,TextField retu,TextField clas){
            txts = txtF;
            methDef = meth;
            classDef = clas;
            ret = retu;
            stmt = stmtF;
            var = varF;
        }
       
        
        public Statement copy(){
            return new Statement(stmt,txts,var,methDef,ret,classDef);
        }
    }
    
    static ArrayList<Statement> stmts = new ArrayList<Statement>();
    static ArrayList<Statement> updated;
    
    
    public static boolean isVar(String s){
        s = s.trim();
        String [] words = s.split(" ");
        
        for(int i = 0; i < words.length;i++){
            //if(words[i].contains("//")) return false;
            if(words[i].startsWith("//")) return false;
            if(words[i].equals("class")) return false;
            if(words[i].equals("import")) return false;
            if(words[i].equals("return")) return false;
            if(words[i].equals("if")) return false;
            if(words[i].equals("else")) return false;
            if(words[i].equals("switch")) return false;
        }
        
        if(s.indexOf("=") != -1){
            if(!((Character.isSpaceChar(s.charAt(s.indexOf("=")+1)) || s.charAt(s.indexOf("=")+1) == '\"' || s.charAt(s.indexOf("=")+1) == '\'' || Character.isLetterOrDigit(s.charAt(s.indexOf("=")+1))) 
              && (Character.isSpaceChar(s.charAt(s.indexOf("=")-1)) ||s.charAt(s.indexOf("=")-1) == '\"' || s.charAt(s.indexOf("=")-1) == '\'' || Character.isLetterOrDigit(s.charAt(s.indexOf("=")-1)))))return false;
            s = (s.substring(0,s.indexOf("="))).trim();
            if (s.split(" ").length ==1) return false;
        }
        if(words.length == 1 ) return false;
       
        if(s.indexOf('(') != -1 || s.indexOf(')') != -1 || s.indexOf('-') != -1 || s.indexOf('+') != -1 || s.indexOf('}') != -1) return false;
        
        
        return true;
    }
    
     public static boolean isClass(String s){
        s = s.trim();
        String [] words = s.split(" ");
        
        for(int i = 0; i < words.length;i++){
            if(words[i].equals("class")){
                if(words[i].startsWith("/*")) return false;
                if(words[i].startsWith("//")) return false;
                return true;
            }
            
        }
        
        return false;
    }
    
    public static void Test()throws Exception{
        BufferedReader br = new BufferedReader(new FileReader("MethTestIn"));
        PrintWriter pw = new PrintWriter("MethTestOut");
        
        for(int i = Integer.parseInt(br.readLine());i > 0; i --){
            String s = br.readLine();
            pw.println(s+ "\t\t\t" + isMethod(s));
        }
        pw.close();
    }
    
    public static void readFile(File file)throws Exception{

        BufferedReader br = new BufferedReader(new FileReader(file));
        String fileData = "";
        String temp = br.readLine();
        for(;temp != null;){
            fileData += temp + '\n'+"";
            temp = br.readLine();
        }
        int lineNo = 0;
        //fileData = removeComments(fileData);
        //System.out.println(fileData);
        boolean cont1  = true;
        boolean cont2  = true;
        for(int i = 0; i < fileData.length()-1;i++){
         
            if(fileData.substring(i).startsWith("//")){
               cont1 =false;
                
            }
              if(fileData.substring(i).startsWith(""+'\n')){
               cont1 =true;
            }
            
            if(fileData.substring(i).startsWith("/*")){
               cont2 =false;
                
            }
              if(fileData.substring(i).startsWith("*/")){
               cont2 =true;
            }
            
            
            if(!cont1 || !cont2) continue;
           
               if(fileData.substring(i).startsWith("for") || fileData.startsWith("switch")){
                stmts.add(new Statement(fileData.substring(0,fileData.indexOf("{")+1)));
                fileData = fileData.substring(fileData.indexOf("{")+1);
                i = 0;
            }
             if(fileData.charAt(i) == ';' || fileData.charAt(i) == '{'|| fileData.charAt(i) == '}') {
                stmts.add(new Statement(fileData.substring(0,i+1)));
                //System.out.println(fileData.substring(0,i+1));
                fileData = fileData.substring(i+1);
                i = 0;
            }
        }
        //printStmts(stmts);
    }
    
    public static boolean isMethod(String s){
        if(isVar(s)) return false;
        if(s.indexOf('(') == -1) return false;
        if(s.indexOf('=') != -1 || s.indexOf(";") != -1) return false;
        if(s.replaceAll(" ","").startsWith("while(") || s.replaceAll(" ","").startsWith("for(") || s.replaceAll(" ","").startsWith("switch(") || s.replaceAll(" ","").startsWith("catch(")|| s.replaceAll(" ","").startsWith("elseif(")|| s.replaceAll(" ","").startsWith("if(")) return false;
        if(s.trim().startsWith("/*")) return false;
        if(s.trim().startsWith("//")) return false;
        return true;
    }
    
    
    public static String[] getParams(String s){
        try{
            String brack = s.substring(s.indexOf("(")+1,s.indexOf(")"));
            if(brack.replaceAll(" ","").equals("")) return null;
            String [] params = brack.split(",");
            return params;
        }
        catch (Exception e){
            return null;
        }
    }
    
    public static void fillType(){
        for(int i = 0; i < stmts.size();i++){
            String s = stmts.get(i).stmt.replaceAll('\n'+"","");
            if(isVar(s)) stmts.get(i).stmt += "/*"+ "VARIABLE INIT/DEC"+ "*/";
            if(isMethod(s)) stmts.get(i).stmt += "/*"+ "method Def"+ "*/";
            
        }
    }
    
    public static String removeComments(String s){ 
        try{   
            if(s.length() < 2) return s;
        
            s = removeQuotes(s);
            if(s.indexOf("/*") != -1 && s.indexOf("//") != -1){
               
                if(s.indexOf("/*") < s.indexOf("//")){
                   
                    String oldStr = s.substring(0,s.indexOf("/*"));
                    String newStr = s.substring(s.indexOf("*/",s.indexOf("/*"))+2);
                    
                    System.out.println(s+"\n"+oldStr + newStr+"\n\n");
                    return removeComments(oldStr+newStr);
                }
                else{
                   String newStr;
                    String oldStr = s.substring(0,s.indexOf("//"));
                    if(s.indexOf('\n'+"",s.indexOf("//")) == -1) newStr = "";
                    else newStr = s.substring(s.indexOf('\n'+"",s.indexOf("//")));
                    return removeComments(oldStr+newStr);
                }
            }
            else if(s.indexOf("//") != -1){
                String newStr;
                String oldStr = s.substring(0,s.indexOf("//"));
                if(s.indexOf('\n'+"",s.indexOf("//")) == -1) newStr = "";
                else newStr = s.substring(s.indexOf('\n'+"",s.indexOf("//")));
         
                return removeComments(oldStr+newStr);
            }
            else if (s.indexOf("/*") != -1){
                String oldStr = s.substring(0,s.indexOf("/*"));
                String newStr = s.substring(s.indexOf("*/",s.indexOf("/*"))+2);
                return removeComments(oldStr+newStr);
            }
        }
        catch(Exception e){
            System.out.println("HEAP SPACE at " + s);
        }

        
        return s;
    }
     
 
   
    public static ArrayList<Statement> copy(ArrayList<Statement> arr){
        ArrayList<Statement> mod = new ArrayList<Statement>();
        
        for(int i = 0; i < arr.size();i++){
            mod.add(arr.get(i).copy());
        }
        
        return mod;
    }
    
    public static void update(ArrayList<Statement> stmts){
        for(int i = 0; i < stmts.size();i++){
            String s =  removeComments(stmts.get(i).stmt).replaceAll('\n' + "","");
            if(isVar(s)) addVarDoc(stmts.get(i));
            else if(isMethod(s)) addMethDoc(stmts.get(i));
            else if (isClass(s)) addClassDoc(stmts.get(i));
        }
        updated = stmts;
    }
    
     public static void addClassDoc(Statement stmt){
        if(stmt.classDef.getText().equals("")) return;
         
        String indentSub = stmt.stmt.substring(0,stmt.stmt.indexOf(stmt.stmt.replaceAll('\n'+"","").trim().charAt(0)));
        String indent = indentSub.substring(indentSub.lastIndexOf('\n'+"")+1);
        String doc ='\n' +indent + "/**" +'\n'+indent + "  * " + stmt.classDef.getText();
       
        doc += '\n'+indent + " */";
        
        stmt.stmt = doc + stmt.stmt;
    }
    
    public static void addVarDoc(Statement stmt){
        try{
            if(!stmt.var.getText().equals("")){
                stmt.stmt += " /* " + stmt.var.getText() + " */";
            }
        }
        catch(Exception e){
            System.out.println(stmt);
        }
    }
     
   
     public static void addMethDoc(Statement stmt){
         
        if(stmt.methDef.getText().equals("")) return;
        String indentSub = stmt.stmt.substring(0,stmt.stmt.indexOf(stmt.stmt.replaceAll('\n'+"","").trim().charAt(0)));
        String indent = indentSub.substring(indentSub.lastIndexOf('\n'+"")+1);
        
        
        
        String doc = '\n'+ indent +"/**" +'\n'+indent + "  *   " + stmt.methDef.getText();
        if(stmt.txts == null) return;
        String []params = getParams(stmt.stmt);
        for(int i = 0; i < stmt.txts.length;i++){
            doc += '\n'+indent + "  *" + "@param " + params[i].substring(params[i].lastIndexOf(" ")) + "  " + stmt.txts[i].getText();
        }
        doc += '\n'+indent + "  *" + "@return " + stmt.ret.getText() +  '\n'+indent + "*/";
        
        stmt.stmt = doc + stmt.stmt;
    }
    
    public static void printStmts(ArrayList<Statement> stmts){
       for(int i =0 ; i < stmts.size();i++){
           String s = stmts.get(i).stmt;
           for(;s.indexOf('\n'+"") != -1;){
               System.out.println(s.substring(0,s.indexOf('\n'+"")));
               s = s.substring(s.indexOf('\n'+"")+1);
            }
            System.out.print(s);
            
        }
    }
    
    
    
    public static void writeFile(ArrayList<Statement> stmts,File file) throws Exception{
        PrintWriter pw = new PrintWriter(file);
      
        
         for(int i =0 ; i < stmts.size();i++){
           String s = stmts.get(i).stmt;
           for(;s.indexOf('\n'+"") != -1;){
               pw.println(s.substring(0,s.indexOf('\n'+"")));
               s = s.substring(s.indexOf('\n'+"")+1);
            }
            pw.print(s);
            
        }
        
        pw.close();
    }
    
    public static String removeQuotes(String s){
        if(s.length() <= 1) return s;
        
        if(s.indexOf("\"") != -1){
            String oldStr = s.substring(0,s.indexOf("\""));
            
            //while(!gotQuote){
                
            //}
            
            String newStr = s.substring(s.lastIndexOf("\"")+1);
            return removeQuotes(oldStr+newStr);
        }
        
        return s;  
    }  
    
   
    
    public static void main(String[] args)throws Exception{
        String [] s = {"xgxg","xfcg","xfffffffffff","fg","\"\"\"\"","xfg","","hello;ldkfj;lk\"|'dsdfgpj"};
        for(int i = 0; i < s.length;i++){
            System.out.println(removeQuotes(s[i]));
        }
    }
    
    
}
