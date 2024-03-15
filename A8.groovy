package org.junit
import org.junit.IdC

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
            Value funVal = interp(expr.f, env);
            List<Value> argVals = new ArrayList<>();
            for (ExprC arg : expr.p) {
                argVals.add(interp(arg, env));
            }
            if (funVal instanceof CloV) {
                CloV cloVal = (CloV) funVal;
                List<String> lamArgs = cloVal.args;
                if (lamArgs.size() == argVals.size()) {
                    Env newEnv = new Env();
                    for (int i = 0; i < lamArgs.size(); i++) {
                        newEnv.bind(lamArgs.get(i), argVals.get(i));
                    }
                    return interp(cloVal.body, newEnv);
                } else {
                    throw new RuntimeException("OAZO: Incorrect number of arguments");
                }
            } else if (funVal instanceof PrimV) {
                PrimV primVal = (PrimV) funVal;
                return interpPrimitive(primVal.op, argVals, env);
            } else {
                throw new RuntimeException("OAZO: Cannot apply to type " + funVal.getClass().getName());
            }
        }
}


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
            Value funVal = interp(expr.f, env);
            List<Value> argVals = new ArrayList<>();
            for (ExprC arg : expr.p) {
                argVals.add(interp(arg, env));
            }
            if (funVal instanceof CloV) {
                CloV cloVal = (CloV) funVal;
                List<String> lamArgs = cloVal.args;
                if (lamArgs.size() == argVals.size()) {
                    Env newEnv = new Env();
                    for (int i = 0; i < lamArgs.size(); i++) {
                        newEnv.bind(lamArgs.get(i), argVals.get(i));
                    }
                    return interp(cloVal.body, newEnv);
                } else {
                    throw new RuntimeException("OAZO: Incorrect number of arguments");
                }
            } else if (funVal instanceof PrimV) {
                PrimV primVal = (PrimV) funVal;
                return interpPrimitive(primVal.op, argVals, env);
            } else {
                throw new RuntimeException("OAZO: Cannot apply to type " + funVal.getClass().getName());
            }
        }

        }
    }
    
    static Value interpPrimitive(String op, List<Value> args, Env env) {
    switch (op) {
        case "+":
            if (args.size() == 2 && args.get(0) instanceof NumV && args.get(1) instanceof NumV) {
                double a = ((NumV) args.get(0)).n;
                double b = ((NumV) args.get(1)).n;
                return new NumV(a + b);
            } else {
                throw new RuntimeException("OAZO: Incorrect for +");
            }
        case "-":
            if (args.size() == 2 && args.get(0) instanceof NumV && args.get(1) instanceof NumV) {
                double a = ((NumV) args.get(0)).n;
                double b = ((NumV) args.get(1)).n;
                return new NumV(a - b);
            } else {
                throw new RuntimeException("OAZO: Incorrect arguments for -");
            }
        case "*":
            if (args.size() == 2 && args.get(0) instanceof NumV && args.get(1) instanceof NumV) {
                double a = ((NumV) args.get(0)).n;
                double b = ((NumV) args.get(1)).n;
                return new NumV(a * b);
            } else {
                throw new RuntimeException("OAZO: Incorrect arguments for *");
            }
        case "/":
           if (args.size() == 2 && args.get(0) instanceof NumV && args.get(1) instanceof NumV) {
                double a = ((NumV) args.get(0)).n;
                double b = ((NumV) args.get(1)).n;
                if( b == 0){
                    throw new RuntimeException("OAZO: Divide by zero error");
                }else{
                return new NumV(a / b);
                }
            } else {
                throw new RuntimeException("OAZO: Incorrect arguments for *");
            }
        case "equal?":
            if (args.size() == 2) {
                Value val1 = args.get(0);
                Value val2 = args.get(1);
                if (val1.getClass() == val2.getClass()) {
                    if (val1 instanceof NumV) {
                        return new BoolV(((NumV) val1).n == ((NumV) val2).n);
                    } else if (val1 instanceof StrV) {
                        return new BoolV(((StrV) val1).str.equals(((StrV) val2).str));
                    } else if (val1 instanceof BoolV) {
                        return new BoolV(((BoolV) val1).bool == ((BoolV) val2).bool);
                    } else {
                        // For CloV and PrimV, consider them equal only if they refer to the same object (reference equality)
                        return new BoolV(val1 == val2);
                    }
                } else {
                    return new BoolV(false);
                }
            } else {
                throw new RuntimeException("OAZO: Incorrect number of arguments for equal?");
            }
        case "<=":
            if (args.size() == 2 && args.get(0) instanceof NumV && args.get(1) instanceof NumV) {
                double a = ((NumV) args.get(0)).n;
                double b = ((NumV) args.get(1)).n;
                return new BoolV(a <= b);
            } else {
                throw new RuntimeException("OAZO: Incorrect arguments for *");
            }
        case "error":
            // Implementation for error
            if (args.size() == 1) {
                Value v = args.get(0);
                String errorMessage = "user-error: " + serialize(v);
                throw new RuntimeException(errorMessage);
            } else {
                throw new RuntimeException("OAZO: Incorrect number of arguments for error");
            }
        default:
            throw new RuntimeException("OAZO: Undefined primitive operation: " + op);
    }
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

        //AppC Tests
        LamC helloWorld = new LamC([], new StrC("Hello, world!"))
        AppC helloWorldApp = new AppC(helloWorld, [])
        Value helloWorldResult = interp(helloWorldApp, env)
        println "AppC Test 1: Expected StrV('Hello, world!'), ${helloWorldResult instanceof StrV && helloWorldResult.str == 'Hello, world!' ? 'Pass' : 'Fail'}"

        Value identityResult = interp(new AppC(new IdC("equal?"), [new NumC(5), new NumC(5)]), env)
        println "AppC Test 2: Expected BoolV, ${identityResult instanceof BoolV ? 'Pass' : 'Fail'}"

        Value identityResult2 = interp(new AppC(new IdC("equal?"), [new NumC(5), new NumC(999)]), env)
        println "AppC Test 2: Expected BoolV, ${identityResult2 instanceof BoolV ? 'Pass' : 'Fail'}"

        // Test case for addition
        AppC additionApp = new AppC(new IdC('+'), [new NumC(10), new NumC(5)])
        Value additionResult = interp(additionApp, env)
        println "AppC Addition Test: Expected NumV(15), ${additionResult instanceof NumV && additionResult.n == 15 ? 'Pass' : 'Fail'}"

        // Test case for subtraction
        AppC subtractionApp = new AppC(new IdC('-'), [new NumC(10), new NumC(5)])
        Value subtractionResult = interp(subtractionApp, env)
        println "AppC Subtraction Test: Expected NumV(5), ${subtractionResult instanceof NumV && subtractionResult.n == 5 ? 'Pass' : 'Fail'}"

        // Test case for multiplication
        AppC multiplicationApp = new AppC(new IdC('*'), [new NumC(10), new NumC(5)])
        Value multiplicationResult = interp(multiplicationApp, env)
        println "AppC Multiplication Test: Expected NumV(50), ${multiplicationResult instanceof NumV && multiplicationResult.n == 50 ? 'Pass' : 'Fail'}"

        // Test case for division
        AppC divisionApp = new AppC(new IdC('/'), [new NumC(10), new NumC(2)])
        Value divisionResult = interp(divisionApp, env)
        println "AppC Division Test: Expected NumV(5), ${divisionResult instanceof NumV && divisionResult.n == 5 ? 'Pass' : 'Fail'}"

        // Test case for equality
        AppC equalityApp = new AppC(new IdC('equal?'), [new NumC(10), new NumC(10)])
        Value equalityResult = interp(equalityApp, env)
        println "AppC Equality Test: Expected BoolV(true), ${equalityResult instanceof BoolV && equalityResult.bool == true ? 'Pass' : 'Fail'}"

        // Test case for less than or equal to
        AppC lessThanOrEqualApp = new AppC(new IdC('<='), [new NumC(10), new NumC(5)])
        Value lessThanOrEqualResult = interp(lessThanOrEqualApp, env)
        println "AppC Less Than Or Equal Test: Expected BoolV(false), ${lessThanOrEqualResult instanceof BoolV && lessThanOrEqualResult.bool == false ? 'Pass' : 'Fail'}"

        // Test case for error
        AppC errorApp = new AppC(new IdC('error'), [new StrC("An error occurred")])
        try {
            interp(errorApp, env)
            println "AppC Error Test: Fail - Expected an error but did not throw"
        } catch (RuntimeException e) {
            println "AppC Error Test: Pass"
        }


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

        // Test case for equal? with two equal values
        Value equalTest1 = interp(new AppC(new IdC("equal?"), [new NumC(5), new NumC(5)]), env)
        println "Equal Test 1: Expected true, ${equalTest1 instanceof BoolV && equalTest1.bool == true ? 'Pass' : 'Fail'}"

        // Test case for equal? with two unequal values
        Value equalTest2 = interp(new AppC(new IdC("equal?"), [new StrC("hello"), new StrC("world")]), env)
        println "Equal Test 2: Expected false, ${equalTest2 instanceof BoolV && equalTest2.bool == false ? 'Pass' : 'Fail'}"

        // Test case for equal? with a closure value
        Value equalTest3 = interp(new AppC(new IdC("equal?"), [new LamC([], new NumC(1)), new LamC([], new NumC(1))]), env)
        println "Equal Test 3: Expected false, ${equalTest3 instanceof BoolV && equalTest3.bool == false ? 'Pass' : 'Fail'}"
        
        // Test case for error with a value
        try {
            interp(new AppC(new IdC("error"), [new NumC(42)]), env)
            println "Error Test 1: Fail - Expected an error but did not throw"
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("user-error: ")) {
                println "Error Test 1: Pass"
            } else {
                println "Error Test 1: Fail - Expected error message format is incorrect"
            }
        }

        // Test case for error with a string value
        try {
            interp(new AppC(new IdC("error"), [new StrC("Error message")])), env
            println "Error Test 2: Fail - Expected an error but did not throw"
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("user-error: ")) {
                println "Error Test 2: Pass"
            } else {
                println "Error Test 2: Fail - Expected error message format is incorrect"
            }
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


