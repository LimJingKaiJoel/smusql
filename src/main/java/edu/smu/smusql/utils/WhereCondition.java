package edu.smu.smusql.utils;

import java.util.List;

public class WhereCondition {
    private Condition condition1;
    private Condition condition2;
    private String operator;

    public WhereCondition() {
        this.condition1 = null;
        this.condition2 = null;
        this.operator = null;
    }

    public WhereCondition(Condition condition1, Condition condition2, String operator) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.operator = operator;
    }

    public Condition getCondition1() {
        return condition1;
    }

    public void setCondition1(Condition condition1) {
        this.condition1 = condition1;
    }

    public Condition getCondition2() {
        return condition2;
    }

    public void setCondition2(Condition condition2) {
        this.condition2 = condition2;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
