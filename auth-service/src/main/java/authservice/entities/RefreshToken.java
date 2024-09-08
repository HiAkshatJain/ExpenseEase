package authservice.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity // Marks this class as a JPA entity, meaning it represents a table in the database.
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods automatically.
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields as parameters.
@NoArgsConstructor // Lombok annotation to generate a no-arguments constructor.
@Builder // Lombok annotation for the builder pattern, useful for constructing complex objects.
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Specifies the naming strategy for JSON serialization and deserialization (using snake_case).
@Table(name = "tokens") // Specifies the table name in the database.
public class RefreshToken {

    @Id // Marks this field as the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Defines auto-incrementing behavior for the primary key.
    private int id; // The unique identifier for the refresh token.

    private String token; // The refresh token string itself, usually randomly generated.

    private Instant expiryDate; // The expiration time of the refresh token (stored as a timestamp).

    @OneToOne // Defines a one-to-one relationship with the `UserInfo` entity.
    @JoinColumn(name = "id", referencedColumnName = "user_id") // Specifies the foreign key column (id) that refers to the user_id in the `UserInfo` table.
    private UserInfo userInfo; // The user associated with the refresh token.
}
