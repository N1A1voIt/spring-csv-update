package itu.labs.springcsvupdate.tableListing;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class MTable {
    String table;
    HashMap<String,Variable> variables;
}
