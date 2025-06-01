package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {
    private String number;
    private String operator;
    private Integer ownerCc;
    private String database; // para seleccionar entre MARIA o MONGO
}