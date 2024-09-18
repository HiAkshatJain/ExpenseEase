from flask import Flask, request, jsonify
from kafka import KafkaProducer
import os
import json
from app.service.messageService import MessageService

# Initialize the Flask application
app = Flask(__name__)

# Create an instance of the MessageService
messageService = MessageService()

# Retrieve Kafka server configuration from environment variables
kafka_host = os.getenv('KAFKA_HOST', 'localhost')
kafka_port = os.getenv('KAFKA_PORT', '9092')

# Construct the Kafka bootstrap servers string
kafka_bootstrap_servers = f"{kafka_host}:{kafka_port}"

# Print the Kafka server information (for debugging purposes)
print("Kafka server is " + kafka_bootstrap_servers)
print("\n")

# Initialize Kafka producer with the specified configuration
producer = KafkaProducer(
    bootstrap_servers=kafka_bootstrap_servers,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')  # Serialize messages as JSON
)

# Define a route for handling POST requests at '/api/v1/llm/message'
@app.route('/api/v1/llm/message', methods=['POST'])
def handle_message():
    # Extract the 'message' field from the incoming JSON request
    message = request.json.get('message')
    
    # Process the message using the MessageService
    result = messageService.process_message(message)

    # If processing returns a result, return the original message
    if result is not None:
        serialized_result = result.serialize()
        producer.send('expense_service', serialized_result)
        return jsonify(serialized_result)
    
    # If no result is returned, respond with a default message
    else:
        return jsonify({'error': 'Invalid message format'}), 400

# Define a route for handling GET requests at '/api/v1/llm/health/'
@app.route('/api/v1/llm/health/', methods=['GET'])
def handle_health():
    # Respond with a simple status message to indicate the service is running
    return 'Alert service is working'

# Run the Flask application if this script is executed directly
if __name__ == "__main__":
    app.run(host='localhost', port=8010, debug=True)
