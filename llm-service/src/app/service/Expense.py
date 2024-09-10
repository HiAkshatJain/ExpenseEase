from typing import Optional
from langchain_core.pydantic_v1 import BaseModel, Field

class Expense(BaseModel):
    """
    A model representing information about a transaction made on any Card.
    This includes details like the transaction amount, the merchant, and the currency used.
    """

    # The amount spent in the transaction, stored as a string. It's optional.
    amount: Optional[str] = Field(title="expense", description="Expense made on the transaction")

    # The name of the merchant where the transaction occurred, stored as a string. It's optional.
    merchant: Optional[str] = Field(title="merchant", description="Merchant name where the transaction was made")

    # The currency used for the transaction, stored as a string. It's optional.
    currency: Optional[str] = Field(title="currency", description="Currency of the transaction")

    def serialize(self):
        """
        Serializes the Expense object into a dictionary format.
        This allows for easy conversion of the object into a JSON-compatible format.
        """
        return {
            "amount": self.amount,
            "merchant": self.merchant,
            "currency": self.currency
        }
