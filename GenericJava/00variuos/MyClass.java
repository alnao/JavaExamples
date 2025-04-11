
import java.util.* ;

public class MyClass {
  public static void main(String args[]) {
    
    BackerValidatorImpl service=new BackerValidatorImpl();
    boolean result = service.validate( "{{()()}()}" );
    System.out.println("Risultato " + (result ? "OK" : "KO")) ;
  }

//in java: definisci una classe BracketObj per identificare un oggetto con uno solo attributo immutabile che rappresenti una parentesi valida, 
// la lista delle parentesi valide sono { } [ ] ( ). Fornire una implementazione della interfaccia BackerValidator per verificare se l'array di parentesi 
//passato in input rappresenti una sequenza valida di parentesi bilanciatre. 
// Esempio valido "{{()()}()}", esempio non valido "{[}()}". 
// dammi anche un test auotmatico per il metodo isvalid.

    static final class BracketObj{
        public final static String validBracket="{}[]()";
        public final static String openBracket="{[(";
        public static boolean isValid(char c){
            return validBracket.indexOf(c)>-1;
        }
        private final char bracket;
        public BracketObj(char bracket){
            if (this.isValid(bracket)){
                this.bracket=bracket;
            }else{
                this.bracket='_'; //throw new Exception("Bracket " +  bracket + " not valid");
            }
        }
        public char getValue(){ return bracket;}
        public boolean isOpenBracket(){
            return openBracket.indexOf(this.bracket)>-1;
        }
    }
    interface BackerValidator{
        public boolean validate(BracketObj list[] );
    }
    static class BackerValidatorImpl implements BackerValidator{
        public char complete(char bracket){
            switch(bracket){
                case '[': return ']';
                case '(': return ')';
                case '{': return '}';
                default: return '_';
            }
        }
        public boolean validate(BracketObj list[] ){
            Stack<BracketObj> stack = new Stack<>();
            for (BracketObj b : list ){
                if ( b.isOpenBracket() ){//parentesi aperta
                    stack.push(b);
                }else{
                    char val=stack.pop().getValue(); //
                    if ( complete(val) != b.getValue() ){ //parentesi chiusa
                        return false;
                    }
                }
            }
            return stack.isEmpty();
        }
        public boolean validate(String bracket ){
            BracketObj list[] = new BracketObj[bracket.length()];
            for (int i=0;i<bracket.length();i++){
                if (BracketObj.isValid( bracket.charAt(i) ))
                    list[i]=new BracketObj(bracket.charAt(i));
                else
                    return false;
            }
            return validate(list);
        } 
    }
}