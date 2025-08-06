package com.example.domain;

import com.example.models.ElementData;
import com.example.models.ElementPercentageFitStore;
import com.example.models.TypeAlloyMatch;
import com.example.utils.Calculator;
import com.example.utils.DatabaseConnection;
import com.example.utils.PeriodicTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.utils.Formatter.getPrefixFromFit;

public class ElementDataMapFactory {

    public static Map<Integer, ElementData> getElementDataMap(
            List<TypeAlloyMatch> matches,
            List<ElementPercentageFitStore> elements,
            int resultId) throws SQLException {

        // 1) Group element-fits under each alloy. Keeps the same order of keys as in matches List.
        // At this stage, elements are grouped as it is in the database.
        LinkedHashMap<TypeAlloyMatch, List<ElementPercentageFitStore>> grouped = groupByAlloy(matches, elements);

        // 2) Regroup for the table view: one ElementData per element-index
        Map<Integer, ElementData> byElementNumber = new HashMap<>();
        for (Integer eNumber : getAllElementNumbers(grouped)){
            ElementData elementData = fetchElementDataFromComposition(resultId, eNumber);
            if (elementData == null) continue;
            int alloyPosition = 1;
            // ...and pull up to 3 alloy-fits into columns M1, M2, M3:
            for (TypeAlloyMatch alloyType : grouped.keySet()) {
                elementData.setAlloy(alloyPosition, getAlloyData(getFirstStore(grouped.get(alloyType), eNumber)));
                alloyPosition++;
            }
            byElementNumber.put(eNumber, elementData);
        }
        return byElementNumber;
    }

    private static LinkedHashMap<TypeAlloyMatch,List<ElementPercentageFitStore>> groupByAlloy(
            List<TypeAlloyMatch> matches,
            List<ElementPercentageFitStore> flatElements) {
        LinkedHashMap<TypeAlloyMatch,List<ElementPercentageFitStore>> map = new LinkedHashMap<>();
        int idx = 0;
        for (TypeAlloyMatch m : matches) {
            List<ElementPercentageFitStore> sub = new ArrayList<>();
            for (int i = 0; i < m.elementsNum() && idx < flatElements.size(); i++, idx++) {
                sub.add(flatElements.get(idx));
            }
            map.put(m, sub);
        }
        return map;
    }

    private static String getAlloyData(ElementPercentageFitStore eStore) throws SQLException{
        if (eStore == null) return "";
        if (eStore.min() < 0 || eStore.max() < 0) return "";
        String prefix = getPrefixFromFit(eStore.fit());

        String AlloyValue;
        if (eStore.commentId() == 0){
            AlloyValue = String.valueOf(eStore.min()) + " – " + String.valueOf(eStore.max());
        } else {
            AlloyValue = fetchCommentByCommentId(eStore.commentId());
        }
        return prefix + " " + AlloyValue;
    }


    private static String fetchCommentByCommentId(int commentId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = "SELECT Comment FROM MatchAlloyComments WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, commentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
        }
        return "";
    }

    private static Set<Integer> getAllElementNumbers(Map<TypeAlloyMatch, List<ElementPercentageFitStore>> grouped){
        Set<Integer> numbers = new HashSet<>();
        for (List<ElementPercentageFitStore> eList : grouped.values()) {
            numbers.addAll(eList.stream().map(ElementPercentageFitStore::ind).collect(Collectors.toSet()) );
        }
        return numbers;
    }

    private static ElementData fetchElementDataFromComposition(int resultId, Integer eNumber) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = "SELECT Min, Max FROM Composition WHERE ResultId = ? AND ElementId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, resultId);
                ps.setInt(2, eNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        float min = rs.getFloat(1);
                        float max = rs.getFloat(2);
                        return new ElementData(
                                PeriodicTable.getElementName(eNumber),
                                Calculator.round(Calculator.concentration(min, max), 3),
                                Calculator.round(Calculator.deviation(min, max), 3),
                                "", "", "" // Mark values unknown at this step - to be filled later.
                        );
                    } else { // Data not found in the Composition table
                        return new ElementData(PeriodicTable.getElementName(eNumber), 0, 0, "", "", "");
                    }
                }

            }
        }
    }

    private static ElementPercentageFitStore getFirstStore(List<ElementPercentageFitStore> eList, Integer eNumber){
        ElementPercentageFitStore found = null;
        for (ElementPercentageFitStore e : eList) {
            if (e.ind() == eNumber) {
                found = e;
                break;
            }
        }
        return found;
    }

}
