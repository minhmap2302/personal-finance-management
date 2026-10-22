package org.example.backend.model.projection;
 
public interface SpentByCategoryMonth {
    Long getCategoryId();
    Integer getBudgetYear();
    Integer getBudgetMonth();
    Long getTotal();
}