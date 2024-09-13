-- Import the typedefs module from Kong's schema definitions
local typedefs = require "kong.db.schema.typedefs"

-- Return a table defining the schema for the custom authentication plugin
return {
  name = "custom-auth",  -- The name of the plugin. This should be unique within Kong.

  -- Define the fields for the plugin configuration
  fields = {
    -- This field is used to associate the plugin with a consumer. 
    -- 'typedefs.no_consumer' indicates that this plugin does not require a consumer.
    { consumer = typedefs.no_consumer },

    -- Define which protocols the plugin supports. 'typedefs.protocols_http' means it applies to HTTP protocols.
    { protocols = typedefs.protocols_http },

    -- The 'config' field contains the plugin-specific configuration.
    { config = {
        type = "record",  -- Indicates that 'config' is a record type (i.e., a structured object).
        fields = {  -- Define the fields within the 'config' record.
          -- The 'auth_service_url' field is used to specify the URL of the authentication service.
          -- It is a required field with a default value.
          -- 'type = "string"' specifies that the field must be a string.
          -- 'required = true' means this field must be provided.
          -- 'default = "http://authservice:9898/auth/ping"' provides a default URL if none is supplied.
          { auth_service_url = { type = "string", required = true, default = "http://authservice:9898/auth/ping" } },
        },
      },
    },
  },
}
