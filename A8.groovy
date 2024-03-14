package org.junit
import org.junit.IdC


// Example Test Runner Class
class TestRunner {
    // Simple interpreter
    static Value interp(ExprC expr, Env env) {
        if (expr instanceof NumC) {
            return new NumV(expr.n)
        } else if (expr instanceof StrC) {
            return new StrV(expr.str)
        } else if (expr instanceof IdC){
            return env.lookup(expr.x)
        } else if (expr instanceof IfC) {
            Value testVal = interp(expr.test, env)
            if (testVal instanceof BoolV) {
                if (testVal.bool) {
                    return interp(expr.thenBranch, env)
                } else {
                    return interp(expr.elseBranch, env)
                }
            } else {
                throw new RuntimeException("Expected boolean value for test expression")
            }
        } else if (expr instanceof LamC){
            return new CloV(expr.args, expr.body, env)
        } else if (expr instanceof AppC){
            
        }
        // Additional cases can be handled here in the future
        return null
    }

    //Serialize Values to String
    static String serialize(Value v) {
        if (v instanceof NumV){
            return Double.toString(v.n)
        } else if (v instanceof StrV){
            return v.str
            // Might have to return string in different format
        } else if (v instanceof BoolV){
            if (v.bool == false){
                return "false"
            } else {
                return "true"
            }
        } else if (v instanceof PrimV) {
            return "#<primop>"
        } else if (v instanceof CloV) {
            return "#<procedure>"
        }
    }

    static String topInterp(ExprC expr, env){
        return serialize(interp(expr, env))
    }

    static void main(String[] args) {
        // Initialize the top-level environment
        Env topEnv = new Env()
        topEnv.bind('true', new BoolV(true))
        topEnv.bind('false', new BoolV(false))
        topEnv.bind('+', new PrimV('+'))
        topEnv.bind('-', new PrimV('-'))
        topEnv.bind('*', new PrimV('*'))
        topEnv.bind('/', new PrimV('/'))
        topEnv.bind('<=', new PrimV('<='))
        topEnv.bind('equal?', new PrimV('equal?'))
        topEnv.bind('error', new PrimV('error'))
        // Other bindings as needed...

        // Run test cases
        runTestCases(topEnv)
    }

    static void runTestCases(Env env) {
        // NumC test case
        NumC numExpr = new NumC(26)
        Value result1 = interp(numExpr, env)
        println "NumC Test: Expected NumV(26), ${result1 instanceof NumV && result1.n == 26 ? 'Pass' : 'Fail'}"
    
        // StrC test case
        StrC strExpr = new StrC("hello world")
        Value result2 = interp(strExpr, env)
        println "StrC Test: Expected StrV('hello world'), ${result2 instanceof StrV && result2.str == 'hello world' ? 'Pass' : 'Fail'}"
        
        // IdC test case
        IdC idExpr = new IdC('+')
        Value idResult = interp(idExpr, env)
        println "IdC Test: Expected PrimV('+'), ${idResult instanceof PrimV && idResult.op == '+' ? 'Pass' : 'Fail'}"

        // If test case
        // if (condition) then 37 else 0
        IfC ifExpr = new IfC(new IdC('true'), new NumC(37), new NumC(0))
        Value ifResult = interp(ifExpr, env)
        println "IfC Test: Expected NumV(37), ${ifResult instanceof NumV && ifResult.n == 37 ? 'Pass' : 'Fail'}"
        
        
        // If test case with false condition
        // Update environment to test false condition
        env.bind('condition', new BoolV(false)) // Update condition to false for this test
        IfC ifFalseExpr = new IfC(new IdC('condition'), new NumC(66), new NumC(0))
        Value ifFalseResult = interp(ifFalseExpr, env)
        println "IfC False Test: Expected NumV(0), ${ifFalseResult instanceof NumV && ifFalseResult.n == 0 ? 'Pass' : 'Fail'}"

        // LamC test case
        LamC lamExpr = new LamC([new NumC(1)], new NumC(1))
        Value lamResult = interp(lamExpr, env)
        println "LamC Test: Expected CloV(), ${lamResult instanceof CloV && lamResult.body instanceof NumC ? 'Pass' : 'Fail'}"

        //AppC Test
        


        //Serialize test cases
        //NumV serialize test case
        NumV numValue = new NumV(1.0)
        String serResult1 = serialize(numValue)
        println "Serialize Test (NumV): Expected '1.0' , ${serResult1 == "1.0" ? 'Pass' : 'Fail'}"

        //StrV serialize test case
        StrV strValue = new StrV("test")
        String serResult2 = serialize(strValue)
        println "Serialize Test (StrV): Expected 'test' , ${serResult2 == "test" ? 'Pass' : 'Fail'}"
        
        //BoolV serialize test case true
        BoolV boolValue = new BoolV(true)
        String serResult3 = serialize(boolValue)
        println "Serialize Test (BoolV): Expected 'true' , ${serResult3 == 'true' ? 'Pass' : 'Fail'}"

        //BoolV serialize test case false
        BoolV boolValue2 = new BoolV(false)
        String serResult4 = serialize(boolValue2)
        println "Serialize Test (BoolV): Expected 'false' , ${serResult4 == 'false' ? 'Pass' : 'Fail'}"

        //PrimV serialize test case
        PrimV primValue = new PrimV("+")
        String serResult5 = serialize(primValue)
        println "Serialize Test (PrimV): Expected '#<primop>' , ${serResult5 == '#<primop>' ? 'Pass' : 'Fail'}"

        //CloV serialize test case
        CloV cloValue = new CloV(["x"], new AppC(new IdC('f'), [new IdC('x')]), env)
        String serResult6 = serialize(cloValue)
        println "Serialize Test (CloV): Expected '#<procedure>' , ${serResult6 == '#<procedure>' ? 'Pass' : 'Fail'}"
    
        //Top-interp test cases
        // NumC test case
        NumC numExp = new NumC(26)
        String res1 = topInterp(numExp, env)
        println "NumC Test: Expected '26.0', ${res1 == "26.0" ? 'Pass' : 'Fail'}"
    
        // StrC test case
        StrC strExp = new StrC("hello world")
        String res2 = topInterp(strExp, env)
        println "StrC Test: Expected 'hello world', ${res2 == 'hello world' ? 'Pass' : 'Fail'}"
        
        // IdC test case
        IdC idExp = new IdC('+')
        String idRes = topInterp(idExp, env)
        println "IdC Test: Expected '#<primop>', ${idRes == "#<primop>" ? 'Pass' : 'Fail'}"

        // If test case
        // if (condition) then 37 else 0
        IfC ifExp = new IfC(new IdC('true'), new NumC(37), new NumC(0))
        String ifRes = topInterp(ifExp, env)
        println "IfC Test: Expected NumV(37), ${ifRes == "37.0" ? 'Pass' : 'Fail'}"
        
        
        // If test case with false condition
        // Update environment to test false condition
        env.bind('condition', new BoolV(false)) // Update condition to false for this test
        IfC ifFalseExp = new IfC(new IdC('condition'), new NumC(66), new NumC(0))
        String ifFalseRes = topInterp(ifFalseExp, env)
        println "IfC False Test: Expected '0.0'', ${ifFalseRes == "0.0" ? 'Pass' : 'Fail'}"

        // LamC test case
        LamC lamExp = new LamC([new NumC(1)], new NumC(1))
        String lamRes = topInterp(lamExp, env)
        println "LamC Test: Expected '#<procedure>', ${lamRes == "#<procedure>" ? 'Pass' : 'Fail'}"

        // Additional test cases can be added here
            
    }
}

// Base class for expression types
abstract class ExprC {}

// Represents a numeric constant
class NumC extends ExprC {
    double n
    
    NumC(double n) {
        this.n = n
    }
}

// Represents a binary operation
class Binop extends ExprC {
    String s
    ExprC l
    ExprC r
    
    Binop(String s, ExprC l, ExprC r) {
        this.s = s
        this.l = l
        this.r = r
    }
}

// Represents an identifier
class IdC extends ExprC {
    String x
    
    IdC(String x) {
        this.x = x
    }
}

// Represents an if expression
class IfC extends ExprC {
    ExprC test
    ExprC thenBranch
    ExprC elseBranch
    
    IfC(ExprC test, ExprC thenBranch, ExprC elseBranch) {
        this.test = test
        this.thenBranch = thenBranch
        this.elseBranch = elseBranch
    }
}

// Represents a function application
class AppC extends ExprC {
    ExprC f
    List<ExprC> p
    
    AppC(ExprC f, List<ExprC> p) {
        this.f = f
        this.p = p
    }
}

// Represents a lambda expression
class LamC extends ExprC {
    List<String> args
    ExprC body
    
    LamC(List<String> args, ExprC body) {
        this.args = args
        this.body = body
    }
}

// Represents a string constant
class StrC extends ExprC {
    String str
    
    StrC(String str) {
        this.str = str
    }
}

// Base class for value types
abstract class Value {}

// Represents a numeric value
class NumV extends Value {
    double n // Groovy doesn't explicitly have 'Real', but 'double' can be used for floating-point numbers
    
    NumV(double n) {
        this.n = n
    }
}

// Represents a boolean value
class BoolV extends Value {
    boolean bool
    
    BoolV(boolean bool) {
        this.bool = bool
    }
}

//Represents a closure value
class CloV extends Value {
    List<String> args
    ExprC body
    Env env
    
    CloV(List<String> args, ExprC body, Env env) {
        this.args = args
        this.body = body
        this.env = env
    }
}

// Represents a primitive operation
class PrimV extends Value {
    String op
    
    PrimV(String op) {
        this.op = op
    }
}

// Represents a string value
class StrV extends Value {
    String str
    
    StrV(String str) {
        this.str = str
    }
}



class Env {
    // A map to store variable names associated with their values
    private Map<String, Value> bindings = [:]

    // Adds a new binding to the environment
    void bind(String name, Value value) {
        bindings[name] = value
    }

    // Retrieves a value for a given name from the environment
    // Returns null if the name is not found
    Value lookup(String name) {
        return bindings.getOrDefault(name, null)
    }
}







