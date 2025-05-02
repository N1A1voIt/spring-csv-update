package itu.labs.springcsvupdate.tableListing;
import lombok.*;

@Getter
@Setter
@ToString
public class Variable {
    private String databaseType;
    private String keyType;
    private String variableName;
    private String variableType;
}