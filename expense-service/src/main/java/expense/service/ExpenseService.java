package expense.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expense.dto.ExpenseDto;
import expense.entities.Expense;
import expense.repository.ExpenseRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExpenseService {

    // Repository for interacting with Expense entities in the database
    private final ExpenseRepository expenseRepository;

    // ObjectMapper for converting between DTOs and entities
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor for dependency injection of the ExpenseRepository
    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Creates a new expense record in the database.
     *
     * @param expenseDto Data transfer object containing expense details
     * @return true if the expense was saved successfully, false otherwise
     */
    public boolean createExpense(ExpenseDto expenseDto) {
        // Set a default currency if none is provided
        setCurrency(expenseDto);
        try {
            // Convert DTO to entity and save it to the repository
            expenseRepository.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;
        } catch (Exception ex) {
            // Log the exception or handle it as needed
            return false;
        }
    }

    /**
     * Updates an existing expense record in the database.
     *
     * @param expenseDto Data transfer object containing updated expense details
     * @return true if the expense was updated successfully, false if not found
     */
    public boolean updateExpense(ExpenseDto expenseDto) {
        // Set a default currency if none is provided
        setCurrency(expenseDto);
        // Find the existing expense based on user ID and external ID
        Optional<Expense> expenseFoundOpt = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());
        if (expenseFoundOpt.isEmpty()) {
            return false; // Expense not found
        }

        // Get the found expense entity
        Expense expense = expenseFoundOpt.get();
        // Update fields with values from DTO, if present
        expense.setAmount(expenseDto.getAmount());
        expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant()) ? expenseDto.getMerchant() : expense.getMerchant());
        expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency()) ? expenseDto.getCurrency() : expense.getCurrency());
        // Save the updated expense entity
        expenseRepository.save(expense);
        return true;
    }

    /**
     * Retrieves a list of expenses for a given user.
     *
     * @param userId The ID of the user whose expenses are to be retrieved
     * @return A list of ExpenseDto objects representing the user's expenses
     */
    public List<ExpenseDto> getExpenses(String userId) {
        // Retrieve expenses from the repository
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        // Convert the list of entities to a list of DTOs
        return objectMapper.convertValue(expenses, new TypeReference<List<ExpenseDto>>() {});
    }

    /**
     * Sets a default currency for the expense DTO if none is provided.
     *
     * @param expenseDto The expense DTO to be updated
     */
    private void setCurrency(ExpenseDto expenseDto) {
        if (Objects.isNull(expenseDto.getCurrency())) {
            expenseDto.setCurrency("inr"); // Default currency
        }
    }
}
