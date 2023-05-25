package com.example.happywed.Models;

public class BudgetModel {

    private int budgetId;
    private String title;
    private double estimatedCost;


    public int getBudgetId() {
        return budgetId;
    }

    public BudgetModel setBudgetId(int budgetId) {
        this.budgetId = budgetId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BudgetModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public BudgetModel setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
        return this;
    }


}
