package co.edu.javeriana.as.personapp.terminal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioModelCli {
    private Integer personId;
    private Integer professionId;
    private String graduationDate;
    private String universityName;
}