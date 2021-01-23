/**
 * @author aleo.liu
 * @version 1.0
 * @date 2021/1/23 下午2:40
 * @description
 */
public class SqlCondition {

    private String logicalOperator;

    private String RelationOperator;

    private String field;

    private String value;

    public SqlCondition(String logicalOperator, String relationOperator, String field, String value) {
        this.logicalOperator = logicalOperator;
        RelationOperator = relationOperator;
        this.field = field;
        this.value = value;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public String getRelationOperator() {
        return RelationOperator;
    }

    public void setRelationOperator(String relationOperator) {
        RelationOperator = relationOperator;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
