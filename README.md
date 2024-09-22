# ExpenseEase

**Overview:**  
ExpenseEase is an intuitive and powerful expense tracking application aimed at simplifying personal finance management. Whether you’re trying to save more money, stick to a budget, or simply keep track of your spending, ExpenseEase offers a comprehensive set of tools to help you achieve your financial goals.

## 1. Authentication Service

**Functionality:**

- **JWT Token Generation:** Handles the creation and issuance of JWTs for authenticated users.
- **Token Validation:** Validates JWTs for incoming requests to ensure they are legitimate and not expired.
- **Token Refresh:** Issues refresh tokens and generates new JWTs when a refresh token is presented.

## 2. User Service

**Functionality:**

- **User Registration:** Manages user sign-up processes.
- **User Profile Management:** Handles user details, preferences, and settings.
- **User Lookup:** Provides endpoints for retrieving user information based on user ID or other identifiers.

## 3. Expense Service

**Functionality:**

- **Expense Tracking:** Allows users to add, update, and delete expense records.
- **Category Management:** Manages expense categories and tags.

## 4. LLM Service

**Functionality:**

- **Notification Listener:** An app component that listens to and captures notifications on the user's phone.
- **Data Transmission:** Securely transmits captured notifications to a backend server.
- **Database Storage:** Stores the structured data in a database for further analysis or retrieval.
- **Processing and Analysis:** Uses a pre-trained LLM to analyze and convert unstructured notification text into structured data.
