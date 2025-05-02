package itu.labs.springcsvupdate.dataprops;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DataRow {
    Map<String,String> row;
}
