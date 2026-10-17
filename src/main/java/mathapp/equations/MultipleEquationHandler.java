package mathapp.equations;

import mathapp.equations.api.EquationSolver;
import mathapp.logging.MathLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultipleEquationHandler {

    private static final String CAT = "EQUATION_SYSTEM";

    private final EquationGenerator generator;
    private final EquationSolver solver;

    public MultipleEquationHandler(EquationGenerator generator, EquationSolver solver) {
        this.generator = generator;
        this.solver = solver;
    }

    public Map<String, Double> solveSystem(List<String> equations, String rawValues) {
        MathLogger.operationStarted("solveSystem", "equations=" + equations + ", values=" + rawValues);

        Map<String, Double> valuesMap = parseInitialValues(rawValues);
        MathLogger.operationStep(CAT, "PARSE_INITIAL_VALUES", "values=" + valuesMap);

        List<String> cleanEquations = new ArrayList<>();
        for (String eq : equations) {
        	cleanEquations.add(EquationGenerator.normalizeImplicitMultiplication(eq.trim()));
        }
        MathLogger.operationStep(CAT, "NORMALIZE_EQUATIONS", "clean=" + cleanEquations);

        boolean progressMade = true;
        Set<String> solvedEquations = new HashSet<>();
        int guard = cleanEquations.size() * 10 + 1;

        while (progressMade && solvedEquations.size() < cleanEquations.size() && guard-- > 0) {
            progressMade = false;

            for (String eq : cleanEquations) {
                if (solvedEquations.contains(eq)) continue;

                List<String> unknownsInEq = getUnknownVariables(eq, valuesMap);
                MathLogger.operationStep(CAT, "ANALYZE_EQUATION", "eq='" + eq + "' unknowns=" + unknownsInEq);

                if (unknownsInEq.size() == 1) {
                    String targetVar = unknownsInEq.get(0);
                    String syntheticRawData = buildRawDataPayload(valuesMap, targetVar);
                    MathLogger.operationStep(CAT, "SOLVE_SINGLE", "target=" + targetVar + " payload=" + syntheticRawData);

                    try {
                        Map<String, Double> localResult = generator.processAndSolve(syntheticRawData, eq);
                        if (localResult != null) {
                            MathLogger.operationStep(CAT, "SOLVE_SINGLE_RESULT", "result=" + localResult);
                            valuesMap.putAll(localResult);
                        } else {
                            MathLogger.operationWarning(CAT, "Equation produced null result: " + eq);
                        }
                    } catch (Exception ex) {
                        MathLogger.operationFailed(CAT, "Equation failed to solve: " + eq, ex);
                    }

                    solvedEquations.add(eq);
                    progressMade = true;
                } else if (unknownsInEq.size() == 0) {
                    MathLogger.operationWarning(CAT, "Equation has no unknowns: " + eq);
                } else {
                    MathLogger.operationStep(CAT, "DEFER_EQUATION", "eq='" + eq + "' has " + unknownsInEq.size() + " unknowns");
                }
            }
        }

        if (solvedEquations.size() < cleanEquations.size()) {
            MathLogger.operationWarning(CAT, "Some equations could not be solved due to circular dependencies or missing data. solved=" + solvedEquations.size() + "/" + cleanEquations.size());
        }

        MathLogger.operationSuccess(CAT, "solveSystem completed. result=" + valuesMap);
        return valuesMap;
    }

    private Map<String, Double> parseInitialValues(String rawValues) {
        Map<String, Double> values = new HashMap<>();
        String[] assignments = rawValues.trim().split("\\s+");
        for (String assignment : assignments) {
            String[] parts = assignment.split("=");
            if (parts.length != 2) continue;
            String varName = parts[0].trim();
            String valStr = parts[1].trim();
            if (!valStr.equals("?")) {
                values.put(varName, Double.parseDouble(valStr));
            }
        }
        return values;
    }

    private List<String> getUnknownVariables(String equation, Map<String, Double> knownValues) {
        List<String> unknowns = new ArrayList<>();
        String[] tokens = equation.split("[=\\+\\-\\*/\\^\\(\\)]");

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            // ✅ LOGIC FIX: Regex updated to support case-sensitive variables, numbers, and underscores
            // e.g., 'Speed', 'speed', 'time_1', 'VarA' are all valid and distinct.
            if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !knownValues.containsKey(token)) {
                if (!unknowns.contains(token)) {
                    unknowns.add(token);
                }
            }
        }
        return unknowns;
    }

    private String buildRawDataPayload(Map<String, Double> valuesMap, String targetVar) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : valuesMap.entrySet()) {
            double val = entry.getValue();

            if (val == (long) val) {
                sb.append(entry.getKey()).append("=").append((long) val).append(" ");
            } else {
                sb.append(entry.getKey()).append("=").append(val).append(" ");
            }
        }
        sb.append(targetVar).append("=?");
        return sb.toString();
    }
}