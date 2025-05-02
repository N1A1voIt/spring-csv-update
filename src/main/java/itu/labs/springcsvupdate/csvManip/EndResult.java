package itu.labs.springcsvupdate.csvManip;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EndResult {
    List<Map<String,String>> before;
    List<Map<String,String>> after;
    List<Map<String,Object>> affectedRows;
    List<Map<String,Object>> newRows;
}
