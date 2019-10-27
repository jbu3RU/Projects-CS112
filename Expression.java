package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

    public static String delims = " \t*+-/()[]";
            
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/
        /** DO NOT create new vars and arrays - they are already created before being sent in
         ** to this method - you just need to fill them in.
         **/
        String newExpr = expr + "0";
        int length = newExpr.length();
        //System.out.println("Variable List" + vars);
        for(int i = 0; i < length; i++) {
            
            //Array List
            if(Character.isLetter(newExpr.charAt(i)) == true && (newExpr.charAt(i + 1) == '[' || i == length - 1)) {
                Array newArr = new Array(Character.toString(expr.charAt(i)));
                arrays.add(newArr);
            }
            
            //Variable List
            if(Character.isLetter(newExpr.charAt(i)) == true && (newExpr.charAt(i + 1) != '[' || i == length -1)) {
                int count = 0;
                for(int j = i; j < length; j++) {
                    if(Character.isLetter(newExpr.charAt(j)) == true){
                        count++;
                        //System.out.println(count);
                        //System.out.println("I am Here");
                        continue;
                    }else if(Character.isLetter(newExpr.charAt(j)) != true && count == 1) {
                        Variable newVars =new Variable(Character.toString(expr.charAt(i)));//creates a new varibale
                        vars.add(newVars);
                        i = j;
                        count = 0;
                        //System.out.println(newVars);
                        break;
                    }else if(Character.isLetter(newExpr.charAt(j)) != true && count > 1) {
                        //Array List
                        Array newArr = new Array(expr.substring(i,j));
                        if (newExpr.charAt(j) == '[') {
                            //System.out.println("I am Here");
                            arrays.add(newArr);
                            i = j;
                            break;
                        }
                        Variable newVars =new Variable(expr.substring(i,j));//creates a new variable
                        //System.out.println(newVars);
                        vars.add(newVars);
                        i = j;
                        count = 0;
                        break;
                    }
                }
            }
        }
        
        //gets rid of duplicate variables
        for(int i = 0; i < vars.size(); i++) {
            String varName = vars.get(i).name;
            //System.out.println(varName);
            int count = 0;
            for(int j = 0; j < vars.size(); j++) {
                String varName1 = vars.get(j).name;
                if(varName1.equals(varName)) {
                    count++;
                    //System.out.println(count);
                }
                if(count > 1) {
                    vars.remove(i);
                }
            }
        }
        
        //gets rid of duplicate arrays
        for(int i = 0; i < arrays.size(); i++) {
            String arrName = arrays.get(i).name;
            //System.out.println(arrName);
            int count = 0;
            for(int j = 0; j < arrays.size(); j++) {
                String arrName1 = arrays.get(j).name;
                if(arrName1.equals(arrName)) {
                    count++;
                    //System.out.println(count);
                }
                if(count > 1) {
                    arrays.remove(i);
                }
            }
        }
        
        //System.out.println("Variable List" + vars);
        //System.out.println("Array List" + arrays);
        
 
    
  
}
  
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
                continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
                arr = arrays.get(arri);
                arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        /** COMPLETE THIS METHOD **/
        // following line just a placeholder for compilation
        expr = expr.replaceAll("\\s+", "");
        StringTokenizer st = new StringTokenizer(expr,delims,true);
        Stack<Float> values = new Stack<Float>();
        Stack<String> Ops = new Stack<String>();
        
        while(st.hasMoreTokens()) {
            
            String next = st.nextToken();
            //System.out.println(next);
            
            boolean checkNumber = isNumber(next);
            boolean checkVar = isVar(vars, next);
            boolean checkArr = isArr(arrays, next);
            boolean checkOp = isOp(next);
            
        
                    
            //If token is number
            if (checkNumber == true) {
                float number = getNumber(next);
                values.push(number);
                //System.out.println("1");
                //System.out.println(values.peek());
            }
            
            //if token is a variable and pushes value of variable to stack 
            if (checkVar == true) {
                float val = getVar(vars, next);
                values.push(val);
                //System.out.println("2");
                //System.out.println(values.peek());
            }
            
            
            
            //if token is an operator and uses pemdas
            if(checkOp == true) {
                //System.out.println("3");
                while(Ops.isEmpty() != true && pemdas(next, Ops.peek(),Ops)){
                    subEval(values,Ops);
                }
                Ops.push(next);
                //System.out.println(Ops.peek());
                //System.out.println(values.peek());
            }
            
            //if token is an array and stores it into the ops
            if(checkArr == true) {
                //System.out.println("4");
                Ops.push(next);
                //System.out.println(Ops.peek());
            }
            
            //if token is a closing bracket and gets value of array and pushes it to values stack
            if(next.equals("]")) {
                //System.out.println("5");
                while(Ops.peek().equals("[") == false) {//&& values.isEmpty() == false && Ops.isEmpty() == false) {
                    subEval(values,Ops);
                }
                Ops.pop();
                float res = getValArr(arrays, values.pop(),Ops.pop());
                values.push(res);
                //System.out.println(res);
            }
            
            //if token is a closing ")" and returns value of subexpression
            if(next.equals(")")) {
                //System.out.println("6");
                while(Ops.peek().equals("(") == false) {
                    //System.out.println(Ops.peek());
                    //System.out.println(values.peek());
                    subEval(values,Ops);
                }
                Ops.pop();
            }
            
            if(next.equals("(") || next.equals("[")) {
                //System.out.println("7");
                Ops.push(next);
                //System.out.println(Ops.peek());
            }
        }
        while(Ops.isEmpty() == false) {
            subEval(values, Ops);
        }
        return values.pop();
    }
    
    //My Methods
    private static float getValArr(ArrayList<Array> arrays, float num, String name) {
        int index =(int) num;
        for (int i = 0;i < arrays.size(); i++ ) {
            if(arrays.get(i).name.equals(name)){
                Array arrs = arrays.get(i);
                //System.out.println(arrs.values[index]);
                return arrs.values[index];
                
            }
        }
        return 0;
    }
    
    private static float getNumber(String number) {
        if(Character.isDigit(number.charAt(0)) == true) {
            float newNumber = Float.parseFloat(number);
            return newNumber;
        }
        return 0;
    }
    
    private static boolean isNumber(String number) {
        if(Character.isDigit(number.charAt(0)) == true) {
            return true;
        }
        return false;
    }
   private static boolean isVar(ArrayList<Variable> vars, String name) {
       for(int i = 0; i < vars.size(); i++) {
           Variable varName = vars.get(i);
           if(varName.name.equals(name)) {
               return true;
           }
       }
       return false;       
   }
   
   private static boolean isArr(ArrayList<Array> arrays, String name) {
       for(int i = 0; i < arrays.size(); i++) {
           Array arrName = arrays.get(i);
           if(arrName.name.equals(name)) {
               return true;
           }
       }
       return false;
   }
   
   private static boolean isOp(String next) {
       if(next.equals("+") || next.equals("-") || next.equals("/") || next.equals("*")) {
           return true;
       }
       return false;
   }
   
   private static float getVar(ArrayList<Variable> vars, String name) {
       for(int i = 0; i < vars.size(); i++) {
           Variable varName = vars.get(i);
           if(varName.name.contentEquals(name)) {
               float val = (float)varName.value;
               return val;
           }
       }
       return 0;
   }
   
   private static boolean pemdas(String next , String peek, Stack<String> Ops) {
       if (Ops.peek().equals("(") || Ops.peek().equals("[")) {
           return false;
       }
       if ((next.equals("/") || next.equals("*")) && (peek.equals("+")|| peek.contentEquals("-"))) {
           return false;
       }
       return true;
   }
   
   private static void subEval(Stack<Float> values, Stack<String> Ops) { 
          float num2 = values.pop();
          float num1 = values.pop();
          String operator = Ops.pop();
          
          if(operator.equals("+")) {
              values.push(num1 + num2);
          }
          
          if(operator.equals("-")) {
              values.push(num1 - num2);
          }
          
          if(operator.equals("*")) {
              values.push(num1 * num2);
          }
          
          if(operator.equals("/")) {
              values.push(num1 / num2);
          }
   }
    
}
