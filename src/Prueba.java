import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prueba {
    static final  String REGEX = "\\s+(\\d+)\\s+(\\+|\\-|\\*|\\:)\\s+(\\d+)$";
    public static void main(String[] args) {
        String expresion = "    10    :   7";

        Pattern patron = Pattern.compile(REGEX);
        Matcher m = patron.matcher(expresion);

        int resultado = 0;
        if(m.find()){
            int x = Integer.parseInt(m.group(1));
            String signo = m.group(2);
            int y = Integer.parseInt(m.group(3));

            switch (signo){
                case "+":
                    resultado = x + y;
                    break;

                case "-":
                resultado = x - y;
                break;

                case "*":
                resultado = x * y;
                break;

                default:
                resultado = x / y;
                break;
            }

            System.out.println(resultado);
        }
    }
}
