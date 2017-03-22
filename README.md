# Reasoning Programming Asssignment - 1522391
This is the repository for the Reasoning Programming Assignment. Contained within this repo is a program that takes an input file containing logical expressions (which can be in a variety of different formats), and performs one of several operations on it.

## Using the Theorem prover
The main executable provided is ```TheoremProver.jar```. Usage is as follows: ```java -jar TheoremProver.jar <mode> <format> <file>```.

## Example of usage
**Input (test.txt):** 

```{(A | B), (A -> P), (B -> Q)} : (P | Q)```

**Command to execute:**

```$ java -jar TheoremProver.jar -sat -set test.txt```

**Output follows:** 

```Input expression:
{(A | B), (A -> P), (B -> Q)} : (P | Q)
Clause Normal Form:
{{P, Q}, {P, -A}, {A, B}, {Q, -B}}
Performing DPLL to find a model
Model:
I(P) = T, I(Q) = T, I(A) = T, I(B) = T
Initial expression is SATISFIABLE```

## Source code and test cases
Source code can be found at ```src/reasoning```. There are also a number of test cases at ```src\test```. 

## Modes
### Clause Normal Form conversion
**Argument:** ```-cnf``` 

Converts the input expression into clause normal form. No negation occurs. The resultant clause normal form is displayed on screen. <br>
Works by first converting the expression into conjunctive normal form recursively (using an algorithm adapted from https://www.cs.jhu.edu/~jason/tutorials/convert-to-CNF.html) and then creating clauses from the series of disjunctions as one would normally do.

### Resolution Proof
**Argument:** ```-res```

Performs a resolution proof on the input expression. If the input is a simple expression, negates the entire input. If the input is a set of clauses and conclusion, only negates the conclusion. The resultant proof is shown on screen, whether it results in a contradiction or ends due to lack of terms to resolve. <br>
Works by looking for complementing terms in the clause normal form, resolving them and adding them to the clause set. Terminates if the empty clause is derived, or when there are no more terms to resolve.

### DPLL Model Generation
**Argument:** ```-sat```

Attempts to find a model for the input expression. No negation occurs. The model (if one is found) is displayed on screen. <br>
Works by first performing unit propogation and pure literal to add definite atoms to the model, and then performs splitting in an attempt to find a model with the remaining atoms.

### Comparing Resolution and DPLL
**Argument:** ```-cmp```

Compares the runtime for Resolution and DPLL. DPLL is first performed on the negation of the input to check if the negation is unsat. Resolution is then performed on the negation. The times for both procedures are then displayed on screen. <br>
Uses ```System.nanoTime()``` for timing, so times may be affected by background activity and garbage collection.

## Input formats 
### Simple expression
**Argument:** ```-exp```
Input is just an expression, using only the terminals listed in the grammar provided on the assignment brief. 

**Example:**

```(((A -> B) & C) -> D)```

### Set of premises and conclusion
**Argument:** ```-set```

Input is a set of premises, separated by commas and surrounded by curly braces, and a conclusion, separated from the premises by a colon.

**Example:** 

```{(A -> B), C} : D```

### DIMACS file format
**Argument:** ```-dimacs```

Input is in the DIMACS file format. This input can only be used for clause normal form conversion and DPLL.

**Example:** 

```c This is in DIMACS format
p cnf 2 3
1 2 0
-1 0
2 -1 0```

## Additional Commands
Debug mode can be accessed by suffixing ```-debug``` to the initial query. Debug mode prints more intermediary steps to the console to show how the procedure is getting on.