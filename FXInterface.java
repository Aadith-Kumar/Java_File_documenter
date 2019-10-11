
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.paint.*;
import javax.swing.*;
import javafx.*;
import javafx.scene.image.*;



import java.util.*;
import java.io.*;

public class FXInterface extends Application
{
    
    
    public void start(Stage stg) throws Exception{
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stg);
        Classifier.readFile(file);
        
        ArrayList<Classifier.Statement> stmts = Classifier.stmts;
        ArrayList<Classifier.Statement> mStmts = new ArrayList<Classifier.Statement>();
        VBox screen = new VBox();
        {
            HBox line = new HBox();
            for(int i = 0; i < stmts.size();i++){
               String s = stmts.get(i).stmt;
               
               for(;s.indexOf('\n'+"") != -1;){
                   screen.getChildren().add(line); 
                   line = new HBox();
                   screen.getChildren().add(new Label(s.substring(0,s.indexOf('\n'+""))));
                   s = s.substring(s.indexOf('\n'+"")+1);
                }
                line.getChildren().add(new Label(s+"                    "));
                s = stmts.get(i).stmt;
                
                String toCheck = Classifier.removeComments(s).replaceAll('\n'+"","");
                //System.out.println(s);
                if(Classifier.isVar(toCheck)){
                    
                    TextField txt = new TextField();
                    
                    TitledPane tp = new TitledPane("",txt);
                    tp.setExpanded(false);
                    stmts.get(i).var = txt;
                    line.getChildren().add(tp);
                    mStmts.add(stmts.get(i));
                }
                else if(Classifier.isMethod(toCheck)){
                    String[] param = Classifier.getParams(s.replaceAll('\n'+"",""));
                    VBox params  = new VBox();
                    HBox subLine;
                    TextField []txts;
                    subLine = new HBox();
                    subLine.getChildren().add(new Label("Function: "));
                    stmts.get(i).methDef = new TextField();
                    subLine.getChildren().add(stmts.get(i).methDef);
                    params.getChildren().add(subLine);
                    
                   
                    if(param != null){
                        txts = new TextField[param.length];
                        params.getChildren().add(new Label("------------------"));
                        for(int k = 0;param != null && k < param.length;k++){
                            subLine = new HBox();
                            subLine.getChildren().add(new Label(param[k]));
                            txts[k] = new TextField();
                            subLine.getChildren().add(txts[k]);
                            params.getChildren().add(subLine);
                        }
                        stmts.get(i).txts = txts;
                    }
                    
                    params.getChildren().add(new Label("------------------"));
                    subLine = new HBox();
                    subLine.getChildren().add(new Label("Returns: "));
                    stmts.get(i).ret = new TextField();
                    subLine.getChildren().add(stmts.get(i).ret);
                    params.getChildren().add(subLine);
                    
                    
                    TitledPane tp = new TitledPane("Method",params);
                    tp.setExpanded(false);
                    line.getChildren().add(tp);
                    mStmts.add(stmts.get(i));
                }
                
                
                else if(Classifier.isClass(toCheck)){
                    
                  VBox params  = new VBox();
                    HBox subLine;
                    TextField []txts;
                    subLine = new HBox();
                    subLine.getChildren().add(new Label("Function: "));
                    stmts.get(i).classDef = new TextField();
                    subLine.getChildren().add(stmts.get(i).classDef);
                    params.getChildren().add(subLine);
                    
                    
                    TitledPane tp = new TitledPane("Class",params);
                    tp.setExpanded(false);
                    line.getChildren().add(tp);
                    mStmts.add(stmts.get(i));
                }
                
                else {
                    mStmts.add(stmts.get(i));
                }
            }
        }
           
        Button save = new Button("SAVE");
        save.setMinSize(1000,30);
        save.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                try{
                    Classifier.update(Classifier.copy(mStmts));
                    Classifier.writeFile(Classifier.updated,new File("Doc-"+ file.getName()));
                }catch(Exception ex){
                    System.out.println("Exception at Handle");
                }
            }
            
        });
        
        ScrollPane scr= new ScrollPane();
        scr.setContent(screen);
        
        VBox finalScr = new VBox();
        finalScr.getChildren().add(scr);
        finalScr.getChildren().add(save);
        
       
        
        Scene scene = new Scene(finalScr,600,600);
        
        stg.setScene(scene);
        stg.show();
    }
   
    
    public static void main(String[] args){
        launch(args);
        Classifier.printStmts(Classifier.updated);
        System.exit(0);
    }
    
    
    /* // Backup
     * 
     * for(int i = 0; i < stmts.size();i++){
               String s = stmts.get(i).stmt;
               for(;s.indexOf('\n'+"") != -1;){
                   screen.getChildren().add(line); 
                   line = new HBox();
                   screen.getChildren().add(new Label(s.substring(0,s.indexOf('\n'+""))));
                   s = s.substring(s.indexOf('\n'+"")+1);
               }
                
                line.getChildren().add(new Label(s+"                    "));
                
                s = stmts.get(i).stmt;
                if(Classifier.isVar(s.replaceAll('\n'+"",""))){
                    TextField txt = new TextField();
                    //txt.setOnAction
                    stmts.get(i).txt = txt;
                    line.getChildren().add(txt);
                }
                else if(Classifier.isMethod(s.replaceAll("~",""))){
                    
                    
                    
                }
               
               
            }
     * 
     */
}
