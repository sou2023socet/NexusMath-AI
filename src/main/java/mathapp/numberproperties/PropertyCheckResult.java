package mathapp.numberproperties;

public class PropertyCheckResult {
    private final String lhsExpression;
    private final String rhsExpression;
    private final double lhsValue;
    private final double rhsValue;
    private final boolean isEqual;
    private final String propertyDetected;
    private final String reason;

    public PropertyCheckResult(String lhsExpression, String rhsExpression, double lhsValue, 
                               double rhsValue, boolean isEqual, String propertyDetected, String reason) {
        this.lhsExpression = lhsExpression;
        this.rhsExpression = rhsExpression;
        this.lhsValue = lhsValue;
        this.rhsValue = rhsValue;
        this.isEqual = isEqual;
        this.propertyDetected = propertyDetected;
        this.reason = reason;
    }

    public String getLhsExpression() { return lhsExpression; }
    public String getRhsExpression() { return rhsExpression; }
    public double getLhsValue() { return lhsValue; }
    public double getRhsValue() { return rhsValue; }
    public boolean isEqual() { return isEqual; }
    public String getPropertyDetected() { return propertyDetected; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("L.H.S: ").append(lhsExpression).append(" = ").append(lhsValue).append("\n");
        sb.append("R.H.S: ").append(rhsExpression).append(" = ").append(rhsValue).append("\n");
        sb.append("L.H.S == R.H.S : ").append(isEqual ? "YES ✅" : "NO ❌").append("\n");
        sb.append("Property Detected : ").append(propertyDetected).append("\n");
        sb.append("Reason : ").append(reason).append("\n");
        sb.append("--------------------------------------------------");
        return sb.toString();
    }
}