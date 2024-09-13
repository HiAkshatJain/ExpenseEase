-- Import the Kong object and the resty.http library for making HTTP requests
local kong = kong
local http = require "resty.http"

-- Define the custom authentication handler
local CustomAuthHandler = {
  PRIORITY = 1000,  -- The priority determines the order in which the plugin is executed
  VERSION = "1.0",  -- Plugin version
}

-- Function to handle incoming requests and perform authentication
function CustomAuthHandler:access(config)
  -- Retrieve the URL of the authentication service from the configuration
  local auth_service_url = config.auth_service_url
  
  -- Create a new HTTP client instance
  local httpc = http.new()
  
  -- Make an HTTP GET request to the authentication service
  local res, err = httpc:request_uri(auth_service_url, {
    method = "GET",  -- HTTP method to use for the request
    headers = {
      ["Authorization"] = kong.request.get_header("Authorization")  -- Pass the Authorization header from the incoming request
    }
  })
  
  -- Check if the request failed
  if not res then
    kong.log.err("Failed to call auth service: ", err)  -- Log an error message
    return kong.response.exit(500, { message = "Internal Server Error" })  -- Respond with a 500 status code
  end
  
  -- Check if the response status is not 200 OK
  if res.status ~= 200 then
    return kong.response.exit(res.status, { message = "Unauthorized" })  -- Respond with the status code from the auth service and a generic Unauthorized message
  end
  
  -- If authentication is successful, set a custom header with the user ID from the response body
  local user_id = res.body  -- Assuming the authentication service returns the user_id in the response body
  kong.service.request.set_header("X-User-ID", user_id)  -- Set the X-User-ID header for the upstream service request
end

-- Return the handler object to make it available for use by Kong
return CustomAuthHandler
