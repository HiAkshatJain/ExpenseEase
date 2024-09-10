from app.utils.messagesUtil import MessagesUtil
from app.service.llmService import LLMService

class MessageService:
    def __init__(self):
        """
        Initializes the MessageService class.
        Creates instances of MessagesUtil and LLMService.
        """
        # Instantiate the MessagesUtil class to handle message-related utilities
        self.messageUtil = MessagesUtil()
        
        # Instantiate the LLMService class to interact with the language model
        self.llmService = LLMService()
    
    def process_message(self, message: str):
        """
        Processes the provided message to extract relevant information.
        
        Checks if the message is a bank SMS using MessagesUtil. If it is, 
        it processes the message using the LLMService. If not, returns None.
        
        Args:
            message (str): The text message to be processed.
        
        Returns:
            Optional[dict]: The result from the language model if the message is a bank SMS; otherwise, None.
        """
        # Check if the message is identified as a bank SMS
        if self.messageUtil.isBankSms(message):
            # Process the message with the language model if it is a bank SMS
            return self.llmService.runLLM(message)
        else:
            # Return None if the message is not a bank SMS
            return None
