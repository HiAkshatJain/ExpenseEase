import os
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_mistralai import ChatMistralAI
from app.service.Expense import Expense
from dotenv import load_dotenv

class LLMService:
    def __init__(self):
        """
        Initializes the LLMService class.
        Loads environment variables, sets up the prompt template, 
        initializes the language model, and prepares the runnable prompt.
        """
        # Load environment variables from a .env file
        load_dotenv()
        
        # Create a chat prompt template with predefined system and human messages
        self.prompt = ChatPromptTemplate.from_messages(
            [
                (
                    "system",
                    "You are an expert extraction algorithm. "
                    "Only extract relevant information from the text. "
                    "If you do not know the value of an attribute asked to extract, "
                    "return null for the attribute's value.",
                ),
                ("human", "{text}")
            ]
        )
        
        # Retrieve the API key for the LLM from environment variables
        self.apiKey = os.getenv('OPENAI_API_KEY')
        
        # Initialize the language model with the provided API key and model configuration
        self.llm = ChatMistralAI(api_key=self.apiKey, model="mistral-large-latest", temperature=0)
        
        # Combine the prompt template with the language model to create a runnable instance
        self.runnable = self.prompt | self.llm.with_structured_output(schema=Expense)

    def runLLM(self, message: str) -> dict:
        """
        Runs the language model on the provided message and returns the result.
        
        Args:
            message (str): The text message to be processed by the language model.
        
        Returns:
            dict: The result from the language model, structured according to the schema.
        """
        return self.runnable.invoke({"text": message})
